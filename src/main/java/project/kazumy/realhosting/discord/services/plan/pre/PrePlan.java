package project.kazumy.realhosting.discord.services.plan;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

import java.io.File;
import java.math.BigDecimal;

public interface PrePlan {

    String getId();

    String getTitle();

    BigDecimal getPrice();

    UnicodeEmoji getEmoji();

    String getType();

    File getLogo();

    String getDescription();

    PlanHardware getHardware();
}
