package uk.firedev.alan.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.checkbox.Checkbox;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReleaseCommand {

    public static SlashCommandData get() {
        return Commands.slash("release", "Create a new release").addOptions(getOptions());
    }

    private static List<OptionData> getOptions() {
        List<Command.Choice> choices = List.of(
            new Command.Choice("EvenMoreFish", "main"),
            new Command.Choice("EMFPiñata", "pinata"),
            new Command.Choice("EMFFishStew", "stew")
        );

        return List.of(
            new OptionData(OptionType.STRING, "plugin", "Plugin").addChoices(choices).setRequired(true),
            new OptionData(OptionType.STRING, "version", "Version").setRequired(true),
            new OptionData(OptionType.STRING, "description", "Description (Small Changelog)")
        );
    }

    public static void release(@NotNull SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) {
            event.reply("You cannot use this command.").setEphemeral(true).queue();
            return;
        }
        boolean allowed = member.getRoles().stream()
            .map(Role::getIdLong)
            .anyMatch(id -> id.equals(831390121332572170L) || id.equals(831390152571879434L));
        if (!allowed) {
            event.reply("You cannot use this command.").setEphemeral(true).queue();
            return;
        }

        String plugin = event.getOption("plugin").getAsString();
        String version = event.getOption("version").getAsString();
        String description = Optional.ofNullable(event.getOption("description"))
            .map(OptionMapping::getAsString)
            .orElse(null);

        switch (plugin) {
            case "main" -> sendEmbed(member, "EvenMoreFish", "EvenMoreFish", 1359237713555099698L, version, description);
            case "pinata" -> sendEmbed(member, "EMFPinata", "EMFPiñata", 1516197982922608811L, version, description);
            case "stew" -> sendEmbed(member, "EMFFishStew", "EMFFishStew", 1516198059992682546L, version, description);
            default -> throw new IllegalStateException("Unexpected value: " + plugin);
        };
        event.reply("Release created.").setEphemeral(true).queue();
    }

    private static void sendEmbed(@NotNull Member member, @NotNull String location, @NotNull String title, long pingId, @NotNull String version, @Nullable String description) {
        List<String> message = new ArrayList<>();

        message.add("# " + title + " " + version); // Title
        message.add("");

        message.add("<@&" + pingId + ">");
        if (description != null && !description.isBlank()) {
            Arrays.stream(description.split("\n"))
                .map(s -> "> - " + s)
                .forEach(message::add);
        }
        message.add("");
        message.add("This release can be downloaded from the following places:");
        message.add("- <:modrinth:1556680734457077962> [Modrinth](https://modrinth.com/plugin/" + location + "/version/" + version + ")");
        message.add("- <:hangar:1556680710360801423> [Hangar](https://hangar.papermc.io/EvenMoreFish/" + location + "/versions/" + version + ")");
        message.add("- <:github:1556680688227590144> [GitHub](https://github.com/EvenMoreFish/" + location + "/releases/tag/v" + version + ")");
        message.add("");
        message.add("- " + member.getEffectiveName()); // Footer containing author

        Alan.get().getUpdatesChannel().sendMessage(String.join("\n", message))
            .setSuppressEmbeds(true)
            .queue(msg -> msg.crosspost().queue());
    }

}
