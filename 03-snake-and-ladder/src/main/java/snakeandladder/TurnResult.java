package snakeandladder;

public record TurnResult(Player player, int roll, int from, int landing, int destination,
                         boolean overshoot, GameStatus status) {}
