package games.boardless.in_sync_server.handlers;

import games.boardless.in_sync_server.dtos.ErrorDto;
import games.boardless.in_sync_server.exceptions.BadRequestException;
import games.boardless.in_sync_server.exceptions.NotFoundException;
import games.boardless.in_sync_server.exceptions.ServiceUnavailableException;
import games.boardless.in_sync_server.utils.ToString;
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

  @ExceptionHandler(exception = {NotFoundException.class})
  public ResponseEntity<ErrorDto> handleNotFound(Exception e) {
    logger.info(ToString.toString(e, 1));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(e.getMessage()));
  }

  @ExceptionHandler(exception = RuntimeException.class)
  public ResponseEntity<ErrorDto> handleInternalServerError(Exception e) {
    logger.error(e.getMessage(), e);
    return ResponseEntity.internalServerError()
        .body(new ErrorDto("We're sorry. Something went wrong. Please try again later."));
  }
}
