package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.configuration.basic.GuildValue;
import project.kazumy.realhosting.discord.configuration.basic.TicketValue;
import project.kazumy.realhosting.discord.configuration.embed.TicketEmbedValue;
import project.kazumy.realhosting.discord.configuration.menu.TicketMenuValue;
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
    private JDA jda;

    public TicketManager() {
        super(3L);
    }

    public void sendTicketMenu() {
        val textChannel = jda.getTextChannelById(TicketValue.get(TicketValue::channel));
        textChannel.retrievePinnedMessages().queue(message -> {
            if (message.stream().anyMatch(embed -> embed.getAuthor().isBot()))
                return;
            textChannel.sendMessageEmbeds(TicketEmbedValue.instance().toEmbed()).addActionRow(TicketMenuValue.instance().toMenu("menu-ticket")).queue();
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
                        Logger.getGlobal().severe("Erro ao carregar o ticket " + file.getName() + "! O canal não foi encontrado.");
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
                    Logger.getGlobal().info("Ticket " + file.getName() + " carregado para a memória.");
                });
            } catch (Exception ex) {
                Logger.getGlobal().severe(String.format("Não foi possível carregar o ticket %s", config.getString("ticket.id")));
            }
        });
    }

    @Override
    public BaseService service(JDA jda) {
        this.jda = jda;

        //sendTicketMenu();
        loadOpenedTicket(jda.getGuildById(GuildValue.get(GuildValue::id)));

        return this;
    }
}
