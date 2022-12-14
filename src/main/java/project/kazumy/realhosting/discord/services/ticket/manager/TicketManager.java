package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ContextException;
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

    public TicketManager() {
        super(3L);
    }

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
                    .addOption("Adquirir Servi??os", "comprar", "Contrate um dos planos do nosso servi??o de hospedagem.", Emoji.fromUnicode("U+1F4B5"))
                    .addOption("Aprimorar Servi??os", "aprimorar", "Aprimore seus servi??os para um melhor aproveitamento.", Emoji.fromUnicode("U+1F680"))
                    .addOption("N??o... s??rio, preciso de ajuda!", "t??cnico", "Contate-nos pra solucionar problemas em seu servi??o.", Emoji.fromUnicode("U+1F4BB"))
                    .addOption("Esclarecer D??vidas", "d??vida", "Acabe com aquela pulga atr??s da orelha.", Emoji.fromUnicode("U+1F4A1"))
                    .addOption("Cr??tica Construtiva", "sugest??o", "D?? uma cr??tica construtiva em algo que podemos melhorar.", Emoji.fromUnicode("U+1F48C"))
                    .build();

            textChannel.sendMessageEmbeds(embed.build()).addActionRow(menu).queue();
        });
    }

    public Ticket getTicketByTextChannelId(String textChannelId) {
        return getTicketMap().entrySet()
                .stream()
                .filter(value -> value.getValue().getChannelId().equals(textChannelId))
                .findFirst().get().getValue();
    }

    public String getTicketIdByUserId(String userId) {
        return getTicketByUserId(userId).getId();
    }

    public Ticket getTicketByUserId(String userId) {
        return getTicketMap().entrySet()
                .stream()
                .filter(value -> value.getValue().getAuthor().getId().equals(userId))
                .findFirst().get().getValue();
    }

    public Ticket getTicketById(String id) {
        return getTicketMap().entrySet()
                .stream().filter(value -> value.getValue().getId().equals(id))
                .findFirst().get().getValue();
    }

    public boolean hasOpenedTicket(String id) {
        return getTicketMap().entrySet()
                .stream().anyMatch(value -> value.getValue().getId().equals(id));
    }

    public boolean hasOpenedTicketByChannelId(String channelId) {
        return getTicketMap().entrySet()
                .stream().anyMatch(value -> value.getValue().getChannelId().equals(channelId));
    }

    public boolean hasOpenedTicket(Member member) {
        return this.ticketMap.containsKey(member.getId());
    }

    @SneakyThrows
    public void recordOpenedTicket(Ticket ticket) {
        val config = new Configuration("services/tickets/opened-ticket/" + ticket.getAuthor().getAsTag() + ".yml")
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
        val config = new Configuration("services/tickets/opened-ticket/" + ticket.getAuthor().getAsTag() + ".yml")
                .buildIfNotExists()
                ;
        new File("services/tickets/opened-ticket/" + ticket.getChannelId() + ".png")
                .delete();

        config.deleteFile();
    }

    public void loadOpenedTicket(Guild guild) {
        val ticketFolder = new File("services/tickets/opened-ticket/");

        if (!ticketFolder.exists()) return;

        Arrays.asList(ticketFolder.listFiles())
                .stream()
                .filter(file -> file.getName().endsWith(".yml"))
                .forEach(file -> {
            val config = new Configuration("services/tickets/opened-ticket/" + file.getName())
                    .buildIfNotExists();
            try {
                jda.retrieveUserById(config.getString("ticket.author")).queue(user -> {
                    if (jda.getTextChannelById(config.getString("ticket.channelId")) == null) {
                        Logger.getGlobal().severe("Erro ao carregar o ticket " + file.getName() + "! O canal n??o foi encontrado.");
                        return;
                    }
                    val ticket = Ticket.builder()
                            .id(config.getString("ticket.id"))
                            .channelId(config.getString("ticket.channelId"))
                            .category(config.getString("ticket.category"))
                            .author(user)
                            .build();

                    ticket.setConfig(ticket.getOpenedTicketConfig());
                    this.ticketMap.put(config.getString("ticket.author"), ticket);
                    Logger.getGlobal().info("Ticket " + file.getName() + " carregado para a mem??ria.");
                });
            } catch (Exception ex) {
                Logger.getGlobal().severe(String.format("N??o foi poss??vel carregar o ticket %s", config.getString("ticket.id")));
            }
        });
    }

    @Override
    public BaseService service(JDA jda, Configuration config) {
        this.jda = jda;
        this.config = config;

        setDefaultTicketMessage();
        sendTicketMenu();
        loadOpenedTicket(jda.getGuildById("832601856403701771"));

        return this;
    }
}
