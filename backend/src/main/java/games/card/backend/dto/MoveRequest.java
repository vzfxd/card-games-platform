package games.card.backend.dto;

import games.card.backend.games.Card;
import lombok.Data;

@Data
public class MoveRequest {
    private String gameMove;
    private Card card;
}
