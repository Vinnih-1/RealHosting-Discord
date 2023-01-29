package project.kazumy.realhosting.database;

import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import lombok.val;

import java.io.File;

@Data(staticConstructor = "of")
public class SQLProvider {

    public SQLExecutor createDefaults() {
        val connector = SQLiteDatabaseType.builder()
                .file(new File("database/realhosting.db"))
                .build().connect();

        val executor = new SQLExecutor(connector);
        executor.updateQuery("CREATE TABLE IF NOT EXISTS ticket (\n" +
                "    id           INTEGER PRIMARY KEY ON CONFLICT REPLACE AUTOINCREMENT\n" +
                "                         NOT NULL,\n" +
                "    name         TEXT    NOT NULL,\n" +
                "    owner        TEXT    NOT NULL,\n" +
                "    category     TEXT    NOT NULL,\n" +
                "    chat         TEXT    NOT NULL,\n" +
                "    closed       TEXT    NOT NULL,\n" +
                "    feedback     TEXT,\n" +
                "    participants TEXT    NOT NULL,\n" +
                "    history      TEXT    NOT NULL,\n" +
                "    creation     TEXT    NOT NULL,\n" +
                "    cancellation TEXT\n" +
                ");");

        executor.updateQuery("CREATE TABLE IF NOT EXISTS clients (\n" +
                "    id        TEXT (18) UNIQUE ON CONFLICT REPLACE\n" +
                "                        NOT NULL,\n" +
                "    firstname TEXT,\n" +
                "    lastname  TEXT,\n" +
                "    username  TEXT,\n" +
                "    email     TEXT\n" +
                ");\n");

        executor.updateQuery("CREATE TABLE IF NOT EXISTS plans (\n" +
                "    id          TEXT (15) UNIQUE ON CONFLICT REPLACE\n" +
                "                          NOT NULL,\n" +
                "    owner       TEXT (18) NOT NULL,\n" +
                "    intent      TEXT      NOT NULL,\n" +
                "    type        TEXT      NOT NULL,\n" +
                "    stage       TEXT      NOT NULL,\n" +
                "    server      TEXT      NOT NULL,\n" +
                "    creation    TEXT      NOT NULL,\n" +
                "    payment     TEXT      NOT NULL,\n" +
                "    expiration  TEXT      NOT NULL,\n" +
                "    external_id TEXT\n" +
                ");\n");

        executor.updateQuery("CREATE TABLE IF NOT EXISTS coupons (\n" +
                "    name       TEXT NOT NULL\n" +
                "                    UNIQUE ON CONFLICT REPLACE,\n" +
                "    limits     TEXT,\n" +
                "    percentage TEXT NOT NULL,\n" +
                "    createat   TEXT NOT NULL,\n" +
                "    expireat TEXT NOT NULL\n" +
                ");\n");

        return executor;
    }
}
