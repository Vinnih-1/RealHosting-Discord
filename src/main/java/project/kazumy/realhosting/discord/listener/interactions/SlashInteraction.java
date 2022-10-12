package project.kazumy.realhosting.discord.listener.interactions;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class SlashInteraction extends InteractionService<SlashCommandInteractionEvent> {

    public SlashInteraction() {
        super("slash.user.command");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}
