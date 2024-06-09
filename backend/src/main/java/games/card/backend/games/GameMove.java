package games.card.backend.games;

public interface GameMove {
    void perform(Game game, Player player);
}
