package project.kazumy.realhosting.discord.configuration.registry;

import com.henryfabio.free.configinjector.injector.FreeConfigurationInjector;
import project.kazumy.realhosting.discord.configuration.embed.CloseTicketEmbedValue;
import project.kazumy.realhosting.discord.configuration.embed.TicketEmbedValue;

public class ConfigurationRegistry {

    public void register() {
        new FreeConfigurationInjector()
                .injectConfiguration(TicketEmbedValue.instance(),
                        CloseTicketEmbedValue.getInstance());
    }
}
