package greenlink.databse;

import global.BotMain;
import global.config.Config;
import global.utils.Utils;
import greenlink.User;
import greenlink.economy.EconomyUser;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public class DatabaseConnector {
    private String url;
    private String user;
    private String password;
    private Connection connection;
    private boolean useSQLdDB;
    private static DatabaseConnector instance;
    private long lastTimeConnect;

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    private DatabaseConnector() {
        lastTimeConnect = 0;
        try {
            Config config = Config.getInstance();
            boolean isEnable = config.isMySQLEnable();
            if (isEnable) {
                String host = config.getMySQLHost();
                String port = config.getMySQLPort();
                user = config.getMySQLUser();
                String dbname = config.getMySQLDbname();
                password = config.getMySQLPassword();
                url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;

                connection = null;
                if (Utils.loadMysqlDriver()) {
                    connection = getConnection();
                    BotMain.logger.info("Using MySQL");
                    useSQLdDB = true;
                }
            } else {
                useSQLdDB = false;
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }

        if (!useSQLdDB) return;
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `users_economy` (
                    `uuid` BIGINT NOT NULL,
                    `coins` INT NULL DEFAULT 0,
                    `bank` INT NULL DEFAULT 0,
                    `xp` INT NULL DEFAULT 0,
                    `level` INT NULL DEFAULT 0,
                    `first_received_coin` BIGINT NULL DEFAULT NULL,
                    PRIMARY KEY (`uuid`)
                    )
                    COLLATE='utf8_unicode_ci'
                    ;""");

            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS `users_cooldown` (
                    `uuid` BIGINT NOT NULL,
                    `work` BIGINT NULL DEFAULT 0,
                    `timely` BIGINT NULL DEFAULT 0,
                    `daily` BIGINT NULL DEFAULT 0,
                    `weekly` BIGINT NULL DEFAULT 0,
                    `monthly` BIGINT NULL DEFAULT 0,
                    `rob` BIGINT NULL DEFAULT 0,
                    PRIMARY KEY (`uuid`)
                    )
                    COLLATE='utf8_unicode_ci'
                    ;""");
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }
    }

    @NotNull
    public EconomyUser getEconomyUser(@NotNull Long uuid) {
        EconomyUser economyUser = null;
        if (!useSQLdDB) {
            return new EconomyUser(uuid, 0, 0, 0, 0, 0);
        }
        try (Connection conn = getConnection();
             PreparedStatement selectStatement = conn.prepareStatement("SELECT coins, bank, xp, level, first_received_coin FROM users_economy WHERE uuid = ?")) {

            selectStatement.setLong(1, uuid);
            try (ResultSet result = selectStatement.executeQuery()) {
                if (!result.next()) {
                    economyUser = new EconomyUser(uuid, 0, 0, 0, 0, 0);
                    String sql = "INSERT INTO users_economy (uuid) VALUES (?)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(sql)) {
                        insertStatement.setLong(1, uuid);
                        insertStatement.executeUpdate();
                    }
                } else {
                    int coins = result.getInt("coins");
                    int bank = result.getInt("bank");
                    int xp = result.getInt("xp");
                    int level = result.getInt("level");
                    long firstReceivedCoin = result.getLong("first_received_coin");
                    economyUser = new EconomyUser(uuid, coins, bank, xp, level, firstReceivedCoin);
                }
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
            economyUser = new EconomyUser(uuid, 0, 0, 0, 0, 0);
        }
        return economyUser;
    }

    public void saveUser(User user) {
        if (!useSQLdDB || !(user instanceof EconomyUser economyUser)) {
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO users_economy (uuid, coins, bank, xp, level, first_received_coin) " +
                             "VALUES (?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE coins = VALUES(coins), bank = VALUES(bank), xp = VALUES(xp), level = VALUES(level), first_received_coin = VALUES(first_received_coin)")) {

            statement.setLong(1, economyUser.getUuid());
            statement.setInt(2, economyUser.getCashBalance());
            statement.setInt(3, economyUser.getBankBalance());
            statement.setInt(4, economyUser.getCurrentXP());
            statement.setInt(5, economyUser.getCurrentLevel());
            statement.setLong(6, economyUser.getFirstReceivedCoin());

            statement.executeUpdate();
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }

        BotMain.logger.debug("User " + user.getUuid() + " has been successfully saved");
    }

    private Connection getConnection() throws SQLException {
        long currentTimeMillis = System.currentTimeMillis();
        if (connection == null || connection.isClosed() || currentTimeMillis - lastTimeConnect > TimeUnit.HOURS.toMillis(1)) {
            closeConnection();
            connection = this.user != null ? DriverManager.getConnection(this.url, this.user, this.password) : DriverManager.getConnection(this.url);
            lastTimeConnect = currentTimeMillis;
        }
        return connection;
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            BotMain.logger.error("", e);
        }
    }
}