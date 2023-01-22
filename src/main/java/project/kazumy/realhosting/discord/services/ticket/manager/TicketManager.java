package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Data;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.util.function.Consumer;

@Data(staticConstructor = "of")
public class TicketManager {

    private final DiscordMain discordMain;

    public void makeTicket(Ticket ticket, Consumer<TextChannel> onSuccess) {

    }
}
