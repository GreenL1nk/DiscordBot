package greenlink.economy;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import global.BotMain;
import greenlink.databse.DatabaseConnector;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author t.me/GreenL1nk
 * 19.01.2024
 */
public class EconomyManager {
    private static EconomyManager instance;
    Cache<Long, EconomyUser> cache;

    private EconomyManager() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .removalListener(removalListener)
                .build();
    }

    public EconomyUser getEconomyUser(long uuid) {
        try {
            return cache.get(uuid, () -> DatabaseConnector.getInstance().getEconomyUser(uuid));
        } catch (ExecutionException e) {
            BotMain.logger.warn(e.getMessage());
            BotMain.logger.warn("Загрузка пользователя вызвало ошибку, создаём нового для " + uuid);
            return DatabaseConnector.getInstance().getEconomyUser(uuid);
        }
    }

    RemovalListener<Long, EconomyUser> removalListener = notification -> {
        BotMain.logger.debug("User " + notification.getKey() + " removed from cache");
    };

    public static synchronized EconomyManager getInstance() {
        if (instance == null) {
            instance = new EconomyManager();
        }
        return instance;
    }
}