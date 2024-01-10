import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;

//Created by @GreenL1nk 10.01.2024
public class BotMain {

    public static String BOT_TOKEN = "MTA4NzM3MDUwODYxMTM3MTEzOQ.G6_Vlt.WaF-UTjmXZ6qMw2mOkIq3G_s87TywtL1UWtNqk";

    private final ShardManager shardManager;
    private static BotMain instance;

    public BotMain() throws LoginException {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(BOT_TOKEN);
        shardManager = builder.build();
        instance = this;
    }

    public static BotMain getInstance() {
        return instance;
    }

    public ShardManager getShardManager() { return shardManager; }

    public static void main(String[] args) {
        try {
            BotMain bot = new BotMain();
        } catch (LoginException e) {
            System.out.println("ERROR: Provided bot token is invalid!");
        }
    }

}
