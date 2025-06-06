package uk.firedev.alan;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class Server {

    protected Guild guild;

    public Server() {}

    public abstract @NotNull String getId();

    public abstract @NotNull Consumer<JDABuilder> preInit();

    public void load(@NotNull JDA jda) {
        this.guild = jda.getGuildById(getId());
        if (this.guild == null) {
            System.out.println("Could not load server " + getId());
        } else {
            System.out.println("Loaded server " + guild.getName());
        }
    }

    public abstract @NotNull Consumer<CommandListUpdateAction> commandInit();

    public @Nullable Guild getGuild() {
        return this.guild;
    }

}
