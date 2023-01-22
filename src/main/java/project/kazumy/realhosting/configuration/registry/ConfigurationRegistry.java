package project.kazumy.realhosting.configuration.registry;

import com.henryfabio.free.configinjector.injector.FreeConfigurationInjector;
import project.kazumy.realhosting.configuration.basic.GuildValue;
import project.kazumy.realhosting.configuration.basic.PanelValue;
import project.kazumy.realhosting.configuration.basic.PaymentValue;
import project.kazumy.realhosting.configuration.basic.TicketValue;
import project.kazumy.realhosting.configuration.embed.*;
import project.kazumy.realhosting.configuration.menu.FeedbackMenuValue;
import project.kazumy.realhosting.configuration.menu.PlanMenuValue;
import project.kazumy.realhosting.configuration.menu.ServerMenuValue;
import project.kazumy.realhosting.configuration.menu.TicketMenuValue;

public class ConfigurationRegistry {

    public void register() {
        new FreeConfigurationInjector()
                .injectConfiguration(
                        GuildValue.instance(),
                        PanelValue.instance(),
                        PaymentValue.instance(),
                        TicketValue.instance(),

                        FeedbackTicketEmbedValue.instance(),
                        CloseTicketEmbedValue.instance(),
                        PanelEmbedValue.instance(),
                        PaymentEmbedValue.instance(),
                        PreExpirationEmbedValue.instance(),
                        ServerEmbedValue.instance(),
                        TicketEmbedValue.instance(),

                        FeedbackMenuValue.instance(),
                        PlanMenuValue.instance(),
                        ServerMenuValue.instance(),
                        TicketMenuValue.instance()
                );
    }
}
