package games.boardless.in_sync.services;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

@ExtendWith(MockitoExtension.class)
class InSyncServiceTests {
  @Mock private TaskScheduler taskScheduler;

  @Spy private Clock clock = Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneOffset.UTC);

  @Spy @InjectMocks private InSyncService inSyncService;

  @BeforeEach
  void beforeEach() {
    this.inSyncService.games.clear();
  }

  // @Test
  // void newGame_withMaxedOutGames_shouldThrowServiceUnavailableException() {
  // for (int i = 0; i < MAX_NUM_GAMES; ++i) {
  // final String gameCode = UUID.randomUUID().toString();
  // this.inSyncService.games.put(gameCode, mock(Game.class));
  // }
  // assertThrows(
  // ServiceUnavailableException.class,
  // () -> {
  // this.inSyncService.newGame();
  // });
  // }

  // @Test
  // void newGame_shouldCreateNewGame() throws BadRequestException,
  // ServiceUnavailableException {
  // final ResponseEntity<GameCodeDto> result = this.inSyncService.newGame();

  // assertEquals(HttpStatus.CREATED, result.getStatusCode());
  // final GameCodeDto body = result.getBody();
  // assertNotNull(body);
  // assertNotNull(this.inSyncService.games.get(body.gameCode()));

  // final ArgumentCaptor<Runnable> taskCaptor =
  // ArgumentCaptor.forClass(Runnable.class);
  // final ArgumentCaptor<Instant> startTimeCaptor =
  // ArgumentCaptor.forClass(Instant.class);
  // verify(this.taskScheduler).schedule(taskCaptor.capture(),
  // startTimeCaptor.capture());

  // doNothing().when(this.inSyncService).autoDeleteGame(anyString());
  // taskCaptor.getValue().run();
  // verify(this.inSyncService).autoDeleteGame(anyString());

  // assertEquals(
  // this.clock.instant().plusMillis(GAME_AUTO_DELETE_TIME),
  // startTimeCaptor.getValue());
  // }

  // @Test
  // void newPlayer_withInvalidGameCode_shouldThrowBadRequestException() {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String validationResult = UUID.randomUUID().toString();
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.of(validationResult));

  // final BadRequestException e =
  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.newPlayer("", new NameDto(""));
  // });

  // assertEquals(validationResult, e.getMessage());
  // inputValidationStaticMock.verify(() ->
  // InputValidation.validateGameCode(anyString()));
  // }
  // }

  // @Test
  // void newPlayer_withNullName_shouldThrowBadRequestException() {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // final String gameCode = UUID.randomUUID().toString();

  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.newPlayer(gameCode, null);
  // });

  // inputValidationStaticMock.verify(() ->
  // InputValidation.validateGameCode(gameCode));
  // }
  // }

  // @Test
  // void newPlayer_withInvalidName_shouldThrowBadRequestException() {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String validationResult = UUID.randomUUID().toString();
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.of(validationResult));

  // final BadRequestException e =
  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.newPlayer("", new NameDto(""));
  // });

  // assertEquals(validationResult, e.getMessage());
  // inputValidationStaticMock.verify(() ->
  // InputValidation.validateGameCode(anyString()));
  // inputValidationStaticMock.verify(() ->
  // InputValidation.validateName(anyString()));
  // }
  // }

  // @Test
  // void newPlayer_withNonExistentGameCode_shouldThrowBadRequestException() {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final String gameCode = UUID.randomUUID().toString();
  // final BadRequestException e =
  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.newPlayer(gameCode, new NameDto(""));
  // });

  // assertTrue(e.getMessage().contains(gameCode));
  // }
  // }

  // @Test
  // void newPlayer_shouldAddPlayer() throws BadRequestException,
  // ServiceUnavailableException {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final Game game = mock();
  // when(game.getGameCode()).thenReturn(gameCode);
  // this.inSyncService.games.put(gameCode, game);

  // final ResponseEntity<Void> result = this.inSyncService.newPlayer(gameCode,
  // new NameDto(name));

  // verify(game).addPlayer(name);
  // assertEquals(HttpStatus.CREATED, result.getStatusCode());
  // assertNull(result.getBody());

  // final ArgumentCaptor<Runnable> taskCaptor =
  // ArgumentCaptor.forClass(Runnable.class);
  // final ArgumentCaptor<Instant> startTimeCaptor =
  // ArgumentCaptor.forClass(Instant.class);
  // verify(this.taskScheduler).schedule(taskCaptor.capture(),
  // startTimeCaptor.capture());

  // doNothing().when(this.inSyncService).autoRemovePlayer(anyString(),
  // anyString());
  // taskCaptor.getValue().run();
  // verify(this.inSyncService).autoRemovePlayer(gameCode, name);

  // assertEquals(
  // this.clock.instant().plusMillis(PLAYER_AUTO_REMOVE_TIME),
  // startTimeCaptor.getValue());
  // }
  // }

  // @Test
  // void startGame_withInvalidGameCode_shouldThrowBadRequestException() {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String validationResult = UUID.randomUUID().toString();
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.of(validationResult));

  // final BadRequestException e =
  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.startGame("");
  // });

  // assertEquals(validationResult, e.getMessage());
  // inputValidationStaticMock.verify(() ->
  // InputValidation.validateGameCode(anyString()));
  // }
  // }

  // @Test
  // void startGame_withNonExistentGameCode_shouldThrowBadRequestException() {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // final String gameCode = UUID.randomUUID().toString();
  // final BadRequestException e =
  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.startGame(gameCode);
  // });

  // assertTrue(e.getMessage().contains(gameCode));
  // }
  // }

  // @Test
  // void startGame_withFailingAutoStart_shouldHaveExceptionResult()
  // throws BadRequestException, ServiceUnavailableException {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // final String gameCode = UUID.randomUUID().toString();
  // final Game game = mock();
  // this.inSyncService.games.put(gameCode, game);

  // final DeferredResult<ResponseEntity<Void>> deferredResult =
  // this.inSyncService.startGame(gameCode);
  // verify(game).initialize();
  // assertFalse(deferredResult.hasResult() || deferredResult.isSetOrExpired());

  // final ArgumentCaptor<Runnable> taskCaptor =
  // ArgumentCaptor.forClass(Runnable.class);
  // final ArgumentCaptor<Instant> startTimeCaptor =
  // ArgumentCaptor.forClass(Instant.class);
  // verify(this.taskScheduler).schedule(taskCaptor.capture(),
  // startTimeCaptor.capture());

  // final String errorMessage = UUID.randomUUID().toString();
  // doThrow(new BadRequestException(errorMessage))
  // .when(this.inSyncService)
  // .autoStartGame(anyString());
  // taskCaptor.getValue().run();
  // verify(this.inSyncService).autoStartGame(gameCode);
  // assertTrue(deferredResult.hasResult());
  // final Object result = deferredResult.getResult();
  // if (result instanceof BadRequestException) {
  // assertEquals(errorMessage, ((BadRequestException) result).getMessage());
  // } else {
  // fail("result should be an instance of BadRequestException.");
  // }

  // assertEquals(
  // this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME),
  // startTimeCaptor.getValue());
  // }
  // }

  // @Test
  // void startGame_shouldStartGame() throws BadRequestException,
  // ServiceUnavailableException {
  // try (MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // final String gameCode = UUID.randomUUID().toString();
  // final Game game = mock();
  // this.inSyncService.games.put(gameCode, game);

  // final DeferredResult<ResponseEntity<Void>> deferredResult =
  // this.inSyncService.startGame(gameCode);
  // verify(game).initialize();
  // assertFalse(deferredResult.hasResult() || deferredResult.isSetOrExpired());

  // final ArgumentCaptor<Runnable> taskCaptor =
  // ArgumentCaptor.forClass(Runnable.class);
  // final ArgumentCaptor<Instant> startTimeCaptor =
  // ArgumentCaptor.forClass(Instant.class);
  // verify(this.taskScheduler).schedule(taskCaptor.capture(),
  // startTimeCaptor.capture());

  // doNothing().when(this.inSyncService).autoStartGame(anyString());
  // taskCaptor.getValue().run();
  // verify(this.inSyncService).autoStartGame(gameCode);
  // assertTrue(deferredResult.hasResult());
  // final Object result = deferredResult.getResult();
  // if (result instanceof ResponseEntity<?>) {
  // final ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
  // assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  // assertNull(responseEntity.getBody());
  // } else {
  // fail("result should be an instance of ResponseEntity.");
  // }

  // assertEquals(
  // this.clock.instant().plusMillis(WAIT_PLAYER_READY_TIME),
  // startTimeCaptor.getValue());
  // }
  // }

  // @Test
  // void connect_withNullSession_shouldNotCloseSession() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(null);
  // });

  // uriComponentsBuilderStaticMock.verifyNoInteractions();
  // }
  // }

  // @Test
  // void connect_withNoGameCode_shouldCloseSession() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockSession).close(CloseStatus.BAD_DATA);
  // }
  // }

  // @Test
  // void connect_withFailureToCloseSession_shouldLogException() throws
  // IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());
  // doThrow(IOException.class).when(mockSession).close(CloseStatus.BAD_DATA);

  // final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  // final Logger logger = (Logger) LoggerFactory.getLogger(InSyncService.class);
  // listAppender.start();
  // logger.addAppender(listAppender);

  // try {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockSession).close(CloseStatus.BAD_DATA);

  // final ILoggingEvent log = listAppender.list.getLast();
  // assertEquals(Level.ERROR, log.getLevel());
  // assertTrue(log.getFormattedMessage().contains("Failed to close"));
  // } finally {
  // logger.detachAppender(listAppender);
  // listAppender.stop();
  // }
  // }
  // }

  // @Test
  // void connect_withUnexpectedException_shouldLogException() {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenThrow(RuntimeException.class);

  // final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  // final Logger logger = (Logger) LoggerFactory.getLogger(InSyncService.class);
  // listAppender.start();
  // logger.addAppender(listAppender);

  // try {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // final ILoggingEvent log = listAppender.list.getLast();
  // assertEquals(Level.ERROR, log.getLevel());
  // assertTrue(log.getFormattedMessage().contains("Unexpected error"));
  // } finally {
  // logger.detachAppender(listAppender);
  // listAppender.stop();
  // }
  // }
  // }

  // @Test
  // void connect_withInvalidGameCode_shouldCloseSession() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.of(""));

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockSession).close(CloseStatus.BAD_DATA);
  // }
  // }

  // @Test
  // void connect_withNonExistentGameCode_shouldCloseSession() throws IOException
  // {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockSession).close(CloseStatus.BAD_DATA);
  // }
  // }

  // @Test
  // void connect_withNoName_shouldCloseSession() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockGame, never()).connect(anyString(), any(WebSocketSession.class));
  // verify(mockSession).close(CloseStatus.BAD_DATA);
  // }
  // }

  // @Test
  // void connect_withInvalidName_shouldCloseSession() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.of(""));

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockGame, never()).connect(anyString(), any(WebSocketSession.class));
  // verify(mockSession).close(CloseStatus.BAD_DATA);
  // }
  // }

  // @Test
  // void connect_withFailingGameConnect_shouldCloseSession() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // doThrow(BadRequestException.class)
  // .when(mockGame)
  // .connect(anyString(), any(WebSocketSession.class));
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockGame).connect(name, mockSession);
  // verify(mockSession).close(CloseStatus.BAD_DATA);
  // }
  // }

  // @Test
  // void connect_shouldConnect() throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockGame).connect(name, mockSession);
  // verify(mockSession, never()).close(any(CloseStatus.class));
  // }
  // }

  // @Test
  // void disconnect_withNullSession_shouldNotCloseSession() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(null);
  // });

  // uriComponentsBuilderStaticMock.verifyNoInteractions();
  // }
  // }

  // @Test
  // void disconnect_withNoGameCode_shouldNotThrow() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });
  // }
  // }

  // @Test
  // void disconnect_withUnexpectedException_shouldLogException() {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenThrow(RuntimeException.class);

  // final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  // final Logger logger = (Logger) LoggerFactory.getLogger(InSyncService.class);
  // listAppender.start();
  // logger.addAppender(listAppender);

  // try {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // final ILoggingEvent log = listAppender.list.getLast();
  // assertEquals(Level.ERROR, log.getLevel());
  // assertTrue(log.getFormattedMessage().contains("Unexpected error"));
  // } finally {
  // logger.detachAppender(listAppender);
  // listAppender.stop();
  // }
  // }
  // }

  // @Test
  // void disconnect_withInvalidGameCode_shouldNotThrow() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.of(""));

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });
  // }
  // }

  // @Test
  // void disconnect_withNonExistentGameCode_shouldNotThrow() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });
  // }
  // }

  // @Test
  // void disconnect_withNoName_shouldNotThrow() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // verify(mockGame, never()).disconnect(anyString());
  // }
  // }

  // @Test
  // void disconnect_withInvalidName_shouldNotThrow() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.of(""));

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.connect(mockSession);
  // });

  // verify(mockGame, never()).disconnect(anyString());
  // }
  // }

  // @Test
  // void disconnect_withFailingGameDisconnect_shouldNotThrow()
  // throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // doThrow(BadRequestException.class).when(mockGame).disconnect(anyString());
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // verify(mockGame).disconnect(name);
  // }
  // }

  // @Test
  // void
  // disconnect_withLobbyStatusAndRemainingPlayers_shouldDisconnectAndRemovePlayer()
  // throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // when(mockGame.getStatus()).thenReturn(GameStatus.LOBBY);
  // when(mockGame.numPlayers()).thenReturn(1);
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // verify(mockGame).disconnect(name);
  // verify(mockGame).removePlayer(name);
  // verify(this.inSyncService, never()).deleteGame(anyString());
  // }
  // }

  // @Test
  // void disconnect_withLobbyStatusAndZeroPlayers_shouldDisconnectAndDeleteGame()
  // throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // when(mockGame.getStatus()).thenReturn(GameStatus.LOBBY);
  // when(mockGame.numPlayers()).thenReturn(0);
  // when(mockGame.getGameCode()).thenReturn(gameCode);
  // this.inSyncService.games.put(gameCode, mockGame);

  // doNothing().when(this.inSyncService).deleteGame(anyString());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // verify(mockGame).disconnect(name);
  // verify(mockGame).removePlayer(name);
  // verify(this.inSyncService).deleteGame(gameCode);
  // }
  // }

  // @Test
  // void
  // disconnect_withNonLobbyStatusAndNoConnectedPlayers_shouldDisconnectAndDeleteGame()
  // throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // when(mockGame.getStatus()).thenReturn(GameStatus.IN_GAME);
  // when(mockGame.hasConnectedPlayers()).thenReturn(false);
  // when(mockGame.getGameCode()).thenReturn(gameCode);
  // this.inSyncService.games.put(gameCode, mockGame);

  // doNothing().when(this.inSyncService).deleteGame(anyString());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // verify(mockGame).disconnect(name);
  // verify(this.inSyncService).deleteGame(gameCode);
  // }
  // }

  // @Test
  // void disconnect_withNonLobbyStatusAndConnectedPlayers_shouldDisconnectOnly()
  // throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // when(mockGame.getStatus()).thenReturn(GameStatus.IN_GAME);
  // when(mockGame.hasConnectedPlayers()).thenReturn(true);
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.disconnect(mockSession);
  // });

  // verify(mockGame).disconnect(name);
  // verify(mockGame, never()).removePlayer(anyString());
  // verify(this.inSyncService, never()).deleteGame(anyString());
  // }
  // }

  // @Test
  // void handlePongMessage_withNullSession_shouldNotCloseSession() throws
  // IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(null);
  // });

  // uriComponentsBuilderStaticMock.verifyNoInteractions();
  // }
  // }

  // @Test
  // void handlePongMessage_withNoGameCode_shouldNotThrow() throws IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });
  // }
  // }

  // @Test
  // void handlePongMessage_withUnexpectedException_shouldLogException() {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenThrow(RuntimeException.class);

  // final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  // final Logger logger = (Logger) LoggerFactory.getLogger(InSyncService.class);
  // listAppender.start();
  // logger.addAppender(listAppender);

  // try {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });

  // final ILoggingEvent log = listAppender.list.getLast();
  // assertEquals(Level.ERROR, log.getLevel());
  // assertTrue(log.getFormattedMessage().contains("Unexpected error"));
  // } finally {
  // logger.detachAppender(listAppender);
  // listAppender.stop();
  // }
  // }
  // }

  // @Test
  // void handlePongMessage_withInvalidGameCode_shouldNotThrow() throws
  // IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.of(""));

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });
  // }
  // }

  // @Test
  // void handlePongMessage_withNonExistentGameCode_shouldNotThrow() throws
  // IOException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });
  // }
  // }

  // @Test
  // void handlePongMessage_withNoName_shouldNotThrow() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });

  // verify(mockGame, never()).setReady(anyString());
  // }
  // }

  // @Test
  // void handlePongMessage_withInvalidName_shouldNotThrow() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", "");
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.of(""));

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });

  // verify(mockGame, never()).setReady(anyString());
  // }
  // }

  // @Test
  // void handlePongMessage_withFailingGameSetReady_shouldNotThrow()
  // throws IOException, BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // doThrow(BadRequestException.class).when(mockGame).setReady(anyString());
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });

  // verify(mockGame).setReady(name);
  // }
  // }

  // @Test
  // void handlePongMessage_shouldSetReady() throws IOException,
  // BadRequestException {
  // try (MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock =
  // mockStatic(UriComponentsBuilder.class);
  // MockedStatic<InputValidation> inputValidationStaticMock =
  // mockStatic(InputValidation.class)) {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final MultiValueMap<String, String> queryParams = new
  // LinkedMultiValueMap<>();
  // queryParams.add("gameCode", gameCode);
  // queryParams.add("name", name);
  // this.provideMockQueryParams(uriComponentsBuilderStaticMock, queryParams);

  // final WebSocketSession mockSession = mock();
  // when(mockSession.getUri()).thenReturn(mock());

  // inputValidationStaticMock
  // .when(() -> InputValidation.validateGameCode(anyString()))
  // .thenReturn(Optional.empty());
  // inputValidationStaticMock
  // .when(() -> InputValidation.validateName(anyString()))
  // .thenReturn(Optional.empty());

  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.handlePongMessage(mockSession);
  // });

  // verify(mockGame).setReady(name);
  // }
  // }

  // @Test
  // void autoDeleteGame_withNullGameCode_shouldNotDeleteGame() {
  // this.inSyncService.autoDeleteGame(null);

  // verify(this.inSyncService, never()).deleteGame(anyString());
  // }

  // @Test
  // void autoDeleteGame_withNonExistentGameCode_shouldNotDeleteGame() {
  // this.inSyncService.autoDeleteGame("");

  // verify(this.inSyncService, never()).deleteGame(anyString());
  // }

  // @Test
  // void autoDeleteGame_withConnectedPlayers_shouldNotDeleteGame() {
  // final String gameCode = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // when(mockGame.hasConnectedPlayers()).thenReturn(true);
  // this.inSyncService.games.put(gameCode, mockGame);

  // this.inSyncService.autoDeleteGame(gameCode);

  // verify(this.inSyncService, never()).deleteGame(anyString());
  // }

  // @Test
  // void autoDeleteGame_shouldDeleteGame() {
  // final String gameCode = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // when(mockGame.hasConnectedPlayers()).thenReturn(false);
  // this.inSyncService.games.put(gameCode, mockGame);

  // doNothing().when(this.inSyncService).deleteGame(anyString());

  // this.inSyncService.autoDeleteGame(gameCode);

  // verify(this.inSyncService).deleteGame(gameCode);
  // }

  // @Test
  // void deleteGame_withNullGameCode_shouldNotThrow() {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.deleteGame(null);
  // });
  // }

  // @Test
  // void deleteGame_withNonExistentGameCode_shouldLogMessage() {
  // final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  // final Logger logger = (Logger) LoggerFactory.getLogger(InSyncService.class);
  // listAppender.start();
  // logger.addAppender(listAppender);

  // try {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.deleteGame("");
  // });

  // final ILoggingEvent log = listAppender.list.getLast();
  // assertEquals(Level.ERROR, log.getLevel());
  // assertTrue(log.getFormattedMessage().contains("Failed to delete game"));
  // } finally {
  // logger.detachAppender(listAppender);
  // listAppender.stop();
  // }
  // }

  // @Test
  // void deleteGame_shouldDeleteGame() {
  // final String gameCode = UUID.randomUUID().toString();
  // this.inSyncService.games.put(gameCode, mock());

  // this.inSyncService.deleteGame(gameCode);

  // assertTrue(this.inSyncService.games.isEmpty());
  // }

  // @Test
  // void autoRemovePlayer_withNullGameCode_shouldNotThrow() {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoRemovePlayer(null, "");
  // });
  // }

  // @Test
  // void autoRemovePlayer_withNullName_shouldNotThrow() {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoRemovePlayer("", null);
  // });
  // }

  // @Test
  // void autoRemovePlayer_withNonExistentGameCode_shouldNotRemovePlayer() {
  // final String gameCode = UUID.randomUUID().toString();
  // assertNull(this.inSyncService.games.get(gameCode));
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoRemovePlayer(gameCode, "");
  // });
  // }

  // @Test
  // void autoRemovePlayer_withConnectedPlayer_shouldNotRemovePlayer() throws
  // BadRequestException {
  // final String gameCode = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // when(mockGame.isConnected(anyString())).thenReturn(true);
  // this.inSyncService.games.put(gameCode, mockGame);

  // this.inSyncService.autoRemovePlayer(gameCode, "");

  // verify(mockGame, never()).removePlayer(anyString());
  // }

  // @Test
  // void autoRemovePlayer_withFailingRemovePlayer_shouldLogExceptionAndNotThrow()
  // throws BadRequestException {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // when(mockGame.isConnected(anyString())).thenReturn(false);
  // doThrow(BadRequestException.class).when(mockGame).removePlayer(anyString());
  // this.inSyncService.games.put(gameCode, mockGame);

  // final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
  // final Logger logger = (Logger) LoggerFactory.getLogger(InSyncService.class);
  // listAppender.start();
  // logger.addAppender(listAppender);

  // try {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoRemovePlayer(gameCode, name);
  // });

  // verify(mockGame).removePlayer(name);

  // final ILoggingEvent log = listAppender.list.getLast();
  // assertEquals(Level.ERROR, log.getLevel());
  // assertTrue(log.getFormattedMessage().contains("Failed to auto remove"));
  // } finally {
  // logger.detachAppender(listAppender);
  // listAppender.stop();
  // }
  // }

  // @Test
  // void autoRemovePlayer_shouldRemovePlayer() throws BadRequestException {
  // final String gameCode = UUID.randomUUID().toString();
  // final String name = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // when(mockGame.isConnected(anyString())).thenReturn(false);
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoRemovePlayer(gameCode, name);
  // });

  // verify(mockGame).removePlayer(name);
  // }

  // @Test
  // void autoStartGame_withNullGameCode_shouldNotThrow() {
  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoStartGame(null);
  // });
  // }

  // @Test
  // void autoStartGame_withNonExistentGameCode_shouldThrowBadRequestException() {
  // assertThrows(
  // BadRequestException.class,
  // () -> {
  // this.inSyncService.autoStartGame("");
  // });
  // }

  // @Test
  // void
  // autoStartGame_withFailingStartGame_shouldThrowServiceUnavailableException()
  // throws ServiceUnavailableException {
  // final String gameCode = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // doThrow(ServiceUnavailableException.class).when(mockGame).start();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertThrows(
  // ServiceUnavailableException.class,
  // () -> {
  // this.inSyncService.autoStartGame(gameCode);
  // });
  // verify(mockGame).start();
  // }

  // @Test
  // void autoStartGame_shouldStartGame() throws ServiceUnavailableException {
  // final String gameCode = UUID.randomUUID().toString();
  // final Game mockGame = mock();
  // this.inSyncService.games.put(gameCode, mockGame);

  // assertDoesNotThrow(
  // () -> {
  // this.inSyncService.autoStartGame(gameCode);
  // });
  // verify(mockGame).start();
  // }

  /** Sets the query params for a mock UriComponentsBuilder. */
  // private void provideMockQueryParams(
  // final MockedStatic<UriComponentsBuilder> uriComponentsBuilderStaticMock,
  // final MultiValueMap<String, String> queryParams) {
  // final UriComponents mockUriComponents = mock();
  // when(mockUriComponents.getQueryParams()).thenReturn(queryParams);
  // final UriComponentsBuilder mockUriComponentsBuilder = mock();
  // when(mockUriComponentsBuilder.build()).thenReturn(mockUriComponents);
  // uriComponentsBuilderStaticMock
  // .when(() -> UriComponentsBuilder.fromPath(anyString()))
  // .thenReturn(mockUriComponentsBuilder);
  // uriComponentsBuilderStaticMock
  // .when(() -> UriComponentsBuilder.fromUri(any(URI.class)))
  // .thenReturn(mockUriComponentsBuilder);
  // uriComponentsBuilderStaticMock
  // .when(() -> UriComponentsBuilder.fromUriString(anyString()))
  // .thenReturn(mockUriComponentsBuilder);
  // uriComponentsBuilderStaticMock
  // .when(() -> UriComponentsBuilder.fromUriString(anyString(),
  // any(ParserType.class)))
  // .thenReturn(mockUriComponentsBuilder);
  // }
}
