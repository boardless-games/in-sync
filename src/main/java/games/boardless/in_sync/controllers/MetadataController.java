package games.boardless.in_sync.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping
public class MetadataController {
  private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

  @Operation(summary = "Returns the server's health.")
  @ApiResponse(responseCode = "200", description = "The server is up!", content = @Content)
  @GetMapping("/health")
  public ResponseEntity<Void> health() {
    logger.info("The server is up!");
    return ResponseEntity.ok().build();
  }
}
