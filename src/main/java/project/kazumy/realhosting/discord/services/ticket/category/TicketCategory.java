package project.kazumy.realhosting.discord.services.ticket.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import project.kazumy.realhosting.configuration.embed.ServerEmbedValue;
import project.kazumy.realhosting.configuration.embed.SupportTicketEmbedValue;
import project.kazumy.realhosting.configuration.embed.UpgradeTicketEmbedValue;
import project.kazumy.realhosting.configuration.menu.ServerMenuValue;

@AllArgsConstructor
public enum TicketCategory {

    PURCHASE(
            "comprar",
            ServerEmbedValue.get(ServerEmbedValue::toEmbed),
            ServerMenuValue.instance().toMenu("purchase-server-menu")
    ),

    UPGRADE(
            "aprimorar",
            UpgradeTicketEmbedValue.get(UpgradeTicketEmbedValue::toEmbed),
            null
    ),

    SUPPORT(
            "suporte",
            SupportTicketEmbedValue.get(SupportTicketEmbedValue::toEmbed),
            null
            );

    @Getter private String name;
    @Getter private MessageEmbed embed;
    @Getter private SelectMenu menu;
}