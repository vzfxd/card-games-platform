package games.card.backend.configuration;

import games.card.backend.repository.RoomRepository;
import games.card.backend.service.JwtService;
import games.card.backend.service.RoomService;
import games.card.backend.service.WebSocketService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    private final WebSocketService webSocketService;
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final JwtService jwtService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/subscribe");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket/game/{id}").setAllowedOriginPatterns("*");
        registry.addEndpoint("/websocket/chat/{id}").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry){
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {

            final Map<String, WebSocketHandlerDecorator> endpointDecorators = new HashMap<>();

            {
                endpointDecorators.put("game", new GameWebSocketHandlerDecorator(handler,webSocketService,roomService, jwtService, roomRepository));
                endpointDecorators.put("chat", new ChatWebSocketHandlerDecorator(handler, webSocketService));
            }

            private WebSocketHandlerDecorator getDecorator(WebSocketSession session){
                URI uri = session.getUri();
                String endpoint = webSocketService.getEndpointAfter(uri,"/websocket/");
                return endpointDecorators.get(endpoint);
            }

            @Override
            public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
                WebSocketHandlerDecorator decorator = getDecorator(session);
                decorator.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
                WebSocketHandlerDecorator decorator = getDecorator(session);
                decorator.afterConnectionClosed(session,status);
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                if(Objects.equals(webSocketService.readPayload((String) message.getPayload(),1), "CONNECT")){
                    super.handleMessage(session,message);
                    return;
                }

                URI uri =  session.getUri();
                if(!webSocketService.isAllowedToMessage(uri,(String) message.getPayload())){
                    return;
                }

                super.handleMessage(session,message);
            }
        });
    }
}