package project.kazumy.realhosting.discord.services.payment.plan;

import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

@Builder
@Data
public class PlanData {

    private UnicodeEmoji emojiUnicode;

    private String planId, skuId, userId, externalReference, userAsTag, description, logo, title;

    public User toUser(Guild guild) {
        if (guild.getMemberById(userId) != null)
            return guild.getMemberById(userId).getUser();
        return null;
    }
}
