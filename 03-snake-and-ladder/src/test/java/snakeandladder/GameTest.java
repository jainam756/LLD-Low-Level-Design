package snakeandladder;

import java.util.List;
import java.util.Map;

public final class GameTest {
    private static int checks;
    private static void check(boolean condition) {
        checks++;
        if (!condition) throw new AssertionError("Check " + checks + " failed");
    }
    private static void rejects(Class<? extends RuntimeException> type, Runnable action) {
        try { action.run(); } catch (RuntimeException e) {
            if (!type.isInstance(e)) throw e;
            checks++;
            return;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
    private static Board board(int size, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
        return new Board(new BoardConfig(size, snakes, ladders), new CellFactory());
    }
    public static void main(String[] args) {
        Board chain = board(20, Map.of(12, 5), Map.of(2, 12, 5, 18));
        check(chain.resolvePosition(2) == 18);
        check(chain.resolvePosition(0) == 0);
        check(chain.resolvePosition(20) == 20);
        check(chain.move(19, 2) == 19);
        check(chain.move(19, Integer.MAX_VALUE) == 19);
        check(chain.move(19, 1) == 20);
        check(chain.move(10, 2) == 18);
        rejects(IllegalArgumentException.class, () -> chain.resolvePosition(21));
        rejects(IllegalArgumentException.class, () -> chain.move(-1, 1));
        rejects(IllegalArgumentException.class, () -> chain.move(0, 0));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(12, 2), Map.of(2, 12)));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(12, 5), Map.of(12, 18)));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(2, 12), Map.of()));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(), Map.of(12, 2)));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(20, 2), Map.of()));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(), Map.of(0, 12)));
        rejects(IllegalArgumentException.class, () -> board(20, Map.of(), Map.of(2, 21)));
        rejects(IllegalArgumentException.class, () -> board(1, Map.of(), Map.of()));
        CellFactory factory = new CellFactory();
        BoardConfig config = new BoardConfig(20, Map.of(12, 5), Map.of(2, 18));
        check(factory.createCell(3, config) instanceof NormalCell);
        check(factory.createCell(12, config) instanceof SnakeCell);
        check(factory.createCell(2, config) instanceof LadderCell);
        Player a = new Player("A"), b = new Player("B"), c = new Player("C");
        int[] rolls = {6, 1, 1, 5, 1, 1, 4};
        int[] cursor = {0};
        Game game = new Game(board(10, Map.of(), Map.of()), () -> rolls[cursor[0]++], List.of(a, b, c));
        check(game.getPosition(a) == 0 && game.getWinner().isEmpty());
        check(game.playTurn().destination() == 6 && game.getCurrentPlayer().equals(b));
        game.playTurn(); game.playTurn();
        check(game.getCurrentPlayer().equals(a));
        TurnResult skipped = game.playTurn();
        check(skipped.overshoot() && skipped.destination() == 6 && game.getCurrentPlayer().equals(b));
        game.playTurn(); game.playTurn();
        check(game.playTurn().status() == GameStatus.WON);
        check(game.getWinner().orElseThrow().equals(a) && game.getPosition(a) == 10);
        rejects(IllegalStateException.class, game::playTurn);
        check(cursor[0] == 7);
        Game invalidDice = new Game(chain, () -> 0, List.of(a, b));
        rejects(IllegalArgumentException.class, invalidDice::playTurn);
        check(invalidDice.getCurrentPlayer().equals(a) && invalidDice.getPosition(a) == 0);
        Game ladderWin = new Game(board(20, Map.of(), Map.of(2, 20)), () -> 2, List.of(a, b));
        check(ladderWin.playTurn().status() == GameStatus.WON);
        rejects(IllegalArgumentException.class, () -> new Game(chain, () -> 1, List.of(a)));
        rejects(IllegalArgumentException.class, () -> new Game(chain, () -> 1, List.of(a, a)));
        rejects(IllegalArgumentException.class, () -> game.getPosition(new Player("Unknown")));
        for (int i = 0; i < 100; i++) {
            int one = new NormalDice().roll(), two = new TwoDice().roll();
            check(one >= 1 && one <= 6 && two >= 2 && two <= 12);
        }
        System.out.println("All " + checks + " checks passed.");
    }
}
