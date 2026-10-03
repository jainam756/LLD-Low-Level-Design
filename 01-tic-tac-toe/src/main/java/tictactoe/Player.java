package tictactoe;

import java.util.Objects;

public record Player(String name, Symbol symbol) {
    public Player {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(symbol, "symbol");
        if (name.isBlank()) throw new IllegalArgumentException("Player name cannot be blank");
    }
}
