package project.kazumy.realhosting.discord.services.panel;

import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationEgg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.kazumy.realhosting.discord.InitBot;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ServerType {

    MINECRAFT("quay.io/pterodactyl/core:java",
            InitBot.panelManager.getApplication()
                    .retrieveEggById(InitBot.panelManager.getApplication()
                            .retrieveNestById(1).execute(), 1).execute(),
            minecraftEnviroment());

    public static Map<String, EnvironmentValue<?>> minecraftEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("SERVER_JARFILE", EnvironmentValue.of("server.jar"));
        map.put("VERSION", EnvironmentValue.of("1.8.8"));
        return map;
    }

    private final String dockerImage;
    private final ApplicationEgg egg;
    private final Map<String, EnvironmentValue<?>> enviroment;
}
