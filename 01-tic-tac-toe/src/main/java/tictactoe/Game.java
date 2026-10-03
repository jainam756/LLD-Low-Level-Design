package tictactoe;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Owns all board mutations; one instance is intended for one thread. */
public final class Game {
    private final Board board;
    private final List<Player> players;
    private final WinningStrategy winningStrategy;
    private final Deque<Move> moveHistory = new ArrayDeque<>();
    private final Deque<Move> redoHistory = new ArrayDeque<>();
    private int currentPlayerIndex;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private Player winner;

    public Game(int size, Player first, Player second, WinningStrategy winningStrategy) {
        board = new Board(size);
        players = List.of(Objects.requireNonNull(first), Objects.requireNonNull(second));
        if (first.symbol() == second.symbol())
            throw new IllegalArgumentException("Players need different symbols");
        this.winningStrategy = Objects.requireNonNull(winningStrategy);
    }
    public Player currentPlayer() { return players.get(currentPlayerIndex); }
    public GameStatus status() { return status; }
    public Optional<Player> winner() { return Optional.ofNullable(winner); }
    public int size() { return board.size(); }
    public Symbol symbolAt(int row, int col) { return board.symbolAt(row, col); }
    public List<Move> moveHistory() { return List.copyOf(moveHistory); }
    public boolean canUndo() { return !moveHistory.isEmpty(); }
    public boolean canRedo() { return !redoHistory.isEmpty(); }
    public String renderBoard() { return board.toString(); }

    /** True means accepted/applied, not won. Invalid moves throw without changing state. */
    public boolean makeMove(int row, int col) {
        if (status != GameStatus.IN_PROGRESS) throw new IllegalStateException("Game has finished");
        board.validate(row, col);
        if (board.symbolAt(row, col) != null) throw new IllegalArgumentException("Cell is occupied");
        apply(new Move(currentPlayer(), row, col));
        redoHistory.clear();
        return true;
    }
    private void apply(Move move) {
        board.mark(move.row(), move.col(), move.player().symbol());
        boolean won;
        try {
            won = winningStrategy.hasWon(board, move);
        } catch (RuntimeException | Error failure) {
            board.clear(move.row(), move.col());
            throw failure;
        }
        moveHistory.addLast(move);
        winner = won ? move.player() : null;
        status = won ? GameStatus.WON : board.isFull() ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
        // Retain the last mover for terminal states; otherwise advance the turn.
        if (status == GameStatus.IN_PROGRESS) currentPlayerIndex = 1 - currentPlayerIndex;
    }
    public boolean undo() {
        if (!canUndo()) return false;
        Move move = moveHistory.removeLast();
        board.clear(move.row(), move.col());
        redoHistory.addLast(move);
        currentPlayerIndex = players.indexOf(move.player());
        status = GameStatus.IN_PROGRESS;
        winner = null;
        return true;
    }
    public boolean redo() {
        if (!canRedo()) return false;
        Move move = redoHistory.peekLast();
        apply(move);
        redoHistory.removeLast();
        return true;
    }
}
