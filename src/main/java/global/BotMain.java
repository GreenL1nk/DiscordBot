package global;

import global.buttons.ButtonListener;
import global.buttons.ButtonManager;
import global.commands.SlashCommandsListener;
import global.commands.SlashCommandsManager;
import global.config.ConfigManager;
import global.selectmenus.SelectMenuListener;
import global.selectmenus.SelectMenusManager;
import greenlink.music.selectmenus.SelectTrackMenu;
import greenlink.music.buttons.*;
import greenlink.music.commands.PlayCommand;
import greenlink.music.selectmenus.SetTrackMenu;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Created by @GreenL1nk 10.01.2024
public class BotMain {
    public static final Logger logger = LoggerFactory.getLogger(BotMain.class);

    private final JDA jda;
    private static BotMain instance;
    private final Dotenv config;

    public BotMain() {
        ConfigManager.getInstance().ensureConfigFileExists();
        config = Dotenv.configure().load();
        String token = config.get("Token");
        jda = JDABuilder.createDefault(token)
                .addEventListeners(
                        new SlashCommandsListener(),
                        new ButtonListener(),
                        new SelectMenuListener()
                )
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        addCommands();
        addButtons();
        addSelectMenus();
        SlashCommandsManager.getInstance().updateCommands(jda);

        instance = this;
    }

    public void addCommands() {
        SlashCommandsManager.getInstance().addCommands(
                new PlayCommand()
        );
    }

    public void addSelectMenus() {
        SelectMenusManager.getInstance().addMenus(
                new SelectTrackMenu(),
                new SetTrackMenu()
        );
    }

    public void addButtons() {
        ButtonManager.getInstance().addButtons(
                new PreviousButton(),
                new NextButton(),
                new StopButton(),
                new PauseButton(),
                new IncreaseVolumeButton(),
                new DecreaseVolumeButton(),
                new RepeatTrackButton(),
                new RepeatPlaylistButton(),
                new ShuffleButton(),
                new ViewQueueButton(),
                new CancelButton()
        );
    }

    public static BotMain getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        BotMain bot = new BotMain();
    }

    public JDA getJda() {
        return jda;
    }

    public Dotenv getConfig() {
        return config;
    }
}
