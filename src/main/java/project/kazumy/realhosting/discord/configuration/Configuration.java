package project.kazumy.realhosting.discord.configuration;

import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.simpleyaml.configuration.file.YamlFile;

import java.awt.*;
import java.util.function.Consumer;

public class Configuration extends YamlFile {

    public Configuration(String path) {
        super(path);
    }

    @SneakyThrows
    public Configuration buildIfNotExists() {
        if (!this.exists()) this.createNewFile(false);
        else this.load();
        return this;
    }

    @SneakyThrows
    public Configuration addDefaults(Consumer<YamlFile> consumer) {
        consumer.accept(this);
        this.save();
        return this;
    }

    public SelectMenu.Builder getMenuFromConfig(Configuration config, String path, String menuId) {
        val menu = SelectMenu.create(menuId);
        val section = config.getConfigurationSection(path).getKeys(true);

        for (int i = 1; i < section.size(); i++) {
            val name = config.getString(path + "." + i + ".name");
            val value = config.getString(path + "." + i + ".value");
            val description = config.getString(path + "." + i + ".description");
            val emoji = config.getString(path + "." + i + ".emoji");

            if (name == null || value == null || description == null || emoji == null) continue;
            menu.addOption(name, value, description, Emoji.fromUnicode(emoji));
        }

        return menu;
    }

    public EmbedBuilder getEmbedMessageFromConfig(Configuration config, String path) {
        val section = "bot.guild." + path + ".";
        val title = config.getString(section + "title");
        val footer = config.getString(section + "footer");
        val description = config.getString(section + "description");

        val embed = new EmbedBuilder();

        embed.setTitle(title);
        embed.setFooter(footer);
        embed.setColor(Color.YELLOW);
        val configSection = config.getConfigurationSection(section + "fields").getKeys(true);

        for (int i = 1; i <= configSection.size(); i++) {
            val name = config.getString(section + "fields." + i + ".name");
            val value = config.getString(section + "fields." + i + ".value");
            val inline = config.get(section + "fields." + i + ".inline");

            if (name == null || value == null || inline == null) continue;
            embed.addField(name, value, Boolean.parseBoolean(String.valueOf(inline)));
        }
        embed.setDescription(description);
        embed.setThumbnail(config.getString(section + "thumbnail"));
        embed.setFooter(config.getString(section + "footer"), config.getString(section + "thumbnail"));

        return embed;
    }
}
