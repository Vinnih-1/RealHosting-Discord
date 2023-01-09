import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDABuilder;
import project.kazumy.realhosting.discord.configuration.embed.CloseTicketEmbedValue;
import project.kazumy.realhosting.discord.configuration.embed.PanelEmbedValue;
import project.kazumy.realhosting.discord.configuration.embed.PaymentEmbedValue;
import project.kazumy.realhosting.discord.configuration.menu.ServerMenuValue;
import project.kazumy.realhosting.discord.configuration.registry.ConfigurationRegistry;

public class MainRealTest {
    @SneakyThrows
    public static void main(String[] args) {
        new ConfigurationRegistry().register();
        val jda = JDABuilder.createDefault("MTA0NjQwOTU2OTczMzIwMTkzMQ.GPSTyO.0jFfMT1CnG5LIOF0xd1ESmiVZpfuhf-E5NtQ8I")
                .addEventListeners(new EventListenerTest())
                .build().awaitReady();
        val guild = jda.getGuildById("896553346738057226");
        guild.upsertCommand("modals", "teste de modals").queue();
        guild.getTextChannelById("1043521638223851520").sendMessageEmbeds(PanelEmbedValue.get(PanelEmbedValue::toEmbed))
                .addActionRow(ServerMenuValue.instance().toMenu("server"))
                .queue();
    }
}
