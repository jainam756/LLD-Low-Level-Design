package snakeandladder;

import java.util.List;
import java.util.Map;

public final class Main {
    public static void main(String[] args) {
        Board board = new Board(new BoardConfig(100, Map.of(99, 54, 70, 55, 52, 42),
                Map.of(6, 25, 11, 40, 60, 85)), new CellFactory());
        Game game = new Game(board, new NormalDice(), List.of(new Player("Jainam"), new Player("Alex")));
        for (int turn = 0; turn < 10000 && game.getStatus() == GameStatus.IN_PROGRESS; turn++) {
            TurnResult result = game.playTurn();
            System.out.printf("%s rolled %d: %d -> %d -> %d%s%n", result.player().name(),
                    result.roll(), result.from(), result.landing(), result.destination(),
                    result.overshoot() ? " (overshoot: stay)" : "");
        }
        System.out.println(game.getWinner().map(p -> "Winner: " + p.name()).orElse("Demo turn limit reached"));
    }
}
