package global.config;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import global.BotMain;
import global.config.configs.*;
import global.utils.Utils;
import greenlink.economy.jobs.Job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author t.me/GreenL1nk
 * 20.01.2024
 */
public class Config {
    private static Config instance;
    private static final String CONFIG_FILE_NAME = "config.json";
    private int botVoiceTimeout;
    private String token;
    private boolean mySQLEnable;
    private String mySQLHost;
    private String mySQLPort;
    private String mySQLUser;
    private String mySQLDbname;
    private String mySQLPassword;
    private double xpFormula;
    private int firstLevelXP;
    private List<Job> jobs;
    private WorkConfig work;
    private TimelyConfig timely;
    private DailyConfig daily;
    private WeeklyConfig weekly;
    private MonthlyConfig monthly;
    private RobConfig rob;
    private IconConfig icon;


    private Config() {
        ensureConfigFileExists();
    }

    public int getBotVoiceTimeout() {
        return botVoiceTimeout;
    }

    private static void ensureConfigFileExists() {
        try {
            String jarPath = BotMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarDir = new File(jarPath).getParent();

            String configFilePath = Paths.get(jarDir, CONFIG_FILE_NAME).toString();

            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                copyResourceFileToDirectory(configFile);
            }
        } catch (URISyntaxException | IOException e) {
            BotMain.logger.error("", e);
        }
    }

    private static void copyResourceFileToDirectory(File destination) throws IOException {
        try (InputStream inputStream = BotMain.class.getClassLoader().getResourceAsStream(Config.CONFIG_FILE_NAME)) {
            if (inputStream != null) {
                Files.copy(inputStream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Не удалось найти ресурс: " + Config.CONFIG_FILE_NAME);
            }
        }
    }

    public static void loadFromFile() {
        try {
            JsonElement jsonElement = Utils.readConfig(Path.of(BotMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()), CONFIG_FILE_NAME);

            Type type = new TypeToken<Config>() {}.getType();
            Config fromFile = Json.GSON.fromJson(jsonElement, type);
            if (fromFile == null) {
                BotMain.logger.info(CONFIG_FILE_NAME + " not found, try creates new");
                ensureConfigFileExists();
            } else {
                instance = fromFile;
            }
        } catch (URISyntaxException e) {
            BotMain.logger.error("", e);
        }
    }

    public String getToken() {
        return token;
    }

    public boolean isMySQLEnable() {
        return mySQLEnable;
    }

    public String getMySQLHost() {
        return mySQLHost;
    }

    public String getMySQLPort() {
        return mySQLPort;
    }

    public String getMySQLUser() {
        return mySQLUser;
    }

    public String getMySQLDbname() {
        return mySQLDbname;
    }

    public String getMySQLPassword() {
        return mySQLPassword;
    }

    public double getXpFormula() {
        return xpFormula;
    }

    public int getFirstLevelXP() {
        return firstLevelXP;
    }

    public WorkConfig getWork() {
        return work;
    }

    public TimelyConfig getTimely() {
        return timely;
    }

    public DailyConfig getDaily() {
        return daily;
    }

    public WeeklyConfig getWeekly() {
        return weekly;
    }

    public MonthlyConfig getMonthly() {
        return monthly;
    }

    public RobConfig getRob() {
        return rob;
    }

    public IconConfig getIcon() {
        return icon;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
            loadFromFile();
        }
        return instance;
    }
}