package greenlink;

import greenlink.databse.DatabaseConnector;
import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import greenlink.economy.UserCooldown;
import greenlink.observer.Observer;

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

    public EconomyUser getEconomyUser() {
         return EconomyManager.getInstance().getEconomyUser(this.uuid);
    }

    public void onEconomyUpdate() {
        onEconomyUserUpdate(this);
    }

    public UserCooldown getUserCooldown() {
        return userCooldown;
    }
}
