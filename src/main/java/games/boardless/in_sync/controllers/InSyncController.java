package games.boardless.in_sync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import games.boardless.in_sync.dtos.ErrorDto;
import games.boardless.in_sync.dtos.NewGameDto;
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

  @Operation(summary = "Creates a new game.")
  @ApiResponse(responseCode = "201", description = "A new game was created.", content = {
      @Content(schema = @Schema(implementation = NewGameDto.class), mediaType = "application/json") })
  @ApiResponse(responseCode = "400", description = "An empty or invalid name was provided.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @ApiResponse(responseCode = "503", description = "The server is at maximum capacity.", content = {
      @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = "application/json") })
  @PostMapping("/game")
  public ResponseEntity<NewGameDto> newGame(
      @RequestParam @Parameter(description = "The player's name.", example = "Craiglington") final String name)
      throws BadRequestException, ServiceUnavailableException {
    return inSyncService.newGame(name);
  }
}
