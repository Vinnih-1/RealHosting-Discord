package project.kazumy.realhosting.discord.services.payment.plan;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PlanType {

    BASIC(100L, 1L, 5L, 1L, 1L),
    MEDIAN(100L, 2L, 5L, 1L, 1L),
    ADVANCED(200L, 4L, 10L, 5L, 2L),
    ULTRA(400L, 8L, 5L, 5L, 2L),
    MEGA(600L, 16L, 5L, 10L, 4L);

    @Getter private long cpu, memory, disk, backup, database;
}
