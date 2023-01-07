package project.kazumy.realhosting.discord.model.plan.pre;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Vinícius Albert
 */
@Getter
@Builder
@ToString
public class PlanHardware {

    private long cpu, ram, disk, backup, database;
}
