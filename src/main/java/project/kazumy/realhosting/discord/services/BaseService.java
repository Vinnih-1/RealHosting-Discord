package project.kazumy.realhosting.discord.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Data
@RequiredArgsConstructor
public abstract class BaseService {

    private final long property;

    private boolean operating;
    public abstract BaseService service(JDA jda);
}
