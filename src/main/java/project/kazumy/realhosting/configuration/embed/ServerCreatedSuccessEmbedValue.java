package project.kazumy.realhosting.configuration.embed;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.model.plan.Plan;

import java.awt.*;
import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("embed.server-created-success")
@ConfigFile("embed.yml")
public class ServerCreatedSuccessEmbedValue implements ConfigurationInjectable {

    @Getter private static final ServerCreatedSuccessEmbedValue instance = new ServerCreatedSuccessEmbedValue();

    @ConfigField("title") private String title;
    @ConfigField("footer") private String footer;
    @ConfigField("description") private String description;
    @ConfigField("thumbnail") private String thumbnail;
    @ConfigField("site") private String site;
    @ConfigField("fields") private ConfigurationSection section;

    @SneakyThrows
    public MessageEmbed toEmbed(Plan plan) {
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
                            value.append(section.getString(field)
                                    .replace("%servidor", plan.getPrePlan().getTitle())
                                    .replace("%id", plan.getId()));
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

    public static <T> T get(Function<ServerCreatedSuccessEmbedValue, T> function) {
        return function.apply(instance);
    }
}