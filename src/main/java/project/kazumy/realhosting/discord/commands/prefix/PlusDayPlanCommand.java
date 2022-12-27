package project.kazumy.realhosting.discord.commands.prefix;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;

import java.awt.*;

public class PlusDayPlanCommand extends BasePrefixCommand {

    public PlusDayPlanCommand() {
        super("plusday", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        val paymentManager = InitBot.paymentManager;
        val panelManager = InitBot.panelManager;
        val channel = message.getChannel().asTextChannel();
        if (args.length < 2) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription(String.format("<@%s>, não encontrei nenhum servidor com este identificador!", member.getId()))
                    .build()).queue();
            return;
        }

        if (!paymentManager.hasPlanByPlanId(args[0])) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(String.format("<@%s>, não encontrei nenhum servidor com este identificador!", member.getId()))
                    .build()).queue();
            return;
        }

        if (!panelManager.serverExistsByPlanId(args[0])) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(String.format("<@%s>, não encontrei nenhum servidor com este identificador!", member.getId()))
                    .build()).queue();
            return;
        }

        try {
            val plan = paymentManager.getPlanById(args[0]);
            plan.setExpirationDate(plan.getExpirationDate().plusDays(Long.parseLong(args[1])));
            plan.saveConfig();

            channel.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setDescription(String.format("<@%s>, você adicionou **%s dia(s)** ao plano %s, pertencente ao usuário <@%s>",
                                    member.getId(), args[1], plan.getPlanData().getPlanId(), plan.getPlanData().getUserId()))
                    .build()).queue();
        } catch (NumberFormatException ex) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(String.format("<@%s>, você precisa especificar a quantidade de dias em formato de números!", member.getId()))
                    .build()).queue();
        }
    }
}
