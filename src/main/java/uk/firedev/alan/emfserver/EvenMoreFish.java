package uk.firedev.alan.emfserver;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHRepository;
import uk.firedev.alan.Config;
import uk.firedev.alan.Main;
import uk.firedev.alan.Server;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// The EMF Support Server
public class EvenMoreFish extends Server {

    private static final EvenMoreFish instance = new EvenMoreFish();

    public static EvenMoreFish get() {
        return instance;
    }

    @NotNull
    @Override
    public String getId() {
        return Config.get().getServerId();
    }

    @NotNull
    @Override
    public Consumer<JDABuilder> preInit() {
        return jdaBuilder -> jdaBuilder.addEventListeners(new EMFListener());
    }

    @NotNull
    @Override
    public Consumer<CommandListUpdateAction> commandInit() {
        return commands -> commands.addCommands(
            Commands
                .slash("downloads", "Retrieve the download links").addOptions(getEmbedData()),
            // /jenkins
            Commands
                .slash("jenkins", "Retrieve the Jenkins link").addOptions(getEmbedData()),
            // /wiki
            Commands
                .slash("wiki", "Retrieves the Wiki link").addOptions(getEmbedData()),
            // /pr
            Commands
                .slash("pr", "Retrieve a Pull Request")
                .addOption(OptionType.INTEGER, "pr", "The Pull Request to retrieve", true)
                .addOptions(getEmbedData()),
            // /issue
            Commands
                .slash("issue", "Retrieve an Issue")
                .addOption(OptionType.INTEGER, "issue", "The Issue to retrieve", true)
                .addOptions(getEmbedData()),
            // /suggestions
            Commands
                .slash("suggestions", "View the suggestion tracker").addOptions(getEmbedData()),
            // /bugs
            Commands
                .slash("bugs", "View the bug tracker").addOptions(getEmbedData()),
            // /featurerequest
            Commands
                .slash("featurerequest", "Request a new feature"),
            // /bugreport
            Commands
                .slash("bugreport", "Report a bug")
        );
    }

    public @NotNull List<Role> getSupportRoles() {
        return Config.get().getDocument().getStringList("support-roles").stream()
            .map(guild::getRoleById)
            .filter(Objects::nonNull)
            .toList();
    }

    public @NotNull GHRepository getEMFRepository() {
        try {
            return Main.GITHUB.getRepository("EvenMoreFish/EvenMoreFish");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private OptionData getEmbedData() {
        return new OptionData(OptionType.BOOLEAN, "embed", "Include the embed? (Default: false)", false);
    }

}
