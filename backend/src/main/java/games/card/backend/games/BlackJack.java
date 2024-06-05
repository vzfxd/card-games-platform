package games.card.backend.games;

public class BlackJack extends BaseGame{
    @Override
    public boolean isLegalMove(Card c) {
        return true;
    }

    @Override
    public void dealCards() {
        for(Player player: this.players){
            getCardFromDeck(player);
        }
    }
}
