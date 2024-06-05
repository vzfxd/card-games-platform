package games.card.backend.games;

import lombok.Data;

import java.util.List;

@Data
public class Player {
    private String username;
    private List<Card> cards;
    private boolean turn;
}
