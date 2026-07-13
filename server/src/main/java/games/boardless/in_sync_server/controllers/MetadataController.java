package games.boardless.in_sync_server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MetadataController {
  @Operation(summary = "Returns the server's health.")
  @ApiResponse(responseCode = "200", description = "The server is up!", content = @Content)
  @GetMapping("/health")
  public ResponseEntity<Void> health() {
    return ResponseEntity.ok().build();
  }
}
