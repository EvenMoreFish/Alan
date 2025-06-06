package uk.firedev.alan;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import uk.firedev.alan.emfserver.Alan;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    public static final GitHub GITHUB;

    static {
        try {
            String auth = Config.get().getGitHubToken();
            GITHUB = new GitHubBuilder().withOAuthToken(auth).build();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void main(String[] args) {
        Alan.get().load();
        System.out.println("Loaded!");
    }

    public static Logger getLogger() {
        return Logger.getLogger("Alan");
    }

}