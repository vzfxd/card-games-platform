package games.card.backend.games;

public class MovePass implements GameMove{
    @Override
    public void perform(Game game, Player player) {
        player.setPass(true);
    }
}
