package project.kazumy.realhosting.discord.configuration.registry;

import com.henryfabio.free.configinjector.injector.FreeConfigurationInjector;
import project.kazumy.realhosting.discord.configuration.basic.GuildValue;
import project.kazumy.realhosting.discord.configuration.basic.PanelValue;
import project.kazumy.realhosting.discord.configuration.basic.PaymentValue;
import project.kazumy.realhosting.discord.configuration.basic.TicketValue;
import project.kazumy.realhosting.discord.configuration.embed.*;
import project.kazumy.realhosting.discord.configuration.menu.PlanMenuValue;
import project.kazumy.realhosting.discord.configuration.menu.ServerMenuValue;
import project.kazumy.realhosting.discord.configuration.menu.TicketMenuValue;

public class ConfigurationRegistry {

    public void register() {
        new FreeConfigurationInjector()
                .injectConfiguration(
                        GuildValue.instance(),
                        PanelValue.instance(),
                        PaymentValue.instance(),
                        TicketValue.instance(),
                        CloseTicketEmbedValue.instance(),
                        PanelEmbedValue.instance(),
                        PaymentEmbedValue.instance(),
                        PreExpirationEmbedValue.instance(),
                        ServerEmbedValue.instance(),
                        TicketEmbedValue.instance(),
                        PlanMenuValue.instance(),
                        ServerMenuValue.instance(),
                        TicketMenuValue.instance()
                );
    }
}
