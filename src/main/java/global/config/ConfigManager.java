package global.config;

import global.BotMain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author t.me/GreenL1nk
 * 14.01.2024
 */
public class ConfigManager {
    private static ConfigManager instance;
    private static final String CONFIG_FILE_NAME = ".env";

    private ConfigManager() {
    }


    public void ensureConfigFileExists() {
        try {
            String jarPath = BotMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String jarDir = new File(jarPath).getParent();

            String configFilePath = Paths.get(jarDir, CONFIG_FILE_NAME).toString();

            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                copyResourceFileToDirectory(CONFIG_FILE_NAME, configFile);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void copyResourceFileToDirectory(String resourceName, File destination) throws IOException {
        try (InputStream inputStream = BotMain.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                Files.copy(inputStream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IOException("Не удалось найти ресурс: " + resourceName);
            }
        }
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
}