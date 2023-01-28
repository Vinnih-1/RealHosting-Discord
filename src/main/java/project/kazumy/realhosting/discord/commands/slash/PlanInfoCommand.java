package project.kazumy.realhosting.discord.commands.slash;

import lombok.val;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;

public class PlanInfoCommand extends BaseSlashCommand {

    public PlanInfoCommand(DiscordMain discordMain) {
        super("plano",
                "Utilize este comando para verificar seu plano.",
                null,
                discordMain);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        val modal = Modal.create("plan-info-modal", "Informação do Plano")
                .addActionRow(TextInput.create("plan", "ID do Plano", TextInputStyle.SHORT)
                        .setPlaceholder("Informe o ID do seu Plano")
                        .setMaxLength(15)
                        .setMinLength(15)
                        .build())
                .build();
        event.replyModal(modal).queue();
    }
}
