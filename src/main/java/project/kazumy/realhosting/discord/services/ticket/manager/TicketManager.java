package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.ticket.BaseTicket;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@SuppressWarnings("all")
public class TicketManager extends BaseService {

    private final Set<BaseTicket> ticket = new HashSet<>();
    private final Configuration config = InitBot.config;

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
            embed.setColor(Color.YELLOW);
            val configSection = config.getConfigurationSection(section + "fields").getKeys(true);

            for (int i = 1; i <= configSection.size(); i++) {
                val name = config.getString(section + "fields." + i + ".name");
                val value = config.getString(section + "fields." + i + ".value");
                val inline = config.get(section + "fields." + i + ".inline");

                if (name == null || value == null || inline == null) continue;
                embed.addField(name, value, Boolean.parseBoolean(String.valueOf(inline)));
            }
            embed.setDescription(description);
            embed.setThumbnail(config.getString(section + "thumbnail"));
            embed.setAuthor("RealHosting", config.getString(section + "site"), config.getString(section + "thumbnail"));
            embed.setFooter(config.getString(section + "footer"), config.getString(section + "thumbnail"));

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
        return this.ticket.stream()
                .anyMatch(ticket -> ticket.getAuthor().getUser().getId().equals(member.getUser().getId()));
    }

    public void closeTicket(Ticket ticket) {
        System.out.println(this.ticket.remove(ticket));
    }

    public void openTicket(Ticket ticket) {
        val category = this.jda.getCategoryById(this.config.getString("bot.guild.ticket.ticket-category-id"));

        category.createTextChannel(ticket.getAuthor().getEffectiveName())
                .addMemberPermissionOverride(
                        ticket.getAuthor().getIdLong(),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        Arrays.asList(Permission.ADMINISTRATOR)
                ).queue(channel -> {
                    channel.sendMessage("This is my first ticket system! :)").queue();
        });
        this.ticket.add(ticket);
    }

    public void loadOpenedTicket() {
    }

    @Override
    public BaseService loadService(JDA jda) {
        this.jda = jda;

        setDefaultTicketMessage();
        sendTicketMenu();

        return this;
    }
}
