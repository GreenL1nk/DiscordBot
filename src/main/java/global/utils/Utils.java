package global.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class Utils {

    public static String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = (timeInMillis % TimeUnit.HOURS.toMillis(1)) / TimeUnit.MINUTES.toMillis(1);
        final long seconds = (timeInMillis % TimeUnit.MINUTES.toMillis(1)) / TimeUnit.SECONDS.toMillis(1);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
        }
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

}
