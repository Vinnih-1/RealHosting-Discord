package project.kazumy.realhosting.discord.commands.slash;

import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;

public class GuildAnnounceCommand extends BaseSlashCommand {

    public GuildAnnounceCommand(DiscordMain discordMain) {
        super("anunciar",
                "Utilize este comando para fazer um anúncio",
                null,
                discordMain);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        val modal = Modal.create("announce-command-modal", "Criar um Anúncio")
                .addActionRow(TextInput.create("title", "Título", TextInputStyle.SHORT).build())
                .addActionRow(TextInput.create("description", "Descrição", TextInputStyle.PARAGRAPH).build())
                .build();
        event.replyModal(modal).queue();
    }
}
