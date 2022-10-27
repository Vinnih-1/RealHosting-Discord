package project.kazumy.realhosting.discord.services.ticket;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.Configuration;

import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Builder()
public class Ticket {

    private String id, category, channelId;

    private Member author;

    private Configuration config;

    private List<Message> history;

    @SneakyThrows
    public void saveTicket() {
        val channel = InitBot.ticketManager.getJda().getTextChannelById(this.getChannelId());
        val chatHistory = MessageHistory.getHistoryFromBeginning(channel).submit();
        this.setHistory(new ArrayList<>(chatHistory.get().getRetrievedHistory()));

        InitBot.ticketManager.destroyRecordedTicket(this);
        InitBot.ticketManager.getTicketMap().remove(this.getAuthor().getId());

        val config = new Configuration("services/tickets/saved-ticket/" + author.getId() + "/" + author.getEffectiveName() + "-" + id + ".yml")
                .buildIfNotExists();

        Collections.reverse(history);

        history.forEach(message -> {
            val date = message.getTimeCreated()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.of("America/Sao_Paulo")));
            config.set("ID: " + message.getId() + " " + date + " - " + message.getAuthor().getAsTag() + ": ", message.getContentStripped());
        });
        config.save();
        channel.delete().queueAfter(10, TimeUnit.SECONDS);
    }

    public File getOpenedTicketFolder() {
        return new File("services/tickets/opened-ticket/");
    }

    public Configuration getOpenedTicketConfig() {
        return new Configuration("services/tickets/opened-ticket/" + this.getAuthor().getUser().getAsTag() + ".yml");
    }

    public TextChannel getTicketChannel(JDA jda) {
        return jda.getTextChannelById(this.getChannelId());
    }
}