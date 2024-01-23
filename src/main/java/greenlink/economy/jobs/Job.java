package greenlink.economy.jobs;

import java.util.Random;

/**
 * @author t.me/GreenL1nk
 * 23.01.2024
 */
public record Job(String message, int highValue, int lowValue) {

    public int getRandomValue() {
        Random random = new Random();
        return random.nextInt(highValue - lowValue + 1) + lowValue;
    }
}
