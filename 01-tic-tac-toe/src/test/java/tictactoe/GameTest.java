package tictactoe;

/** Dependency-free regression checks. Run with java tictactoe.GameTest. */
public final class GameTest {
    private static int checks;
    private static Game game() {
        return new Game(3, new Player("A", Symbol.X), new Player("B", Symbol.O), new StandardWinningStrategy());
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    private static void rejects(Runnable action) {
        try { action.run(); } catch (IllegalArgumentException | IllegalStateException expected) { checks++; return; }
        throw new AssertionError("Expected rejection");
    }
    private static void moves(Game game, int[][] positions) {
        for (int[] position : positions) check(game.makeMove(position[0], position[1]), "Move accepted");
    }
    public static void main(String[] args) {
        Game g = game();
        check(!g.undo() && !g.redo(), "Empty histories");
        rejects(() -> g.makeMove(-1, 0));
        rejects(() -> g.makeMove(0, 3));
        check(g.moveHistory().isEmpty() && g.currentPlayer().symbol() == Symbol.X, "Rejection preserves turn");
        g.makeMove(0, 0);
        rejects(() -> g.makeMove(0, 0));
        check(g.moveHistory().size() == 1 && g.currentPlayer().symbol() == Symbol.O, "Occupied rejection preserves state");
        g.undo();
        rejects(() -> g.makeMove(4, 4));
        check(g.canRedo(), "Invalid new move preserves redo");
        g.redo();
        check(g.symbolAt(0, 0) == Symbol.X && g.currentPlayer().symbol() == Symbol.O, "Redo restores move and turn");
        g.undo();
        g.makeMove(1, 1);
        check(!g.canRedo(), "Accepted branch clears redo");
        int[][][] wins = {
            {{0,0},{1,0},{0,1},{1,1},{0,2}},
            {{0,0},{0,1},{1,0},{1,1},{2,0}},
            {{0,0},{0,1},{1,1},{0,2},{2,2}},
            {{0,2},{0,0},{1,1},{1,0},{2,0}}
        };
        for (int[][] sequence : wins) {
            Game won = game();
            moves(won, sequence);
            check(won.status() == GameStatus.WON && won.winner().orElseThrow().symbol() == Symbol.X, "Win detected");
            rejects(() -> won.makeMove(2, 1));
            won.undo();
            check(won.status() == GameStatus.IN_PROGRESS && won.winner().isEmpty(), "Undo win reopens game");
            won.redo();
            check(won.status() == GameStatus.WON, "Redo restores win");
            while (won.undo()) { }
            check(won.currentPlayer().symbol() == Symbol.X && won.moveHistory().isEmpty(), "Undo all");
            while (won.redo()) { }
            check(won.status() == GameStatus.WON && won.moveHistory().size() == 5, "Redo all in order");
        }
        Game draw = game();
        moves(draw, new int[][]{{0,0},{0,1},{0,2},{1,1},{1,0},{1,2},{2,1},{2,0},{2,2}});
        check(draw.status() == GameStatus.DRAW && draw.winner().isEmpty(), "Draw detected");
        draw.undo();
        check(draw.status() == GameStatus.IN_PROGRESS, "Undo draw");
        draw.redo();
        check(draw.status() == GameStatus.DRAW, "Redo draw");
        Game secondWins = game();
        moves(secondWins, new int[][]{{0,0},{1,0},{0,1},{1,1},{2,2},{1,2}});
        check(secondWins.winner().orElseThrow().symbol() == Symbol.O, "Second player wins");
        Game large = new Game(4, new Player("A", Symbol.X), new Player("B", Symbol.O), new StandardWinningStrategy());
        moves(large, new int[][]{{0,0},{1,0},{0,1},{1,1},{0,2},{1,2},{0,3}});
        check(large.status() == GameStatus.WON, "N x N supported");
        rejects(() -> new Game(2, new Player("A", Symbol.X), new Player("B", Symbol.O), new StandardWinningStrategy()));
        rejects(() -> new Game(3, new Player("A", Symbol.X), new Player("B", Symbol.X), new StandardWinningStrategy()));
        Game failing = new Game(3, new Player("A", Symbol.X), new Player("B", Symbol.O), (board, move) -> {
            throw new IllegalStateException("Strategy failure");
        });
        rejects(() -> failing.makeMove(0, 0));
        check(failing.symbolAt(0, 0) == null && failing.moveHistory().isEmpty()
                && failing.currentPlayer().symbol() == Symbol.X, "Strategy failure rolls back");
        System.out.println("Passed " + checks + " checks.");
    }
}
