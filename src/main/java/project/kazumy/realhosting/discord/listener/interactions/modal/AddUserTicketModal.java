package project.kazumy.realhosting.discord.listener.interactions.modal;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;

public class AddUserTicketModal extends InteractionService<ModalInteractionEvent> {

    private final DiscordMain discordMain;

    public AddUserTicketModal(DiscordMain discordMain) {
        super("adduser-ticket-modal", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        val ticket = discordMain.getTicketManager().findTicketByChatId(event.getChannel().getId());

        if (ticket.getParticipants().contains(event.getValue("user-id").getAsString())) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription(":x: **|** Você não pode adicionar uma pessoa que já está neste ticket!")
                    .build()).queue();
            return;
        }
        event.getGuild().loadMembers()
                .onSuccess(members -> members.stream()
                        .filter(member -> member.getId().equals(event.getValue("user-id").getAsString())).findFirst()
                        .ifPresentOrElse(member -> {
                            discordMain.getTicketManager().addParticipant(ticket, event.getChannel().asTextChannel(), member);
                            event.deferReply().addEmbeds(new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setDescription(String.format(":white_check_mark: **PERFEITO** | Você adicionou o usuário <@%s> ao ticket.", member.getId()))
                                    .build()).queue();
                        }, () -> {
                            event.deferReply(true).addEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setDescription(":x: **|** Este jogador não está presente neste discord!")
                                    .build()).queue();
                        }));
    }
}
