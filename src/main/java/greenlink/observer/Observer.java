package greenlink.observer;

import greenlink.User;
import greenlink.databse.DatabaseConnector;

/**
 * @author t.me/GreenL1nk
 * 20.01.2024
 */
public interface Observer {
    default void onEconomyUserUpdate(User user) {
        DatabaseConnector.getInstance().saveUser(user);
    }

}
