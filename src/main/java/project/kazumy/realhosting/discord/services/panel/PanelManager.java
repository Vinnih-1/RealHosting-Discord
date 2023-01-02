package project.kazumy.realhosting.discord.services.panel;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.ServerStatus;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.basic.PanelValue;
import project.kazumy.realhosting.discord.configuration.embed.PreExpirationEmbedValue;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.panel.exceptions.EmailAlreadyExistsException;
import project.kazumy.realhosting.discord.services.panel.exceptions.UsernameAlreadyExistsException;
import project.kazumy.realhosting.discord.services.panel.exceptions.WrongEmailException;
import project.kazumy.realhosting.discord.services.panel.exceptions.WrongUsernameException;
import project.kazumy.realhosting.discord.services.plan.PlanBuilder;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PanelManager extends BaseService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.+)@(\\S+)$");
    private static final Pattern USER_PATTERN = Pattern.compile("([A-z]{1,15}\\d{0,9}[^\\W_])");

    private final Set<Integer> PORT_RANGE = new HashSet<>();

    @Getter private PteroApplication application;

    public PanelManager() {
        super(1L);
    }

    public void expireServerTimer(Guild guild) {
        val timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InitBot.paymentManager.getExpiredPlans(1L)
                        .stream().filter(plan -> !plan.isNotified())
                        .forEach(plan -> {
                            Logger.getGlobal().info(String.format("Notificando o autor do plano %s por falta de pagamento...", plan.getPlanData().getPlanId()));
                            guild.retrieveMemberById(plan.getPlanData().getUserId()).queue(member -> {
                                member.getUser()
                                        .openPrivateChannel().queue(channel -> {
                                            channel.sendMessageEmbeds(PreExpirationEmbedValue.instance().toEmbed(plan)).queue();
                                            plan.setNotified(true);
                                            plan.saveConfig();
                                            Logger.getGlobal().info(String.format("Autor %s notificado sobre a falta de pagamento", plan.getPlanData().getUserAsTag()));
                                        });
                                    });
                        });

                InitBot.paymentManager.getExpiredPlans()
                        .forEach(plan -> {
                            val server = getServerByPlanId(plan.getPlanData().getPlanId());
                            if (server.getStatus() == ServerStatus.SUSPENDED) return;
                            server.getController().suspend().execute();
                            plan.disablePlan();
                            Logger.getGlobal().info(String.format("O servidor do ID %s foi suspenso por falta de pagamento!", plan.getPlanData().getPlanId()));
                        });
            }
        }, 0L, 10000L);
    }

    public void createUser(UserPanel user, Consumer<ApplicationUser> consumer) {
        application.getUserManager()
                .createUser()
                .setUserName(user.getUserName())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .executeAsync(consumer::accept);
    }

    public void createServer(ApplicationUser user, ServerType serverType, PlanBuilder plan, Consumer<ApplicationServer> server) {
        application.createServer()
                .setOwner(user)
                .setEgg(serverType.getEgg())
                .setEnvironment(serverType.getEnviroment())
                .setCPU(plan.getPlanType().getCpu())
                .setBackups(plan.getPlanType().getBackup())
                .setDisk(plan.getPlanType().getDisk(), plan.getPlanType().getDataType())
                .setMemory(plan.getPlanType().getMemory(), plan.getPlanType().getDataType())
                .setDatabases(plan.getPlanType().getDatabase())
                .setName(plan.getPlanData().getTitle())
                .setAllocations(1L)
                .setPortRange(PORT_RANGE)
                .setDescription(plan.getPlanData().getPlanId())
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
                .setMemory(plan.getPlanType().getMemory(), plan.getPlanType().getDataType())
                .setDatabases(plan.getPlanType().getDatabase())
                .setName(plan.getPlanData().getTitle())
                .setAllocations(1L)
                .setPortRange(PORT_RANGE)
                .setDescription(plan.getPlanData().getPlanId());
    }

    public static boolean checkUserFields(UserPanel userPanel) throws WrongEmailException, WrongUsernameException, EmailAlreadyExistsException, UsernameAlreadyExistsException {
        if (!EMAIL_PATTERN.matcher(userPanel.getEmail()).matches())
            throw new WrongEmailException();
        if (!USER_PATTERN.matcher(userPanel.getUserName()).matches())
            throw new WrongUsernameException();
        if (InitBot.panelManager.userExistsByEmail(userPanel.getEmail()))
            throw new EmailAlreadyExistsException();
        if (InitBot.panelManager.userExistsByUsername(userPanel.getUserName()))
            throw new UsernameAlreadyExistsException();

        return true;
    }


    public ApplicationUser getUserByUsername(String username) {
        return application.retrieveUsersByUsername(username, true).execute().get(0);
    }

    public List<ApplicationUser> getUsersByUsername(String username) {
        return application.retrieveUsersByUsername(username, true).execute();
    }

    public ApplicationUser getUserByEmail(String email) {
        return application.retrieveUsersByEmail(email, true)
                .execute().get(0);
    }

    public List<ApplicationUser> getUsersByEmail(String email) {
        return application.retrieveUsersByEmail(email, true)
                .execute();
    }

    public List<ApplicationServer> getServers() {
        return application.retrieveServers().execute();
    }

    public ApplicationServer getServerByPlanId(String planId) {
        return getServers().stream()
                .filter(server -> server.getDescription().equals(planId))
                .findAny().get();
    }

    public boolean serverExistsByPlanId(String planId) {
        return getServers().stream()
                .anyMatch(server -> server.getDescription().equals(planId));
    }

    public boolean userExistsByEmail(String email){
        return !getUsersByEmail(email).isEmpty();
    }

    public boolean userExistsByUsername(String username) {
        return !getUsersByUsername(username).isEmpty();
    }

    @Override
    public BaseService service(JDA jda) {
        System.out.println(PanelValue.get(PanelValue::url) + PanelValue.get(PanelValue::token));

        for(int i = 5000; i <= 5500; i++) PORT_RANGE.add(i);
        application = PteroBuilder.createApplication(PanelValue.get(PanelValue::url), PanelValue.get(PanelValue::token));

        return this;
    }
}
