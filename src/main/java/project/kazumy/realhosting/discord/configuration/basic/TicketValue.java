package project.kazumy.realhosting.discord.configuration.basic;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("bot.guild")
@ConfigFile("config.yml")
public class TicketValue implements ConfigurationInjectable {

    @Getter private static final TicketValue instance = new TicketValue();

    @ConfigField("ticket-category-id") private String category;
    @ConfigField("ticket-chat-id") private String channel;

    public static <T> T get(Function<TicketValue, T> function) {
        return function.apply(instance);
    }
}
