package games.card.backend.service;

import games.card.backend.model.RoomEntity;
import games.card.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final JwtService jwtService;
    private final RoomService roomService;
    private final RoomRepository roomRepository;

    private String getEndpointAfter(URI uri, String after){
        String path = uri.getPath();
        int pos = path.indexOf(after);
        String afterWebSocket = path.substring(pos + after.length());
        return afterWebSocket.split("/")[0];
    }

    public Long getEndpointId(URI uri){
        String path = uri.getPath();
        String[] split = path.split("/");
        return Long.parseLong(split[split.length-1]);
    }

    private String readPayload(String payload, int line) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(payload));
        String read = "";
        for(int i=0; i<line; i++){
            read = reader.readLine();
        }
        return read;
    }

    public boolean isAllowedToSend(URI uri, String payload) throws IOException, URISyntaxException {
        if(!Objects.equals(readPayload(payload,1), "SEND")){
            return true;
        }
        String dest = readPayload(payload,2);
        Long sendId = getEndpointId(new URI(dest));
        Long connectedId = getEndpointId(uri);
        return sendId.equals(connectedId);
    }

    public boolean isAllowedToConnect(URI uri, String payload) throws IOException {
        if(!Objects.equals(readPayload(payload,1), "CONNECT")){
            return true;
        }

        String webSocketType = getEndpointAfter(uri,"/websocket/");
        String jwt = jwtService.getJwtFromUri(uri);
        Optional<RoomEntity> playerRoom = roomService.findPlayerRoom(jwt);
        Optional<RoomEntity> connectingRoom = roomRepository.findById(getEndpointId(uri));

        if(webSocketType.equals("game")){
            return playerRoom.isEmpty() && connectingRoom.isPresent();
        }

        if(webSocketType.equals("chat") && playerRoom.isPresent()){
            Long id = getEndpointId(uri);
            return id.equals(playerRoom.get().getId());
        }

        return false;
    }
}
