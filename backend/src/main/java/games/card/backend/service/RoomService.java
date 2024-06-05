package games.card.backend.service;

import games.card.backend.dto.CreateRoomRequest;
import games.card.backend.dto.Response;
import games.card.backend.model.AuthorityEntity;
import games.card.backend.model.RoomEntity;
import games.card.backend.model.RoomInfo;
import games.card.backend.model.UserEntity;
import games.card.backend.repository.AuthorityRepository;
import games.card.backend.repository.RoomRepository;
import games.card.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final AuthorityRepository authorityRepository;

    public boolean join(Long id) {
        RoomEntity roomEntity = roomRepository.findById(id).orElse(null);

        if(roomEntity == null){
            return false;
        }

        if(roomEntity.getPlayers().size() >= roomEntity.getPlayerLimit()){
            return false;
        }

        roomRepository.save(roomEntity);

        return true;
    }

    public Response<String> create(Jwt jwt, CreateRoomRequest request, Response<String> response) {
        String username = jwt.getSubject();
        UserEntity user = userRepository.findByUsername(username).orElseThrow();

        RoomEntity roomEntity = new RoomEntity();
        List<UserEntity> players = new ArrayList<>();
        players.add(user);

        roomEntity.setOwner(user);
        roomEntity.setGameType(request.getGameType());
        roomEntity.setPlayers(players);
        roomEntity.setPlayerLimit(request.getPlayerLimit());
        roomEntity.setActive(true);
        roomRepository.save(roomEntity);

        Long roomId = roomEntity.getId();
        AuthorityEntity authority = new AuthorityEntity();
        authority.setName("WEBSOCKET_"+roomId.toString());
        authorityRepository.save(authority);

        user.addAuthority(authority);
        userRepository.save(user);

        response.setInfo(roomEntity.getId().toString());
        return response;
    }

    public Response<RoomInfo> getInfo(Long id, Response<RoomInfo> response){
        RoomEntity roomEntity = roomRepository.findById(id).orElse(null);

        if(roomEntity == null){
            response.setInfo(null);
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
            return response;
        }

        List<RoomEntity> l = new ArrayList<>();
        l.add(roomEntity);
        response.setInfo(RoomInfo.convert(l).get(0));
        return response;
    }
}
