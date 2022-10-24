package project.kazumy.realhosting.discord.services.panel;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.panel.exceptions.WrongEmailException;
import project.kazumy.realhosting.discord.services.panel.exceptions.WrongUsernameException;
import project.kazumy.realhosting.discord.services.payment.plan.PlanBuilder;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class PanelManager extends BaseService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(\\S+)$");
    private static final Pattern USER_PATTERN = Pattern.compile("([A-z]{1,15}\\d{0,9}[^\\W_])");

    private final Set<Integer> PORT_RANGE = new HashSet<>();

    @Getter private PteroApplication application;

    public PanelManager() {
        super(1L);
    }

    public void createUser(UserPanel user, Consumer<ApplicationUser> consumer) throws Exception {
        application.getUserManager()
                .createUser()
                .setUserName(user.getUserName())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .executeAsync(success -> {
                    consumer.accept(success);
                });
    }

    public void createServer(ApplicationUser user, ServerType serverType, PlanBuilder plan, Consumer<ApplicationServer> server) {
        application.createServer()
                .setOwner(user)
                .setEgg(serverType.getEgg())
                .setEnvironment(serverType.getEnviroment())
                .setCPU(plan.getPlanType().getCpu())
                .setBackups(plan.getPlanType().getBackup())
                .setDisk(plan.getPlanType().getDisk(), DataType.GB)
                .setMemory(plan.getPlanType().getMemory(), DataType.GB)
                .setDatabases(plan.getPlanType().getDatabase())
                .setName(plan.getTitle())
                .setAllocations(1L)
                .setPortRange(PORT_RANGE)
                .setDescription(plan.getPlanId())
                .executeAsync(server::accept);
    }

    public void createServer(ApplicationUser user, ServerType serverType, PlanBuilder plan) {
        application.createServer()
                .setOwner(user)
                .setEgg(serverType.getEgg())
                .setEnvironment(serverType.getEnviroment())
                .setDockerImage(serverType.getDockerImage())
                .setCPU(plan.getPlanType().getCpu())
                .setBackups(plan.getPlanType().getBackup())
                .setDisk(plan.getPlanType().getDisk(), DataType.GB)
                .setMemory(plan.getPlanType().getMemory(), DataType.GB)
                .setDatabases(plan.getPlanType().getDatabase())
                .setName(plan.getTitle())
                .setAllocations(1L)
                .setPortRange(PORT_RANGE)
                .setDescription(plan.getPlanId());
    }

    public static boolean checkUserFields(UserPanel userPanel) throws WrongEmailException, WrongUsernameException {
        if (!EMAIL_PATTERN.matcher(userPanel.getEmail()).matches())
            throw new WrongEmailException();
        if (!USER_PATTERN.matcher(userPanel.getUserName()).matches())
            throw new WrongUsernameException();

        return true;
    }

    public ApplicationUser getUserByEmail(String email) {
        return application.retrieveUsersByEmail(email, true)
                .execute().get(0);
    }

    public void emailMenu(Configuration config, Ticket ticket, JDA jda) {
        val embed = config.getEmbedMessageFromConfig(config, "panel");
        ticket.getTicketChannel(jda)
                .sendMessageEmbeds(embed.setColor(Color.GREEN).build()).queue();
    }

    @Override
    public BaseService service(JDA jda, Configuration config) {
        for(int i = 5000; i <= 5500; i++) PORT_RANGE.add(i);

        application = PteroBuilder.createApplication(config.getString("bot.panel.url"), config.getString("bot.panel.token"));
        return this;
    }
}
