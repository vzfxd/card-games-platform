package games.card.backend.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class BaseGame implements GameRules {
    protected Stack<Card> deck;
    protected Stack<Card> cardStack;
    protected List<Player> players;

    private ArrayList<Card> generateDeck(){
        ArrayList<Card> deck = new ArrayList<>();

        for(CardSuit suit: CardSuit.values()){
            for(CardValue value: CardValue.values()){
                deck.add( new Card(suit, value) );
            }
        }

        return deck;
    }
    
    protected void getCardFromDeck(Player player){
        player.getCards().add(deck.pop());
    }

    protected void putCardOnStack(Card card){
        cardStack.push(card);
    }
}
