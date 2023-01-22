package project.kazumy.realhosting.configuration.basic;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

@Getter @Accessors(fluent = true)
@ConfigSection("bot.payment.mercado-pago")
@ConfigFile("config.yml")
public class PaymentValue implements ConfigurationInjectable {

    @Getter private static final PaymentValue instance = new PaymentValue();

    @ConfigField("access-token") public String accessToken;
    @ConfigField("external-pos-id") public String posId;
    @ConfigField("user-id") public Integer userId;

    public static <T> T get(Function<PaymentValue, T> function) {
        return function.apply(instance);
    }
}
