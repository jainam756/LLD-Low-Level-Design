package snakeandladder;

public final class CellFactory {
    public Cell createCell(int position, BoardConfig config) {
        if (config.snakes().containsKey(position)) return new SnakeCell(config.snakes().get(position));
        if (config.ladders().containsKey(position)) return new LadderCell(config.ladders().get(position));
        return new NormalCell(position);
    }
}
