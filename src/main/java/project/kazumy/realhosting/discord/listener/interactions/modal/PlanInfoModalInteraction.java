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

public class PlanInfoModalInteraction extends InteractionService<ModalInteractionEvent> {

    private final DiscordMain discordMain;

    public PlanInfoModalInteraction(DiscordMain discordMain) {
        super("plan-info-modal", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        val plan = discordMain.getPlanManager().getPlanById(event.getValue("plan").getAsString());

        if (plan == null) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription(String.format(":x: **|** <@%s>, não consegui encontrar nenhum plano com este ID", event.getMember().getId()))
                    .build()).queue();
            return;
        }
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setAuthor(event.getUser().getAsTag(), "https://app.realhosting.com.br",
                event.getUser().getAvatarUrl());
        embed.setTitle("[INFORMAÇÕES DO PLANO]");
        embed.setFooter(String.format("RealHosting © • Hoje às %s",
                        formatter.format(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))),
                event.getJDA().getSelfUser().getAvatarUrl());
        embed.addField("Autor", plan.getOwner(), true);
        embed.addField("Plano", plan.getServerType().toString(), true);
        embed.addField("ID do Plano", plan.getId(), true);
        embed.addField("Data de Criação", plan.getCreation().format(formatter), true);
        embed.addField("Data de Pagamento", plan.getPayment().format(formatter), true);
        embed.addField("Data de Expiração", plan.getExpiration().format(formatter), true);
        embed.addBlankField(false);
        embed.addField("Intenção de Pagamento", plan.getPaymentIntent().toString(), true);
        embed.addField("Referência Externa", plan.getId(), true);

        event.deferReply().addEmbeds(embed.build()).queue();
    }
}
