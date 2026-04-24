package games.boardless.in_sync.services;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.NameDto;
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

  public synchronized ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    logger.info("New game requested.");

    // Is the server already at the max capacity?
    if (games.size() >= MAX_NUM_GAMES) {
      throw new ServiceUnavailableException("The server is at max capacity. Please try again later.");
    }

    // Generate and validate the new game code.
    String gameCode;
    do {
      gameCode = Game.generateGameCode();
    } while (games.containsKey(gameCode));

    // Create and add the new game and player.
    final Game newGame = new Game(gameCode);
    games.put(gameCode, newGame);

    // Create auto delete timer.
    this.taskScheduler.schedule(() -> {
      deleteGame(newGame.getGameCode(), false);
    }, Instant.now().plusMillis(AUTO_DELETE_TIME));

    logger.info(String.format("New game created with game code: %s.", gameCode));

    return ResponseEntity.status(HttpStatus.CREATED).body(new GameCodeDto(gameCode));
  }

  public ResponseEntity<Object> newPlayer(final String gameCode, final NameDto nameDto)
      throws BadRequestException, ServiceUnavailableException {
    logger.info(String.format("%s requested to join game with game code: %s.", nameDto.name(), gameCode));

    // Validate the game code.
    final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
    if (gameCodeValidation.isPresent()) {
      throw new BadRequestException(gameCodeValidation.get());
    }

    // Validate the name.
    final Optional<String> nameValidation = InputValidation.validateName(nameDto.name());
    if (nameValidation.isPresent()) {
      throw new BadRequestException(nameValidation.get());
    }

    // Get the game.
    final Game game = games.get(gameCode);
    if (game == null) {
      throw new BadRequestException(String.format("No game found with gamecode: %s.", gameCode));
    }

    // Add the player
    game.addPlayer(nameDto.name());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public void connectionEstablished(final WebSocketSession session) {
    // Get the query params.
    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();

    try {
      // Get and validate the game code.
      final String gameCode = queryParams.get("gameCode").getFirst();
      final Optional<String> gameCodeValidation = InputValidation.validateGameCode(gameCode);
      if (gameCodeValidation.isPresent()) {
        throw new BadRequestException(gameCodeValidation.get());
      }

      // Get the game.
      final Game game = games.get(gameCode);
      if (game == null) {
        throw new BadRequestException(String.format("No game found with gamecode: %s.", gameCode));
      }

      // Get and validate the name.
      final String name = queryParams.get("name").getFirst();
      final Optional<String> nameValidation = InputValidation.validateName(name);
      if (nameValidation.isPresent()) {
        throw new BadRequestException(nameValidation.get());
      }

      // Set the player's session.
      if (!game.setPlayerSession(name, session)) {
        throw new BadRequestException(String.format("No player found in game %s with name %s.", gameCode, name));
      }

    } catch (NullPointerException | BadRequestException badDataException) {
      logger.info(
          String.format("Closing session after connection established due to bad data. %s.", session.toString()),
          badDataException);
      try {
        session.close(CloseStatus.BAD_DATA);
      } catch (IOException ioException) {
        logger.error(
            String.format("Failed to close session after connection established due to bad data. %s.",
                session.toString()),
            ioException);
      }
    }
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
