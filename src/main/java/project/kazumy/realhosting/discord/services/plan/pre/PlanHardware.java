package project.kazumy.realhosting.discord.services.plan;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanHardware {

    private long cpu, ram, disk, backup, database;
}
