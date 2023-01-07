package project.kazumy.realhosting.model.plan.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import project.kazumy.realhosting.model.plan.pre.PrePlan;
import project.kazumy.realhosting.model.plan.pre.PlanHardware;

import java.io.File;
import java.math.BigDecimal;

/**
 * @author Vinícius Albert
 */
@Getter
@ToString
@RequiredArgsConstructor
public class PrePlanImpl implements PrePlan {

    private final String id, title, type, description;
    private final BigDecimal price;
    private final UnicodeEmoji emoji;
    private final File logo;
    private final PlanHardware hardware;
}
