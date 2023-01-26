package project.kazumy.realhosting.discord.listener.interactions.button;

import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class AddUserTicketButton extends InteractionService<ButtonInteractionEvent> {

    private final DiscordMain discordMain;

    public AddUserTicketButton(DiscordMain discordMain) {
        super("user-ticket-button", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        val modal = Modal.create("adduser-ticket-modal", "Adicione usuários no Ticket")
                .addActionRow(TextInput.create("user-id", "ID do usuário", TextInputStyle.SHORT)
                        .setPlaceholder("Insira o ID do usuário")
                        .setMinLength(18)
                        .setMaxLength(18)
                        .build())
                .build();

        event.replyModal(modal).queue();
    }
}
