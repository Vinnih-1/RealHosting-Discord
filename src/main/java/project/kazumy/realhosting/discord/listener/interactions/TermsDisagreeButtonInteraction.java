package project.kazumy.realhosting.discord.listener.interactions;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class TermsDisagreeButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public TermsDisagreeButtonInteraction() {
        super("terms-disagree");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        event.deferReply().setContent("Você discordou dos nossos termos, caso você tenha alguma sugestão não " +
                "deixe de nos contatar para que possamos melhorar. Fecharemos o seu ticket em alguns segundos...").queue();

        this.initBotInstance().ticketManager.getTicketByTextChannelId(event.getChannel().getId()).saveTicket();
    }
}
