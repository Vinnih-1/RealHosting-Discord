package project.kazumy.realhosting.discord.listener.interactions.menu;

import lombok.val;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;
import project.kazumy.realhosting.discord.services.ticket.impl.TicketImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class TicketMenuInteraction extends InteractionService<SelectMenuInteraction> {

    private final DiscordMain discordMain;

    public TicketMenuInteraction(DiscordMain discordMain) {
        super("menu-ticket", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(SelectMenuInteraction event) {
        val ticket = TicketImpl.builder()
                .name(event.getUser().getAsTag() + "-" + event.getSelectedOptions().get(0).getValue())
                .owner(event.getUser().getId())
                .closed(false)
                .creation(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .participants(new ArrayList<>())
                .history(new ArrayList<>())
                .category(TicketCategory.valueOf(event.getSelectedOptions().get(0).getValue().toUpperCase()))
                .build();

        discordMain.getTicketManager().makeTicket(ticket, success -> {
            ticket.setChatId(success.getId());
            ticket.getParticipants().add(ticket.getOwner());
        });
    }
}