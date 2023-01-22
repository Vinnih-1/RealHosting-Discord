package project.kazumy.realhosting.discord.listener.interactions.button;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;

public class CancelTicketButton extends InteractionService<ButtonInteractionEvent> {

    private final DiscordMain discordMain;

    public CancelTicketButton(DiscordMain discordMain) {
        super("cancel-ticket-button", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        val ticket = discordMain.getTicketManager().getRepository().findByChatId(event.getChannel().getId());

        if (!ticket.getOwner().equals(event.getUser().getId())) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.GRAY)
                            .setTitle(":warning: | Você não pode fechar este ticket!")
                            .setDescription("Você não pode fechar um ticket que não pertence a você!")
                            .build())
                    .queue();
            return;
        }
        discordMain.getTicketManager().saveParticipants(ticket);
        discordMain.getTicketManager().saveMessages(ticket, success -> {
            discordMain.getTicketManager().cancelTicket(success, (unused) -> {
                event.deferReply().addEmbeds(new EmbedBuilder()
                        .setTitle(":warning: | Iniciando cancelamento de Ticket")
                        .setDescription(String.format("Salvando um total de `%s` mensagens de `%s` participantes.",
                                success.getHistory().size(), success.getParticipants().size()))
                        .setFooter("Todas as informações serão guardadas")
                        .build()).queue();
            });
        });
    }
}
