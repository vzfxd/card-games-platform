package games.card.backend.configuration;

import games.card.backend.service.WebSocketService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;


public class ChatWebSocketHandlerDecorator extends WebSocketHandlerDecorator {
    public ChatWebSocketHandlerDecorator(WebSocketHandler delegate, WebSocketService webSocketService) {
        super(delegate);
    }
}
