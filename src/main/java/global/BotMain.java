package global;

import global.buttons.ButtonListener;
import global.buttons.ButtonManager;
import global.commands.SlashCommandsListener;
import global.commands.SlashCommandsManager;
import global.config.Config;
import global.selectmenus.SelectMenuListener;
import global.selectmenus.SelectMenusManager;
import greenlink.economy.commands.*;
import greenlink.music.BotLeftScheduler;
import greenlink.music.MusicBotListener;
import greenlink.music.buttons.*;
import greenlink.music.selectmenus.SelectTrackMenu;
import greenlink.music.commands.PlayCommand;
import greenlink.music.selectmenus.SetTrackMenu;
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

    public BotMain() {
        Config.getInstance();

        jda = JDABuilder.createDefault(Config.getInstance().getToken())
                .addEventListeners(
                        new SlashCommandsListener(),
                        new ButtonListener(),
                        new SelectMenuListener(),
                        new BotLeftScheduler(),
                        new MusicBotListener()
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
                new PlayCommand(),
                new ProfileCommand(),
                new WorkCommand(),
                new TimelyCommand(),
                new DailyCommand(),
                new WeeklyCommand(),
                new MonthlyCommand(),
                new RobCommand(),
                new DepositCommand(),
                new WithdrawCommand(),
                new LeaderBoardCommand()
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
}
