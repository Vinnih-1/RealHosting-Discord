package project.kazumy.realhosting.discord.listener.interactions.modal;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.payment.utils.PaymentUtils;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PurchasePlanModalInteraction extends InteractionService<ModalInteractionEvent> {

    private final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(\\S+)$");
    private final DiscordMain discordMain;

    public PurchasePlanModalInteraction(DiscordMain discordMain) {
        super("purchase-plan-modal", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        if (!EMAIL_PATTERN.matcher(event.getValue("email").getAsString()).matches()) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription(":x: **|** Você deve inserir um email no padrão correto!")
                    .build()).queue();
            return;
        }
        val planId = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();
        val client = discordMain.getClientManager().getClientById(event.getMember().getId());

        if (client.getPanel().userExistsByUsername(event.getValue("username").getAsString())) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(":x: **|** Este usuário que você inseriu já existe!")
                    .build()).queue();
            return;
        }
        client.setName(event.getValue("name").getAsString());
        client.setLastname(event.getValue("lastname").getAsString());
        client.setUsername(event.getValue("username").getAsString());
        client.setEmail(event.getValue("email").getAsString());
        discordMain.getClientManager().saveClient(client);

        discordMain.getPlanManager().getPlanByClientId(event.getMember().getId()).stream()
                .filter(plan -> plan.getId().equals(planId)).findFirst()
                .ifPresent(plan -> {
                    plan.setPrePlan(discordMain.getPlanManager().getPrePlanByType(discordMain.getPlanManager().getPrePlanTypeFromPlanById(planId)));
                    val qrData = discordMain.getPlanManager().purchase(plan, success -> {
                        plan.setPaymentIntent(PaymentIntent.NONE);
                        plan.setStageType(StageType.ACTIVED);
                        discordMain.getPlanManager().savePlan(plan);

                        client.getPanel().syncUserByEmail(client.getEmail(), user -> {
                            client.getPanel().createServer(plan, server -> {
                                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setTitle(":white_check_mark:  |  Seu servidor foi criado com êxito")
                                        .setDescription("Observe que após a data de criação do servidor, você terá mais 30 (trinta) " +
                                                "dias para utilizar deste serviço, podendo ser renovado quando você quiser.")
                                        .addField("Nome do Servidor", server.getName(), true)
                                        .addField("Identificador", planId, true)
                                        .setFooter("Aproveite! Os nossos serviços estão disponíveis")
                                        .build()).queue();
                            }, error -> {
                                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setTitle(":warning:  |  Houve uma falha na criação deste servidor")
                                        .setDescription("Desculpe-nos pelo incômodo, resolveremos isso o mais rápido possível.")
                                        .setFooter("Identificador", planId)
                                        .setFooter("Houve um erro inesperado! Contate a equipe.")
                                        .build()).queue();
                            });
                        }, failure -> failure.createUser(client.getName(), client.getLastname(), client.getUsername(), client.getEmail(), user -> {
                            client.getPanel().createServer(plan, server -> {
                                event.getChannel().sendMessageEmbeds(
                                        new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setTitle(":bust_in_silhouette:  |  Seu usuário foi criado com êxito")
                                                .setDescription("Seu usuário foi criado e está pronto para ser utilizado dentro do nosso [painel](https://app.realhosting.com.br/)")
                                                .addField("Email", client.getEmail(), true)
                                                .addField("Password", client.getPanel().getPassword(), true)
                                                .setFooter("Seu usuário foi criado no painel")
                                                .build(),
                                        new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setTitle(":white_check_mark:  |  Seu servidor foi criado com êxito")
                                                .setDescription("Observe que após a data de criação do servidor, você terá mais 30 (trinta) " +
                                                        "dias para utilizar deste serviço, podendo ser renovado quando você quiser.")
                                                .addField("Nome do Servidor", server.getName(), true)
                                                .addField("Identificador", planId, true)
                                                .setFooter("Aproveite! Os nossos serviços estão disponíveis")
                                        .build()).queue();
                            }, error -> {
                                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                                .setColor(Color.RED)
                                                .setTitle(":warning:  |  Houve uma falha na criação deste servidor")
                                                .setDescription("Desculpe-nos pelo incômodo, resolveremos isso o mais rápido possível.")
                                                .setFooter("Identificador", planId)
                                                .setFooter("Houve um erro inesperado! Contate a equipe.")
                                        .build()).queue();
                            });
                        }, error -> {
                            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setTitle(":warning:  |  Houve uma falha na criação deste usuário")
                                    .setDescription("Desculpe-nos pelo incômodo, resolveremos isso o mais rápido possível.")
                                    .setFooter("Identificador", planId)
                                    .setFooter("Houve um erro inesperado! Contate a equipe.")
                                    .build()).queue();
                        }));
                    });
                    event.deferReply(false).setContent("Cobrança Automática de Serviços Prestados")
                            .addEmbeds(new EmbedBuilder()
                                    .setColor(Color.GRAY)
                                    .setTitle(":white_check_mark: | PIX! Basta copiar e colar!")
                                    .setDescription(qrData)
                                    .build())
                            .addFiles(FileUpload.fromData(PaymentUtils.getAsImage(event.getMember().getId(), qrData))).timeout(1, TimeUnit.MINUTES).queue();
                    event.getMessage().delete().queue();
                });
    }
}