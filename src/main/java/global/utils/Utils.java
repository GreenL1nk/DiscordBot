package global.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import global.BotMain;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 15.01.2024
 */
public class Utils {

    public static String formatTime(long timeInMillis) {
        final long days = timeInMillis / TimeUnit.DAYS.toMillis(1);
        final long hours = (timeInMillis % TimeUnit.DAYS.toMillis(1)) / TimeUnit.HOURS.toMillis(1);
        final long minutes = (timeInMillis % TimeUnit.HOURS.toMillis(1)) / TimeUnit.MINUTES.toMillis(1);
        final long seconds = (timeInMillis % TimeUnit.MINUTES.toMillis(1)) / TimeUnit.SECONDS.toMillis(1);

        if (days > 0) {
            return String.format("%ddд:%02dч:%02dм:%02dс", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%02dч:%02dм:%02dс", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02dм:%02dс", minutes, seconds);
        } else {
            return String.format("%dс", seconds);
        }
    }

    @Nullable
    public static JsonElement readConfig(Path path, String fileName) {
        Path parent = path.getParent();
        Path configFolder = parent.resolve(fileName);
        try {
            return JsonParser.parseReader(Files.newBufferedReader(configFolder));
        } catch (IOException e) {
            BotMain.logger.error("", e);
            return null;
        }
    }

    private static boolean loadJdbcDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor(new Class[0]).newInstance();
            return true;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            BotMain.logger.error("", e);
            return false;
        }
    }

    public static boolean loadMysqlDriver() {
        return loadJdbcDriver();
    }

}
