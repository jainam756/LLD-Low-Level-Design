package tictactoe;

public final class Cell {
    private Symbol symbol;

    public Symbol symbol() { return symbol; }
    public boolean isEmpty() { return symbol == null; }
    void mark(Symbol value) {
        if (!isEmpty()) throw new IllegalArgumentException("Cell is occupied");
        symbol = java.util.Objects.requireNonNull(value);
    }
    void clear() { symbol = null; }
}
