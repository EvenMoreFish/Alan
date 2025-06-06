package uk.firedev.alan.emfserver;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import uk.firedev.alan.Bot;
import uk.firedev.alan.Config;

import java.util.EnumSet;

public class Alan extends Bot {

    private static final Alan instance = new Alan();

    public static Alan get() {
        return instance;
    }

    private Alan() {
        servers.add(EvenMoreFish.get());
    }

    @Override
    protected JDABuilder initializeBuilder() {
        return JDABuilder.createLight(
            Config.get().getBotToken(),
            EnumSet.allOf(GatewayIntent.class)
        );
    }

}
