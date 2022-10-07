package project.kazumy.realhosting.discord.services.ticket;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class Ticket extends BaseTicket {

    Ticket(String id, String category, Member author, boolean isClosed, List<Message> history) {
        super(id, category, author, isClosed, history);
    }
}
