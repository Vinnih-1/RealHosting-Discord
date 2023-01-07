package project.kazumy.realhosting.discord.model.panel;

import com.mattmalec.pterodactyl4j.DataType;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationServer;
import com.mattmalec.pterodactyl4j.application.entities.ApplicationUser;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import lombok.val;
import org.apache.commons.lang.RandomStringUtils;
import project.kazumy.realhosting.discord.configuration.basic.PanelValue;
import project.kazumy.realhosting.discord.model.plan.Plan;
import project.kazumy.realhosting.discord.services.panel.PanelService;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author Vinícius Albert
 */
public class Panel {

    private PteroApplication application;

    private ApplicationUser user;

    /**
     * Autentica esta instância com o painel da RealHosting,
     * por meio da URL e do token da API do painel.
     *
     * @return uma instância desta classe.
     */
    public Panel authenticate() {
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
    public void syncUserByEmail(String email, Consumer<ApplicationUser> success, Consumer<Throwable> failure) {
        application.retrieveUsersByEmail(email, true).executeAsync(onSuccess -> {
            if (onSuccess.get(0) == null) {
                Logger.getGlobal().severe("Houve uma falha ao sincronizar o usuário a este email!");
                return;
            }
            this.user = onSuccess.get(0);
            success.accept(this.user);
        }, failure);
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
    public void createUser(String firsname, String lastname, String username, String email, Consumer<ApplicationUser> success, Consumer<Throwable> failure) {
        val password = RandomStringUtils.randomAlphanumeric(8);
        application.getUserManager().createUser()
                .setFirstName(firsname)
                .setLastName(lastname)
                .setUserName(username)
                .setEmail(email)
                .setPassword(password)
                .executeAsync(success, failure);
    }

    /**
     * Cria um servidor no painel utilizando as referências
     * de hardware do plano pré-moldado escolhido pelo cliente.
     *
     * @param plan plano do cliente.
     * @param success executado quando o servidor foi criado com êxito.
     * @param failure executado quando houver alguma falha ao criar o servidor.
     */
    public void createServer(Plan plan, Consumer<ApplicationServer> success, Consumer<Throwable> failure) {
        val hardware = plan.getPrePlan().getHardware();

        application.createServer()
                .setOwner(user)
                .setCPU(hardware.getCpu())
                .setMemory(hardware.getRam(), DataType.MB)
                .setDisk(hardware.getDisk(), DataType.MB)
                .setDatabases(hardware.getDatabase())
                .setBackups(hardware.getBackup())
                .setName(plan.getPrePlan().getTitle())
                .setDescription(plan.getId())
                .setEgg(plan.getServerType().getEgg())
                .setEnvironment(plan.getServerType().getEnviroment())
                .setDockerImage(plan.getServerType().getDockerImage())
                .setAllocations(1L)
                .setPortRange(PanelService.PORT_RANGE)
                .executeAsync(success, failure);
    }
}
