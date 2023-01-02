package project.kazumy.realhosting.discord.commands.prefix;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;
import project.kazumy.realhosting.discord.configuration.embed.PaymentEmbedValue;
import project.kazumy.realhosting.discord.configuration.menu.PlanMenuValue;

public class SellMenuCommand extends BasePrefixCommand {
    public SellMenuCommand() {
        super("sellmenu", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        message.getChannel().sendMessageEmbeds(PaymentEmbedValue.get(PaymentEmbedValue::toEmbed))
                .addActionRow(PlanMenuValue.instance().toMenu("price-menu"));
    }
}
