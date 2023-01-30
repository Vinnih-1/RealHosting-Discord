package project.kazumy.realhosting.discord.listener.interactions.menu;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import project.kazumy.realhosting.configuration.menu.PlanMenuValue;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class UpgradePlanTicketMenuInteraction extends InteractionService<StringSelectInteractionEvent> {

    private final DiscordMain discordMain;

    public UpgradePlanTicketMenuInteraction(DiscordMain discordMain) {
        super("upgrade-plan-menu", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(StringSelectInteractionEvent event) {
        event.editSelectMenu(event.getSelectMenu().createCopy().setDefaultValues("").build()).queue();
        val plan = discordMain.getPlanManager().getPlanById(event.getSelectedOptions().get(0).getValue());
        val embed = new EmbedBuilder(event.getMessage().getEmbeds().get(0));
        embed.getFields().clear();
        embed.addField("Dono", plan.getOwner(), true);
        embed.addField("Identificador", plan.getId(), true);
        event.getChannel().sendMessageEmbeds(embed.build()).addActionRow(PlanMenuValue.instance().toMenu("purchase-upgrade-menu")).queue();
    }
}