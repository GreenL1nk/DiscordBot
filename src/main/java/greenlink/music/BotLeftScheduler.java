package greenlink.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author t.me/GreenL1nk
 * 19.01.2024
 */
public class BotLeftScheduler extends ListenerAdapter {


    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        GuildVoiceState botVoiceState = event.getGuild().getSelfMember().getVoiceState();

        if (botVoiceState == null) return;
        if (!botVoiceState.inAudioChannel()) return;
        AudioChannelUnion channel = botVoiceState.getChannel();
        if (channel == null) return;
        if (channel.getMembers().stream().noneMatch(member -> member.getIdLong() != botVoiceState.getIdLong())) {
            GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
            musicManager.startScheduler(event.getGuild());
        }
    }
}
