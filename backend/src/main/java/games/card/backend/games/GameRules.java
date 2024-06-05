package games.card.backend.games;

public interface GameRules {
    boolean isLegalMove(Card c);
    void dealCards();
}
