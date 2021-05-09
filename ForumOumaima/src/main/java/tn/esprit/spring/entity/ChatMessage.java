package tn.esprit.spring.entity;

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

    public ChatMessage(String s) {
        this.text = s;
    }
}