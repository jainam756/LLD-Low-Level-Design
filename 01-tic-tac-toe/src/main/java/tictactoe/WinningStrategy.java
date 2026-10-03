package tictactoe;

@FunctionalInterface
public interface WinningStrategy {
    boolean hasWon(Board board, Move lastMove);
}
