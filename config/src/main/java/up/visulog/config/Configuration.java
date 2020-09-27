package up.visulog.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represent configuration from a git command and some plugins.
 */
public class Configuration {

    /**
     * Used git command.
     */
    private final Path gitPath;
    /**
     * Plugins used by the git command.
     */
    private final Map<String, PluginConfig> plugins;

    /**
     * Creates a new configuration.
     * @param gitPath Used git command.
     * @param plugins Plugin used by the git command.
     */
    public Configuration(Path gitPath, Map<String, PluginConfig> plugins) {
        this.gitPath = gitPath;
        this.plugins = Map.copyOf(plugins);
    }

    /**
     * @return Return the used git command
     */
    public Path getGitPath() {
        return gitPath;
    }

    /**
     * @return Return plugin used by the git command.
     */
    public Map<String, PluginConfig> getPluginConfigs() {
        return plugins;
    }
}
