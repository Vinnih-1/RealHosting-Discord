package project.kazumy.realhosting.discord.commands.prefix;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CreatePromotionCodeCommand extends BasePrefixCommand {

    public CreatePromotionCodeCommand() {
        super("createcode", Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(Member member, Message message, String[] args) {
        val channel = message.getChannel().asTextChannel();
        if (args.length < 2) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Você precisa especificar todos os argumentos para criar um código de promoção!")
                    .build()).queue();
            return;
        }

        try {
            val expirationCode = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusDays(Long.parseLong(args[1]));

        } catch (NumberFormatException ex) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Não foi possível definir o tempo de expiração do código de promoção!")
                    .build()).queue();
        }
    }
}
