package project.kazumy.realhosting.discord.services.ticket.manager;

import lombok.Data;
import lombok.val;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import project.kazumy.realhosting.configuration.basic.TicketValue;
import project.kazumy.realhosting.discord.services.ticket.Ticket;
import project.kazumy.realhosting.discord.services.ticket.repository.TicketRepository;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Data(staticConstructor = "of")
public class TicketManager {

    private final Guild guild;
    private final TicketRepository repository;

    public void makeTicket(Ticket ticket, Consumer<TextChannel> onSuccess) {
                guild.getCategoryById(TicketValue.get(TicketValue::category))
                .createTextChannel(ticket.getName())
                .addMemberPermissionOverride(Long.parseLong(ticket.getOwner()),
                        Arrays.asList(Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_SEND),
                        Arrays.asList(Permission.ADMINISTRATOR))
                .queue(success -> {
                    onSuccess.accept(success);
                    repository.save(ticket);
                });
    }

    public void saveFeedback(Ticket ticket) {
        repository.feedback(ticket);
    }

    public void cancelTicket(Ticket ticket, Consumer<Void> onSuccess) {
        val channel = guild.getTextChannelById(ticket.getChatId());
        repository.close(ticket);
        onSuccess.accept(null);
        channel.delete().queueAfter(4, TimeUnit.SECONDS);
    }

    public void saveMessages(Ticket ticket, Consumer<Ticket> onSuccess) {
        historyMessages(ticket, messages -> {
            messages.stream()
                    .map(message -> String.format("[%s] - %s: %s (%s)",
                            message.getAuthor().getAsTag(),
                            message.getTimeCreated()
                                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                                            .withZone(ZoneId.of("America/Sao_Paulo"))),
                            message.getContentRaw(),
                            message.getAttachments().isEmpty() ? "" : message.getAttachments().toString()
                    ))
                    .forEach(ticket.getHistory()::add);
            onSuccess.accept(ticket);
        });
    }

    public void saveParticipants(Ticket ticket) {
        historyMessages(ticket, messages -> {
            messages.stream()
                    .filter(message -> !ticket.getParticipants().contains(message.getAuthor().getId()))
                    .map(message -> message.getAuthor().getId())
                    .forEach(ticket.getParticipants()::add);
        });
    }

    public void historyMessages(Ticket ticket, Consumer<List<Message>> consumer) {
        MessageHistory.getHistoryFromBeginning(guild.getTextChannelById(ticket.getChatId()))
                .queue(success -> consumer.accept(success.getRetrievedHistory()));
    }

    public Ticket findTicketByChatId(String chatId) {
        return repository.findByChatId(chatId);
    }
}
