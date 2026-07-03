package games.boardless.in_sync.config;

import games.boardless.in_sync.dtos.ErrorDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.utils.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(
      exception = {
        BadRequestException.class,
        MissingServletRequestParameterException.class,
        HttpMessageConversionException.class
      })
  public ResponseEntity<ErrorDto> handleBadRequest(Exception e) {
    logger.info(ToString.toString(e, 1));
    return ResponseEntity.badRequest().body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = {ServiceUnavailableException.class})
  public ResponseEntity<ErrorDto> handleServiceUnavailable(Exception e) {
    logger.info(ToString.toString(e, 1));
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = RuntimeException.class)
  public ResponseEntity<ErrorDto> handleInternalServerError(Exception e) {
    logger.error(e.getMessage(), e);
    return ResponseEntity.internalServerError()
        .body(new ErrorDto("We're sorry. Something went wrong. Please try again later."));
  }
}
