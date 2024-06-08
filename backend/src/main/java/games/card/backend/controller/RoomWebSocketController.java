package games.card.backend.controller;

import games.card.backend.dto.ChatMessage;
import games.card.backend.service.JwtService;
import games.card.backend.service.RoomService;
import games.card.backend.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    @MessageMapping("/game/{id}")
    @SendTo("/subscribe/game/{id}")
    public String roomController(String username){
        return username + "game info";
    }

    @MessageMapping("/chat/{id}")
    @SendTo("/subscribe/chat/{id}")
    public ChatMessage chatController(JwtAuthenticationToken jwt, ChatMessage chatMessage){
        return new ChatMessage(jwt.getName(), chatMessage.getMsg());
    }
}

