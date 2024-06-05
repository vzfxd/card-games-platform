package games.card.backend.controller;

import games.card.backend.dto.ChatMessage;
import games.card.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    private final RoomService roomService;

    @MessageMapping("/game")
    @SendTo("/subscribe/game")
    public String roomJoin(String username){
        return username + "game info";
    }

    @MessageMapping("/chat")
    @SendTo("/subscribe/chat")
    public ChatMessage chatJoin(ChatMessage chatMessage){
        return chatMessage;
    }
}

