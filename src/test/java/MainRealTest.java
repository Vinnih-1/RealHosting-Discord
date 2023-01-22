import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.SneakyThrows;
import lombok.val;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.database.SQLProvider;
import project.kazumy.realhosting.discord.services.ticket.adapter.TicketAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class MainRealTest {
    @SneakyThrows
    public static void main(String[] args) {
        val executor = SQLProvider.of().createDefaults();
        val ticket = executor.resultOneQuery("SELECT * FROM ticket WHERE id = ?",
                s -> s.set(1, 1),
                TicketAdapter.class);
        System.out.println(ticket);
    }
}
