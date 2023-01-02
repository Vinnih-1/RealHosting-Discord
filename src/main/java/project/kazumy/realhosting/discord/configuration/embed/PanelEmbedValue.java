package project.kazumy.realhosting.discord.configuration.embed;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.awt.*;
import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("embed.panel")
@ConfigFile("embed.yml")
public class PanelEmbedValue implements ConfigurationInjectable {

    @Getter private static final PanelEmbedValue instance = new PanelEmbedValue();

    @ConfigField("title") private String title;
    @ConfigField("footer") private String footer;
    @ConfigField("description") private String description;
    @ConfigField("thumbnail") private String thumbnail;
    @ConfigField("site") private String site;
    @ConfigField("fields") private ConfigurationSection section;

    @SneakyThrows
    public MessageEmbed toEmbed() {
        val yamlFile = new YamlFile("configuration/embed.yml");
        yamlFile.load();
        val embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle(title);
        embed.setFooter(footer);
        embed.setDescription(description);
        if (section == null) return embed.build();

        val name = new StringBuilder();
        val value = new StringBuilder();
        val inline = new StringBuilder();
        section.getKeys(true).stream()
                .filter(field -> !yamlFile.getConfigurationSection(section.getCurrentPath()).getString(field).startsWith("MemorySection"))
                .distinct()
                .forEach(field -> {
                    switch (field.split("\\.")[1]) {
                        case "name":
                            name.append(section.getString(field));
                            break;

                        case "value":
                            value.append(section.getString(field));
                            break;

                        case "inline":
                            inline.append(section.getString(field));
                            break;
                    }
                    if (!name.toString().isEmpty() && !value.toString().isEmpty() && !inline.toString().isEmpty()) {
                        embed.addField(name.toString(), value.toString(), Boolean.parseBoolean(inline.toString()));
                        name.setLength(0);
                        value.setLength(0);
                        inline.setLength(0);
                    }
                });
        return embed.build();
    }

    public static <T> T get(Function<PanelEmbedValue, T> function) {
        return function.apply(instance);
    }
}
