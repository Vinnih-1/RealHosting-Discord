package project.kazumy.realhosting.model.panel;

import com.mattmalec.pterodactyl4j.EnvironmentValue;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationEgg;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ServerType {

    PAPER_SPIGOT("quay.io/pterodactyl/core:java",
            null,
            spigotEnviroment()),

    BUNGEECORD("quay.io/pterodactyl/core:java",
            null,
            bungeecordEnviroment()),

    POCKET_MINE("ghcr.io/parkervcp/yolks:debian",
            null,
            pocketmineEnviroment()),

    NUKKIT("ghcr.io/pterodactyl/yolks:java_17",
            null,
            nukkitEnviroment()),

    BEDROCK("ghcr.io/parkervcp/yolks:debian",
            null,
            bedrockEnviroment()),

    JAVA_BOT("ghcr.io/parkervcp/yolks:java_17",
            null,
            javaDiscordEnviroment()),

    NODEJS_BOT("ghcr.io/parkervcp/yolks:nodejs_18",
            null,
            nodejsDiscordEnviroment());

    public static Map<String, EnvironmentValue<?>> spigotEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("SERVER_JARFILE", EnvironmentValue.of("server.jar"));
        map.put("VERSION", EnvironmentValue.of("1.8.8"));
        return map;
    }

    public static Map<String, EnvironmentValue<?>> bungeecordEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("SERVER_JARFILE", EnvironmentValue.of("bungeecord.jar"));
        map.put("BUNGEE_VERSION", EnvironmentValue.of("latest"));
        return map;
    }

    public static Map<String, EnvironmentValue<?>> pocketmineEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("VERSION", EnvironmentValue.of("pm4"));
        return map;
    }

    public static Map<String, EnvironmentValue<?>> nukkitEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("SERVER_JARFILE", EnvironmentValue.of("server.jar"));
        map.put("NUKKIT_VERSION", EnvironmentValue.of("latest"));
        map.put("DL_PATH", EnvironmentValue.of(""));
        return map;
    }

    public static Map<String, EnvironmentValue<?>> bedrockEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("BEDROCK_VERSION", EnvironmentValue.of("latest"));
        map.put("LD_LIBRARY_PATH", EnvironmentValue.of("."));
        map.put("SERVERNAME", EnvironmentValue.of("Bedrock Dedicated Server"));
        map.put("GAMEMODE", EnvironmentValue.of("survival"));
        map.put("DIFFICULTY", EnvironmentValue.of("easy"));
        map.put("CHEATS", EnvironmentValue.of("false"));
        return map;
    }

    public static Map<String, EnvironmentValue<?>> javaDiscordEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("JARFILE", EnvironmentValue.of("JavaDiscordBot.jar"));
        return map;
    }

    public static Map<String, EnvironmentValue<?>> nodejsDiscordEnviroment() {
        Map<String, EnvironmentValue<?>> map = new HashMap<>();
        map.put("USER_UPLOAD", EnvironmentValue.of("0"));
        map.put("AUTO_UPDATE", EnvironmentValue.of("0"));
        map.put("JS_FILE", EnvironmentValue.of("index.js"));
        return map;
    }

    private final String dockerImage;
    private final ApplicationEgg egg;
    private final Map<String, EnvironmentValue<?>> enviroment;
}
