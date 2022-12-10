package project.kazumy.realhosting.discord.commands;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;

import java.awt.*;

public class ForceApprovalCommand extends BasePrefixCommand {

    public ForceApprovalCommand() {
        super("forceapproval", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        val paymentManager = InitBot.paymentManager;
        val channel = message.getChannel().asTextChannel();
        message.delete().queue();

        if (args.length == 0) return;

        if (!paymentManager.hasPlanByPlanId(args[0])) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Não encontrei nenhum plano com este identificador!")
                    .build()).queue();
            return;
        }
        val plan = paymentManager.getPlanById(args[0]);
        plan.setForcedApproved(true);
        channel.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setDescription(String.format("Você forçou a aprovação do pagamento do plano `%s`", plan.getPlanData().getPlanId()))
                .build()).queue();
    }
}
