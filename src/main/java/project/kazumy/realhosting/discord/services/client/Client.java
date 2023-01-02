package project.kazumy.realhosting.discord.services.client;

import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.discord.services.plan.Plan;

import java.util.List;

public interface Client {

    String getId();

    List<Plan> getPlan();

    YamlFile getConfig();
}
