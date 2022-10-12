package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("all")
public class TicketManager extends BaseService {

    private final Map<String, Ticket> ticket = new HashMap<>();
    private Configuration config;

    private JDA jda;

    @SneakyThrows
    public void setDefaultTicketMessage() {
        config.addDefaults(consumer -> {
            consumer.addDefault("bot.guild.ticket.title", "Title");
            consumer.addDefault("bot.guild.ticket.footer", "Footer");
            consumer.addDefault("bot.guild.ticket.description", "Hello World \\n Zumo was here!");
            consumer.addDefault("bot.guild.ticket.thumbnail", "thumbnail-url");
            consumer.addDefault("bot.guild.ticket.site", "site-url");
            consumer.addDefault("bot.guild.ticket.fields.1.name", "My Name is");
            consumer.addDefault("bot.guild.ticket.fields.1.value", "Zumo");
            consumer.addDefault("bot.guild.ticket.fields.1.inline", true);
            consumer.addDefault("bot.guild.ticket.fields.2.name", "My Job is");
            consumer.addDefault("bot.guild.ticket.fields.2.value", "Supporter of RealHosting");
            consumer.addDefault("bot.guild.ticket.fields.2.inline", true);

            consumer.addDefault("bot.guild.close-ticket.title", ":x: Do you want to close this ticket?");
            consumer.addDefault("bot.guild.close-ticket.footer", "Footer");
            consumer.addDefault("bot.guild.close-ticket.thumbnail", "thumbnail-url");
            consumer.addDefault("bot.guild.close-ticket.description", "Click on red button to close this ticket!");
            consumer.addDefault("bot.guild.close-ticket.site", "site-url");
            consumer.addDefault("bot.guild.close-ticket.fields.1.name", "My Name is");
            consumer.addDefault("bot.guild.close-ticket.fields.1.value", "Zumo");
            consumer.addDefault("bot.guild.close-ticket.fields.1.inline", true);
            consumer.addDefault("bot.guild.close-ticket.fields.2.name", "My Job is");
            consumer.addDefault("bot.guild.close-ticket.fields.2.value", "Supporter of RealHosting");
            consumer.addDefault("bot.guild.close-ticket.fields.2.inline", true);
        });
        config.save();
    }

    public void sendTicketMenu() {
        val section = "bot.guild.ticket.";

        val textChannel = jda.getTextChannelById(config.getString("bot.guild.ticket-chat-id"));
        textChannel.retrievePinnedMessages().queue(message -> {
            if (message.stream().anyMatch(embed -> embed.getAuthor().isBot()))
                return;

            val embed = config.getEmbedMessageFromConfig(this.config, "ticket");
            val menu = SelectMenu.create("menu-ticket")
                    .addOption("Adquirir Serviços", "real.buy", "Contrate um dos planos do nosso serviço de hospedagem.", Emoji.fromUnicode("U+1F4B5"))
                    .addOption("Não... sério, preciso de ajuda!", "real.support", "Contate-nos pra solucionar problemas em seu serviço.", Emoji.fromUnicode("U+1F4BB"))
                    .addOption("Esclarecer Dúvidas", "real.help", "Acabe com aquela pulga atrás da orelha.", Emoji.fromUnicode("U+1F4A1"))
                    .addOption("Crítica Construtiva", "real.suggest", "Dê uma crítica construtiva em algo que podemos melhorar.", Emoji.fromUnicode("U+1F48C"))
                    .build();

            textChannel.sendMessageEmbeds(embed.build()).addActionRow(menu).queue();
        });
    }

    public boolean hasOpenedTicket(Member member) {
        return this.ticket.containsKey(member.getId());
    }

    @SneakyThrows
    public void closeTicket(Ticket ticket) {
        val channel = jda.getTextChannelById(ticket.getChannelId());
        val history = MessageHistory.getHistoryFromBeginning(channel).submit();
        ticket.setHistory(new ArrayList<>(history.get().getRetrievedHistory()));
        ticket.saveTicket();
        channel.delete().queue();
        this.ticket.remove(ticket.getAuthor().getId());
    }

    @SneakyThrows
    public void openTicket(Ticket ticket) {
        val category = this.jda.getCategoryById(this.config.getString("bot.guild.ticket-category-id"));

        category.createTextChannel(ticket.getAuthor().getEffectiveName() + "-" + ticket.getCategory())
                .addMemberPermissionOverride(
                        ticket.getAuthor().getIdLong(),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Arrays.asList(Permission.ADMINISTRATOR)
                ).queue(channel -> {
                    val embed = config.getEmbedMessageFromConfig(this.config, "close-ticket");

                    channel.sendMessageEmbeds(embed.build()).addActionRow(Button.danger("close-ticket", Emoji.fromUnicode("U+2716"))).queue();
                    ticket.setChannelId(channel.getId());
        });
        this.ticket.put(ticket.getAuthor().getId(), ticket);
    }

    public void loadOpenedTicket() {
    }

    @Override
    public BaseService loadService(JDA jda, Configuration config) {
        this.jda = jda;
        this.config = config;

        setDefaultTicketMessage();
        sendTicketMenu();

        return this;
    }
}
