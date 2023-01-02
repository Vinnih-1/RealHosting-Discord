package project.kazumy.realhosting.discord.configuration.basic;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

@ConfigSection("bot.guild")
@Getter @Accessors(fluent = true)
@ConfigFile("config.yml")
public class GuildValue implements ConfigurationInjectable {

    @Getter private static final GuildValue instance = new GuildValue();

    @ConfigField("id") private String id;
    @ConfigField("logs-chat-id") private String logs;

    public static <T> T get(Function<GuildValue, T> function) {
        return function.apply(instance);
    }
}
