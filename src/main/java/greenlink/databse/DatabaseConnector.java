package greenlink.databse;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import global.BotMain;
import global.commands.ICommand;
import global.commands.SlashCommandsManager;
import global.config.Config;
import greenlink.User;
import greenlink.economy.EconomyUser;
import greenlink.economy.UserCooldown;
import greenlink.economy.bank.BankFee;
import greenlink.mentions.MentionObject;
import greenlink.mentions.MentionType;
import greenlink.mentions.Mentionable;
import greenlink.shop.RoleShop;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DatabaseConnector {
    private String url;
    private String user;
    private String password;
    private Connection connection;
    public boolean useSQLdDB;
    private HikariDataSource dataSource;
    private static DatabaseConnector instance;
    private long lastTimeConnect;

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }

    private DatabaseConnector() {
        try {
            Config config = Config.getInstance();
            useSQLdDB = config.isMySQLEnable();
            if (useSQLdDB) {
                HikariConfig hikariConfig = getHikariConfig(config);

                dataSource = new HikariDataSource(hikariConfig);

                try (Connection conn = getConnection();
                     PreparedStatement statement = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `users_economy` ( " +
                                     "`uuid` BIGINT NOT NULL, " +
                                     "`coins` INT NULL DEFAULT 0, " +
                                     "`bank` INT NULL DEFAULT 0, " +
                                     "`xp` INT NULL DEFAULT 0, " +
                                     "`level` INT NULL DEFAULT 0, " +
                                     "`first_received_coin` BIGINT NULL DEFAULT NULL, " +
                                     "PRIMARY KEY (`uuid`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");

                     PreparedStatement statement2 = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `users_cooldown` ( " +
                                     "`uuid` BIGINT NOT NULL, " +
                                     "`work` BIGINT NULL DEFAULT 0, " +
                                     "`timely` BIGINT NULL DEFAULT 0, " +
                                     "`daily` BIGINT NULL DEFAULT 0, " +
                                     "`weekly` BIGINT NULL DEFAULT 0, " +
                                     "`monthly` BIGINT NULL DEFAULT 0, " +
                                     "`rob` BIGINT NULL DEFAULT 0, " +
                                     "PRIMARY KEY (`uuid`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");

                     PreparedStatement statement3 = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `command_mention` ( " +
                                     "`uuid` BIGINT NOT NULL, " +
                                     "`work` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`timely` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`daily` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`weekly` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`monthly` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`rob` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "PRIMARY KEY (`uuid`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");

                     PreparedStatement statement4 = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `user_stats` ( " +
                                     "`uuid` BIGINT NOT NULL, " +
                                     "`earned_rob` INT NULL DEFAULT 0, " +
                                     "`lost_rob` INT NULL DEFAULT 0, " +
                                     "`voice_time` INT NULL DEFAULT 0, " +
                                     "`total_message` LONG NULL DEFAULT 0, " +
                                     "`success_rob` INT NULL DEFAULT 0, " +
                                     "`fail_rob` INT NULL DEFAULT 0, " +
                                     "PRIMARY KEY (`uuid`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");

                     PreparedStatement statement5 = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `bank_fee` ( " +
                                     "`uuid` BIGINT NOT NULL, " +
                                     "`last_operation` BIGINT NULL DEFAULT 0, " +
                                     "PRIMARY KEY (`uuid`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");

                     PreparedStatement statement6 = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `shop_roles` ( " +
                                     "`id` BIGINT NOT NULL, " +
                                     "`work_exp` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`timely_exp` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`daily_exp` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`weekly_exp` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`monthly_exp` VARCHAR(60) NULL DEFAULT NULL, " +
                                     "`coin_multiplier` DOUBLE NULL DEFAULT -1, " +
                                     "`left_count` INT NULL DEFAULT -1, " +
                                     "`price` INT NULL DEFAULT -1, " +
                                     "PRIMARY KEY (`id`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");

                     PreparedStatement statement7 = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS `message_data` ( " +
                                     "`message_id` BIGINT NOT NULL, " +
                                     "`author_id` BIGINT NOT NULL, " +
                                     "`content` VARCHAR(2000) NULL DEFAULT NULL, " +
                                     "PRIMARY KEY (`message_id`) ) " +
                                     "COLLATE='utf8_unicode_ci' ;");
                )
                {


                    statement.executeUpdate();
                    statement2.executeUpdate();
                    statement3.executeUpdate();
                    statement4.executeUpdate();
                    statement5.executeUpdate();
                    statement6.executeUpdate();
                    statement7.executeUpdate();
                }
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }
    }

    private boolean triggerExists(Connection conn, String triggerName) throws SQLException {
        String query = "SELECT trigger_name FROM information_schema.triggers WHERE trigger_schema = ? AND trigger_name = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, conn.getCatalog()); // Используем текущую базу данных
            statement.setString(2, triggerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Возвращает true, если триггер существует
            }
        }
    }

    @NotNull
    private static HikariConfig getHikariConfig(Config config) {
        String host = config.getMySQLHost();
        String port = config.getMySQLPort();
        String user = config.getMySQLUser();
        String dbname = config.getMySQLDbname();
        String password = config.getMySQLPassword();
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        return hikariConfig;
    }

    @NotNull
    public EconomyUser getEconomyUser(@NotNull Long uuid) {
        EconomyUser economyUser;
        if (!useSQLdDB) {
            return new EconomyUser(uuid, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        try (Connection conn = getConnection();
             PreparedStatement selectStatement = conn.prepareStatement("SELECT coins, bank, xp, level, first_received_coin FROM users_economy WHERE uuid = ?")) {
            selectStatement.setLong(1, uuid);
            try (ResultSet result = selectStatement.executeQuery()) {
                if (!result.next()) {
                    economyUser = new EconomyUser(uuid, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                    String sql = "INSERT INTO users_economy (uuid) VALUES (?)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(sql)) {
                        insertStatement.setLong(1, uuid);
                        insertStatement.executeUpdate();
                    }
                    try (Connection c = getConnection();
                         PreparedStatement statement = c.prepareStatement(
                                 "INSERT INTO user_stats (uuid, earned_rob, lost_rob, voice_time, total_message, success_rob, fail_rob) " +
                                         "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                                         "ON DUPLICATE KEY UPDATE earned_rob = VALUES(earned_rob), lost_rob = VALUES(lost_rob), voice_time = VALUES(voice_time), total_message = VALUES(total_message), success_rob = VALUES(success_rob), fail_rob = VALUES(fail_rob)")) {

                        statement.setLong(1, economyUser.getUuid());
                        statement.setInt(2, economyUser.getEarnedFromRobs());
                        statement.setInt(3, economyUser.getLostFromRobs());
                        statement.setLong(4, economyUser.getVoiceTime());
                        statement.setInt(5, economyUser.getTotalMessages());
                        statement.setInt(6, economyUser.getSuccessRobs());
                        statement.setInt(7, economyUser.getFailedRobs());

                        statement.executeUpdate();
                    }
                } else {
                    int coins = result.getInt("coins");
                    int bank = result.getInt("bank");
                    int xp = result.getInt("xp");
                    int level = result.getInt("level");
                    long firstReceivedCoin = result.getLong("first_received_coin");

                    int earnedRob = 0;
                    int lostRob = 0;
                    long voiceTime = 0;
                    int totalMessage = 0;
                    int successRob = 0;
                    int failRob = 0;
                    String userStatsSql = "SELECT earned_rob, lost_rob, voice_time, total_message, success_rob, fail_rob FROM user_stats WHERE uuid = ?";
                    try (PreparedStatement userStatsStatement = conn.prepareStatement(userStatsSql)) {
                        userStatsStatement.setLong(1, uuid);
                        try (ResultSet userStatsResult = userStatsStatement.executeQuery()) {
                            if (userStatsResult.next()) {
                                earnedRob = userStatsResult.getInt("earned_rob");
                                lostRob = userStatsResult.getInt("lost_rob");
                                voiceTime = userStatsResult.getLong("voice_time");
                                totalMessage = userStatsResult.getInt("total_message");
                                successRob = userStatsResult.getInt("success_rob");
                                failRob = userStatsResult.getInt("fail_rob");
                            }
                        }
                    }

                    economyUser = new EconomyUser(uuid, coins, bank, xp, level, firstReceivedCoin, earnedRob, lostRob, successRob, failRob, voiceTime, totalMessage);
                }
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
            economyUser = new EconomyUser(uuid, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
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
            BotMain.logger.debug("User " + user.getUuid() + " has been successfully saved");
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO user_stats (uuid, earned_rob, lost_rob, voice_time, total_message, success_rob, fail_rob) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE earned_rob = VALUES(earned_rob), lost_rob = VALUES(lost_rob), voice_time = VALUES(voice_time), total_message = VALUES(total_message), success_rob = VALUES(success_rob), fail_rob = VALUES(fail_rob)")) {

            statement.setLong(1, economyUser.getUuid());
            statement.setInt(2, economyUser.getEarnedFromRobs());
            statement.setInt(3, economyUser.getLostFromRobs());
            statement.setLong(4, economyUser.getVoiceTime());
            statement.setInt(5, economyUser.getTotalMessages());
            statement.setInt(6, economyUser.getSuccessRobs());
            statement.setInt(7, economyUser.getFailedRobs());

            statement.executeUpdate();
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }
    }

    public UserCooldown getUserCooldown(long uuid) {
        UserCooldown userCooldown;
        if (!useSQLdDB) {
            return new UserCooldown(uuid, 0, 0, 0, 0, 0, 0);
        }
        try (Connection conn = getConnection();
             PreparedStatement selectStatement = conn.prepareStatement("SELECT work, timely, daily, weekly, monthly, rob FROM users_cooldown WHERE uuid = ?")) {

            selectStatement.setLong(1, uuid);
            try (ResultSet result = selectStatement.executeQuery()) {
                if (!result.next()) {
                    userCooldown = new UserCooldown(uuid, 0, 0, 0, 0, 0, 0);
                    String sql = "INSERT INTO users_cooldown (uuid) VALUES (?)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(sql)) {
                        insertStatement.setLong(1, uuid);
                        insertStatement.executeUpdate();
                    }
                } else {
                    long work = result.getLong("work");
                    long timely = result.getLong("timely");
                    long daily = result.getLong("daily");
                    long weekly = result.getLong("weekly");
                    long monthly = result.getLong("monthly");
                    long rob = result.getLong("rob");
                    userCooldown = new UserCooldown(uuid, work, timely, daily, weekly, monthly, rob);
                }
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
            userCooldown = new UserCooldown(uuid, 0, 0, 0, 0, 0, 0);
        }
        return userCooldown;
    }

    public void saveCommandsTime(UserCooldown userCooldown) {
        if (!useSQLdDB) return;

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO users_cooldown (uuid, work, timely, daily, weekly, monthly, rob) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE work = VALUES(work), timely = VALUES(timely), daily = VALUES(daily), weekly = VALUES(weekly), monthly = VALUES(monthly), rob = VALUES(rob)")) {

            statement.setLong(1, userCooldown.getUuid());
            statement.setLong(2, userCooldown.getWorkLastTime());
            statement.setLong(3, userCooldown.getTimelyLastTime());
            statement.setLong(4, userCooldown.getDailyLastTime());
            statement.setLong(5, userCooldown.getWeeklyLastTime());
            statement.setLong(6, userCooldown.getMonthlyLastTime());
            statement.setLong(7, userCooldown.getRobLastTime());

            statement.executeUpdate();
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }
    }

    @Nullable
    public MentionObject getMentionObject(ICommand command) {
        if (!useSQLdDB) return null;

        try (Connection conn = getConnection()) {

            String sql = "SELECT uuid, " + command.getName() + " FROM users_cooldown ORDER BY " + command.getName() + " ASC";
            try (PreparedStatement statement = conn.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    long uuid = resultSet.getLong("uuid");
                    Mentionable mentionable = (Mentionable) SlashCommandsManager.getInstance().getCommands().stream().filter(c -> c.getName().equalsIgnoreCase(command.getName())).findFirst().orElse(null);
                    if (mentionable == null) return null;
                    MentionObject mentionValue = getMentionObject(conn, uuid, command, resultSet.getLong(command.getName()) + mentionable.config().getCooldown());
                    if (mentionValue != null) {
                        return mentionValue;
                    }
                }
            }
        } catch (SQLException e) {
            BotMain.logger.error("Error sending command mentions", e);
        }
        return null;
    }

    @Nullable
    private MentionObject getMentionObject(Connection conn, long uuid, ICommand command, long time) throws SQLException {
        String sql = "SELECT " + command.getName() + " FROM command_mention WHERE uuid = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    net.dv8tion.jda.api.entities.User user = BotMain.getInstance().getJda().getUserById(uuid);
                    if (user != null && user.isBot()) return null;

                    String string = resultSet.getString(command.getName());
                    if (string == null) return null;
                    String[] split = string.split("-");

                    MentionType mentionType = MentionType.valueOf(split[0]);
                    String channelId = split[1];
                    String guildId = split[2];
                    return new MentionObject(user, mentionType, channelId, guildId, time);
                }
            }
        }
        return null;
    }

    public void saveMentionUser(ICommand command, net.dv8tion.jda.api.entities.User user, Guild guild, Channel channel, MentionType mentionType) {
        if (!useSQLdDB) return;
        if (user.isBot()) return;

        String commandName = command.getName();
        String userString =  mentionType.toString() + "-" + channel.getId() + "-" + guild.getId();

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO command_mention (uuid, " + commandName + ") " +
                             "VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE " + commandName + " = VALUES(" + commandName + ")")) {

            statement.setLong(1, user.getIdLong());
            statement.setString(2, userString);

            statement.executeUpdate();
            BotMain.logger.debug("Save mention for command " + command.getName() + " for user " + user.getId());
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }
    }

    public void deleteMention(ICommand command, net.dv8tion.jda.api.entities.User user) {
        if (!useSQLdDB) return;
        if (user.isBot()) return;

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "UPDATE command_mention SET " + command.getName() + " = NULL WHERE uuid = ?")) {

            statement.setLong(1, user.getIdLong());
            statement.executeUpdate();

            BotMain.logger.debug("Set mention for command " + command.getName() + " to null for user " + user.getId());
        } catch (SQLException e) {
            BotMain.logger.error("Error setting mention to null", e);
        }
    }

    public long lastFeeTime(long uuid, boolean update) {
        if (!useSQLdDB) return -1;

        long lastTime = -1;
        try (Connection conn = getConnection();
             PreparedStatement selectStatement = conn.prepareStatement("SELECT last_operation FROM bank_fee WHERE uuid = ?")) {

            selectStatement.setLong(1, uuid);
            try (ResultSet result = selectStatement.executeQuery()) {
                if (!result.next() || update) {
                    lastTime = System.currentTimeMillis();
                    String sql = "INSERT INTO bank_fee (uuid, last_operation) " +
                            "VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE last_operation = VALUES(last_operation)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(sql)) {
                        insertStatement.setLong(1, uuid);
                        insertStatement.setLong(2, lastTime);
                        insertStatement.executeUpdate();
                    }
                } else {
                    lastTime = result.getLong("last_operation");
                }
            }
        } catch (Exception e) {
            BotMain.logger.error("", e);
        }
        return lastTime;
    }

    @Nullable
    public BankFee getLowestNextFeeTime() {
        if (!useSQLdDB) return null;

        try (Connection conn = getConnection()) {

            String sql = "SELECT uuid, last_operation FROM bank_fee ORDER BY last_operation ASC";
            try (PreparedStatement statement = conn.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    long uuid = resultSet.getLong("uuid");
                    return new BankFee(uuid,resultSet.getLong("last_operation") + TimeUnit.MINUTES.toMillis((long) Config.getInstance().getBankFeePeriodMinutes()));
                }
            }
        } catch (SQLException e) {
            BotMain.logger.error("Error getLowestNextFeeTime", e);
        }
        return null;
    }

    public void saveRoleShop(RoleShop shopRole) {
        String query = "INSERT INTO shop_roles (id, work_exp, timely_exp, daily_exp, weekly_exp, monthly_exp, left_count, coin_multiplier, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "work_exp = VALUES(work_exp), " +
                "timely_exp = VALUES(timely_exp), " +
                "daily_exp = VALUES(daily_exp), " +
                "weekly_exp = VALUES(weekly_exp), " +
                "monthly_exp = VALUES(monthly_exp), " +
                "left_count = VALUES(left_count), " +
                "coin_multiplier = VALUES(coin_multiplier), " +
                "price = VALUES(price)";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, shopRole.getRole().getIdLong());
            pstmt.setString(2, shopRole.getWorkExp());
            pstmt.setString(3, shopRole.getTimelyExp());
            pstmt.setString(4, shopRole.getDailyExp());
            pstmt.setString(5, shopRole.getWeeklyExp());
            pstmt.setString(6, shopRole.getMonthlyExp());
            pstmt.setInt(7, shopRole.getLeftCount());
            pstmt.setDouble(8, shopRole.getCoinMultiplier());
            pstmt.setInt(9, shopRole.getPrice());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BotMain.logger.error("Error save roleshop", e);
        }
    }
    public RoleShop loadShopRole(long roleId) {
        String query = "SELECT * FROM shop_roles WHERE id = ?";

        Role role = BotMain.getInstance().getJda().getRoleById(roleId);

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String workExp = rs.getString("work_exp");
                    String timelyExp = rs.getString("timely_exp");
                    String dailyExp = rs.getString("daily_exp");
                    String weeklyExp = rs.getString("weekly_exp");
                    String monthlyExp = rs.getString("monthly_exp");
                    int leftCount = rs.getInt("left_count");
                    double coinMultiplier = rs.getDouble("coin_multiplier");
                    int price = rs.getInt("price");

                    return new RoleShop(workExp, timelyExp, dailyExp, weeklyExp, monthlyExp, leftCount, coinMultiplier, price, role);
                }
            }
        } catch (SQLException e) {
            BotMain.logger.error("Error load shoprole", e);
        }
        return new RoleShop("x1", "x1", "x1", "x1", "x1", 0, 1, 1, role);
    }

    public void deleteRoleShopById(long roleId) {
        String sql = "DELETE FROM shop_roles WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, roleId);

            pstmt.executeUpdate();
        } catch (Exception e) {
            BotMain.logger.error("Error delete shoprole", e);
        }
    }

    public void saveMessage(MessageReceivedEvent event) {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(
                     "INSERT INTO message_data (message_id, author_id, content) VALUES (?, ?, ?)")
        ) {
            statement.setLong(1, event.getMessageIdLong());
            statement.setLong(2, event.getAuthor().getIdLong());
            statement.setString(3, event.getMessage().getContentRaw());

            statement.executeUpdate();
        } catch (SQLException e) {
            BotMain.logger.error("Failed to save message", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}