package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class MenuTicketInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public MenuTicketInteraction() {
        super("menu-ticket");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {
        if (event.getMember() == null) return;

        if (InitBot.ticketManager.hasOpenedTicket(event.getMember())) {
            event.deferReply(true).setContent(":x: Feche o ticket atual para abrir outro!").queue();
            return;
        }

        val ticket = Ticket.builder()
                .author(event.getMember())
                .category(event.getSelectedOptions().get(0).getValue())
                .id(event.getUser().getAsTag()).build();

        val ticketManager = InitBot.ticketManager;

        val category = ticketManager.getJda().getCategoryById(ticketManager.getConfig().getString("bot.guild.ticket-category-id"));

        category.createTextChannel(ticket.getAuthor().getEffectiveName() + "-" + ticket.getCategory())
                .addMemberPermissionOverride(
                        ticket.getAuthor().getIdLong(),
                        Arrays.asList(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_SEND,
                                Permission.MESSAGE_ATTACH_FILES
                        ),
                        List.of(Permission.ADMINISTRATOR)
                ).queue(channel -> {
                    val embed = ticketManager.getConfig().getEmbedMessageFromConfig(ticketManager.getConfig(), "close-ticket");
                    embed.setColor(Color.RED);

                    channel.sendMessageEmbeds(embed.build()).addActionRow(Button.danger("close-ticket-button", Emoji.fromUnicode("U+2716"))).queue();
                    ticket.setChannelId(channel.getId());
                    ticketManager.recordOpenedTicket(ticket);
                });
        ticketManager.getTicketMap().put(ticket.getAuthor().getId(), ticket);

        event.deferReply(true).setContent(":incoming_envelope: Seu ticket está aberto para o uso!").queue();
    }
}
