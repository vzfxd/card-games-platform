package games.card.backend.games;


import lombok.*;

import java.util.List;
import java.util.Stack;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class TwentyOne extends Game{
    public TwentyOne(Long id, String owner) {
        super(id,owner);
    }

    public TwentyOne(Long id, String owner, List<Player> players, Stack<Card> deck, Stack<Card> cardsOnTable, String winner, String turn) {
        super(id,owner,players,deck,cardsOnTable,winner,turn);
    }



    @Override
    public void nextTurn() {
        Player curr = getPlayer(getTurn());
        if(curr.getCardsValue() >= 21) {
            System.out.println("wymuszony pass");
            curr.setPass(true);
        }

        List<Player> activePlayers = players.stream().filter(p->!p.isPass() || p.getUsername().equals(turn)).toList();
        if(activePlayers.isEmpty()) return;
        System.out.println(activePlayers);

        int i = 0;
        for(Player player : activePlayers) {
            i = (i+1) % activePlayers.size();
            if(player.getUsername().equals(turn)) break;
        }
        setTurn(activePlayers.get(i).getUsername());
    }

    @Override
    public boolean isFinished() {
        return players.stream().filter(p->!p.isPass()).findAny().isEmpty();
    }

    @Override
    public void start() {
        this.players.forEach(player -> player.addCard(deck.pop()));
        setTurn(players.get(0).getUsername());
    }

    @Override
    public Game getState(Player player) {
        Game copy = new TwentyOne(getId(),getOwner(),getPlayers(),getDeck(),getCardsOnTable(),getWinner(),getTurn());
        Stack<Card> deck = new Stack<>();
        deck.add(new Card(CardSuit.BACK,CardValue.BLANK));
        copy.setDeck(deck);
        return copy;
    }

    @Override
    public String findWinner() {
        int goal = 21;
        int best = goal;
        String winner = null;
        for(Player player: getPlayers()){
            int points = Math.abs(goal - player.getCardsValue());
            if(points < best){
                winner = player.getUsername();
                best = points;
            }
        }

        return winner;
    }
}
