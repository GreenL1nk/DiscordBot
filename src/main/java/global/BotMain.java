package global;

import global.commands.ICommand;
import global.commands.SlashCommand;
import global.commands.SlashCommandsListener;
import greenlink.music.commands.PlayCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//Created by @GreenL1nk 10.01.2024
public class BotMain {

    public static String BOT_TOKEN = "MTA4NzM3MDUwODYxMTM3MTEzOQ.G6_Vlt.WaF-UTjmXZ6qMw2mOkIq3G_s87TywtL1UWtNqk"; //WOMEN
//    public static String BOT_TOKEN = "NjUyMDkyNjcxNjg2NDc1ODA2.Gl19K4.FSzBK--EGfAk3gHgOQmk5V_EM-6Jbp1ywxxxsI"; //PSQ
    public static final Logger logger = LoggerFactory.getLogger(BotMain.class);
    private final List<ICommand> commandMap = new ArrayList<>();

    private final JDA jda;
    private static BotMain instance;

    public BotMain() {
        jda = JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new SlashCommandsListener(this))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableCache(CacheFlag.VOICE_STATE)
                .build();
        addCommands();
        updateCommands();

        instance = this;
    }

    public void addCommands() {
        getCommands().add(new PlayCommand());
    }

    private void updateCommands() {
        List<CommandData> commands = new ArrayList<>();
        getCommands().forEach((command) -> {
            if (command instanceof SlashCommand slashCommand) {
                slashCommand.updateOptions();
                commands.add(Commands.slash(command.getName(), slashCommand.getDescription()).addOptions(slashCommand.getOptions()));
            }
            command.updateAliases();
            command.updateChannels(jda);
            command.updateRoles(jda);
        });
        jda.updateCommands().addCommands(commands).queue();
    }

    public List<ICommand> getCommands() { return commandMap; }

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
