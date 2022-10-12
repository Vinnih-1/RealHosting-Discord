package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@SuppressWarnings("all")
public class TicketManager extends BaseService {

    private final Map<String, Ticket> ticketMap = new HashMap<>();
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
                    .addOption("Adquirir Serviços", "comprar", "Contrate um dos planos do nosso serviço de hospedagem.", Emoji.fromUnicode("U+1F4B5"))
                    .addOption("Não... sério, preciso de ajuda!", "técnico", "Contate-nos pra solucionar problemas em seu serviço.", Emoji.fromUnicode("U+1F4BB"))
                    .addOption("Esclarecer Dúvidas", "dúvida", "Acabe com aquela pulga atrás da orelha.", Emoji.fromUnicode("U+1F4A1"))
                    .addOption("Crítica Construtiva", "sugestão", "Dê uma crítica construtiva em algo que podemos melhorar.", Emoji.fromUnicode("U+1F48C"))
                    .build();

            textChannel.sendMessageEmbeds(embed.build()).addActionRow(menu).queue();
        });
    }

    public boolean hasOpenedTicket(Member member) {
        return this.ticketMap.containsKey(member.getId());
    }

    @SneakyThrows
    public void recordOpenedTicket(Ticket ticket) {
        val config = new Configuration("services/tickets/opened-ticket/"+ ticket.getId() +".yml")
                .buildIfNotExists();
        config.set("ticket.id", ticket.getId());
        config.set("ticket.author", ticket.getAuthor().getId());
        config.set("ticket.channelId", ticket.getChannelId());
        config.set("ticket.category", ticket.getCategory());
        config.set("ticket.history", ticket.getHistory());
        config.save();
    }

    @SneakyThrows
    public void destroyRecordedTicket(Ticket ticket) {
        val config = new Configuration("services/tickets/opened-ticket/" + ticket.getId() + ".yml")
                .buildIfNotExists();
        config.deleteFile();
    }

    public void loadOpenedTicket() {
        val ticketFile = new File("services/tickets/opened-ticket/");

        if (!ticketFile.exists()) return;

        Arrays.asList(ticketFile.listFiles()).forEach(file -> {
            val config = new Configuration("services/tickets/opened-ticket/" + file.getName())
                    .buildIfNotExists();

            val member = jda.getGuilds()
                    .stream()
                    .filter(guild -> guild.getId().equals(this.config.getString("bot.guild.id")))
                    .findFirst()
                    .get()
                    .getMemberById(config.getString("ticket.author"));

            if (member == null) {
                Logger.getGlobal().severe("Erro ao carregar o ticket " + file.getName() + "! O autor não foi encontrado.");
                return;
            }

            if (jda.getTextChannelById(config.getString("ticket.channelId")) == null) {
                Logger.getGlobal().severe("Erro ao carregar o ticket " + file.getName() + "! O canal não foi encontrado.");
                return;
            }
            
            this.ticketMap.put(config.getString("ticket.author"),
                    Ticket.builder()
                            .id(config.getString("ticket.id"))
                            .channelId(config.getString("ticket.channelId"))
                            .category(config.getString("ticket.category"))
                            .author(member)
                            .build());

           Logger.getGlobal().info("Ticket " + file.getName() + " carregado para a memória.");
        });
    }

    @Override
    public BaseService loadService(JDA jda, Configuration config) {
        this.jda = jda;
        this.config = config;

        setDefaultTicketMessage();
        sendTicketMenu();
        loadOpenedTicket();

        return this;
    }
}
