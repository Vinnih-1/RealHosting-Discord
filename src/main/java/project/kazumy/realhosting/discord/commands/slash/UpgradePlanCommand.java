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
import project.kazumy.realhosting.discord.configuration.menu.PlanMenuValue;
import project.kazumy.realhosting.discord.services.plan.PaymentIntent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class UpgradePlanCommand extends BaseSlashCommand {

    public UpgradePlanCommand() {
        super("aprimorar",
                "Utilize para aprimorar um plano que já existe",
                Arrays.asList(new OptionData(OptionType.STRING, "plano", "ID do plano a ser aprimorado", true)));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        val paymentManager = InitBot.paymentManager;
        val panelManager = InitBot.panelManager;
        val planString = event.getOption("plano").getAsString();

        if (!event.getChannel().getName().contains("aprimorar")) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Este comando precisa ser utilizado em um ticket de aprimoramento!")
                    .build()).queue();
            return;
        }

        if (!paymentManager.hasPlanByPlanId(planString)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Não consegui encontrar nenhum plano com este identificador!")
                    .build()).queue();
            return;
        }

        if (!panelManager.serverExistsByPlanId(planString)) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Não consegui encontrar nenhum servidor com este identificador!")
                    .build()).queue();
            return;
        }
        val plan = paymentManager.getPlanById(planString);

        if (!plan.getPlanData().getUserId().equals(event.getUser().getId())) {
            event.deferReply().addEmbeds(new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setDescription("Você está tentando aprimorar um plano que não é seu, tem certeza que deseja realizar este processo?")
                    .build()).addActionRow(Button.primary(String.format("upgrade-agree;%s;%s", event.getUser().getId(), plan.getPlanData().getPlanId()), "Concordo").withEmoji(Emoji.fromUnicode("U+2705")),
                    Button.danger(String.format("upgrade-disagree;%s;%s", event.getUser().getId(), plan.getPlanData().getPlanId()), "Não concordo").withEmoji(Emoji.fromUnicode("U+2716"))).queue();
            return;
        }
        plan.updatePaymentIntent(PaymentIntent.UPGRADE_PLAN);
        val member = event.getGuild().getMemberById(plan.getPlanData().getUserId());
        val dateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        val externalReference = plan.getPlanData().getExternalReference();
        val embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setAuthor(event.getUser().getAsTag(), "https://app.realhosting.com.br", plan.getPlanData().getLogo());
        embed.setTitle(plan.getPlanData().getTitle());
        embed.setFooter("RealHosting Bot às " + dateTime.format(formatter), plan.getPlanData().getLogo());
        embed.setThumbnail(member != null ? member.getUser().getAvatarUrl() : plan.getPlanData().getLogo());
        embed.addField("Autor", plan.getPlanData().getUserAsTag(), true);
        embed.addField("Plano", plan.getPlanType().toString(), true);
        embed.addField("ID do Plano", plan.getPlanData().getPlanId(), true);
        embed.addField("Data de Criação", plan.getCreateDate().format(formatter), true);
        embed.addField("Data de Pagamento", plan.getPaymentDate().format(formatter), true);
        embed.addField("Data de Expiração", plan.getExpirationDate().format(formatter), true);
        embed.addBlankField(false);
        embed.addField("Intenção de Pagamento", plan.getPaymentIntent().toString(), true);
        embed.addField("Referência Externa", externalReference == null ? "Nenhuma" : plan.getPlanData().getExternalReference(), true);

        event.deferReply().addEmbeds(embed.build()).addActionRow(PlanMenuValue.instance().toMenu("upgrade-menu")).queue();
    }
}
