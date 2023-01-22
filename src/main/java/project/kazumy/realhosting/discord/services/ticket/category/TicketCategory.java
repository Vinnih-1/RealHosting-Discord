package project.kazumy.realhosting.discord.services.ticket.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TicketCategory {

    PURCHASE("comprar"), UPGRADE("aprimorar"), SUPPORT("suporte");

    @Getter private String name;
}
