package snakeandladder;

public record NormalCell(int position) implements Cell {
    public int getDestination() { return position; }
}
