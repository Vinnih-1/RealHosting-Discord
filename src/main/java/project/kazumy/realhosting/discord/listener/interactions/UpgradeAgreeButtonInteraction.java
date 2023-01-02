package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.menu.PlanMenuValue;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;

public class UpgradeAgreeButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public UpgradeAgreeButtonInteraction() {
        super("upgrade-agree");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        val paymentManager = InitBot.paymentManager;
        val panelManager = InitBot.panelManager;
        val args = event.getButton().getId().split(";");
        val userId = args[1];
        val planString = args[2];

        if (!event.getUser().getId().equals(userId)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Você não pode interagir com uma requisição não feita por você mesmo!")
                    .build()).queue();
            return;
        }

        if (!paymentManager.hasPlanByPlanId(planString)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Não consegui encontrar nenhum plano com este identificador!")
                    .build()).queue();
            return;
        }

        if (!panelManager.serverExistsByPlanId(planString)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Não consegui encontrar nenhum servidor com este identificador!")
                    .build()).queue();
            return;
        }
        val plan = paymentManager.getPlanById(planString);
        val embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(":white_check_mark: Menu de Aprimoramento de Planos")
                .addField("ID do Plano", plan.getPlanData().getPlanId(), true)
                .addField("Autor do Plano", plan.getPlanData().getUserAsTag(), true)
                .addField("Plano Atual", plan.getPlanType().toString(), true)
                .setDescription("Selecione qual plano você quer aprimorar.")
                .setThumbnail(plan.getPlanData().getLogo())
                .build();

        event.getMessage().delete().queue();
        event.getChannel().sendMessageEmbeds(embed).addActionRow(PlanMenuValue.instance().toMenu("upgrade-menu")).queue();
    }
}
