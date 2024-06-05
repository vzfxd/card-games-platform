package games.card.backend.service;

import games.card.backend.dto.AuthRequest;
import games.card.backend.dto.Response;
import games.card.backend.model.AuthorityEntity;
import games.card.backend.model.UserEntity;
import games.card.backend.repository.AuthorityRepository;
import games.card.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthorityRepository authorityRepository;
    private final JwtService jwtService;


    public Response<String> registerUser(AuthRequest request, Response<String> response){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            response.setStatus(HttpStatus.NOT_ACCEPTABLE);
            response.setInfo("Username already exists");
            return response;
        }

        UserEntity user = new UserEntity();
        List<AuthorityEntity> authorities = new ArrayList<>();
        authorities.add(authorityRepository.findByName("USER").get());

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAuthorities(authorities);
        userRepository.save(user);

        return response;
    }

    public Response<String> loginUser(AuthRequest request, Response<String> response){
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            response.setInfo(jwtService.generateJwt(auth));
            response.setStatus(HttpStatus.OK);
        }catch(Exception e){
            return response;
        }

        return response;
    }
}
