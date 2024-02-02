package global.config.configs;

import java.util.concurrent.TimeUnit;

public class WorkConfig implements ConfigImpl {
    private int cooldown;
    private String icon;

    public long getCooldown() {
        return TimeUnit.MINUTES.toMillis(cooldown);
    }

    public String getIcon() {
        return icon;
    }
}

