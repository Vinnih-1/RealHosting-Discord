package project.kazumy.realhosting.discord.listener.interactions;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class TermsAgreeButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public TermsAgreeButtonInteraction() {
        super("terms-agree");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        event.deferReply(true).setContent("Você concordou com os nossos termos!").queue();
        event.getMessage().delete().queue();

        InitBot.paymentManager.sendServerMenu(event.getChannel().asTextChannel(), InitBot.config);
    }
}
