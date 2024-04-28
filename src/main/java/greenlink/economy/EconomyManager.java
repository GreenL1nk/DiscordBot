package greenlink.economy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import global.BotMain;
import global.config.Config;
import greenlink.databse.DatabaseConnector;
import greenlink.economy.leaderboards.LeaderBoardType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author t.me/GreenL1nk
 * 19.01.2024
 */
public class EconomyManager {
    private static EconomyManager instance;
    Cache<Long, EconomyUser> cache;
    int pageSize = 5;

    private EconomyManager() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(Config.getInstance().maxCacheUserSize)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener(removalListener)
                .build();
    }

    @Nullable
    public EconomyUser getEconomyUser(User user) {
        if (user.isBot()) {
            BotMain.logger.debug("Был получен бот, скипаем economyUser");
            return null;
        }
        try {
            return cache.get(user.getIdLong(), () -> DatabaseConnector.getInstance().getEconomyUser(user.getIdLong()));
        } catch (ExecutionException e) {
            BotMain.logger.warn(e.getMessage());
            BotMain.logger.warn("Загрузка пользователя вызвало ошибку, создаём нового для {}", user.getIdLong());
            return DatabaseConnector.getInstance().getEconomyUser(user.getIdLong());
        }
    }

    public Collection<EconomyUser> getEconomyUsers() {
        return cache.asMap().values();
    }

    RemovalListener<Long, EconomyUser> removalListener = notification -> {
        BotMain.logger.debug("User {} removed from cache", notification.getKey());
    };

    public ArrayList<EconomyUser> getUserTopByPage(LeaderBoardType leaderBoardType, int pageNumber) throws SQLException, ExecutionException {
        ArrayList<EconomyUser> economyUsers = new ArrayList<>();
        if (!DatabaseConnector.getInstance().useSQLdDB) return economyUsers;

        int offset = (pageNumber - 1) * pageSize;

        String query = buildQueryForLeaderBoardTypePage(leaderBoardType, offset, pageSize);

        AtomicInteger loaded = new AtomicInteger();
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement selectStatement = conn.prepareStatement(query)) {
            try (ResultSet result = selectStatement.executeQuery()) {
                while (result.next()) {
                    long uuid = result.getLong("uuid");

                    if (loaded.get() < pageSize) {
                        economyUsers.add(cache.get(uuid, () -> {
                            incrementAndGet(loaded);
                            return getResultUser(uuid);
                        }));
                    } else {
                        economyUsers.add(getResultUser(uuid));
                    }
                }
            }
        }

        return economyUsers;
    }

    private String buildQueryForLeaderBoardTypePage(LeaderBoardType leaderBoardType, int offset, int pageSize) {
        String baseQuery = buildQueryForLeaderBoardType(leaderBoardType);
        return baseQuery + " LIMIT " + pageSize + " OFFSET " + offset;
    }

    private synchronized void incrementAndGet(AtomicInteger counter) {
        counter.getAndIncrement();
    }

    private CacheRestAction<User> getUser(long uuid) {
        return BotMain.getInstance().getJda().retrieveUserById(uuid).useCache(false);
    }

    public RestAction<EconomyUser> getEconomyUser(long uuid) {
        return getUser(uuid).map(this::getEconomyUser);
    }

    private EconomyUser getResultUser(long uuid) {
        return DatabaseConnector.getInstance().getEconomyUser(uuid);
    }

    private String buildQueryForLeaderBoardType(LeaderBoardType leaderBoardType) {
        return switch (leaderBoardType) {
            case ROB -> "SELECT *, (success_rob + fail_rob) AS sumTotal FROM user_stats ORDER BY sumTotal DESC";
            case VOICE -> "SELECT * FROM user_stats ORDER BY voice_time DESC";
            case MESSAGES -> "SELECT * FROM user_stats ORDER BY total_message DESC";
            case LEVEL -> "SELECT * FROM users_economy ORDER BY level DESC";
            case BALANCE -> "SELECT *, (bank + coins) AS sumTotal FROM users_economy ORDER BY sumTotal DESC";
        };
    }

    public int getUserCount(LeaderBoardType leaderBoardType) throws SQLException {
        int userCount = 0;
        if (!DatabaseConnector.getInstance().useSQLdDB) return userCount;

        String query = buildCountQueryForLeaderBoardType(leaderBoardType);

        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement selectStatement = conn.prepareStatement(query);
             ResultSet result = selectStatement.executeQuery()) {
            if (result.next()) {
                userCount = result.getInt(1);
            }
        }

        return userCount;
    }

    private String buildCountQueryForLeaderBoardType(LeaderBoardType leaderBoardType) {
        return switch (leaderBoardType) {
            case ROB, MESSAGES, VOICE -> "SELECT COUNT(*) FROM user_stats";
            case LEVEL, BALANCE -> "SELECT COUNT(*) FROM users_economy";
        };
    }

    public int getCurrentUserRank(LeaderBoardType leaderBoardType, long userId) throws SQLException {
        String query = buildQueryForCurrentUserRank(leaderBoardType);
        int rank = -1;
        try (Connection conn = DatabaseConnector.getInstance().getConnection();
             PreparedStatement selectStatement = conn.prepareStatement(query)) {
            selectStatement.setLong(1, userId);
            try (ResultSet result = selectStatement.executeQuery()) {
                if (result.next()) {
                    rank = result.getInt(result.findColumn("position"));
                }
            }
        }
        return rank;
    }

    private String buildQueryForCurrentUserRank(LeaderBoardType leaderBoardType) {
        return switch (leaderBoardType) {
            case ROB -> "SELECT *, (SELECT COUNT(*) FROM user_stats AS x WHERE success_rob + fail_rob > user_stats.success_rob + user_stats.fail_rob) + 1 AS position FROM user_stats WHERE uuid = ?";
            case VOICE -> "SELECT *, (SELECT COUNT(*) FROM user_stats AS x WHERE voice_time > user_stats.voice_time) + 1 AS position FROM user_stats WHERE uuid = ?";
            case MESSAGES -> "SELECT *, (SELECT COUNT(*) FROM user_stats AS x WHERE total_message > user_stats.total_message) + 1 AS position FROM user_stats WHERE uuid = ?";
            case LEVEL -> "SELECT *, (SELECT COUNT(*) FROM users_economy AS x WHERE level > users_economy.level) + 1 AS position FROM users_economy WHERE uuid = ?";
            case BALANCE -> "SELECT *, (SELECT COUNT(*) FROM users_economy AS x WHERE bank + coins > users_economy.bank + users_economy.coins) + 1 AS position FROM users_economy WHERE uuid = ?";
        };
    }


    public static synchronized EconomyManager getInstance() {
        if (instance == null) {
            instance = new EconomyManager();
        }
        return instance;
    }
}