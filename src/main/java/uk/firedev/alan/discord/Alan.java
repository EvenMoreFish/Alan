package uk.firedev.alan.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import uk.firedev.alan.Config;
import uk.firedev.alan.Main;

import java.util.EnumSet;
import java.util.logging.Level;

public class Alan {

    private static final Alan instance = new Alan();
    private final SupportServer server = SupportServer.get();
    private JDA bot;

    public static Alan get() {
        return instance;
    }

    private Alan() {}

    protected JDABuilder initializeBuilder() {
        return JDABuilder.createLight(
            Config.get().getBotToken(),
            EnumSet.allOf(GatewayIntent.class)
        );
    }

    public void load() {
        JDABuilder builder = initializeBuilder();
        preInit(builder);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        this.bot = buildBot(builder);
        awaitBotReady();
        loadServers();
        updateCommands();
    }

    public JDA getBot() {
        return bot;
    }

    private JDA buildBot(JDABuilder builder) {
        return builder.build();
    }

    private void awaitBotReady() {
        try {
            this.bot.awaitReady();
        } catch (InterruptedException exception) {
            Main.getLogger().log(Level.SEVERE, "Waiting for bot to load was interrupted!", exception);
        }
    }

    private void loadServers() {
        server.load(this.bot);
    }

    private void updateCommands() {
        CommandListUpdateAction commands = this.bot.updateCommands();
        loadCommands(commands);
        commands.queue();
    }

    private void preInit(@NotNull JDABuilder builder) {
        server.preInit().accept(builder);
    }

    private void loadCommands(@NotNull CommandListUpdateAction commands) {
        server.commandInit().accept(commands);
    }
}
