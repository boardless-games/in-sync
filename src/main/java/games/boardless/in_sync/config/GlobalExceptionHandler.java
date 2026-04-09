package games.boardless.in_sync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(exception = Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    logger.error(e.getMessage(), e);
    return ResponseEntity.internalServerError().body("We're sorry. Something went wrong. Please try again later.");
  }
}
