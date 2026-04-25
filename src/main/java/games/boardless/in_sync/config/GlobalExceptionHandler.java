package games.boardless.in_sync.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import games.boardless.in_sync.dtos.ErrorDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(exception = { BadRequestException.class, MissingServletRequestParameterException.class })
  public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {
    logger.info(e.getMessage());
    return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = { ServiceUnavailableException.class })
  public ResponseEntity<ErrorDto> handleServiceUnavailable(Exception e) {
    logger.info(e.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = { HttpRequestMethodNotSupportedException.class })
  public ResponseEntity<ErrorDto> handleRequestMethodNotSupported(Exception e) {
    logger.info(e.getMessage());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = Exception.class)
  public ResponseEntity<ErrorDto> handleInternalServerError(Exception e) {
    logger.error(e.getMessage(), e);
    return ResponseEntity.internalServerError()
        .body(new ErrorDto("We're sorry. Something went wrong. Please try again later."));
  }
}
