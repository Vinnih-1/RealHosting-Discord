package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;

public class UpgradePlanButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public UpgradePlanButtonInteraction() {
        super("upgrade");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        val paymentManager = InitBot.paymentManager;
        val panelManager = InitBot.panelManager;
        event.getMessage().getEmbeds().get(0).getFields().stream()
                .filter(field -> field.getName().equals("ID do Plano"))
                .findFirst()
                .ifPresentOrElse(field -> {
                    if (!paymentManager.hasPlanByPlanId(field.getValue())) {
                        event.deferReply(true).addEmbeds(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription("Este plano aparentemente não existe mais!")
                                .build()).queue();
                        return;
                    }
                    val plan = paymentManager.getPlanById(field.getValue());

                    if (!event.getUser().getId().equals(plan.getPlanData().getUserId())) {
                        event.deferReply(true).addEmbeds(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription("Não é possível interagir com planos que não são de sua autoria!")
                                .build()).queue();
                        return;
                    }

                    if (!panelManager.serverExistsByPlanId(field.getValue())) {
                        event.deferReply(true).addEmbeds(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription("Não foi possível encontrar um servidor com este identificador!")
                                .build()).queue();
                        return;
                    }

                    event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setDescription("Para aprimorar um plano, utilize o comando `/aprimorar`.")
                            .build()).queue();
                }, () -> {
                    event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Não consegui encontrar o ID do Plano desta embed!")
                            .build()).queue();
                });
    }
}
