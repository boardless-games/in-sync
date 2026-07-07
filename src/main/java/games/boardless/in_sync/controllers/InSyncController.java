package games.boardless.in_sync.controllers;

import games.boardless.in_sync.constants.ScheduleType;
import games.boardless.in_sync.dtos.AcknowledgeScheduleDto;
import games.boardless.in_sync.dtos.ErrorDto;
import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.GameSettingsDto;
import games.boardless.in_sync.dtos.PerformanceDto;
import games.boardless.in_sync.dtos.PlayerNameDto;
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
      content = {
        @Content(
            schema = @Schema(implementation = GameCodeDto.class),
            mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game")
  public ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newGame();
  }

  @Operation(summary = "Add a player to a game.")
  @ApiResponse(responseCode = "201", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/player")
  public ResponseEntity<Void> newPlayer(
      @PathVariable final String gameCode, @RequestBody final PlayerNameDto name)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newPlayer(gameCode, name);
  }

  @Operation(summary = "Start a game.")
  @ApiResponse(responseCode = "200", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/start")
  public DeferredResult<ResponseEntity<Void>> startGame(
      @PathVariable final String gameCode, @RequestBody final GameSettingsDto gameSettings)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.startGame(gameCode, gameSettings);
  }

  @Operation(summary = "Quit a game.")
  @ApiResponse(responseCode = "200", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/quit")
  public ResponseEntity<Void> quitGame(@PathVariable final String gameCode)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.quitGame(gameCode);
  }

  @Operation(summary = "Schedule a performance.")
  @ApiResponse(responseCode = "200", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/performance/schedule")
  public DeferredResult<ResponseEntity<Void>> schedulePerformance(
      @PathVariable final String gameCode) throws BadRequestException, ServiceUnavailableException {
    return inSyncService.schedule(gameCode, ScheduleType.PERFORMANCE);
  }

  @Operation(summary = "Submit a performance.")
  @ApiResponse(responseCode = "200", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/performance")
  public ResponseEntity<Void> newPerformance(
      @PathVariable final String gameCode, @RequestBody final PerformanceDto performance)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newPerformance(gameCode, performance);
  }

  @Operation(summary = "Schedule a playback.")
  @ApiResponse(responseCode = "200", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/playback/schedule")
  public DeferredResult<ResponseEntity<Void>> schedulePlayback(@PathVariable final String gameCode)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.schedule(gameCode, ScheduleType.PLAYBACK);
  }

  @Operation(summary = "Acknowledge a schedule.")
  @ApiResponse(responseCode = "200", content = @Content)
  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @ApiResponse(
      responseCode = "503",
      content = {
        @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json")
      })
  @PostMapping("/game/{gameCode}/schedule/acknowledge")
  public ResponseEntity<Void> acknowledgeSchedule(
      @PathVariable final String gameCode, @RequestBody final AcknowledgeScheduleDto ack)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.acknowledgeSchedule(gameCode, ack);
  }
}
