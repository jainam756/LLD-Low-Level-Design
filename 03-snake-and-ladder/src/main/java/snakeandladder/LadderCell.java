package snakeandladder;

public record LadderCell(int destination) implements Cell {
    public int getDestination() { return destination; }
}
