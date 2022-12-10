package project.kazumy.realhosting.discord.services.payment.plan;


import com.mattmalec.pterodactyl4j.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PlanType {

    BOT(50L, 512L, 1L, 1L, 1L, DataType.MB),
    BASIC(100L, 1L, 5L, 1L, 1L, DataType.GB),
    MEDIAN(100L, 2L, 5L, 1L, 1L, DataType.GB),
    ADVANCED(200L, 4L, 10L, 5L, 2L, DataType.GB),
    MEGA(400L, 8L, 10L, 5L, 2L, DataType.GB),
    ULTRA(600L, 16L, 10L, 10L, 4L, DataType.GB);

    @Getter private long cpu, memory, disk, backup, database;
    @Getter private DataType dataType;
}
