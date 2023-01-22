import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class EventListenerTest extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Modal modal = Modal.create("painel", "Dados do Painel")
                .addActionRows(
                        ActionRow.of(TextInput.create("nome", "Nome", TextInputStyle.SHORT)
                                .setPlaceholder("Insira o seu nome aqui")
                                .setMinLength(1)
                                .setMaxLength(100)
                                .build()
                        ),
                        ActionRow.of(TextInput.create("sobrenome", "Sobrenome", TextInputStyle.SHORT)
                                .setPlaceholder("Insira o seu sobrenome aqui.")
                                .setMinLength(1)
                                .setMaxLength(100)
                                .build()
                        ),
                        ActionRow.of(TextInput.create("usuario", "Usuário", TextInputStyle.SHORT)
                                .setPlaceholder("Insira o seu nome de usuário aqui.")
                                .setMinLength(4)
                                .setMaxLength(40)
                                .build()
                        ),
                        ActionRow.of(TextInput.create("email", "Email", TextInputStyle.SHORT)
                                .setPlaceholder("Insira o seu e-mail aqui")
                                .setMinLength(10)
                                .setMaxLength(40)
                                .build()))
                .build();
        event.replyModal(modal).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        System.out.println(event.getInteraction().getValue("nome").getAsString());
        event.deferReply().setContent("teste").queue();
    }
}
