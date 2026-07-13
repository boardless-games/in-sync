package games.boardless.in_sync_server.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import games.boardless.in_sync_server.config.SchedulerConfig;
import games.boardless.in_sync_server.config.WebMvcConfig;
import games.boardless.in_sync_server.dtos.GameCodeDto;
import games.boardless.in_sync_server.dtos.PlayerNameDto;
import games.boardless.in_sync_server.exceptions.BadRequestException;
import games.boardless.in_sync_server.exceptions.ServiceUnavailableException;
import games.boardless.in_sync_server.services.InSyncService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(InSyncController.class)
@Import({WebMvcConfig.class, SchedulerConfig.class})
class InSyncControllerTests {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private InSyncService inSyncService;

  @Test
  void newGame_shouldReturnCreated() throws Exception {
    final GameCodeDto dto = new GameCodeDto("123456");
    when(this.inSyncService.newGame())
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(dto));

    this.mockMvc
        .perform(MockMvcRequestBuilders.post("/in-sync/game"))
        .andExpectAll(
            MockMvcResultMatchers.status().isCreated(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.gameCode").value(dto.gameCode()));
  }

  @Test
  void newGame_withBadRequestException_shouldReturnBadRequest() throws Exception {
    final String message = "Test message.";
    when(this.inSyncService.newGame()).thenThrow(new BadRequestException(message));

    this.mockMvc
        .perform(MockMvcRequestBuilders.post("/in-sync/game"))
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").value(message));
  }

  @Test
  void newGame_withServiceUnavailableException_shouldReturnServiceUnavailable() throws Exception {
    final String message = UUID.randomUUID().toString();
    when(this.inSyncService.newGame()).thenThrow(new ServiceUnavailableException(message));

    this.mockMvc
        .perform(MockMvcRequestBuilders.post("/in-sync/game"))
        .andExpectAll(
            MockMvcResultMatchers.status().isServiceUnavailable(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").value(message));
  }

  @Test
  void newGame_withRuntimeException_shouldReturnInternalServerError() throws Exception {
    when(this.inSyncService.newGame()).thenThrow(RuntimeException.class);

    this.mockMvc
        .perform(MockMvcRequestBuilders.post("/in-sync/game"))
        .andExpectAll(
            MockMvcResultMatchers.status().isInternalServerError(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").isString());
  }

  @Test
  void newPlayer_shouldReturnCreated() throws Exception {
    when(this.inSyncService.newPlayer(anyString(), any(PlayerNameDto.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/in-sync/game/123456/player")
                .content(this.objectMapper.writeValueAsString(new PlayerNameDto("Craiglington")))
                .contentType("application/json"))
        .andExpectAll(
            MockMvcResultMatchers.status().isCreated(), MockMvcResultMatchers.content().string(""));
  }

  @Test
  void newPlayer_withNoBody_shouldReturnBadRequest() throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.post("/in-sync/game/123456/player"))
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").isString());
  }

  @Test
  void newPlayer_withInvalidBody_shouldReturnBadRequest() throws Exception {
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/in-sync/game/123456/player")
                .content("Craiglington")
                .contentType("application/json"))
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").isString());
  }

  @Test
  void newPlayer_withBadRequestException_shouldReturnBadRequest() throws Exception {
    final String message = UUID.randomUUID().toString();
    when(this.inSyncService.newPlayer(anyString(), any(PlayerNameDto.class)))
        .thenThrow(new BadRequestException(message));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/in-sync/game/123456/player")
                .content(this.objectMapper.writeValueAsString(new PlayerNameDto("Craiglington")))
                .contentType("application/json"))
        .andExpectAll(
            MockMvcResultMatchers.status().isBadRequest(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").value(message));
  }

  @Test
  void newPlayer_withServiceUnavailableException_shouldReturnServiceUnavailable() throws Exception {
    final String message = UUID.randomUUID().toString();
    when(this.inSyncService.newPlayer(anyString(), any(PlayerNameDto.class)))
        .thenThrow(new ServiceUnavailableException(message));

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/in-sync/game/123456/player")
                .content(this.objectMapper.writeValueAsString(new PlayerNameDto("Craiglington")))
                .contentType("application/json"))
        .andExpectAll(
            MockMvcResultMatchers.status().isServiceUnavailable(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").value(message));
  }

  @Test
  void newPlayer_withRuntimeException_shouldReturnInternalServerError() throws Exception {
    when(this.inSyncService.newPlayer(anyString(), any(PlayerNameDto.class)))
        .thenThrow(RuntimeException.class);

    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post("/in-sync/game/123456/player")
                .content(this.objectMapper.writeValueAsString(new PlayerNameDto("Craiglington")))
                .contentType("application/json"))
        .andExpectAll(
            MockMvcResultMatchers.status().isInternalServerError(),
            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
            MockMvcResultMatchers.jsonPath("$.error").isString());
  }

  // @Test
  // void startGame_shouldReturnOk() throws Exception {
  // final DeferredResult<ResponseEntity<Void>> deferredResult = new
  // DeferredResult<>(10_000L);

  // when(this.inSyncService.startGame(anyString())).thenReturn(deferredResult);

  // final MvcResult result =
  // this.mockMvc
  // .perform(MockMvcRequestBuilders.post("/in-sync/game/123456/start"))
  // .andExpect(MockMvcResultMatchers.request().asyncStarted())
  // .andReturn();

  // deferredResult.setResult(ResponseEntity.ok().build());

  // this.mockMvc
  // .perform(MockMvcRequestBuilders.asyncDispatch(result))
  // .andExpectAll(
  // MockMvcResultMatchers.status().isOk(),
  // MockMvcResultMatchers.content().string(""));
  // }

  // @Test
  // void
  // startGame_withServiceUnavailableException_shouldReturnServiceUnavailable()
  // throws Exception {
  // final String message = UUID.randomUUID().toString();
  // final DeferredResult<ResponseEntity<Void>> deferredResult = new
  // DeferredResult<>();

  // when(this.inSyncService.startGame(anyString())).thenReturn(deferredResult);

  // final MvcResult result =
  // this.mockMvc
  // .perform(MockMvcRequestBuilders.post("/in-sync/game/123456/start"))
  // .andExpect(MockMvcResultMatchers.request().asyncStarted())
  // .andReturn();

  // deferredResult.setErrorResult(new ServiceUnavailableException(message));

  // this.mockMvc
  // .perform(MockMvcRequestBuilders.asyncDispatch(result))
  // .andExpectAll(
  // MockMvcResultMatchers.status().isServiceUnavailable(),
  // MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
  // MockMvcResultMatchers.jsonPath("$.error").value(message));
  // }

  // @Test
  // void startGame_withBadRequestException_shouldReturnBadRequest() throws
  // Exception {
  // final String message = UUID.randomUUID().toString();
  // final DeferredResult<ResponseEntity<Void>> deferredResult = new
  // DeferredResult<>();

  // when(this.inSyncService.startGame(anyString())).thenReturn(deferredResult);

  // final MvcResult result =
  // this.mockMvc
  // .perform(MockMvcRequestBuilders.post("/in-sync/game/123456/start"))
  // .andExpect(MockMvcResultMatchers.request().asyncStarted())
  // .andReturn();

  // deferredResult.setErrorResult(new BadRequestException(message));

  // this.mockMvc
  // .perform(MockMvcRequestBuilders.asyncDispatch(result))
  // .andExpectAll(
  // MockMvcResultMatchers.status().isBadRequest(),
  // MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
  // MockMvcResultMatchers.jsonPath("$.error").value(message));
  // }

  // @Test
  // void startGame_withRuntimeException_shouldReturnInternalServerError() throws
  // Exception {
  // final DeferredResult<ResponseEntity<Void>> deferredResult = new
  // DeferredResult<>();

  // when(this.inSyncService.startGame(anyString())).thenReturn(deferredResult);

  // final MvcResult result =
  // this.mockMvc
  // .perform(MockMvcRequestBuilders.post("/in-sync/game/123456/start"))
  // .andExpect(MockMvcResultMatchers.request().asyncStarted())
  // .andReturn();

  // deferredResult.setErrorResult(new RuntimeException());

  // this.mockMvc
  // .perform(MockMvcRequestBuilders.asyncDispatch(result))
  // .andExpectAll(
  // MockMvcResultMatchers.status().isInternalServerError(),
  // MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
  // MockMvcResultMatchers.jsonPath("$.error").isString());
  // }
}
