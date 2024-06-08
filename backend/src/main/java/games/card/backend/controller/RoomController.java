package games.card.backend.controller;


import games.card.backend.dto.CreateRoomRequest;
import games.card.backend.dto.Response;
import games.card.backend.model.RoomInfo;
import games.card.backend.repository.RoomRepository;
import games.card.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    @GetMapping("/list")
    public ResponseEntity<List<RoomInfo>> roomList(){
        return ResponseEntity.ok(RoomInfo.convert(roomRepository.findAll()));
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<RoomInfo> roomInfo(@PathVariable Long id){
        return ResponseEntity.ok(roomService.getInfo(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> roomCreate(JwtAuthenticationToken jwt, @RequestBody CreateRoomRequest request){
        return ResponseEntity.ok(roomService.create(jwt.getToken(),request));
    }
}
