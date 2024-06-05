package games.card.backend.games;

public class Macao extends BaseGame{
    @Override
    public boolean isLegalMove(Card c) {
        Card top =  this.cardStack.peek();
        return top.getSuit() == c.getSuit() || top.getValue() == c.getValue();
    }

    @Override
    public void dealCards() {
        for(int i = 0; i < 5; i++){
            for(Player player: this.players){
                getCardFromDeck(player);
            }
        }
    }
}
