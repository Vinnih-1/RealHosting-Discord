package project.kazumy.realhosting.discord.services.ticket.adapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import lombok.SneakyThrows;
import lombok.val;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.discord.services.ticket.Ticket;
import project.kazumy.realhosting.discord.services.ticket.category.TicketCategory;
import project.kazumy.realhosting.discord.services.ticket.impl.TicketImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TicketAdapter implements SQLResultAdapter<Ticket> {

    @Override
    @SneakyThrows
    public Ticket adaptResult(SimpleResultSet result) {
        val participants = (JSONObject) new JSONParser().parse((String) result.get("participants"));
        val history = (JSONObject) new JSONParser().parse((String) result.get("history"));
        val cancellation = result.get("cancellation").toString().isEmpty() ? null : LocalDateTime.parse(result.get("cancellation"));

        return TicketImpl.builder()
                .id(result.get("id"))
                .owner(result.get("owner"))
                .name(result.get("name"))
                .category(TicketCategory.valueOf(result.get("category")))
                .chatId(result.get("chat"))
                .closed(Boolean.parseBoolean(result.get("closed")))
                .participants((ArrayList<String>) participants.get("participants"))
                .history((ArrayList<String>) history.get("history"))
                .creation(LocalDateTime.parse(result.get("creation")))
                .cancellation(cancellation)
                .build();
    }
}
