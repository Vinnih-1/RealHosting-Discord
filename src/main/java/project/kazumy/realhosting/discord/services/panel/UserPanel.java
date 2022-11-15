package project.kazumy.realhosting.discord.services.panel;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPanel {

    private String userName, firstName, lastName, email, password;
}
