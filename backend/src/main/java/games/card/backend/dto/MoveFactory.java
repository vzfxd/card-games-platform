package games.card.backend.dto;

import games.card.backend.games.GameMove;
import games.card.backend.games.MoveDraw;
import games.card.backend.games.MovePass;

public class MoveFactory {
    public static GameMove createGameMove(String gameMove) {
        switch (gameMove) {
            case "DRAW":
                return new MoveDraw();
            case "PASS":
                return new MovePass();
            default:
                throw new IllegalArgumentException("Nieznany typ ruchu: " + gameMove);
        }
    }
}
