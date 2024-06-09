package games.card.backend.games;

public class Macao extends Game{
    public Macao(Long id, String owner) {
        super(id,owner);
    }

    @Override
    public void nextTurn() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public Game getState(Player player) {
        return null;
    }

    @Override
    public String findWinner() {
        return null;
    }
}
