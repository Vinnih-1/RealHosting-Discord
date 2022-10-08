package project.kazumy.realhosting.discord.services;

import lombok.Data;
import net.dv8tion.jda.api.JDA;
import project.kazumy.realhosting.discord.configuration.Configuration;

@Data
public abstract class BaseService {

    private boolean operating;

    public abstract BaseService loadService(JDA jda, Configuration config);
}
