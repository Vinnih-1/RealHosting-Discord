package project.kazumy.realhosting.discord.commands.prefix;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;

public class SellMenuCommand extends BasePrefixCommand {
    public SellMenuCommand() {
        super("sellmenu", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        InitBot.paymentManager.sendBuyMenu(message.getChannel().asTextChannel(), InitBot.config, "price-menu");
    }
}
