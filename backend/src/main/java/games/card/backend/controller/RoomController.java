package games.card.backend.controller;


import games.card.backend.dto.CreateRoomRequest;
import games.card.backend.dto.Response;
import games.card.backend.model.RoomInfo;
import games.card.backend.repository.RoomRepository;
import games.card.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;
    private final RoomRepository roomRepository;

    @GetMapping("/list")
    public Response<List<RoomInfo>> roomList(){
        Response<List<RoomInfo>> response = new Response<>();
        response.setStatus(HttpStatus.OK);
        response.setInfo(RoomInfo.convert(roomRepository.findAll()));
        return response;
    }

    @GetMapping("/info/{id}")
    public Response<RoomInfo> roomInfo(@PathVariable Long id){
        Response<RoomInfo> response = new Response<>();
        response.setStatus(HttpStatus.OK);
        return roomService.getInfo(id, response);
    }

    @PostMapping("/create")
    public Response<String> roomCreate(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateRoomRequest request){
        Response<String> response = new Response<>();
        response.setStatus(HttpStatus.OK);
        return roomService.create(jwt, request, response);
    }


}
