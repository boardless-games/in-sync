package games.boardless.in_sync.config;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import games.boardless.in_sync.dtos.ErrorDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(exception = { BadRequestException.class, MissingServletRequestParameterException.class })
  public ResponseEntity<ErrorDto> handleBadRequests(Exception e) {
    logger.info(e.getMessage(), e);
    return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = Exception.class)
  public ResponseEntity<ErrorDto> handleException(Exception e) {
    logger.error(e.getMessage(), e);
    return ResponseEntity.internalServerError()
        .body(new ErrorDto("We're sorry. Something went wrong. Please try again later."));
  }
}
