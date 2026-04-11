package games.boardless.in_sync.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import games.boardless.in_sync.dtos.AboutDto;
import games.boardless.in_sync.dtos.HealthDto;
import games.boardless.in_sync.services.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("")
public class MetadataController {

  private final MetadataService metadataService;

  @Autowired
  public MetadataController(final MetadataService metadataService) {
    this.metadataService = metadataService;
  }

  @Operation(summary = "Returns the server's health.")
  @ApiResponse(responseCode = "200", description = "The server is healthy!", content = {
      @Content(schema = @Schema(implementation = HealthDto.class), mediaType = "application/json") })
  @GetMapping("/health")
  public ResponseEntity<HealthDto> health() {
    return this.metadataService.health();
  }

  @Operation(summary = "Returns a brief description of this server.")
  @ApiResponse(responseCode = "200", description = "A brief description of the server.", content = {
      @Content(schema = @Schema(implementation = AboutDto.class), mediaType = "application/json") })
  @GetMapping("/about")
  public ResponseEntity<AboutDto> about() {
    return this.metadataService.about();
  }
}
