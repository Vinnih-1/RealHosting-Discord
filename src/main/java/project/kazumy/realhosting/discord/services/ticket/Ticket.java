package project.kazumy.realhosting.discord.services.ticket;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.configuration.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Data
@Builder()
public class Ticket {

    private String id, category, channelId;

    private Member author;

    private boolean isClosed;

    private List<Message> history;

    @SneakyThrows
    public void saveTicket() {
        val config = new Configuration("services/tickets/" + author.getId() + "/" + author.getEffectiveName() + "-" + id + ".yml")
                .buildIfNotExists();
        Collections.reverse(history);
        history.forEach(message -> {
            val date = message.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            config.set("ID: " + message.getId() + " " + date + " - " + message.getAuthor().getAsTag() + ": ", message.getContentStripped());
        });
        config.save();
    }
}
