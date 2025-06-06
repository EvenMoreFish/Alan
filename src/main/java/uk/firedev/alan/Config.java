package uk.firedev.alan;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.Settings;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class Config {

    private static final Config instance = new Config();
    private final YamlDocument document;

    private Config() {
        YamlDocument document;
        try (InputStream resource = Main.class.getClassLoader().getResourceAsStream("config.yml")) {
            document = createDocument(resource);
        } catch (IOException exception) {
            Main.getLogger().log(Level.SEVERE, "Failed to load config. Shutting down.", exception);
            System.exit(0);
            document = null;
        }
        this.document = document;
    }

    public static Config get() {
        return instance;
    }

    private YamlDocument createDocument(@Nullable InputStream resource) throws IOException {
        File file = new File("config.yml");
        Settings[] settings = new Settings[] {
            GeneralSettings.builder().setUseDefaults(false).build(),
            DumperSettings.DEFAULT,
            LoaderSettings.builder().setAutoUpdate(true).build(),
            UpdaterSettings.builder()
                .setVersioning(new BasicVersioning("version"))
                .build()
        };
        return resource == null ? YamlDocument.create(file, settings) : YamlDocument.create(file, resource, settings);
    }

    public YamlDocument getDocument() {
        return this.document;
    }

    public String getBotToken() {
        return this.document.getString("bot-token");
    }

    public String getServerId() {
        return this.document.getString("server-id");
    }

    public String getGitHubToken() {
        return this.document.getString("github-token");
    }

}
