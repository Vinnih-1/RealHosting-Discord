package project.kazumy.realhosting.discord.listener.interactions.modal;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AnnounceCommandModalInteraction extends InteractionService<ModalInteractionEvent> {

    public AnnounceCommandModalInteraction(DiscordMain discordMain) {
        super("announce-command-modal", discordMain);
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a");

        event.deferReply(true).addEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription("Enviando sua mensagem de anúncio...")
                .build()).queue();

        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.ORANGE)
                        .setTitle(event.getValue("title").getAsString())
                        .setDescription(event.getValue("description").getAsString())
                        .setFooter(String.format("RealHosting © • Hoje às %s",
                                formatter.format(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))),
                                event.getJDA().getSelfUser().getAvatarUrl())
                .build()).queue();
    }
}
