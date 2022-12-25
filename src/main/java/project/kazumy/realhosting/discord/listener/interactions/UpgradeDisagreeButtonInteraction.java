package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class UpgradeDisagreeButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public UpgradeDisagreeButtonInteraction() {
        super("upgrade-disagree");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        val args = event.getButton().getId().split(";");
        val userId = args[1];

        if (!event.getUser().getId().equals(userId)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Você não pode interagir com uma requisição não feita por você mesmo!")
                    .build()).queue();
            return;
        }
        event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS, success -> {
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setDescription(String.format("<@%s>, requisição cancelada com êxito.", event.getUser().getId()))
                    .build()).queue();
        });
    }
}
