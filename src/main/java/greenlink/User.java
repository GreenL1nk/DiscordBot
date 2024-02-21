package greenlink;

import global.BotMain;
import greenlink.databse.DatabaseConnector;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import greenlink.economy.UserCooldown;
import greenlink.observer.Observer;
import org.jetbrains.annotations.Nullable;

/**
 * @author t.me/GreenL1nk
 * 20.01.2024
 */
public class User implements Observer {
    private final long uuid;
    private final UserCooldown userCooldown;

    public User(long uuid) {
        this.uuid = uuid;
        this.userCooldown = DatabaseConnector.getInstance().getUserCooldown(uuid);
    }

    public long getUuid() {
        return uuid;
    }

    @Nullable
    public EconomyUser getEconomyUser() {
        net.dv8tion.jda.api.entities.User userById = BotMain.getInstance().getJda().getUserById(uuid);
        if (userById == null) return null;
        return EconomyManager.getInstance().getEconomyUser(userById);
    }

    public void onEconomyUpdate() {
        onEconomyUserUpdate(this);
    }

    public UserCooldown getUserCooldown() {
        return userCooldown;
    }
}
