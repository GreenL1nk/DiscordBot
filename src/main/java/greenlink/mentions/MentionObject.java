package greenlink.mentions;

import net.dv8tion.jda.api.entities.User;

/**
 * @author t.me/GreenL1nk
 * 29.01.2024
 */
public class MentionObject {

    User user;
    MentionType mentionType;
    String channelId;
    String guildId;
    long timeMention;

    public MentionObject(User user, MentionType mentionType, String channelId, String guildId, long timeMention) {
        this.user = user;
        this.mentionType = mentionType;
        this.channelId = channelId;
        this.guildId = guildId;
        this.timeMention = timeMention;
    }
}
