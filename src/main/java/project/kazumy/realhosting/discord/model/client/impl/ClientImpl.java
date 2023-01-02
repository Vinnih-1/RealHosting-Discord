package project.kazumy.realhosting.discord.model.client.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.discord.model.client.Client;
import project.kazumy.realhosting.discord.services.plan.Plan;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class ClientImpl implements Client {

    private final String id;
    private final List<Plan> plan;
    private final YamlFile config;
}
