package project.kazumy.realhosting;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import project.kazumy.realhosting.configuration.registry.ConfigurationRegistry;
import project.kazumy.realhosting.database.SQLProvider;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.model.entity.client.manager.ClientManager;
import project.kazumy.realhosting.model.entity.client.repository.ClientRepository;
import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.payment.Payment;
import project.kazumy.realhosting.model.plan.manager.PlanManager;
import project.kazumy.realhosting.model.plan.repository.PlanRepository;

public class ApplicationMain {

    private static SQLExecutor executor;
    private static ClientManager clientManager;
    private static PlanManager planManager;
    private static Panel panel;
    private static Payment payment;

    public static void main(String[] args) {
        new ConfigurationRegistry().register();
        ApplicationMain.executor = SQLProvider.of().createDefaults();
        ApplicationMain.planManager = new PlanManager(PlanRepository.of(executor)).loadPrePlan();
        ApplicationMain.clientManager = ClientManager.of(ClientRepository.of(executor), planManager);
        ApplicationMain.payment = Payment.of(clientManager, planManager);
        planManager.setPayment(payment);

        ApplicationMain.panel = new Panel();

        DiscordMain.of(
                executor,
                clientManager,
                planManager,
                panel
        ).startup("");
    }
}