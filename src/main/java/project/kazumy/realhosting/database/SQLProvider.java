package project.kazumy.realhosting.database;

import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import lombok.val;
import project.kazumy.realhosting.ApplicationMain;

import java.io.File;

@Data(staticConstructor = "of")
public class SQLProvider {

    private final ApplicationMain applicationMain;

    public SQLExecutor createDefaults() {
        val connector = SQLiteDatabaseType.builder()
                .file(new File("database/realhosting.db"))
                .build().connect();
        val executor = new SQLExecutor(connector);
        executor.updateQuery("CREATE TABLE ticket (\n" +
                "    id           INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                         NOT NULL,\n" +
                "    name         TEXT    NOT NULL,\n" +
                "    owner        TEXT    NOT NULL,\n" +
                "    category     TEXT    NOT NULL,\n" +
                "    participants TEXT    NOT NULL,\n" +
                "    history      TEXT    NOT NULL,\n" +
                "    creation     TEXT    NOT NULL,\n" +
                "    cancellation TEXT    NOT NULL\n" +
                ");");

        return executor;
    }
}
