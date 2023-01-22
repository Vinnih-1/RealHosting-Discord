package project.kazumy.realhosting.discord.services.ticket.utils;

import lombok.val;
import org.json.simple.JSONObject;

import java.util.List;

public class TicketUtils {

    public static String participantsToJson(List<String> participants) {
        val json = new JSONObject();
        json.put("participants", participants);
        return json.toJSONString();
    }

    public static String historyToJson(List<String> history) {
        val json = new JSONObject();
        json.put("history", history);
        return json.toJSONString();
    }
}
