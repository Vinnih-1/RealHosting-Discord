package project.kazumy.realhosting.discord.commands;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;
import project.kazumy.realhosting.discord.services.payment.plan.PaymentIntent;

import java.awt.*;
import java.util.Arrays;

public class UpgradePlanCommand extends BaseSlashCommand {

    public UpgradePlanCommand() {
        super("aprimorar",
                "Utilize para aprimorar um plano que já existe",
                Arrays.asList(new OptionData(OptionType.STRING, "plano", "ID do plano a ser aprimorado", true)));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (true) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setDescription("Este comando está sendo desenvolvido, você será alertado quando tudo estiver pronto. :smiley_cat:")
                    .build()).queue();
            return;
        }
        val paymentManager = InitBot.paymentManager;
        val panelManager = InitBot.panelManager;
        val planString = event.getOption("plano").getAsString();

        if (!event.getChannel().getName().contains("aprimorar")) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Este comando precisa ser utilizado em um ticket de aprimoramento!")
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

        if (!plan.getPlanData().getUserId().equals(event.getUser().getId())) {
            event.deferReply().addEmbeds(new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setDescription("Você está tentando aprimorar um plano que não é seu, tem certeza que deseja realizar este processo?")
                    .build()).addActionRow(Button.primary(String.format("upgrade-agree;%s;%s", event.getUser().getId(), plan.getPlanData().getPlanId()), "Concordo").withEmoji(Emoji.fromUnicode("U+2705")),
                    Button.danger(String.format("upgrade-disagree;%s;%s", event.getUser().getId(), plan.getPlanData().getPlanId()), "Não concordo").withEmoji(Emoji.fromUnicode("U+2716"))).queue();
            return;
        }
        paymentManager.sendBuyMenu(event.getChannel().asTextChannel(), InitBot.config, "upgrade-menu");
    }
}
