package project.kazumy.realhosting.discord.services.ticket;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.configuration.Configuration;

import java.util.List;

@Data
@Builder(builderMethodName = "builder", builderClassName = "build")
public class BaseTicket {

    private String id, category;

    private Member author;

    private boolean isClosed;

    private List<Message> history;

    public void saveTicket() {
        val config = new Configuration("services/tickets/" + author.getId() + "/" + author.getNickname() + "-" + id + ".yml")
                .buildIfNotExists();
        history.forEach(message -> config.addDefault(message.getAuthor().getAsTag() + ": ", message.getContentStripped()));
    }


}
