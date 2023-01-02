package project.kazumy.realhosting.discord.services.plan.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import project.kazumy.realhosting.discord.services.plan.Plan;
import project.kazumy.realhosting.discord.services.plan.PlanHardware;

import java.io.File;
import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class PlanImpl implements Plan {

    private final String id, title, type, description;
    private final BigDecimal price;
    private final UnicodeEmoji emoji;
    private final File logo;
    private final PlanHardware hardware;
}
