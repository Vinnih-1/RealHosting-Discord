package project.kazumy.realhosting.discord.services.terms;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;

import java.awt.*;
import java.io.File;
import java.util.logging.Logger;

public final class TermsOfService extends BaseService {

    public TermsOfService() {
        super(1L);
    }

    public static File getTermsFile() {
        return new File("terms.pdf");
    }

    public static void SendTermsMenu(TextChannel textChannel) {
        val embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("Você concorda com os nossos Termos?");
        embed.setDescription("Ao clicar no botão verde, você está ciente e de acordo com todos os nossos termos.");
        embed.setImage("https://cdn.discordapp.com/attachments/967481970797985822/1041728331114106941/2022-11-14_11-55.png");
        textChannel.sendMessageEmbeds(embed.build()).addActionRow(Button.success("terms-agree", "Sim"),
                Button.danger("terms-disagree", "Não")).queue(success -> success.getChannel().sendFiles(FileUpload.fromData(getTermsFile())).queue());
    }

    private static boolean checkIfTermsExists() {
        return new File("terms.pdf").exists();
    }

    @Override
    public BaseService service(JDA jda, Configuration config) {
        if (!checkIfTermsExists()) {
            Logger.getGlobal().severe("The bot could not initialize! Terms of Service file is missing...");
            System.exit(1);
        }
        return this;
    }
}
