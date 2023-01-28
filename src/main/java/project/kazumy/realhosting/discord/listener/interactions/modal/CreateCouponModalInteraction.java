package project.kazumy.realhosting.discord.listener.interactions.modal;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.model.payment.coupon.impl.CouponImpl;
import project.kazumy.realhosting.model.payment.coupon.repository.CouponRepository;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CreateCouponModalInteraction extends InteractionService<ModalInteractionEvent> {

    private final DiscordMain discordMain;

    public CreateCouponModalInteraction(DiscordMain discordMain) {
        super("create-coupon-modal", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(ModalInteractionEvent event) {
        try {
            val name = event.getValue("name").getAsString();
            val limits = Integer.parseInt(event.getValue("limits").getAsString());
            val percentage = Integer.parseInt(event.getValue("percentage").getAsString());
            val expiration = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))
                    .plusDays(Integer.parseInt(event.getValue("expiration").getAsString()));

            val coupon = CouponImpl.builder()
                    .name(name)
                    .limits(limits)
                    .percentage(percentage)
                    .createAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                    .expirateAt(expiration)
                    .build();
            CouponRepository.of(discordMain.getExecutor()).save(coupon);

            event.deferReply().addEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setDescription(String.format(":white_check_mark: **|** Perfeito! Você criou o cupom **%s** válido até **%s**!",
                                    coupon.getName(), coupon.getExpirateAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' h:mm a"))))
                    .build()).queue();
        } catch (NumberFormatException ignored) {
            event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription(":x: **|** Você precisa especificar o limite, porcentagem e expiração em **NÚMEROS**!")
                    .build()).queue();
        }
    }
}
