package project.kazumy.realhosting.discord.configuration.guild;

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
public class GuildServerValue implements ConfigurationInjectable {

    @Getter private static final GuildServerValue instance = new GuildServerValue();

    @ConfigField("id") private String id;
    @ConfigField("logs-chat-id") private String logsId;

    public <T> T get(Function<GuildServerValue, T> function) {
        return function.apply(instance);
    }
}
