package snakeandladder;

import java.util.concurrent.ThreadLocalRandom;

public final class NormalDice implements DiceStrategy {
    public int roll() { return ThreadLocalRandom.current().nextInt(1, 7); }
}
