package games.boardless.in_sync.controllers;

import games.boardless.in_sync.dtos.ErrorDto;
import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.GameSettingsDto;
import games.boardless.in_sync.dtos.NameDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.services.InSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/in-sync")
public class InSyncController {
  private final InSyncService inSyncService;

  @Autowired
  public InSyncController(final InSyncService inSyncService) {
    this.inSyncService = inSyncService;
  }

  @Operation(summary = "Create a new game.")
  @ApiResponse(
      responseCode = "201",
      description = "A new game was created.",
      content = {
        @Content(
            schema = @Schema(implementation = GameCodeDto.class),
            mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "400",
      description = "An empty or invalid name was provided.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      description = "The server is at maximum capacity.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game")
  public ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newGame();
  }

  @Operation(summary = "Add a player to a game.")
  @ApiResponse(
      responseCode = "201",
      description = "Successfully added the player to the game.",
      content = @Content)
  @ApiResponse(
      responseCode = "400",
      description =
          """
      - An empty or invalid game code provided.
      - An empty or invalid new player was provided.
      """,
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      description = "The game is at maximum capacity.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/player")
  public ResponseEntity<Void> newPlayer(
      @PathVariable final String gameCode, @RequestBody final NameDto name)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newPlayer(gameCode, name);
  }

  @Operation(summary = "Start a game.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully started the game.",
      content = @Content)
  @ApiResponse(
      responseCode = "400",
      description =
          """
      - An empty or invalid game code was provided.
      - Invalid game settings were provided.
      """,
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      description =
          """
      - The game's players are not ready
      - The request took too long
      """,
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/start")
  public DeferredResult<ResponseEntity<Void>> startGame(
      @PathVariable final String gameCode, @RequestBody final GameSettingsDto gameSettings)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.startGame(gameCode, gameSettings);
  }

  @Operation(summary = "Schedule a performance.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully scheduled a performance.",
      content = @Content)
  @ApiResponse(
      responseCode = "400",
      description = "An empty or invalid game code was provided.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      description = "A performance has already been scheduled.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/schedule")
  public DeferredResult<ResponseEntity<Void>> schedulePerformance(
      @PathVariable final String gameCode) throws BadRequestException, ServiceUnavailableException {
    return inSyncService.schedulePerformance(gameCode);
  }

  @Operation(summary = "Acknowledge a performance schedule.")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully acknowledged a performance schedule.",
      content = @Content)
  @ApiResponse(
      responseCode = "400",
      description = "An empty or invalid game code was provided.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      description = "A performance has already been scheduled.",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/schedule")
  public DeferredResult<ResponseEntity<Void>> acknowledgePerformanceSchedule(
      @PathVariable final String gameCode) throws BadRequestException, ServiceUnavailableException {
    return inSyncService.acknowledgePerformanceSchedule(gameCode);
  }
}
