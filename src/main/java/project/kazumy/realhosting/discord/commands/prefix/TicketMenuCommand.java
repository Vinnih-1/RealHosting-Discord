package project.kazumy.realhosting.discord.commands.prefix;

import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.configuration.embed.TicketEmbedValue;
import project.kazumy.realhosting.configuration.menu.TicketMenuValue;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;

public class TicketMenuCommand extends BasePrefixCommand {

    public TicketMenuCommand() {
        super("ticketmenu", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        val channel = message.getChannel().asTextChannel();
        channel.sendMessageEmbeds(TicketEmbedValue.get(TicketEmbedValue::toEmbed))
                .addActionRow(TicketMenuValue.instance().toMenu("menu-ticket")).queue();
    }
}
