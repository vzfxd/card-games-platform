package games.card.backend.service;

import games.card.backend.dto.CreateRoomRequest;
import games.card.backend.games.Game;
import games.card.backend.games.Player;
import games.card.backend.model.RoomEntity;
import games.card.backend.model.RoomInfo;
import games.card.backend.model.UserEntity;
import games.card.backend.repository.RoomRepository;
import games.card.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final GameService gameService;

    public Long create(Jwt jwt, CreateRoomRequest request) {
        String username = jwt.getSubject();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();

        RoomEntity roomEntity = new RoomEntity();
        List<UserEntity> players = new ArrayList<>();

        roomEntity.setOwner(user);
        roomEntity.setGameType(request.getGameType());
        roomEntity.setPlayers(players);
        roomEntity.setPlayerLimit(request.getPlayerLimit());
        roomEntity.setActive(true);
        roomRepository.save(roomEntity);

        gameService.createGame(roomEntity.getId(),username, request.getGameType());

        return roomEntity.getId();
    }

    public Optional<RoomEntity> findPlayerRoom(String jwt){
        String username = jwtService.getUsername(jwt);
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        return roomRepository.findByPlayersContains(user);
    }

    public RoomInfo getInfo(Long id){
        RoomEntity roomEntity = roomRepository.findById(id).orElse(null);
        List<RoomEntity> l = new ArrayList<>();
        l.add(roomEntity);
        return RoomInfo.convert(l).get(0);
    }

    public void addPlayerToRoom(RoomEntity room, String username, WebSocketSession webSocketSession){
        room.addPlayer(userRepository.findByUsername(username).orElseThrow());
        gameService.addPlayerToGame(room.getId(),new Player(username));
        roomRepository.save(room);
    }

    public void removePlayerFromRoom(RoomEntity room, String username){
        UserEntity user = userRepository.findByUsername(username).orElseThrow();
        room.removePlayer(user.getId());
        gameService.removePlayerFromGame(room.getId(),username);
        roomRepository.save(room);
        deleteRoomIfNeeded(room);
    }

    public void deleteRoomIfNeeded(RoomEntity room){
        if(room.isActive() && room.getPlayers().size() == 0){
            roomRepository.delete(room);
            gameService.deleteGame(room.getId());
        }
    }

    public void deleteAllEmpty(){
        List<RoomEntity> list = roomRepository.findAllByPlayersEmpty().orElseThrow();
        roomRepository.deleteAll(list);
        list.forEach(r -> gameService.deleteGame(r.getId()));
    }
}
