package project.kazumy.realhosting.discord.services.ticket.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import project.kazumy.realhosting.discord.services.ticket.Ticket;
import project.kazumy.realhosting.discord.services.ticket.adapter.TicketAdapter;
import project.kazumy.realhosting.discord.services.ticket.utils.TicketUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data(staticConstructor = "of")
public class TicketRepository {

    private final SQLExecutor executor;

    public void save(Ticket ticket) {
        executor.updateQuery("INSERT INTO ticket (name, owner, category, chat, closed, feedback, participants, history, creation, cancellation)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", statement -> {
            statement.set(1, ticket.getName());
            statement.set(2, ticket.getOwner());
            statement.set(3, ticket.getCategory());
            statement.set(4, ticket.getChatId());
            statement.set(5, ticket.isClosed());
            statement.set(6, ticket.getFeedback());
            statement.set(7, TicketUtils.participantsToJson(ticket.getParticipants()));
            statement.set(8, TicketUtils.historyToJson(ticket.getHistory()));
            statement.set(9, ticket.getCreation().toString());
            statement.set(10, ticket.getCancellation() == null ? "" : ticket.getCancellation().toString());
        });
    }

    public void close(Ticket ticket) {
        executor.updateQuery("INSERT INTO ticket (id, name, owner, category, chat, closed, feedback, participants, history, creation, cancellation)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", statement -> {
            statement.set(1, ticket.getId());
            statement.set(2, ticket.getName());
            statement.set(3, ticket.getOwner());
            statement.set(4, ticket.getCategory());
            statement.set(5, ticket.getChatId());
            statement.set(6, false);
            statement.set(7, ticket.getFeedback());
            statement.set(8, TicketUtils.participantsToJson(ticket.getParticipants()));
            statement.set(9, TicketUtils.historyToJson(ticket.getHistory()));
            statement.set(10, ticket.getCreation().toString());
            statement.set(11, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).toString());
        });
    }

    public void feedback(Ticket ticket) {
        executor.updateQuery("INSERT INTO ticket (id, name, owner, category, chat, closed, feedback, participants, history, creation, cancellation)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", statement -> {
            statement.set(1, ticket.getId());
            statement.set(2, ticket.getName());
            statement.set(3, ticket.getOwner());
            statement.set(4, ticket.getCategory());
            statement.set(5, ticket.getChatId());
            statement.set(6, false);
            statement.set(7, ticket.getFeedback());
            statement.set(8, TicketUtils.participantsToJson(ticket.getParticipants()));
            statement.set(9, TicketUtils.historyToJson(ticket.getHistory()));
            statement.set(10, ticket.getCreation().toString());
            statement.set(11, LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).toString());
        });
    }

    public Ticket findByChatId(String chatId) {
        return executor.resultOneQuery("SELECT * FROM ticket WHERE chat = ?", statement -> {
            statement.set(1, chatId);
        }, TicketAdapter.class);
    }
}