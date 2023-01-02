package project.kazumy.realhosting.discord.configuration.basic;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("bot.panel")
@ConfigFile("config.yml")
public class PanelValue implements ConfigurationInjectable {

    @Getter private static final PanelValue instance = new PanelValue();

    @ConfigField("url") private String url;

    @ConfigField("token") private String token;

    public static <T> T get(Function<PanelValue, T> function) {
        return function.apply(instance);
    }
}
