package project.kazumy.realhosting.discord.listener.interactions.modal;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.configuration.embed.ServerCreatedFailureEmbedValue;
import project.kazumy.realhosting.configuration.embed.ServerCreatedSuccessEmbedValue;
import project.kazumy.realhosting.configuration.embed.UserCreatedFailureEmbedValue;
import project.kazumy.realhosting.configuration.embed.UserCreatedSuccessEmbedValue;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.payment.utils.PaymentUtils;

import java.awt.*;
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
        event.deferReply(true).addEmbeds(new EmbedBuilder()
                .setColor(Color.GRAY)
                .setDescription(String.format("<@%s>, estamos processando o seu pedido. Aguarde um momento...",
                        event.getMember().getId()))
                .build()).queue();

        val planId = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();
        val client = discordMain.getClientManager().getClientById(event.getMember().getId());
        val panelUsers = client.getPanel().usersExistsByUsername(event.getValue("username").getAsString());

        if (!panelUsers.isEmpty()) {
            val panelUser = panelUsers.get(0);
            if (panelUser.getUserName().equalsIgnoreCase(event.getValue("username").getAsString())
                    && !panelUser.getEmail().equalsIgnoreCase(event.getValue("email").getAsString())) {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setDescription(":x: **|** Este usuário que você inseriu já existe!")
                        .build()).queue();
                return;
            }
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
                                event.getChannel().sendMessageEmbeds(ServerCreatedSuccessEmbedValue.instance().toEmbed(server, plan)).queue();
                            }, error -> {
                                event.getChannel().sendMessageEmbeds(ServerCreatedFailureEmbedValue.instance().toEmbed(plan)).queue();
                            });
                        }, failure -> failure.createUser(client.getName(), client.getLastname(), client.getUsername(), client.getEmail(), user -> {
                            client.getPanel().createServer(plan, server -> {
                                event.getChannel().sendMessageEmbeds(
                                        UserCreatedSuccessEmbedValue.instance().toEmbed(client.getEmail(), client.getPanel().getPassword()),
                                        ServerCreatedSuccessEmbedValue.instance().toEmbed(server, plan)).queue();
                            }, error -> {
                                event.getChannel().sendMessageEmbeds(ServerCreatedFailureEmbedValue.instance().toEmbed(plan)).queue();
                            });
                        }, error -> {
                            event.getChannel().sendMessageEmbeds(UserCreatedFailureEmbedValue.instance().toEmbed(plan)).queue();
                        }));
                    });

                    event.getChannel().sendMessage("Cobrança Automática de Serviços Prestados")
                            .addEmbeds(new EmbedBuilder()
                                    .setColor(Color.GRAY)
                                    .setTitle(":white_check_mark: | PIX! Basta copiar e colar!")
                                    .setDescription(qrData)
                                    .build())
                            .addFiles(FileUpload.fromData(PaymentUtils.getAsImage(event.getMember().getId(), qrData))).queue();
                    event.getMessage().delete().queue();
                });
    }
}