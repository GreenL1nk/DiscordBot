package global.config.configs;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DailyConfig implements ConfigImpl {
    private int cooldown;
    private int minValue;
    private int maxValue;
    private String icon;

    public long getCooldown() {
        return TimeUnit.MINUTES.toMillis(cooldown);
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public String getIcon() {
        return icon;
    }

    public int getRandomValue() {
        Random random = new Random();
        return random.nextInt(maxValue - minValue + 1) + minValue;
    }
}
