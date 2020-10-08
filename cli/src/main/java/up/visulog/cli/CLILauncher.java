package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Optional;

/**
 * it calls the other modules and print an HTML div representation
 * with the result of the Configuration make from command line arguments
 */
public class CLILauncher {

    /**
     * Print the results of the Config as an HTML div representation
     * or print an error message
     * ( and the list of options )
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {

        var config = makeConfigFromCommandLineArgs(args);

        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            System.out.println(results.toHTML());
        } else displayHelpAndExit();

    }

    /**
     * analyse all the arguments and create plugins from them ( options of arguments can be load from a file )
     * or/and save options to a file
     *
     * @param args The command line arguments
     * @return a Configuration of a hash map that contains plugins and a Path
     */
    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {

        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();

        for (var arg : args) {

            if (arg.startsWith("--")) {
                String[] parts = arg.split("=");

                /** Only one option per argument */
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            /** create a plugin based on the option */
                            // TODO: parse argument and make an instance of PluginConfig
                            // Let's just trivially do this, before the TODO is fixed:
                            plugins.put(pValue, new PluginConfig() {});

                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
                            break;
                        default:
                            return Optional.empty();
                    }
                }
            } else {
                gitPath = FileSystems.getDefault().getPath(arg);
            }
        }

        return Optional.of(new Configuration(gitPath, plugins));
    }

    /**
     * Print an error message
     * ( and the list of options that are available)
     */
    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        /**TODO: print the list of options and their syntax
         * Temporary solution
        */
        System.out.println("Options :");
        System.out.println("- count the commits of every users. Syntax : countCommits");
        System.exit(0);
    }
}
