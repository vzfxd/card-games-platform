package games.card.backend.service;

import games.card.backend.dto.CreateRoomRequest;
import games.card.backend.model.RoomEntity;
import games.card.backend.model.RoomInfo;
import games.card.backend.model.UserEntity;
import games.card.backend.repository.RoomRepository;
import games.card.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

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

    public void addPlayerToRoom(RoomEntity room, String username){
        room.addPlayer(userRepository.findByUsername(username).orElseThrow());
        roomRepository.save(room);
    }
}
