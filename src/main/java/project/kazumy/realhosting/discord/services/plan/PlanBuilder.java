package project.kazumy.realhosting.discord.services.plan;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

import java.math.BigDecimal;

@Builder
@Getter
@ToString
public class PlanBuilder {

    private String skuId, description, logo, title;
    private BigDecimal price;
    private UnicodeEmoji emojiUnicode;

}
