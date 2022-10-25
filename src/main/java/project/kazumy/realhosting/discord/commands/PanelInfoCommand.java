package project.kazumy.realhosting.discord.commands;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang.RandomStringUtils;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;
import project.kazumy.realhosting.discord.services.panel.PanelManager;
import project.kazumy.realhosting.discord.services.panel.UserPanel;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import java.awt.*;
import java.util.Arrays;

public class PanelInfoCommand extends BaseSlashCommand {

    public PanelInfoCommand() {
        super("painel",
                "Utilize este comando para adicionar suas informações no painel",
                Arrays.asList(
                        new OptionData(OptionType.STRING, "email", "Responsável por dizer ao back-end seu email", true),
                        new OptionData(OptionType.STRING, "usuario", "Responsável por dizer ao back-end seu usuário", true),
                        new OptionData(OptionType.STRING, "nome", "Responsável por dizer ao back-end seu nome", true),
                        new OptionData(OptionType.STRING, "sobrenome", "Responsável por dizer ao back-end seu sobrenome", true)));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!InitBot.paymentManager.hasPlanByUserId(event.getUser().getId())) {
            event.deferReply(true).setContent(":x: Você não contém um plano pendente!").queue();
            return;
        }

        InitBot.paymentManager.getPlansByUserId(event.getUser().getId())
                .stream()
                .filter(plan -> plan.getStageType() == StageType.PAYED)
                .findAny()
                .ifPresentOrElse(plan -> {
                    val email = event.getOption("email").getAsString().toLowerCase();
                    val userName = event.getOption("usuario").getAsString();
                    val firstName = event.getOption("nome").getAsString();
                    val lastName = event.getOption("sobrenome").getAsString();
                    val password = RandomStringUtils.randomAlphanumeric(8);

                    try {
                        val userPanel = UserPanel.builder()
                                .email(email)
                                .userName(userName)
                                .firstName(firstName)
                                .lastName(lastName)
                                .password(password)
                                .build();
                        try {
                            PanelManager.checkUserFields(userPanel);
                        } catch (Exception e) {
                            event.deferReply(true).setContent(e.getMessage()).queue();
                            return;
                        }

                        InitBot.panelManager.createUser(userPanel, applicationUser -> {
                            event.deferReply(true).setContent(String.format("Estas são suas novas informações: \n" +
                                    "Email: %s\n" +
                                    "Senha: %s\n" +
                                    "Painel: https://painel.realhosting.com.br", email, password)).queue();
                            val ticket = InitBot.ticketManager.getTicketByTextChannelId(event.getChannel().getId());

                            InitBot.panelManager.createServer(applicationUser, plan.getServerType(), plan, success -> {
                                ticket.getTicketChannel(InitBot.jda)
                                        .sendMessageEmbeds(new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setDescription("Olá! Eu estou criando seu servidor, aguarde um pouco.")
                                                .build()).queue();
                                plan.setStageType(StageType.ACTIVED);
                                plan.updateStageType();
                            });
                        });
                    } catch (Exception e) {
                        event.deferReply(true).setContent(":x: O email ou usuário que você digitou já pertencem a outra pessoa!");
                        return;
                    }
                }, () -> {
                            event.deferReply(true).setContent(":x: Você não contém um plano pendente!").queue();
                            return;
                        });
    }
}
