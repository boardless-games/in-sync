package games.boardless.in_sync.services;

import static games.boardless.in_sync.constants.Constants.GAME_AUTO_DELETE_TIME;
import static games.boardless.in_sync.constants.Constants.MAX_NUM_GAMES;
import static games.boardless.in_sync.constants.Constants.PLAYER_AUTO_REMOVE_TIME;
import static games.boardless.in_sync.constants.Constants.WAIT_PLAYER_READY_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.context.request.async.DeferredResult;

import games.boardless.in_sync.dtos.GameCodeDto;
import games.boardless.in_sync.dtos.NameDto;
import games.boardless.in_sync.exceptions.BadRequestException;
import games.boardless.in_sync.exceptions.ServiceUnavailableException;
import games.boardless.in_sync.models.Game;
import games.boardless.in_sync.utils.InputValidation;

@ExtendWith(MockitoExtension.class)
class InSyncServiceTests {
  @Mock
  private TaskScheduler taskScheduler;

  @Spy
  private Clock clock = Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneOffset.UTC);

  @Spy
  @InjectMocks
  private InSyncService inSyncService;

  @BeforeEach
  void beforeEach() {
    this.inSyncService.games.clear();
  }

  @Test
  void newGame_withMaxedOutGames_shouldThrowServiceUnavailableException() {
    for (int i = 0; i < MAX_NUM_GAMES; ++i) {
      final String gameCode = UUID.randomUUID().toString();
      this.inSyncService.games.put(gameCode, mock(Game.class));
    }
    assertThrows(ServiceUnavailableException.class, () -> {
      this.inSyncService.newGame();
    });
  }

  @Test
  void newGame_shouldCreateNewGame() throws BadRequestException, ServiceUnavailableException {
    final ResponseEntity<GameCodeDto> result = this.inSyncService.newGame();

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    final GameCodeDto body = result.getBody();
    assertNotNull(body);
    assertNotNull(this.inSyncService.games.get(body.gameCode()));

    final ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
    final ArgumentCaptor<Instant> startTimeCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(this.taskScheduler).schedule(taskCaptor.capture(), startTimeCaptor.capture());

    doNothing().when(this.inSyncService).autoDeleteGame(anyString());
    taskCaptor.getValue().run();
    verify(this.inSyncService).autoDeleteGame(anyString());

    assertEquals(this.clock.instant().plusMillis(GAME_AUTO_DELETE_TIME), startTimeCaptor.getValue());
  }

  @Test
  void newPlayer_withInvalidGameCode_shouldThrowBadRequestException() {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      final String validationResult = UUID.randomUUID().toString();
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.of(validationResult));

      final BadRequestException e = assertThrows(BadRequestException.class, () -> {
        this.inSyncService.newPlayer("", new NameDto(""));
      });

      assertEquals(validationResult, e.getMessage());
      mockInputValidation.verify(() -> InputValidation.validateGameCode(anyString()));
    }
  }

  @Test
  void newPlayer_withInvalidName_shouldThrowBadRequestException() {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      final String validationResult = UUID.randomUUID().toString();
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.empty());
      mockInputValidation.when(() -> InputValidation.validateName(anyString()))
          .thenReturn(Optional.of(validationResult));

      final BadRequestException e = assertThrows(BadRequestException.class, () -> {
        this.inSyncService.newPlayer("", new NameDto(""));
      });

      assertEquals(validationResult, e.getMessage());
      mockInputValidation.verify(() -> InputValidation.validateGameCode(anyString()));
      mockInputValidation.verify(() -> InputValidation.validateName(anyString()));
    }
  }

  @Test
  void newPlayer_withNonExistentGameCode_shouldThrowBadRequestException() {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.empty());
      mockInputValidation.when(() -> InputValidation.validateName(anyString()))
          .thenReturn(Optional.empty());

      final String gameCode = UUID.randomUUID().toString();
      final BadRequestException e = assertThrows(BadRequestException.class, () -> {
        this.inSyncService.newPlayer(gameCode, new NameDto(""));
      });

      assertTrue(e.getMessage().contains(gameCode));
    }
  }

  @Test
  void newPlayer_shouldAddPlayer() throws BadRequestException, ServiceUnavailableException {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.empty());
      mockInputValidation.when(() -> InputValidation.validateName(anyString()))
          .thenReturn(Optional.empty());

      final String gameCode = UUID.randomUUID().toString();
      final String name = UUID.randomUUID().toString();
      final Game game = mock();
      when(game.getGameCode()).thenReturn(gameCode);
      this.inSyncService.games.put(gameCode, game);

      final ResponseEntity<Void> result = this.inSyncService.newPlayer(gameCode, new NameDto(name));

      verify(game).addPlayer(name);
      assertEquals(HttpStatus.CREATED, result.getStatusCode());
      assertNull(result.getBody());

      final ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
      final ArgumentCaptor<Instant> startTimeCaptor = ArgumentCaptor.forClass(Instant.class);
      verify(this.taskScheduler).schedule(taskCaptor.capture(), startTimeCaptor.capture());

      doNothing().when(this.inSyncService).autoRemovePlayer(anyString(), anyString());
      taskCaptor.getValue().run();
      verify(this.inSyncService).autoRemovePlayer(gameCode, name);

      assertEquals(this.clock.instant().plusMillis(PLAYER_AUTO_REMOVE_TIME), startTimeCaptor.getValue());
    }
  }

  @Test
  void startGame_withInvalidGameCode_shouldThrowBadRequestException() {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      final String validationResult = UUID.randomUUID().toString();
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.of(validationResult));

      final BadRequestException e = assertThrows(BadRequestException.class, () -> {
        this.inSyncService.startGame("");
      });

      assertEquals(validationResult, e.getMessage());
      mockInputValidation.verify(() -> InputValidation.validateGameCode(anyString()));
    }
  }

  @Test
  void startGame_withNonExistentGameCode_shouldThrowBadRequestException() {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.empty());

      final String gameCode = UUID.randomUUID().toString();
      final BadRequestException e = assertThrows(BadRequestException.class, () -> {
        this.inSyncService.startGame(gameCode);
      });

      assertTrue(e.getMessage().contains(gameCode));
    }
  }

  @Test
  void startGame_withBadAutoStart_shouldHaveExceptionResult() throws BadRequestException, ServiceUnavailableException {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.empty());

      final String gameCode = UUID.randomUUID().toString();
      final Game game = mock();
      this.inSyncService.games.put(gameCode, game);

      final DeferredResult<ResponseEntity<Void>> deferredResult = this.inSyncService.startGame(gameCode);
      verify(game).initialize();
      assertFalse(deferredResult.hasResult() || deferredResult.isSetOrExpired());

      final ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
      final ArgumentCaptor<Instant> startTimeCaptor = ArgumentCaptor.forClass(Instant.class);
      verify(this.taskScheduler).schedule(taskCaptor.capture(), startTimeCaptor.capture());

      final String errorMessage = UUID.randomUUID().toString();
      doThrow(new BadRequestException(errorMessage)).when(this.inSyncService).autoStartGame(anyString());
      taskCaptor.getValue().run();
      verify(this.inSyncService).autoStartGame(gameCode);
      assertTrue(deferredResult.hasResult());
      final Object result = deferredResult.getResult();
      if (result instanceof BadRequestException) {
        assertEquals(errorMessage, ((BadRequestException) result).getMessage());
      } else {
        fail("result should be an instance of BadRequestException.");
      }

      assertEquals(this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME), startTimeCaptor.getValue());
    }
  }

  @Test
  void startGame_shouldStartGame() throws BadRequestException, ServiceUnavailableException {
    try (MockedStatic<InputValidation> mockInputValidation = mockStatic(InputValidation.class)) {
      mockInputValidation.when(() -> InputValidation.validateGameCode(anyString()))
          .thenReturn(Optional.empty());

      final String gameCode = UUID.randomUUID().toString();
      final Game game = mock();
      this.inSyncService.games.put(gameCode, game);

      final DeferredResult<ResponseEntity<Void>> deferredResult = this.inSyncService.startGame(gameCode);
      verify(game).initialize();
      assertFalse(deferredResult.hasResult() || deferredResult.isSetOrExpired());

      final ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
      final ArgumentCaptor<Instant> startTimeCaptor = ArgumentCaptor.forClass(Instant.class);
      verify(this.taskScheduler).schedule(taskCaptor.capture(), startTimeCaptor.capture());

      doNothing().when(this.inSyncService).autoStartGame(anyString());
      taskCaptor.getValue().run();
      verify(this.inSyncService).autoStartGame(gameCode);
      assertTrue(deferredResult.hasResult());
      final Object result = deferredResult.getResult();
      if (result instanceof ResponseEntity<?>) {
        final ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
      } else {
        fail("result should be an instance of ResponseEntity.");
      }

      assertEquals(this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME), startTimeCaptor.getValue());
    }
  }

}
