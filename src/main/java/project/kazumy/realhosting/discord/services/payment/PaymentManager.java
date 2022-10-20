package project.kazumy.realhosting.discord.services.payment;

import io.nayuki.qrcodegen.QrCode;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentManager {

    public void setDefaultBuyMessaage(Configuration config) {
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

        val menu = SelectMenu.create("buy-menu");
        val section = config.getConfigurationSection("bot.guild.payment.plans").getKeys(true);

        for (int i = 1; i < section.size(); i++) {
            val name = config.getString("bot.guild.payment.plans." + i + ".name");
            val value = config.getString("bot.guild.payment.plans." + i + ".value");
            val description = config.getString("bot.guild.payment.plans." + i + ".description");
            val emoji = config.getString("bot.guild.payment.plans." + i + ".emoji");

            if (name == null || value == null || description == null || emoji == null) continue;
            menu.addOption(name, value, description, Emoji.fromUnicode(emoji));
        }
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
}
