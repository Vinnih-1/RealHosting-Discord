package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.ticket.BaseTicket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class TicketManager extends BaseService {

    private final Set<BaseTicket> ticket = new HashSet<>();
    private final Configuration config = InitBot.config;

    private JDA jda;

    @SneakyThrows
    public void setDefaultTicketMessage() {
        config.addDefaults(consumer -> {
            consumer.addDefault("bot.guild.ticket.title", "Title");
            consumer.addDefault("bot.guild.ticket.footer", "Footer");
            consumer.addDefault("bot.guild.ticket.description", "Hello World\\nZumo was here!");
            consumer.addDefault("bot.guild.ticket.fields.1.name", "My Name is");
            consumer.addDefault("bot.guild.ticket.fields.1.value", "Zumo");
            consumer.addDefault("bot.guild.ticket.fields.1.inline", true);
            consumer.addDefault("bot.guild.ticket.fields.2.name", "My Job is");
            consumer.addDefault("bot.guild.ticket.fields.2.value", "Supporter of RealHosting");
            consumer.addDefault("bot.guild.ticket.fields.2.inline", true);
        });
        config.save();
    }

    public void sendTicketMenu() {
        val section = "bot.guild.ticket.";
        val title = config.getString(section + "title");
        val footer = config.getString(section + "footer");
        val description = config.getString(section + "description");

        val textChannel = jda.getTextChannelById(config.getString("bot.guild.ticket-chat-id"));
        textChannel.retrievePinnedMessages().queue(message -> {
            if (message.stream().anyMatch(embed -> embed.getAuthor().isBot()))
                return;

            val embed = new EmbedBuilder();
            embed.setTitle(title);
            embed.setFooter(footer);
            embed.setDescription(description);
            val configSection = config.getConfigurationSection(section + "fields").getKeys(true);

            for (int i = 1; i <= configSection.size(); i++) {
                val name = config.getString(section + "fields." + i + ".name");
                val value = config.getString(section + "fields." + i + ".value");
                val inline = config.get(section + "fields." + i + ".inline");

                if (name == null || value == null || inline == null) continue;
                embed.addField(name, value, Boolean.parseBoolean(String.valueOf(inline)));
            }

            textChannel.sendMessageEmbeds(embed.build()).queue();
        });
    }

    public void loadOpenedTicket() {
    }

    @Override
    public void loadService(JDA jda) {
        this.jda = jda;

        setDefaultTicketMessage();
        sendTicketMenu();
    }
}
