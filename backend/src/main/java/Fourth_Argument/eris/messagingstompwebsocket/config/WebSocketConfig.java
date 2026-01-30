package Fourth_Argument.eris.messagingstompwebsocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/user");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user/");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
          .withSockJS();
  }

  /* Convertisseur cassé pour l'instant, à réparer plus tard */

  /* @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(new ObjectMapper());
    messageConverters.add(converter);
    
    return false;
  } */
} 