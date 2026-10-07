package snakeandladder;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Game {
    private final Board board;
    private final DiceStrategy dice;
    private final List<Player> players;
    private final int[] positions;
    private int currentPlayerIndex;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private Player winner;

    public Game(Board board, DiceStrategy dice, List<Player> players) {
        this.board = Objects.requireNonNull(board);
        this.dice = Objects.requireNonNull(dice);
        this.players = List.copyOf(players);
        if (players.size() < 2 || new HashSet<>(players).size() != players.size())
            throw new IllegalArgumentException("At least two distinct players required");
        positions = new int[players.size()];
    }

    public TurnResult playTurn() {
        if (status != GameStatus.IN_PROGRESS) throw new IllegalStateException("Game already finished");
        int roll = dice.roll();
        if (roll <= 0) throw new IllegalArgumentException("Dice must return a positive roll");
        Player player = getCurrentPlayer();
        int from = positions[currentPlayerIndex];
        boolean overshoot = (long) from + roll > board.getSize();
        int destination = board.move(from, roll);
        positions[currentPlayerIndex] = destination;
        if (destination == board.getSize()) {
            status = GameStatus.WON;
            winner = player;
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
        // On overshoot the attempted square is outside the board; report the unchanged square.
        return new TurnResult(player, roll, from, overshoot ? from : from + roll,
                              destination, overshoot, status);
    }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public GameStatus getStatus() { return status; }
    public Optional<Player> getWinner() { return Optional.ofNullable(winner); }
    public int getPosition(Player player) {
        int index = players.indexOf(player);
        if (index < 0) throw new IllegalArgumentException("Unknown player");
        return positions[index];
    }
}
