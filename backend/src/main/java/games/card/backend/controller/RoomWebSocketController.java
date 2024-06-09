package games.card.backend.controller;

import games.card.backend.dto.ChatMessage;
import games.card.backend.dto.MoveFactory;
import games.card.backend.dto.MoveRequest;
import games.card.backend.games.Game;
import games.card.backend.games.GameMove;
import games.card.backend.games.Player;
import games.card.backend.model.RoomEntity;
import games.card.backend.repository.RoomRepository;
import games.card.backend.service.GameService;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    private final RoomRepository roomRepository;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/start/{id}")
    public void startGame(JwtAuthenticationToken jwt,@DestinationVariable Long id){
        String username = jwt.getName();
        RoomEntity room = roomRepository.findById(id).orElseThrow();
        if(room.getOwner().getUsername().equals(username)){
            room.setActive(false);
            gameService.startGame(room.getId());
            Game game = gameService.findGameById(room.getId());
            List<Player> playerList = game.getPlayers();
            playerList.forEach(player -> sendResponseGamePlayer(game,player));
        }
    }

    @MessageMapping("/game/move/{id}")
    public void makeMove(JwtAuthenticationToken jwt, @DestinationVariable Long id, MoveRequest gameMove){
        String username = jwt.getName();
        Game game = gameService.findGameById(id);
        Player player = game.getPlayer(username);
        GameMove move = MoveFactory.createGameMove(gameMove.getGameMove());

        if(game.getTurn().equals(username)){
            move.perform(game,player);
            game.nextTurn();
            if(game.isFinished()){
                System.out.println("koniec gry");
                game.setWinner(game.findWinner());
                //Todo usuwanie gry
            }
            List<Player> playerList = game.getPlayers();
            playerList.forEach(p -> sendResponseGamePlayer(game,p));
        }
    }

    @MessageMapping("/chat/{id}")
    @SendTo("/subscribe/chat/{id}")
    public ChatMessage chatController(JwtAuthenticationToken jwt, ChatMessage chatMessage){
        return new ChatMessage(jwt.getName(), chatMessage.getMsg());
    }

    private void sendResponseGamePlayer(Game game, Player p){
        messagingTemplate.convertAndSend("/subscribe/game/" + p.getUsername(), game.getState(p));
    }
}

