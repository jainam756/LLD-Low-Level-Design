package tictactoe;

/** A complete row, column, or main diagonal wins on an N x N board. */
public final class StandardWinningStrategy implements WinningStrategy {
    @Override public boolean hasWon(Board board, Move move) {
        int n = board.size();
        Symbol symbol = move.player().symbol();
        boolean row = true, col = true, diagonal = true, antiDiagonal = true;
        for (int i = 0; i < n; i++) {
            row &= board.symbolAt(move.row(), i) == symbol;
            col &= board.symbolAt(i, move.col()) == symbol;
            diagonal &= board.symbolAt(i, i) == symbol;
            antiDiagonal &= board.symbolAt(i, n - 1 - i) == symbol;
        }
        return row || col || (move.row() == move.col() && diagonal)
                || (move.row() + move.col() == n - 1 && antiDiagonal);
    }
}
