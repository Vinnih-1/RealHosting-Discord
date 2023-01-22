package project.kazumy.realhosting.discord.listener.interactions.menu;

import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class TicketMenuInteraction extends InteractionService<SelectMenuInteraction> {

    private final DiscordMain discordMain;

    public TicketMenuInteraction(DiscordMain discordMain) {
        super("menu-ticket", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(SelectMenuInteraction event) {

    }
}