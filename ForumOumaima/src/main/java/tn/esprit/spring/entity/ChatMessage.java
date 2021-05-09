package tn.esprit.spring.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChatMessage {
    private String text;
    private User user;
    private Long destination;
    private LocalDateTime date;

    public ChatMessage(String s) {
        this.text = s;
    }
}