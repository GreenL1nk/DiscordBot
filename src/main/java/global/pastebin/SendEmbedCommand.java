package global.pastebin;

import global.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class SendEmbedCommand extends SlashCommand {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (!isAdmin(member, event)) return;

        String pasteId = event.getOptions().get(0).getAsString().replaceAll("https://pastebin.com/", "");
        GuildMessageChannel textChannel = event.getOptions().get(1).getAsChannel().asGuildMessageChannel();

        Pastebin pastebin = PastebinAPI.getPaste(pasteId);
        if (pastebin == null) return;
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder().setEmbeds(pastebin.embeds()).setContent(pastebin.content());
        textChannel.sendMessage(messageCreateBuilder.build()).queue(message -> {
            event.deferReply().setEphemeral(true).setContent(String.format("Сообщение успешно отправлено в <#%s>", textChannel.getId())).queue();
        });
    }

    @Override
    public void updateOptions() {
        options.add(new OptionData(OptionType.STRING, "url", "ссылка", true));
        options.add(new OptionData(OptionType.CHANNEL, "channel", "канал", true));
    }

    @Override
    public String getName() {
        return "embed";
    }

    @Override
    public String getDescription() {
        return "отправляет embed";
    }
}
