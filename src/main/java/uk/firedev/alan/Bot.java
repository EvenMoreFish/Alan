package uk.firedev.alan;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class Bot {

    protected final List<Server> servers = new ArrayList<>();

    private JDA bot;

    public Bot() {}

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

    protected abstract JDABuilder initializeBuilder();

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
        servers.forEach(server -> server.load(this.bot));
    }

    private void updateCommands() {
        CommandListUpdateAction commands = this.bot.updateCommands();
        loadCommands(commands);
        commands.queue();
    }

    private void preInit(@NotNull JDABuilder builder) {
        servers.forEach(server -> server.preInit().accept(builder));
    }

    private void loadCommands(@NotNull CommandListUpdateAction commands) {
        servers.forEach(server -> server.commandInit().accept(commands));
    }

}
