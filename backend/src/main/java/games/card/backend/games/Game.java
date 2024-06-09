package games.card.backend.games;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Data
@RequiredArgsConstructor
public abstract class Game {
    Long id;
    String owner;
    String turn;
    List<Player> players;
    Stack<Card> deck;
    Stack<Card> cardsOnTable;
    String winner;

    public Game(Long id, String owner) {
        this.id = id;
        this.owner = owner;
        this.deck = generateDeck();
        this.players = new ArrayList<>();
        this.cardsOnTable = new Stack<>();
    }

    public Game(Long id, String owner, List<Player> players, Stack<Card> deck, Stack<Card> cardsOnTable,String winner, String turn) {
        this.id = id;
        this.owner = owner;
        this.players = players;
        this.deck = deck;
        this.cardsOnTable = cardsOnTable;
        this.winner = winner;
        this.turn = turn;
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(String username){
        players.removeIf(p -> p.getUsername().equals(username));
    }

    public Player getPlayer(String username) {
        return players.stream().filter(p->p.getUsername().equals(username)).findFirst().orElseThrow();
    }

    public Stack<Card> generateDeck() {
        Stack<Card> deck = new Stack<>();
        for (CardSuit cardSuit : CardSuit.values()) {
            for (CardValue cardValue : CardValue.values()) {

                if (cardValue.equals(CardValue.BLANK) || cardSuit.equals(CardSuit.BACK)) {
                    continue;
                }

                deck.add(new Card(cardSuit, cardValue));
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    public void setWinner(String winner){
        this.winner = winner;
        this.turn = null;
    }

    public abstract void nextTurn();

    public abstract boolean isFinished();

    public abstract void start();

    public abstract Game getState(Player player);

    public abstract String findWinner();
}
