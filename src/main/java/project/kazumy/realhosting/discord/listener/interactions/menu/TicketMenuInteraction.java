package project.kazumy.realhosting.discord.listener.interactions.menu;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import project.kazumy.realhosting.configuration.embed.CloseTicketEmbedValue;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;
import project.kazumy.realhosting.discord.services.ticket.impl.TicketImpl;
import project.kazumy.realhosting.model.panel.StageType;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;

public class TicketMenuInteraction extends InteractionService<StringSelectInteractionEvent> {

    private final DiscordMain discordMain;

    public TicketMenuInteraction(DiscordMain discordMain) {
        super("menu-ticket", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(StringSelectInteractionEvent event) {
        event.editSelectMenu(event.getSelectMenu().createCopy()
                .setDefaultValues(new ArrayList<>()).build()).queue();

        val category = TicketCategory.valueOf(event.getSelectedOptions().get(0).getValue().toUpperCase());
        val menu = StringSelectMenu.create("a");

        discordMain.getPlanManager().getPlanByClientId(event.getMember().getId()).stream()
                .filter(plan -> plan.getStageType() == StageType.ACTIVED ||
                        plan.getStageType() == StageType.SUSPENDED)
                .peek(plan -> plan.setPrePlan(discordMain.getPlanManager()
                        .getPrePlanByType(discordMain.getPlanManager().getPrePlanTypeFromPlanById(plan.getId()))))
                .forEach(plan -> menu.addOption(plan.getPrePlan().getTitle(),
                        plan.getId(),
                        plan.getId(),
                        plan.getPrePlan().getEmoji()));

        val ticket = TicketImpl.builder()
                .name(event.getUser().getAsTag() + "-" + event.getSelectedOptions().get(0).getValue())
                .owner(event.getUser().getId())
                .closed(false)
                .creation(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .participants(new ArrayList<>())
                .history(new ArrayList<>())
                .category(TicketCategory.valueOf(event.getSelectedOptions().get(0).getValue().toUpperCase()))
                .build();

        discordMain.getTicketManager().makeTicket(ticket, success -> {
            ticket.setChatId(success.getId());
            ticket.getParticipants().add(ticket.getOwner());

            success.sendMessageEmbeds(CloseTicketEmbedValue.get(CloseTicketEmbedValue::toEmbed))
                    .addActionRow(Button.success("user-ticket-button", "Adicionar participante").withEmoji(Emoji.fromUnicode("U+1F464")),
                            Button.danger("cancel-ticket-button", "Cancelar ticket").withEmoji(Emoji.fromUnicode("U+2716")))
                    .queue(message -> success.pinMessageById(message.getId()).queue());


            Arrays.stream(TicketCategory.values())
                    .filter(selected -> selected == category)
                    .map(selected -> category).findFirst()
                    .ifPresent(selected -> {
                        if (selected.getMenu() != null) success.sendMessageEmbeds(selected.getEmbed()).addActionRow(selected.getMenu()).queue();
                        if (menu.getOptions().isEmpty()) {
                            success.sendMessageEmbeds(category.getEmbed()).queue();
                            return;
                        }

                        switch (selected.getName()) {
                            case "aprimorar":
                                success.sendMessageEmbeds(category.getEmbed()).addActionRow(menu.setId("upgrade-plan-menu").build()).queue();
                                break;
                            case "renovar":
                                success.sendMessageEmbeds(category.getEmbed()).addActionRow(menu.setId("renew-plan-menu").build()).queue();
                                break;
                        }
                    });
        });
    }
}