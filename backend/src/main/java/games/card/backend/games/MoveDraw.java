package games.card.backend.games;

public class MoveDraw implements GameMove{
    @Override
    public void perform(Game game, Player player) {
        player.addCard(game.getDeck().pop());
    }
}
