package snakeandladder;

import java.util.Map;
import java.util.Objects;

public record BoardConfig(int size, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders) {
    public BoardConfig {
        if (size < 2) throw new IllegalArgumentException("Board size must be at least 2");
        snakes = Map.copyOf(Objects.requireNonNull(snakes));
        ladders = Map.copyOf(Objects.requireNonNull(ladders));
        validate(size, snakes, true);
        validate(size, ladders, false);
        for (int start : snakes.keySet()) {
            if (ladders.containsKey(start)) throw new IllegalArgumentException("Overlapping start: " + start);
        }
    }

    private static void validate(int size, Map<Integer, Integer> jumps, boolean snake) {
        jumps.forEach((start, end) -> {
            if (start <= 0 || start >= size || end <= 0 || end > size)
                throw new IllegalArgumentException("Jump outside playable board or starts at finish");
            if (snake ? end >= start : end <= start)
                throw new IllegalArgumentException("Invalid snake/ladder direction");
        });
    }
}
