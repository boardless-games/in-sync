package games.boardless.in_sync.controllers;

import static games.boardless.in_sync.constants.Constants.APP_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("")
public class MetadataController {
  private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

  @Value("${client.url}")
  private String clientUrl;

  @Operation(summary = "Returns the server's health.")
  @ApiResponse(responseCode = "200", description = "The server is healthy!")
  @GetMapping("/health")
  public ResponseEntity<String> health() {
    logger.info("The server is healthy!");
    return ResponseEntity.ok("The server is healthy!");
  }

  @Operation(summary = "Returns a brief description of this server.")
  @ApiResponse(responseCode = "200", description = "A brief description of the server.")
  @GetMapping("/about")
  public ResponseEntity<String> about() {
    logger.info("The server is informative!");
    return ResponseEntity.ok(String.format(
        "This is the server for the multiplayer party game %s! Visit the website here: %s.", APP_NAME, clientUrl));
  }
}
