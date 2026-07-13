package games.boardless.in_sync_server.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import games.boardless.in_sync_server.config.WebMvcConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MetadataController.class)
@Import(WebMvcConfig.class)
class MetadataControllerTests {
  @Autowired private MockMvc mockMvc;

  @Test
  void health_shouldReturnOk() throws Exception {
    final MvcResult result =
        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/health"))
            .andExpectAll(
                MockMvcResultMatchers.status().isOk(), MockMvcResultMatchers.content().string(""))
            .andReturn();

    // Redundant but keeping for demonstration purposes.
    assertTrue(result.getResponse().getContentAsString().isEmpty());
  }
}
