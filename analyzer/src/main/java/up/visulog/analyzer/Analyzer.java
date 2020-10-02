package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.lang.reflect.InvocationTargetException;
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

        // TODO: try running them in parallel
        for (var plugin: plugins) plugin.run();

        // Store the results together in an AnalyzerResult instance and return it.
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
        try {
            String className = (pluginName.contains(".") ? pluginName : "up.visulog.analyzer.plugin." + pluginName);
            Class<? extends AnalyzerPlugin> classType = Class.forName(className).asSubclass(AnalyzerPlugin.class);
            return Optional.of(classType.getDeclaredConstructor(Configuration.class).newInstance(config));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }
    }

}
