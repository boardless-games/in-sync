package games.boardless.in_sync.config;

import static games.boardless.in_sync.constants.Constants.ALLOWED_ORIGINS;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins(ALLOWED_ORIGINS).allowedMethods("*");
  }
}
