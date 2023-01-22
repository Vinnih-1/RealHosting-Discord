package project.kazumy.realhosting.discord.services.ticket.impl;

import lombok.*;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.services.ticket.Ticket;
import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TicketImpl implements Ticket {

    @EqualsAndHashCode.Include
    private Integer id;

    private String name, owner;
    @Setter private String chatId, feedback;
    private TicketCategory category;
    private boolean closed;
    private List<String> participants;
    private List<String> history;
    private LocalDateTime creation, cancellation;
}