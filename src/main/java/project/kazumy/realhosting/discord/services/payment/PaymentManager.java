package project.kazumy.realhosting.discord.services.payment;

import io.nayuki.qrcodegen.QrCode;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.payment.plan.PlanBuilder;
import project.kazumy.realhosting.discord.services.payment.plan.PlanType;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PaymentManager extends BaseService {

    @Getter @Setter
    private Set<PlanBuilder> plans;

    public PaymentManager() {
        super(2L);
    }
    public void loadPlans() {
        val planFolder = new File("services/payment/plan/");

        if (!planFolder.exists()) return;

        Arrays.asList(planFolder.listFiles())
                .stream()
                .filter(file -> file.getName().endsWith(".yml"))
                .forEach(file -> {
                    val config = new Configuration("services/payment/plan/" + file.getName())
                            .buildIfNotExists();

                    try {
                        val plan = PlanBuilder.builder()
                                .title(config.getString("plan.title"))
                                .description(config.getString("plan.description"))
                                .price(new BigDecimal(config.getString("plan.price")))
                                .logo(config.getString("plan.logo"))
                                .skuId(config.getString("plan.skuId"))
                                .planId(config.getString("plan.planId"))
                                .userId(config.getString("plan.author.authorId"))
                                .userAsTag(config.getString("plan.author.authorAsTag"))
                                .planType(PlanType.valueOf(config.getString("plan.planType")))
                                .stageType(StageType.valueOf(config.getString("plan.stageType")))
                                .serverType(ServerType.valueOf(config.getString("plan.serverType")))
                                .emojiUnicode(Emoji.fromUnicode(config.getString("plan.emojiUnicode")))
                                .createDate(LocalDateTime.parse(config.getString("plan.createDate")))
                                .enabled(config.getBoolean("plan.enabled"))
                                .build().instanceConfig(config);

                        Logger.getGlobal().info("O plano " + config.getString("plan.planId") + " foi carregado para a memória: "
                                + plans.add(plan));
                    } catch (Exception e) {
                        Logger.getGlobal().severe(String.format("Houve uma falha ao carregar o plano %s", file.getName()));
                    }
                });
    }

    public boolean hasPlanByUserId(String userId) {
        return this.getPlans().stream()
                .anyMatch(plan -> plan.getUserId().equals(userId));
    }

    public boolean hasPlanByPlanId(String planId) {
        return this.getPlans().stream()
                .anyMatch(plan -> plan.getPlanId().equals(planId));
    }

    public PlanBuilder getPlanById(String planId) {
        return this.getPlans().stream()
                .filter(plan -> plan.getPlanId().equals(planId))
                .findFirst().get();
    }

    public Set<PlanBuilder> getPlansByUserId(String userId) {
        return this.getPlans().stream()
                .filter(plan -> plan.getUserId().equals(userId))
                .collect(Collectors.toSet());
    }

    public void setDefaultBuyMessage(Configuration config) {
        config.addDefaults(consumer -> {
            consumer.addDefault("bot.guild.payment.title", ":desktop: Here you can choose your plan");
            consumer.addDefault("bot.guild.payment.footer", "Self-Service official of RealHosting");
            consumer.addDefault("bot.guild.payment.thumbnail", "thumbnail-url");
            consumer.addDefault("bot.guild.payment.description", "Click on any plan and see the magic happens");
            consumer.addDefault("bot.guild.payment.site", "site-url");
            consumer.addDefault("bot.guild.payment.fields.1.name", "My Name is");
            consumer.addDefault("bot.guild.payment.fields.1.value", "Zumo");
            consumer.addDefault("bot.guild.payment.fields.1.inline", true);
            consumer.addDefault("bot.guild.payment.fields.2.name", "My Job is");
            consumer.addDefault("bot.guild.payment.fields.2.value", "Supporter of RealHosting");
            consumer.addDefault("bot.guild.payment.fields.2.inline", true);
        });
    }

    public void sendBuyMenu(TextChannel channel, Configuration config) {
        val embed = config.getEmbedMessageFromConfig(config, "payment");
        embed.setColor(Color.GREEN);
        val menu = config.getMenuFromConfig(config, "bot.guild.payment.plans", "buy-menu");
        channel.sendMessageEmbeds(embed.build()).addActionRow(menu.build()).queue();
    }

    public static BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    public static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0)
            throw new IllegalArgumentException("Value out of range");
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale)
            throw new IllegalArgumentException("Scale or border too large");

        BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

    @Override
    public BaseService service(JDA jda, Configuration config) {
        this.setDefaultBuyMessage(config);
        this.setPlans(new HashSet<>());

        PaymentMP.setAccessToken(InitBot.config.getString("bot.payment.mercado-pago.access-token"));
        new PaymentMP().waitPayment(this, jda);

        return this;
    }
}
