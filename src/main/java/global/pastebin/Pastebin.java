package global.pastebin;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;

public record Pastebin(String content, ArrayList<MessageEmbed> embeds) {

}
