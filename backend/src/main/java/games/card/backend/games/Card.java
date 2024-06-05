package games.card.backend.games;

import lombok.Data;

@Data
public class Card {
    private final CardSuit suit;
    private final CardValue value;
}
