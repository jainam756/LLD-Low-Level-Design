package tictactoe;

public final class Board {
    private final Cell[][] cells;
    private int occupied;

    public Board(int size) {
        if (size < 3) throw new IllegalArgumentException("Board size must be at least 3");
        cells = new Cell[size][size];
        for (int row = 0; row < size; row++)
            for (int col = 0; col < size; col++) cells[row][col] = new Cell();
    }
    public int size() { return cells.length; }
    public Symbol symbolAt(int row, int col) { validate(row, col); return cells[row][col].symbol(); }
    public boolean isFull() { return occupied == size() * size(); }
    void validate(int row, int col) {
        if (row < 0 || row >= size() || col < 0 || col >= size())
            throw new IllegalArgumentException("Coordinates must be within the board");
    }
    void mark(int row, int col, Symbol symbol) {
        validate(row, col);
        cells[row][col].mark(symbol);
        occupied++;
    }
    void clear(int row, int col) {
        validate(row, col);
        if (cells[row][col].isEmpty()) throw new IllegalStateException("Cell is already empty");
        cells[row][col].clear();
        occupied--;
    }
    @Override public String toString() {
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < size(); row++) {
            for (int col = 0; col < size(); col++) {
                if (col > 0) result.append(" | ");
                Symbol symbol = cells[row][col].symbol();
                result.append(symbol == null ? "." : symbol);
            }
            result.append(System.lineSeparator());
        }
        return result.toString();
    }
}
