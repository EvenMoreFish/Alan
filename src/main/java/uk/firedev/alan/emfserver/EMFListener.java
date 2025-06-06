package uk.firedev.alan.emfserver;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import uk.firedev.alan.Main;

import java.io.IOException;
import java.util.List;

public class EMFListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null || !guild.equals(EvenMoreFish.get().getGuild())) {
            return;
        }
        switch (event.getName()) {
            case "downloads" -> showDownloads(event);
            case "jenkins" -> showJenkins(event);
            case "wiki" -> showWiki(event);
            case "pr" -> showPullRequest(event);
            case "issue" -> showIssue(event);
            case "suggestions" -> showSuggestions(event);
            case "bugs" -> showBugs(event);
            case "bugreport" -> bugReport(event);
            case "featurerequest" -> featureRequest(event);
        }
    }

    private boolean isSupport(@Nullable Member member) {
        if (member == null) {
            return false;
        }
        List<Role> roles = EvenMoreFish.get().getSupportRoles();
        if (roles.isEmpty()) {
            return false;
        }
        for (Role role : member.getRoles()) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    private void showDownloads(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        sendMessage(
            event,
            """
                You can download the plugin from the following places:
                
                Modrinth: https://modrinth.com/plugin/evenmorefish
                GitHub Releases: https://github.com/EvenMoreFish/EvenMoreFish/releases
                Jenkins (Dev Builds): https://ci.codemc.io/job/EvenMoreFish/job/EvenMoreFish""",
            ephemeral
        );
    }

    private void showJenkins(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        sendMessage(event, "You can download dev builds [here](https://ci.codemc.io/job/EvenMoreFish/job/EvenMoreFish)", ephemeral);
    }

    private void showWiki(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        sendMessage(event, "You can view the wiki [here](https://evenmorefish.github.io/EvenMoreFish/)", ephemeral);
    }

    private void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String message, boolean ephemeral) {
        OptionMapping embed = event.getOption("embed");
        boolean suppressEmbeds = embed == null || !embed.getAsBoolean();
        event.getInteraction().reply(message).setSuppressEmbeds(suppressEmbeds).setEphemeral(ephemeral).queue();
    }

    private void showPullRequest(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        OptionMapping option = event.getOption("pr");
        if (option == null) {
            sendMessage(event, "Please provide a Pull Request ID.", true);
            return;
        }
        int prId = option.getAsInt();
        GHPullRequest pullRequest;
        try {
            pullRequest = EvenMoreFish.get().getEMFRepository().getPullRequest(prId);
        } catch (GHFileNotFoundException exception) {
            sendMessage(event, "Pull Request #" + prId + " not found.", true);
            return;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        if (pullRequest == null) {
            sendMessage(event, "Pull Request #" + prId + " not found.", true);
            return;
        }
        sendMessage(event, "Pull Request #" + prId + ": " + pullRequest.getHtmlUrl().toString(), ephemeral);
    }

    private void showIssue(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        OptionMapping option = event.getOption("issue");
        if (option == null) {
            sendMessage(event, "Please provide an Issue ID.", true);
            return;
        }
        int issueId = option.getAsInt();
        GHIssue issue;
        try {
            issue = EvenMoreFish.get().getEMFRepository().getIssue(issueId);
        } catch (GHFileNotFoundException exception) {
            sendMessage(event, "Issue #" + issueId + " not found.", true);
            return;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        if (issue == null) {
            sendMessage(event, "Issue #" + issueId + " not found.", true);
            return;
        }
        sendMessage(event, "Issue #" + issueId + ": " + issue.getHtmlUrl().toString(), ephemeral);
    }

    private void showSuggestions(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        sendMessage(event, "Suggestion Tracker: https://github.com/orgs/EvenMoreFish/projects/4", ephemeral);
    }

    private void showBugs(@NotNull SlashCommandInteractionEvent event) {
        boolean ephemeral = !isSupport(event.getInteraction().getMember());
        sendMessage(event, "Bug Tracker: https://github.com/orgs/EvenMoreFish/projects/2", ephemeral);
    }

    // GitHub Issue Creation

    private void bugReport(@NotNull SlashCommandInteractionEvent event) {
        TextInput title = TextInput.create("title", "title", TextInputStyle.SHORT)
            .setMinLength(1)
            .setRequired(true)
            .build();

        TextInput bug = TextInput.create("bug", "bug", TextInputStyle.PARAGRAPH)
            .setMinLength(1)
            .setRequired(true)
            .build();

        event.getInteraction().replyModal(
            Modal.create("bugreport", "Create a bug report")
                .addComponents(ActionRow.of(title), ActionRow.of(bug))
                .build()
        ).queue();
    }

    private void featureRequest(@NotNull SlashCommandInteractionEvent event) {
        TextInput title = TextInput.create("title", "title", TextInputStyle.SHORT)
            .setMinLength(1)
            .setRequired(true)
            .build();

        TextInput request = TextInput.create("request", "request", TextInputStyle.PARAGRAPH)
            .setMinLength(1)
            .setRequired(true)
            .build();

        event.getInteraction().replyModal(
            Modal.create("featurerequest", "Create a feature request")
                .addComponents(ActionRow.of(title), ActionRow.of(request))
                .build()
        ).queue();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null || !guild.equals(EvenMoreFish.get().getGuild())) {
            return;
        }
        switch (event.getModalId()) {
            case "bugreport" -> {
                String title = event.getValue("title") != null ? event.getValue("title").getAsString() : null;
                String bug = event.getValue("bug") != null ? event.getValue("bug").getAsString() : null;
                if (title == null || bug == null) {
                    event.reply("An error has occurred. Please try again later.").setEphemeral(true).queue();
                    return;
                }

                String finalTitle = "[Bug]: " + title + " (" + event.getUser().getName() + ")";
                String finalBug = "Reported by **" + event.getUser().getName() + "** on Discord\n\n" + bug;

                try {
                    GHRepository repo = Main.GITHUB.getRepository("EvenMoreFish/EvenMoreFish");
                    GHIssue issue = repo.createIssue(finalTitle).body(finalBug).label("bug: unconfirmed").create();
                    event.reply("Thank you for your bug report! You can view it here: " + issue.getHtmlUrl()).setEphemeral(true).queue();
                } catch (IOException e) {
                    event.getInteraction().reply("Failed to connect to GitHub. Please try again later.").setEphemeral(true).queue();
                }
            }
            case "featurerequest" -> {
                String title = event.getValue("title") != null ? event.getValue("title").getAsString() : null;
                String request = event.getValue("request") != null ? event.getValue("request").getAsString() : null;
                if (title == null || request == null) {
                    event.reply("An error has occurred. Please try again later.").setEphemeral(true).queue();
                    return;
                }

                String finalTitle = "[Suggestion (Plugin)]: " + title + " (" + event.getUser().getName() + ")";
                String finalRequest = "Requested by **" + event.getUser().getName() + "** on Discord\n\n" + request;

                try {
                    GHRepository repo = Main.GITHUB.getRepository("EvenMoreFish/EvenMoreFish");
                    GHIssue issue = repo.createIssue(finalTitle).body(finalRequest).label("suggestion: plugin").create();
                    event.reply("Thank you for your request! You can keep track of it here: " + issue.getHtmlUrl()).setEphemeral(true).queue();
                } catch (IOException e) {
                    event.getInteraction().reply("Failed to connect to GitHub. Please try again later.").setEphemeral(true).queue();
                }
            }
        }
    }

}
