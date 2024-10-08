package global;

import global.buttons.ButtonListener;
import global.buttons.ButtonManager;
import global.commands.SlashCommandsListener;
import global.commands.SlashCommandsManager;
import global.config.Config;
import global.modals.ModalListener;
import global.modals.ModalManager;
import global.pastebin.SendEmbedCommand;
import global.selectmenus.SelectMenuListener;
import global.selectmenus.SelectMenusManager;
import greenlink.economy.EconomyManager;
import greenlink.economy.bank.BankFeeManager;
import greenlink.economy.commands.*;
import greenlink.economy.leaderboards.LeaderBoardCommand;
import greenlink.economy.leaderboards.LeaderBoardType;
import greenlink.economy.leaderboards.buttons.*;
import greenlink.economy.leaderboards.modals.ChoosePageLBModal;
import greenlink.economy.leaderboards.selectmenus.ChooseBoardTypeMenu;
import greenlink.economy.listeners.EconomyMessageListener;
import greenlink.economy.listeners.EconomyVoiceListener;
import greenlink.mentions.ChooseMentionMenu;
import greenlink.mentions.MentionManager;
import greenlink.moderation.commands.BanCommand;
import greenlink.moderation.events.ChangeNameEvent;
import greenlink.moderation.events.DeleteMessageEvent;
import greenlink.moderation.events.EditMessageEvent;
import greenlink.moderation.events.ReceivedMessageEvent;
import greenlink.moderation.events.role.RoleCreateEvent;
import greenlink.moderation.events.role.RoleUpdateEvent;
import greenlink.music.BotLeftScheduler;
import greenlink.music.MusicBotListener;
import greenlink.music.buttons.*;
import greenlink.music.commands.PlayCommand;
import greenlink.music.selectmenus.SelectTrackMenu;
import greenlink.music.selectmenus.SetTrackMenu;
import greenlink.shop.buttons.setting.DeleteRoleShopButton;
import greenlink.shop.buttons.setting.EditRoleBoostsButton;
import greenlink.shop.buttons.setting.EditRoleButton;
import greenlink.shop.buttons.setting.SaveRoleShopButton;
import greenlink.shop.buttons.shop.BuyRoleButton;
import greenlink.shop.buttons.shop.ShiftingRoleButton;
import greenlink.shop.commands.SettingCommand;
import greenlink.shop.commands.ShopCommand;
import greenlink.shop.menu.SettingChooseRole;
import greenlink.shop.menu.ShopChooseRole;
import greenlink.shop.modals.EditRoleShop;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

//Created by @GreenL1nk 10.01.2024
public class BotMain {
    public static final Logger logger = LoggerFactory.getLogger(BotMain.class);

    private final JDA jda;
    private static BotMain instance;

    public BotMain() {

        instance = this;

        Config.getInstance();

        jda = JDABuilder.createDefault(Config.getInstance().getToken())
                .addEventListeners(
                        new SlashCommandsListener(),
                        new ButtonListener(),
                        new SelectMenuListener(),
                        new ModalListener(),
                        new BotLeftScheduler(),
                        new MusicBotListener(),
                        new EconomyVoiceListener(),
                        new EconomyMessageListener(),
                        new DeleteMessageEvent(),
                        new EditMessageEvent(),
                        new ChangeNameEvent(),
                        new RoleUpdateEvent(),
                        new RoleCreateEvent(),
                        new ReceivedMessageEvent()
                )
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS, CacheFlag.EMOJI)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        addCommands();
        addButtons();
        addSelectMenus();
        addModals();
        SlashCommandsManager.getInstance().updateCommands(jda);

        MentionManager.getInstance();
        BankFeeManager.getInstance();
        Arrays.stream(LeaderBoardType.values()).forEach(leaderBoardType -> {
            try {
                EconomyManager.getInstance().getUserTopByPage(leaderBoardType, 1);
            } catch (SQLException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
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
                new LeaderBoardCommand(),
                new PayCommand(),
                new ShopCommand(),
                new SettingCommand(),
                new BanCommand(),
                new SendEmbedCommand()
        );
    }

    public void addSelectMenus() {
        SelectMenusManager.getInstance().addMenus(
                new SelectTrackMenu(),
                new SetTrackMenu(),
                new ChooseMentionMenu(),
                new ChooseBoardTypeMenu(),
                new SettingChooseRole(),
                new ShopChooseRole()
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
                new CancelButton(),
                new NextPage(),
                new PrevPage(),
                new LeaderBoardDelete(),
                new ChoosePageLB(),
                new LBUserPage(),
                new EditRoleButton(),
                new EditRoleBoostsButton(),
                new SaveRoleShopButton(),
                new DeleteRoleShopButton(),
                new ShiftingRoleButton(),
                new BuyRoleButton()
        );
    }

    public void addModals() {
        ModalManager.getInstance().addModals(
                new ChoosePageLBModal(),
                new EditRoleShop()
        );
    }

    public static BotMain getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new BotMain();
    }

    public JDA getJda() {
        return jda;
    }
}
