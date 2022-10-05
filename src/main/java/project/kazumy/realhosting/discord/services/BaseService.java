package project.kazumy.realhosting.discord.services;

import lombok.Data;
import net.dv8tion.jda.api.JDA;

@Data
public abstract class BaseService {

    private boolean operating;

    public abstract void loadService(JDA jda);
}
