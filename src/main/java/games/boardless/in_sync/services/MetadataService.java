package games.boardless.in_sync.services;

import static games.boardless.in_sync.constants.Constants.APP_DESCRIPTION;
import static games.boardless.in_sync.constants.Constants.APP_NAME;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import games.boardless.in_sync.dtos.AboutDto;
import games.boardless.in_sync.dtos.HealthDto;

@Service
public class MetadataService {
  private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);
  private static final String SERVER_START_DATE = new Date().toString();

  @Value("${client.url}")
  private String clientUrl;

  public ResponseEntity<HealthDto> health() {
    logger.info("The server is healthy!");
    return ResponseEntity.ok(new HealthDto(true, SERVER_START_DATE));
  }

  public ResponseEntity<AboutDto> about() {
    logger.info("The server describes itself!");
    return ResponseEntity.ok(new AboutDto(APP_NAME, APP_DESCRIPTION, clientUrl));
  }
}
