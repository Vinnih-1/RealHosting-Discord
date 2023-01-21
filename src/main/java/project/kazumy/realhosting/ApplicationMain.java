package project.kazumy.realhosting;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.val;
import project.kazumy.realhosting.database.SQLProvider;
import project.kazumy.realhosting.discord.DiscordMain;

public class ApplicationMain {

    private static SQLExecutor executor;

    public static void main(String[] args) {
        val application = new ApplicationMain();

        executor = SQLProvider.of(application).createDefaults();
        new DiscordMain("MTA0NjQwOTU2OTczMzIwMTkzMQ.G-bg17.99VpctJRT7718XJMjvMSWGuBzmi2L31FjARKzk");
    }
}