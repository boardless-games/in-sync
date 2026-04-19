package games.boardless.in_sync.services;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import games.boardless.in_sync.dtos.NewGameDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.models.Game;
import games.boardless.in_sync.utils.InputValidation;

@Service
public class InSyncService {
  private static final Logger logger = LoggerFactory.getLogger(InSyncService.class);
  private static final int MAX_NUM_GAMES = 1;
  private static final long AUTO_DELETE_TIME = 60000;
  private final TaskScheduler taskScheduler;
  private static final Map<String, Game> games = new ConcurrentHashMap<>();

  @Autowired
  public InSyncService(final TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  public synchronized ResponseEntity<NewGameDto> newGame(final String name)
      throws BadRequestException, ServiceUnavailableException {
    logger.info(String.format("New game requested by %s.", name));

    // Is the server already at the max capacity?
    if (games.size() >= MAX_NUM_GAMES) {
      throw new ServiceUnavailableException("The server is at max capacity. Please try again later.");
    }

    // Validate the name.
    final Optional<String> nameValidation = InputValidation.validateName(name);
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    // Generate and validate the new game code.
    String gameCode;
    do {
      gameCode = Game.generateGameCode();
    } while (games.containsKey(gameCode));

    // Create and add the new game and player.
    final Game newGame = new Game(gameCode);
    newGame.addPlayer(name);
    games.put(gameCode, newGame);

    // Create auto delete timer.
    this.taskScheduler.schedule(() -> {
      deleteGame(newGame.getGameCode(), false);
    }, Instant.now().plusMillis(AUTO_DELETE_TIME));

    logger.info(String.format("New game created by %s with game code: %s.", name, gameCode));

    return ResponseEntity.ok(new NewGameDto(gameCode));
  }

  public void deleteGame(final String gameCode, final boolean force) {
    // Find the game.
    final Game game = games.get(gameCode);

    // Don't delete if game doesn't exist or players are still connected.
    if (game == null || (game.hasConnectedPlayers() && !force))
      return;

    // Delete the game.
    if (games.remove(gameCode) == null) {
      logger.error(String.format("Failed to delete game: %s.", game.toString()));
      return;
    }

    logger.info(String.format("Deleted game with game code: %s.", gameCode));
  }
}
