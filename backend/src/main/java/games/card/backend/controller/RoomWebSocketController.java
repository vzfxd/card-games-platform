package games.card.backend.controller;

import games.card.backend.dto.ChatMessage;
import games.card.backend.service.RoomService;
import games.card.backend.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    private final WebSocketService webSocketService;

    @MessageMapping("/game{id}")
    @SendTo("/subscribe/game/{id}")
    public String roomJoin(ChatMessage username){
        return username + "game info";
    }

    @MessageMapping("/chat/{id}")
    @SendTo("/subscribe/chat/{id}")
    public ChatMessage chatJoin(ChatMessage chatMessage){
        return chatMessage;
    }
}

