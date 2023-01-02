package project.kazumy.realhosting.discord.configuration.plan;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.function.Function;

@ConfigSection("menu")
@ConfigFile("plan.yml")
public class PlanMenuValue implements ConfigurationInjectable {

    @Getter private static final PlanMenuValue instance = new PlanMenuValue();

    @ConfigField("plan") private ConfigurationSection plan;

    @SneakyThrows
    public SelectMenu toMenu(String id) {
        val yamlFile = new YamlFile("configuration/plan.yml");
        yamlFile.load();
        val menu = SelectMenu.create(id);
        if (plan == null) return menu.build();

        val name = new StringBuilder();
        val value = new StringBuilder();
        val description = new StringBuilder();
        val emoji = new StringBuilder();
        plan.getKeys(true).stream()
                .filter(field -> !yamlFile.getConfigurationSection(plan.getCurrentPath()).getString(field).startsWith("MemorySection"))
                .distinct()
                .forEach(field -> {
                    switch (field.split("\\.")[1]) {
                        case "name":
                            name.append(plan.getString(field));
                            break;

                        case "value":
                            value.append(plan.getString(field));
                            break;

                        case "description":
                            description.append(plan.getString(field));
                            break;

                        case "emoji":
                            emoji.append(plan.getString(field));
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

    public <T> T get(Function<PlanMenuValue, T> function) {
        return function.apply(instance);
    }
}
