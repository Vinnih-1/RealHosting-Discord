package project.kazumy.realhosting.discord.commands.slash;

import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;

import java.util.List;

public class CreateCouponCommand extends BaseSlashCommand {

    private final DiscordMain discordMain;

    public CreateCouponCommand(DiscordMain discordMain) {
        super("cupom",
                "Comando para criar cupons de desconto",
                null,
                discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;
        val modal = Modal.create("create-coupon-modal", "Criar um Cupom")
                .addActionRow(TextInput.create("name", "Nome do Cupom", TextInputStyle.SHORT).build())
                .addActionRow(TextInput.create("limits", "Limite de Uso", TextInputStyle.SHORT).build())
                .addActionRow(TextInput.create("percentage", "Porcentagem de Desconto", TextInputStyle.SHORT).build())
                .addActionRow(TextInput.create("expiration", "Expiração do Cupom", TextInputStyle.SHORT).build())
                .build();
        event.replyModal(modal).queue();
    }
}
