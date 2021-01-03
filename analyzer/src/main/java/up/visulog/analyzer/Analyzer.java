package up.visulog.analyzer;

import up.visulog.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

        for (var pluginName : config.pluginNames) {
            Optional<AnalyzerPlugin> plugin = makePlugin(pluginName);
            plugin.ifPresent(plugins::add);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Callable<Object>> tasks = new ArrayList<>(plugins.size());

        for (AnalyzerPlugin plugin : plugins) {
            tasks.add(Executors.callable(plugin::run));
        }

        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        // Store the results together in an AnalyzerResult instance and return it.
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    /**
    * Create an instance of a plugin.
    *
    * @param pluginName The name of the plugin.
    * @return a plugin only if it exists.
    */
    private Optional<AnalyzerPlugin> makePlugin(String pluginName) {
        try {
            String className = (pluginName.contains(".") ? pluginName : "up.visulog.analyzer.plugin." + pluginName);
            Class<? extends AnalyzerPlugin> classType = Class.forName(className).asSubclass(AnalyzerPlugin.class);
            return Optional.of(classType.getDeclaredConstructor(Configuration.class).newInstance(config));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            System.out.println("Unknown plugin: '" + pluginName + "'!");
            return Optional.empty();
        }
    }

    public Configuration getConfig() {
        return config;
    }

    // Modified version of this code for our own needs: https://stackoverflow.com/a/7461653
    public static ArrayList<String> getPluginsList() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<String>();;

        String packageName = "up/visulog/analyzer/plugin";
        packageURL = classLoader.getResource(packageName);

        try {
            if (packageURL.getProtocol().equals("jar")) {
                String jarFileName = URLDecoder.decode(packageURL.getFile(), StandardCharsets.UTF_8);
                jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
                JarFile jarFile = new JarFile(jarFileName);
                Enumeration<JarEntry> jarEntries = jarFile.entries();

                while (jarEntries.hasMoreElements()) {
                    String entryName = jarEntries.nextElement().getName();

                    if(entryName.startsWith(packageName) && entryName.length()>packageName.length()+5){
                        entryName = entryName.substring(packageName.length(),entryName.lastIndexOf('.'));

                        if (!entryName.contains("$Result")) {
                            names.add(entryName.replace("/", ""));
                        }
                    }
                }
            } else {
                URI uri = new URI(packageURL.toString());
                File folder = new File(uri.getPath());
                File[] content = folder.listFiles();
                String entryName;

                for (File actual: content) {
                    entryName = actual.getName();
                    entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                    names.add(entryName);
                }
            }

            return names;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
