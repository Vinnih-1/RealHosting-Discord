package project.kazumy.realhosting.discord.commands.prefix;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;

import java.awt.*;

public class ForceApprovalCommand extends BasePrefixCommand {

    private final DiscordMain discordMain;

    public ForceApprovalCommand(DiscordMain discordMain) {
        super("forceapproval", Permission.ADMINISTRATOR, discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        if (args.length < 1) return;
        val plan = discordMain.getPlanManager().getPlanByExternalId(args[0]);

        if (plan == null) {
            message.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(String.format("<@%s>, não consegui encontrar nenhum plano com este identificador!", message.getAuthor().getId()))
                    .build()).queue();
            return;
        }
        plan.setPaymentIntent(PaymentIntent.FORCE_APPROVAL);
        plan.setPrePlan(discordMain.getPlanManager().getPrePlanByType(discordMain.getPlanManager().getPrePlanTypeFromPlanById(plan.getId())));
        discordMain.getPlanManager().savePlan(plan);
    }
}
