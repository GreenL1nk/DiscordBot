package greenlink.economy.listeners;

import greenlink.economy.EconomyManager;
import greenlink.economy.EconomyUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author t.me/GreenL1nk
 * 01.02.2024
 */
public class EconomyVoiceListener extends ListenerAdapter {

    HashMap<Long, Long> usersJoinedMap = new HashMap<>();

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        Member member = event.getMember();

        AudioChannelUnion joinedChannel = event.getChannelJoined();
        AudioChannelUnion leftChannel = event.getChannelLeft();

        long currentTime = System.currentTimeMillis();
        // the member moved between two audio channels in the same guild
        if (joinedChannel != null && leftChannel != null) {
            Long joinedTime = usersJoinedMap.get(member.getIdLong());
            EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getUser());
            if (economyUser == null) {
                return;
            }
            economyUser.addVoiceTime(currentTime - joinedTime);
            usersJoinedMap.put(member.getIdLong(), currentTime);
            return;
        }
        // the member joined an audio channel
        if (joinedChannel != null) {
            usersJoinedMap.put(member.getIdLong(), currentTime);
            return;
        }
        // the member left an audio channel
        if (leftChannel != null) {
            Long joinedTime = usersJoinedMap.get(member.getIdLong());
            EconomyUser economyUser = EconomyManager.getInstance().getEconomyUser(member.getUser());
            if (economyUser == null) {
                return;
            }
            economyUser.addVoiceTime(currentTime - joinedTime);
            usersJoinedMap.remove(member.getIdLong());
        }
    }
}
