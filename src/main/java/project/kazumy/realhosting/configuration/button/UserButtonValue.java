package project.kazumy.realhosting.configuration.button;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

@Getter
@Accessors(fluent = true)
@ConfigSection("menu")
@ConfigFile("menu.yml")
public class UserButtonValue implements ConfigurationInjectable {

    public static final UserButtonValue instance = new UserButtonValue();



    public static <T> T get(Function<UserButtonValue, T> function) {
        return function.apply(instance);
    }
}
