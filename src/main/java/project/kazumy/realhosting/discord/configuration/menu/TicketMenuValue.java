package project.kazumy.realhosting.discord.configuration.menu;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("menu")
@ConfigFile("menu.yml")
public class TicketMenuValue implements ConfigurationInjectable {

    @Getter private static final TicketMenuValue instance = new TicketMenuValue();

    @ConfigField("ticket") private ConfigurationSection menu;

    @SneakyThrows
    public SelectMenu toMenu(String id) {
        val yamlFile = new YamlFile("configuration/menu.yml");
        yamlFile.load();
        val menu = SelectMenu.create(id);
        if (this.menu == null) return menu.build();

        val name = new StringBuilder();
        val value = new StringBuilder();
        val description = new StringBuilder();
        val emoji = new StringBuilder();
        this.menu.getKeys(true).stream()
                .filter(field -> !yamlFile.getConfigurationSection(this.menu.getCurrentPath()).getString(field).startsWith("MemorySection"))
                .distinct()
                .forEach(field -> {
                    switch (field.split("\\.")[1]) {
                        case "name":
                            name.append(this.menu.getString(field));
                            break;

                        case "value":
                            value.append(this.menu.getString(field));
                            break;

                        case "description":
                            description.append(this.menu.getString(field));
                            break;

                        case "emoji":
                            emoji.append(this.menu.getString(field));
                            break;
                    }
                    if (!name.toString().isEmpty() && !value.toString().isEmpty() && !description.toString().isEmpty() && !emoji.toString().isEmpty()) {
                        menu.addOption(name.toString(), value.toString(), description.toString(), Emoji.fromUnicode(emoji.toString()));
                        name.setLength(0);
                        value.setLength(0);
                        description.setLength(0);
                        emoji.setLength(0);
                    }
                });
        return menu.build();
    }

    public static <T> T get(Function<TicketMenuValue, T> function) {
        return function.apply(instance);
    }
}
