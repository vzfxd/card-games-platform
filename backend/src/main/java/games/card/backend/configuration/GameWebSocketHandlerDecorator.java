package games.card.backend.configuration;

import games.card.backend.model.RoomEntity;
import games.card.backend.repository.RoomRepository;
import games.card.backend.service.JwtService;
import games.card.backend.service.RoomService;
import games.card.backend.service.WebSocketService;
import lombok.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.net.URI;
import java.util.Optional;

public class GameWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    private final WebSocketService webSocketService;
    private final RoomService roomService;
    private final JwtService jwtService;
    private final RoomRepository roomRepository;

    public GameWebSocketHandlerDecorator(WebSocketHandler delegate, WebSocketService webSocketService, RoomService roomService, JwtService jwtService, RoomRepository roomRepository) {
        super(delegate);
        this.webSocketService = webSocketService;
        this.roomService = roomService;
        this.jwtService = jwtService;
        this.roomRepository = roomRepository;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if(!webSocketService.isAllowedToConnect(uri)){
            return;
        }
        Optional<RoomEntity> connectingRoom = roomRepository.findById(webSocketService.getEndpointId(uri));
        String jwt = jwtService.getJwtFromUri(uri);
        roomService.addPlayerToRoom(connectingRoom.get(),jwtService.getUsername(jwt));
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        URI uri = session.getUri();
        Optional<RoomEntity> connectingRoom = roomRepository.findById(webSocketService.getEndpointId(uri));
        roomService.removePlayerFromRoom(connectingRoom.get(),jwtService.getUsername(uri));
        super.afterConnectionClosed(session,status);
    }
}
