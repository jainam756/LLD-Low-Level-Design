package snakeandladder;

import java.util.concurrent.ThreadLocalRandom;

public final class TwoDice implements DiceStrategy {
    public int roll() {
        return ThreadLocalRandom.current().nextInt(1, 7) + ThreadLocalRandom.current().nextInt(1, 7);
    }
}
