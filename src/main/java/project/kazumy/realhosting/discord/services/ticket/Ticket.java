package project.kazumy.realhosting.discord.services.ticket;

import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;

import java.time.LocalDateTime;
import java.util.List;

public interface Ticket {

    Integer getId();

    String getName();

    String getOwner();

    TicketCategory getCategory();

    String getChatId();

    boolean isClosed();

    List<String> getParticipants();

    List<String> getHistory();

    void setHistory(List<String> history);

    LocalDateTime getCreation();

    LocalDateTime getCancellation();
}