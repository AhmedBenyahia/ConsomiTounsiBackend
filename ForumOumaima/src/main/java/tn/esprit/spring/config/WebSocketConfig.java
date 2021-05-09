package tn.esprit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import tn.esprit.spring.handler.CustomHandshakeHandler;

@Configuration
//Enables WebSocket message handling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

 @Override
 public void configureMessageBroker(MessageBrokerRegistry config) {
     /*
      * enable a simple memory-based message broker to carry the chat messages
      * back to the client on destinations prefixed with /message
      * */
     config.enableSimpleBroker("/message");
//     config.setPathMatcher(new AntPathMatcher("."));
     /*
     * designates the /app prefix for messages that are bound for
     * methods annotated with @MessageMapping
     * */
     config.setApplicationDestinationPrefixes("/app");
 }

 @Override
 public void registerStompEndpoints(StompEndpointRegistry registry) {
     /*
     * Register /socket endpoint,
     * setAllowedOriginPatterns is used to prevent CORS issue when using the
     * Angular application as a client
     *
     * */
//     registry.addEndpoint("/socket")
//         .setAllowedOriginPatterns("http://localhost:4200")
//         .setAllowedOrigins("http://localhost:4200");
//         .setHandshakeHandler(new CustomHandshakeHandler());

     registry.addEndpoint("/socket")
         .setAllowedOriginPatterns("http://localhost:4200")
         .setAllowedOrigins("http://localhost:4200")
//         .setHandshakeHandler(new CustomHandshakeHandler())
         .withSockJS();
 }
}