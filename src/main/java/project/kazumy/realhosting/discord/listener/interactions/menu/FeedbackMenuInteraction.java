package project.kazumy.realhosting.discord.listener.interactions.menu;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class FeedbackMenuInteraction extends InteractionService<StringSelectInteractionEvent> {

    private final DiscordMain discordMain;

    public FeedbackMenuInteraction(DiscordMain discordMain) {
        super("ticket-feedback-menu", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(StringSelectInteractionEvent event) {
        val channelId = event.getMessage().getEmbeds().get(0).getFields().get(2).getValue();
        val selectedOption = event.getSelectedOptions().get(0);
        val ticket = discordMain.getTicketManager().findTicketByChatId(channelId);
        ticket.setFeedback(selectedOption.getEmoji().getFormatted() + " " + selectedOption.getLabel());
        discordMain.getTicketManager().saveFeedback(ticket);

        val modal = Modal.create("feedback-ticket-modal", "Avaliação de Atendimento")
                .addActionRow(
                        TextInput.create("feedback-text", "Avaliação", TextInputStyle.SHORT)
                                .setPlaceholder("Descreva a Avaliação")
                                .setMinLength(1)
                                .setMaxLength(100)
                                .build())
                .build();
        event.replyModal(modal).queue();
    }
}
