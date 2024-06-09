package games.card.backend.games;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Data
public class Player {
    private String username;
    private List<Card> cards;
    private boolean pass;
    private int stop;

    public Player(String username){
        this.username = username;
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public int getCardsValue(){
        int points = 0;
        for(Card card: getCards()){
            points += card.getValue().getValue();
        }
        return points;
    }
}
