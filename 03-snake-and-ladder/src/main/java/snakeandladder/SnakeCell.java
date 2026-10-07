package snakeandladder;

public record SnakeCell(int destination) implements Cell {
    public int getDestination() { return destination; }
}
