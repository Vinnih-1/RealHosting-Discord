package project.kazumy.realhosting.discord.services.panel;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import net.dv8tion.jda.api.JDA;
import project.kazumy.realhosting.discord.configuration.basic.PanelValue;
import project.kazumy.realhosting.discord.services.BaseService;

import java.util.HashSet;
import java.util.Set;

public class PanelService extends BaseService {

    public static final Set<Integer> PORT_RANGE = new HashSet<>();
    private PteroApplication application;

    public PanelService() {
        super(1L);
    }

    @Override
    public BaseService service(JDA jda) {
        for(int i = 5000; i <= 5500; i++) PORT_RANGE.add(i);
        application = PteroBuilder.createApplication(PanelValue.get(PanelValue::url), PanelValue.get(PanelValue::token));

        return this;
    }
}
