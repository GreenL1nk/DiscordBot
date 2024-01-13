package greenlink.music.commands;

import global.BotMain;
import global.commands.SlashCommand;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author t.me/GreenL1nk
 * 12.01.2024
 */
public class PlayCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if(event.getOptions().isEmpty()) {
            event.reply("You need to specify a music. Usage: '/play <music>'").setEphemeral(true).queue();
            return;
        }
        if (event.getMember() == null) return;
        if (event.getMember().getVoiceState() == null) return;

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("You must be in an audio channel to perform that command.").setEphemeral(true).queue();
            return;
        }
        event.deferReply();

        if(!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()){
            final AudioManager audioManager = event.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }

        String link = event.getOptions().get(0).getAsString();
        boolean isUrl = isUrl(link);
        if (!isUrl) {
            link = "ytsearch:" + link;
        }
        PlayerManager.getInstance().loadAndPlay(event.getChannel().asTextChannel(), link, isUrl);
    }

    private boolean isUrl(String input) {
        try {
            new URI(input).toURL();
            return true;
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            return false;
        }
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.STRING, "link", "Ссылка или название трека/плейлиста", true));
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Ставит трек";
    }
}
