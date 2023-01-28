package project.kazumy.realhosting.discord.listener.interactions.menu;

import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class PurchasePlanMenuInteraction extends InteractionService<StringSelectInteractionEvent> {

    private final DiscordMain discordMain;

    public PurchasePlanMenuInteraction(DiscordMain discordMain) {
        super("purchase-plan-menu", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(StringSelectInteractionEvent event) {
        val planId = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();

        discordMain.getPlanManager().getPlanByClientId(event.getMember().getId()).stream()
                .filter(plan -> plan.getId().equals(planId)).findFirst()
                .ifPresent(plan -> {
                    plan.setPrePlan(discordMain.getPlanManager().getPrePlanById(event.getSelectedOptions().get(0)
                            .getValue().split("\\.")[4]));
                    discordMain.getPlanManager().savePlan(plan);
                    val modal = Modal.create("purchase-plan-modal", "Preencha os Dados")
                            .addActionRow(
                                    TextInput.create("name", "Nome", TextInputStyle.SHORT)
                                            .setPlaceholder("Insira o seu Nome")
                                            .setMaxLength(20)
                                            .setMinLength(1)
                                            .build()
                            )
                            .addActionRow(
                                    TextInput.create("lastname", "Sobrenome", TextInputStyle.SHORT)
                                            .setPlaceholder("Insira o seu Sobrenome")
                                            .setMaxLength(20)
                                            .setMinLength(1)
                                            .build()
                            )
                            .addActionRow(
                                    TextInput.create("username", "Usuário", TextInputStyle.SHORT)
                                            .setPlaceholder("Insira o seu Usuário")
                                            .setMaxLength(40)
                                            .setMinLength(1)
                                            .build()
                            )
                            .addActionRow(
                                    TextInput.create("email", "Email", TextInputStyle.SHORT)
                                            .setPlaceholder("Insira o seu Email")
                                            .setMaxLength(60)
                                            .setMinLength(5)
                                            .build()
                            )
                            .addActionRow(
                                    TextInput.create("coupon", "Cupom", TextInputStyle.SHORT)
                                            .setPlaceholder("Insira um cupom de desconto")
                                            .setRequired(false)
                                            .setMaxLength(40)
                                            .build()
                            )
                            .build();
                    event.replyModal(modal).queue();
                });
    }
}
