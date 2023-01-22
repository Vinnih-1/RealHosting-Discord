package project.kazumy.realhosting.discord.services.ticket.impl;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.services.ticket.Ticket;
import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TicketImpl implements Ticket {

    private Long id;
    private String name, owner;
    private TicketCategory category;
    private List<String> participants;
    private List<Message> history;
    private LocalDateTime creation, cancellation;
}