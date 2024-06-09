package games.card.backend.service;

import games.card.backend.games.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    List<Game> activeGames = new ArrayList<>();

    public Game findGameById(Long id){
        return activeGames.stream().filter(g -> g.getId().equals(id)).findFirst().orElseThrow();
    }

    public void createGame(Long id, String owner, GameType gameType){
        Game game;
        switch(gameType){
            case TWENTYONE -> game = new TwentyOne(id,owner);
            case MACAO -> game = new Macao(id, owner);
            default -> throw new IllegalArgumentException("game type not found");
        }

        activeGames.add(game);
    }

    public void deleteGame(Long id){
        Game game = findGameById(id);
        activeGames.remove(game);
    }

    public void addPlayerToGame(Long id, Player player){
        Game game = findGameById(id);
        game.addPlayer(player);
    }

    public void removePlayerFromGame(Long id, String username){
        Game game = findGameById(id);
        game.removePlayer(username);
    }

    public void startGame(Long id) {
        Game game = findGameById(id);
        game.start();
    }
}
