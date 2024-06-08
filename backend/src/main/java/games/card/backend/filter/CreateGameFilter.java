package games.card.backend.filter;

import games.card.backend.model.RoomEntity;
import games.card.backend.repository.RoomRepository;
import games.card.backend.service.JwtService;
import games.card.backend.service.RoomService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreateGameFilter extends OncePerRequestFilter {

    private final RoomService roomService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("create filter 1");
        System.out.println(request.getRequestURI());
        if(request.getRequestURI().equals("/api/room/create")){
            System.out.println("create filter 2");
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                System.out.println("create filter 3");
                String jwt = token.substring(7);
                Optional<RoomEntity> playerRoom = roomService.findPlayerRoom(jwt);
                System.out.println(playerRoom.toString());
                if(playerRoom.isPresent()){
                    System.out.println("create filter 4");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("Player already in game");
                    return;
                }
            }
        }


        filterChain.doFilter(request, response);
    }
}
