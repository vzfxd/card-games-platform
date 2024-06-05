package games.card.backend.controller;

import games.card.backend.dto.AuthRequest;
import games.card.backend.dto.Response;
import games.card.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authenticationService;

    @PostMapping("/register")
    public Response<String> registerUser(@RequestBody AuthRequest request){
        Response<String> response = new Response<>();
        response.setStatus(HttpStatus.OK);
        return authenticationService.registerUser(request, response);
    }

    @PostMapping("/login")
    public Response<String> loginUser(@RequestBody AuthRequest request){
        Response<String> response = new Response<>();
        response.setStatus(HttpStatus.NOT_ACCEPTABLE);
        response.setInfo("wrong login or password");
        return authenticationService.loginUser(request, response);
    }
}
