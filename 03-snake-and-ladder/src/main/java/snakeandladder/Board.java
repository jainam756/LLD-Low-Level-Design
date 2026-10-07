package snakeandladder;

import java.util.Objects;

public final class Board {
    private final Cell[] cells;

    public Board(BoardConfig config, CellFactory factory) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(factory);
        cells = new Cell[config.size() + 1];
        for (int i = 0; i < cells.length; i++) cells[i] = factory.createCell(i, config);
        // Validate the functional graph once, in O(board size), before any turns.
        byte[] state = new byte[cells.length];
        for (int i = 0; i < cells.length; i++) {
            int p = i;
            while (state[p] == 0 && cells[p].getDestination() != p) {
                state[p] = 1;
                p = cells[p].getDestination();
            }
            if (state[p] == 1) throw new IllegalArgumentException("Snake/ladder cycle detected");
            p = i;
            while (state[p] == 1) {
                state[p] = 2;
                p = cells[p].getDestination();
            }
            state[p] = 2;
        }
    }

    public int getSize() { return cells.length - 1; }

    public int resolvePosition(int position) {
        if (position < 0 || position > getSize()) throw new IllegalArgumentException("Position out of bounds");
        while (cells[position].getDestination() != position) position = cells[position].getDestination();
        return position;
    }

    public int move(int from, int roll) {
        if (from < 0 || from > getSize() || roll <= 0) throw new IllegalArgumentException("Invalid move");
        long landing = (long) from + roll;
        return landing > getSize() ? from : resolvePosition((int) landing);
    }
}
