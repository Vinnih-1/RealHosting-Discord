package project.kazumy.realhosting.discord.model.plan.pre;

import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;

import java.io.File;
import java.math.BigDecimal;

/**
 * @author Vinícius Albert
 */
public interface PrePlan {

    /**
     * Retorna o ientificador único do plano pré-moldado.
     *
     * @return identificador.
     */
    String getId();

    /**
     * Retorna o título do plano pré-moldado.
     *
     * @return título.
     */
    String getTitle();

    /**
     * Retorna o preço do plano pré-moldado.
     *
     * @return preço do plano.
     */
    BigDecimal getPrice();

    /**
     * Retorna o emoji unicode do plano, para uso no discord.
     *
     * @return emoji do plano.
     */
    UnicodeEmoji getEmoji();

    /**
     * Retorna o tipo do plano, para discernir dos
     * outros planos pré-moldados.
     *
     * @return tipo do plano.
     */
    String getType();

    /**
     * Retorna uma imagem do plano, localizado na pasta
     * 'resources' para ser usado como logo do plano.
     *
     * @return logo do plano.
     */
    File getLogo();

    /**
     * Retorna a descrição do plano pré-moldado.
     *
     * @return descrição do plano.
     */
    String getDescription();

    /**
     * Retorna todas as especificações do plano sobre
     * hardware como ram, cpu, disco, etc...
     *
     * @return hardware do plano pré-moldado.
     */
    PlanHardware getHardware();
}
