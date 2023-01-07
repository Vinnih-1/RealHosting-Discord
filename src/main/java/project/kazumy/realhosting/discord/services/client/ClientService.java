package project.kazumy.realhosting.discord.services.client;

import net.dv8tion.jda.api.JDA;
import project.kazumy.realhosting.discord.services.BaseService;

public class ClientService extends BaseService {

    // como pegar uma instância das classes de serviço sem usar static e sem repetir a instanciação?

    public ClientService() {
        super(5L);
    }

    @Override
    public BaseService service(JDA jda) {
        return this;
    }
}
