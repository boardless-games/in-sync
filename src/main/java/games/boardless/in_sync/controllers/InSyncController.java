package games.boardless.in_sync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import games.boardless.in_sync.dtos.ErrorDto;
import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.NameDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.services.InSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/in-sync")
public class InSyncController {
  private final InSyncService inSyncService;

  @Autowired
  public InSyncController(final InSyncService inSyncService) {
    this.inSyncService = inSyncService;
  }

  @Operation(summary = "Create a new game.")
  @ApiResponse(responseCode = "201", description = "A new game was created.", content = {
      @Content(schema = @Schema(implementation = GameCodeDto.class), mediaType = "application/json") })
  @ApiResponse(responseCode = "400", description = "An empty or invalid name was provided.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @ApiResponse(responseCode = "503", description = "The server is at maximum capacity.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @PostMapping("/game")
  public ResponseEntity<GameCodeDto> newGame()
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newGame();
  }

  @Operation(summary = "Add a player to a game.")
  @ApiResponse(responseCode = "201", description = "Successfully added the player to the game.", content = @Content)
  @ApiResponse(responseCode = "400", description = "An empty or invalid game code or name was provided.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @ApiResponse(responseCode = "503", description = "The game is at maximum capacity.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @PostMapping("/game/{gameCode}/player")
  public ResponseEntity<Void> newPlayer(
      @PathVariable @Parameter(description = "The game code.", example = "123456") final String gameCode,
      @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The player's name.", content = {
          @Content(schema = @Schema(implementation = NameDto.class), mediaType = "application/json") }) final NameDto newPlayerDto)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newPlayer(gameCode, newPlayerDto);
  }

  @Operation(summary = "Start a game.")
  @ApiResponse(responseCode = "200", description = "Successfully initiated the start process.", content = @Content)
  @ApiResponse(responseCode = "400", description = "An empty or invalid game code was provided.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @ApiResponse(responseCode = "503", description = "The game's players are not ready.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @PostMapping("/game/{gameCode}/start")
  public DeferredResult<ResponseEntity<Void>> initiateStartGame(
      @PathVariable @Parameter(description = "The game code.", example = "123456") final String gameCode)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.startGame(gameCode);
  }
}
