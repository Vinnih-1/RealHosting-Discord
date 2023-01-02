package project.kazumy.realhosting.discord.services.plan.pre;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PlanHardware {

    private long cpu, ram, disk, backup, database;
}
