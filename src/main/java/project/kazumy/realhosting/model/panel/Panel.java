package project.kazumy.realhosting.model.panel;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationEgg;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang.RandomStringUtils;
import project.kazumy.realhosting.configuration.basic.PanelValue;
import project.kazumy.realhosting.model.plan.Plan;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Vinícius Albert
 */
public class Panel {

    public static final Set<Integer> PORT_RANGE = new HashSet<>();

    private PteroApplication application;

    private ApplicationUser user;

    @Getter @Setter private ApplicationEgg egg;

    @Getter private String password;

    /**
     * Autentica esta instância com o painel da RealHosting,
     * por meio da URL e do token da API do painel.
     *
     * @return uma instância desta classe.
     */
    public Panel authenticate() {
        for(int i = 5000; i <= 5500; i++) PORT_RANGE.add(i);
        application = PteroBuilder.createApplication(PanelValue.get(PanelValue::url), PanelValue.get(PanelValue::token));
        return this;
    }

    /**
     * Retorna se a instância desta classe está
     * com o usuário sincronizado com o usuário do painel
     *
     * @return verdadeiro caso esteja, falso caso não esteja.
     */
    public boolean userSynced() {
        return this.user != null;
    }

    /**
     * Sincroniza o usuário da instância deste plano, com o
     * usuário do painel, proprietário deste plano.
     *
     * @param email email do usuário especificado.
     */
    public void syncUserByEmail(String email, Consumer<ApplicationUser> success, Consumer<Panel> failure) {
        new Thread(() -> {
            try {
                val user = application.retrieveUsersByEmail(email, true).execute().get(0);
                this.user = user;
                success.accept(user);
            } catch (Exception exception) {
                failure.accept(this);
            }
        }).start();
    }

    public boolean userExistsByUsername(String username) {
        return !application.retrieveUsersByUsername(username, true).execute().isEmpty();
    }

    /**
     * Cria um usuário no painel utilizando as referências passadas como parâmetro.
     *
     * @param firsname primeiro nome do usuário
     * @param lastname sobrenome do usuário
     * @param username nome de usuário.
     * @param email email do usuário.
     * @param success executado quando a operação é bem sucedida.
     * @param failure executado quando há algum erro na operação.
     */
    public void createUser(String firsname, String lastname, String username, String email, Consumer<ApplicationUser> success, Consumer<Void> failure) {
        new Thread(() -> {
            val password = RandomStringUtils.randomAlphanumeric(8);
            this.password = password;
            val user = application.getUserManager().createUser()
                    .setFirstName(firsname)
                    .setLastName(lastname)
                    .setUserName(username)
                    .setEmail(email)
                    .setPassword(password)
                    .execute();
            this.user = user;
            if (user != null) success.accept(user);
            else failure.accept(null);
        }).start();
    }

    /**
     * Cria um servidor no painel utilizando as referências
     * de hardware do plano pré-moldado escolhido pelo cliente.
     *
     * @param plan plano do cliente.
     * @param success executado quando o servidor foi criado com êxito.
     * @param failure executado quando houver alguma falha ao criar o servidor.
     */
    public void createServer(Plan plan, Consumer<ApplicationServer> success, Consumer<Void> failure) {
        new Thread(() -> {
            val hardware = plan.getPrePlan().getHardware();
            val server = application.createServer()
                    .setOwner(user)
                    .setCPU(hardware.getCpu())
                    .setMemory(hardware.getRam(), DataType.MB)
                    .setDisk(hardware.getDisk(), DataType.MB)
                    .setDatabases(hardware.getDatabase())
                    .setBackups(hardware.getBackup())
                    .setName(plan.getPrePlan().getTitle())
                    .setDescription(plan.getId())
                    .setEgg(application.retrieveEggById(application.retrieveNestById(
                            plan.getServerType().getNestId()).execute(),
                            plan.getServerType().getEggId()).execute())
                    .setEnvironment(plan.getServerType().getEnviroment())
                    .setDockerImage(plan.getServerType().getDockerImage())
                    .setAllocations(1L)
                    .setPortRange(PORT_RANGE)
                    .execute();
            if (server != null) success.accept(server);
            else failure.accept(null);
        }).start();
    }
}
