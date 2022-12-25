package project.kazumy.realhosting.discord.commands.slash;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class PlanInfoCommand extends BaseSlashCommand {

    public PlanInfoCommand() {
        super("plano",
                "Obtenha informações e utilidades sobre um plano",
                Arrays.asList(new OptionData(OptionType.STRING, "plano", "ID do plano que será exibido.", true)));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        val paymentManager = InitBot.paymentManager;
        val planString = event.getOption("plano").getAsString();

        if (!paymentManager.hasPlanByPlanId(planString)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Não encontrei nenhum plano com este identificador!")
                    .build()).queue();
            return;
        }
        val plan = paymentManager.getPlanById(planString);

        if (plan.getStageType() != StageType.ACTIVED) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("O plano indicado precisa estar ativo!")
                    .build()).queue();
            return;
        }
        val member = event.getGuild().getMemberById(plan.getPlanData().getUserId());
        val dateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val externalReference = plan.getPlanData().getExternalReference();
        val embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setAuthor(event.getUser().getAsTag(), "https://app.realhosting.com.br");
        embed.setTitle(plan.getPlanData().getTitle());
        embed.setFooter("RealHosting Bot às " + dateTime.format(formatter));
        embed.addField("Autor", plan.getPlanData().getUserAsTag(), true);
        embed.addField("Plano", plan.getPlanType().toString(), true);
        embed.addField("ID do Plano", plan.getPlanData().getPlanId(), true);
        embed.addField("Data de Criação", plan.getCreateDate().format(formatter), true);
        embed.addField("Data de Pagamento", plan.getPaymentDate().format(formatter), true);
        embed.addField("Data de Expiração", plan.getExpirationDate().format(formatter), true);
        embed.addBlankField(false);
        embed.addField("Intenção de Pagamento", plan.getPaymentIntent().toString(), true);
        embed.addField("Referência Externa", externalReference == null ? "Nenhuma" : plan.getPlanData().getExternalReference(), true);

        event.deferReply().addEmbeds(embed.build()).addActionRow(
                Button.success("upgrade", "Aprimorar").withEmoji(Emoji.fromUnicode("U+2699"))).queue();
    }
}