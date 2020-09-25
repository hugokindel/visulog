package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* This is the main analyzer class.
* It provides a wrapper to run various plugins (a task).
* based on a configuration (see the config project) and return a result.
*/
public class Analyzer {
	/** The configuration to use in order to know which plugins to compute. */
    private final Configuration config;

    /** The result obtain at the end of the computation of all plugins. */
    private AnalyzerResult result;

    /**
    * Class constructor.
    *
    * @param config The configuration containing the list of plugins to compute.
    */
    public Analyzer(Configuration config) {
        this.config = config;
    }

    /**
    * Compute the results of all plugins based on the configuration given
    * when creating this class.
    *
    * @return a result containing the result of all plugins.
    */
    public AnalyzerResult computeResults() {
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            var pluginName = pluginConfigEntry.getKey();
            var pluginConfig = pluginConfigEntry.getValue();
            var plugin = makePlugin(pluginName, pluginConfig);
            plugin.ifPresent(plugins::add);
        }
        // run all the plugins
        // TODO: try running them in parallel
        for (var plugin: plugins) plugin.run();

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    /**
    * Create an instance of a plugin.
    *
    * @param pluginName The name of the plugin.
    * @param pluginConfig The config to use when creating the plugin.
    * @return a plugin only if it exists.
    */
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
    	// TODO: find a way so that the list of plugins is not hardcoded in this factory
        switch (pluginName) {
            case "countCommits" : return Optional.of(new CountCommitsPerAuthorPlugin(config));
            default : return Optional.empty();
        }
    }

}
