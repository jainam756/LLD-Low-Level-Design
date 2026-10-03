package tictactoe;

import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        Game game = new Game(3, new Player("Jainam", Symbol.X), new Player("Player 2", Symbol.O),
                new StandardWinningStrategy());
        System.out.println("Tic-Tac-Toe: enter row col (0..2), undo, redo, or quit.");
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(game.renderBoard());
                System.out.println(game.status() + game.winner().map(p -> " — winner: " + p.name()).orElse(""));
                if (game.status() == GameStatus.IN_PROGRESS)
                    System.out.println("Turn: " + game.currentPlayer().name() + " (" + game.currentPlayer().symbol() + ")");
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("quit")) break;
                try {
                    if (input.equalsIgnoreCase("undo")) {
                        if (!game.undo()) System.out.println("Nothing to undo.");
                    } else if (input.equalsIgnoreCase("redo")) {
                        if (!game.redo()) System.out.println("Nothing to redo.");
                    } else {
                        String[] parts = input.split("\\s+");
                        if (parts.length != 2) throw new IllegalArgumentException("Enter row col, undo, redo, or quit");
                        game.makeMove(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                    }
                } catch (IllegalArgumentException | IllegalStateException error) {
                    System.out.println("Move rejected: " + error.getMessage());
                }
            }
        }
    }
}
