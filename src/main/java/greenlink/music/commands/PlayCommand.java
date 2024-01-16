package greenlink.music.commands;

import global.commands.SlashCommand;
import greenlink.music.PlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
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
            event.reply("Использование: '/play <music>'").setEphemeral(true).queue();
            return;
        }
        if (event.getMember() == null) return;
        if (event.getMember().getVoiceState() == null) return;

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("Вы должны находиться в голосовом чате, для использования.").setEphemeral(true).queue();
            return;
        }
        if (event.getGuild() == null) return;

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
        MessageChannelUnion channel = event.getChannel();
        PlayerManager.getInstance().loadAndPlay(channel.asTextChannel(), link, isUrl, event);
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
        return "Ставит аудио из названия или ссылки ютуба";
    }
}
