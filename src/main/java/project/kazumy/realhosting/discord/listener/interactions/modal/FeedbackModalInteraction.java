package project.kazumy.realhosting.discord.listener.interactions.modal;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import project.kazumy.realhosting.configuration.basic.GuildValue;
import project.kazumy.realhosting.configuration.basic.TicketValue;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;

public class FeedbackModalInteraction extends InteractionService<ModalInteractionEvent> {

    private final DiscordMain discordMain;

    public FeedbackModalInteraction(DiscordMain discordMain) {
        super("feedback-ticket-modal", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        val channelId = event.getMessage().getEmbeds().get(0).getFields().get(2).getValue();
        val ticket = discordMain.getTicketManager().findTicketByChatId(channelId);

        discordMain.getJda().getGuildById(GuildValue.get(GuildValue::id))
                .getTextChannelById(TicketValue.get(TicketValue::feedbackChatId))
                .sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(ticket.getFeedback())
                        .setDescription(event.getValue("feedback-text").getAsString())
                        .addField("Participantes da Conversação", String.valueOf(ticket.getParticipants().size()), false)
                        .addField("Mensagens enviadas", String.valueOf(ticket.getHistory().size()), false)
                        .setFooter("Avaliação do usuário: " + event.getUser().getAsTag(), event.getUser().getAvatarUrl())
                        .build()).queue();

        event.getMessage().delete().queue();
        event.deferReply().addEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription(":white_check_mark: **|** Obrigado pela sua avaliação! É de grande importância para nós.")
                .build()).queue();
    }
}
