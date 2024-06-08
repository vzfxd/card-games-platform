package games.card.backend.configuration;

import games.card.backend.model.RoomEntity;
import games.card.backend.repository.RoomRepository;
import games.card.backend.service.JwtService;
import games.card.backend.service.RoomService;
import games.card.backend.service.WebSocketService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    private final WebSocketService webSocketService;
    private final RoomRepository roomRepository;
    private final RoomService roomService;
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
        registry.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            @NonNull
            public WebSocketHandler decorate(@NonNull  WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
                        URI uri = session.getUri();
                        String payload = (String) message.getPayload();
                        if(!webSocketService.isAllowedToConnect(uri,payload) || !webSocketService.isAllowedToSend(uri,payload)){
                            return;
                        }
                        super.handleMessage(session,message);
                    }

                    @Override
                    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
                        URI uri = session.getUri();
                        Optional<RoomEntity> connectingRoom = roomRepository.findById(webSocketService.getEndpointId(uri));
                        String jwt = jwtService.getJwtFromUri(uri);

                        roomService.addPlayerToRoom(connectingRoom.get(),jwtService.getUsername(jwt));
                        System.out.println("DODAJEMY");
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
                        System.out.println(session);
                        super.afterConnectionClosed(session,status);
                    }
                };
            }
        });
    }
}