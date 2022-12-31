package project.kazumy.realhosting.discord.configuration.embed;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.math.NumberUtils;
import org.simpleyaml.configuration.ConfigurationSection;

import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("embed.ticket")
@ConfigFile("embed.yml")
public class TicketEmbedValue implements ConfigurationInjectable {

    @Getter private static final TicketEmbedValue instance = new TicketEmbedValue();

    @ConfigField("title") private String title;
    @ConfigField("footer") private String footer;
    @ConfigField("description") private String description;
    @ConfigField("thumbnail") private String thumbnail;
    @ConfigField("site") private String site;

    public MessageEmbed toEmbed() {
        val embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle(title);
        embed.setFooter(footer);
        embed.setDescription(description);

        return embed.build();
    }

    public <T> T get(Function<TicketEmbedValue, T> function) {
        return function.apply(instance);
    }
}
