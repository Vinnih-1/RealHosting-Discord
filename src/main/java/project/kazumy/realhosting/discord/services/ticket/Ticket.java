package project.kazumy.realhosting.discord.services.ticket;

import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;

import java.time.LocalDateTime;
import java.util.List;

public interface Ticket {

    Long getId();

    String getName();

    String getOwner();

    TicketCategory getCategory();

    List<String> getParticipants();

    List<Message> getHistory();

    LocalDateTime getCreation();

    LocalDateTime getCancellation();
}