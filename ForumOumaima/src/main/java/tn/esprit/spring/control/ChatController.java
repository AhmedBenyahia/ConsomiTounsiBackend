package tn.esprit.spring.control;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import tn.esprit.spring.entity.ChatMessage;
import tn.esprit.spring.service.impl.MessageService;

//The chat message-handling Controller
@Controller
@Slf4j
@AllArgsConstructor
public class ChatController {

  private final MessageService  messageService;

 // mapped to handle chat messages to the /send/message destination
 @MessageMapping("/send/message")
 // the return value is broadcast to all subscribers of /message
 @SendTo("/message")
 @CrossOrigin(origins = "http://localhost:4200")
 public ChatMessage chat(ChatMessage message) throws Exception {
     Thread.sleep(1000); // simulated delay
     return new ChatMessage(message.getText(), message.getUser(), message.getDestination());
 }

  @MessageMapping("/hello")
  @SendToUser("/topic/greetings")
  @CrossOrigin(origins = "http://localhost:4200")
  public ChatMessage greeting(String message, @Header("simpSessionId") String sessionId, Principal principal) throws Exception {
    log.info("Received greeting message {} from {} with sessionId {}", message, principal.getName(), sessionId);
    messageService.addUserName(principal.getName());
    messageService.sendMessages(principal.getName());
    Thread.sleep(1000); // simulated delay
    return new ChatMessage("Hello !");
  }

}