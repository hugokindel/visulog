package up.visulog.cli.command;

import up.visulog.analyzer.Analyzer;
import up.visulog.analyzer.AnalyzerResult;
import up.visulog.cli.annotation.Command;
import up.visulog.cli.annotation.Option;
import up.visulog.cli.util.Parser;
import up.visulog.config.Configuration;
import up.visulog.webgen.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * This calls the other modules and print out an HTML div representation
 * with the result of the Configuration make from command line arguments.
 */
@Command(name = "visulog", version = "0.1.0", description = "Tool for analysis and visualization of git logs.")
public class Visulog {
    /** Option to show the help message. */
    @Option(names = {"-h", "--help"}, description = "Show this help message.")
    boolean showHelp;

    /** Option to show the version message. */
    @Option(names = {"-v", "--version"}, description = "Show the current version of this software.")
    boolean showVersion;

    /** Option to define which plugins to use. */
    @Option(names = {"-p", "--plugins"}, description = "Add a plugin (by name) to run.", usage = "<plugin>,...")
    String[] plugins;

    /** Option to load a config file with specified path. */
    @Option(names = {"-l", "--load-config"}, description = "Load a configuration file which contains a list of plugins to run.", usage = "<path>")
    String loadConfig;

    /** Option to save config of this instance to a file to specified path. */
    @Option(names = {"-l", "--save-config"}, description = "Save the configuration file of this command call.")
    String saveConfig;

    /** Class constructor. */
    public Visulog() {
        showHelp = false;
        showVersion = false;
        plugins = new String[0];
        loadConfig = "";
        saveConfig = "";
    }

    /**
     * Run the command.
     *
     * @param args The command line arguments.
     * @return the error code.
     */
    public int run(String[] args) {
        Optional<Configuration> config = makeConfigFromCommandLineArgs(args);

        if (config.isPresent()) {
            Analyzer analyzer = new Analyzer(config.get());
            AnalyzerResult results = analyzer.computeResults();
            new Webgen(results, analyzer.getConfig().getPluginNames()).generate();
        } else {
            displayHelp();
        }

        return 0;
    }

    /**
     * Analyse all the arguments and create plugins from them (options of arguments can be load from a file)
     * or/and save options to a file.
     *
     * @param args The command line arguments.
     * @return a Configuration of a hash map that contains plugins and a Path.
     */
    Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        Path gitPath = FileSystems.getDefault().getPath(".");

        for (String arg : args) {
            if (arg.startsWith("-")) {
                String[] parts = arg.split("=");
                boolean find = false;

                for (Field field : Visulog.class.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Option.class)) {
                        Option option = field.getAnnotation(Option.class);

                        for (String name : option.names()) {
                            if (parts[0].equals(name)) {
                                find = true;

                                try {
                                    if (field.getType() == boolean.class) {
                                        field.set(this, true);
                                    } else {
                                        field.set(this, Parser.parse(parts[1], field.getType()));
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }

                                break;
                            }
                        }

                        if (find) {
                            break;
                        }
                    }
                }

                if (!find) {
                    displayUnknownOption(parts[0]);
                }
            } else {
                // TODO: Add possibility to analyze multiple git repos at once (git path would become an array).
                gitPath = FileSystems.getDefault().getPath(arg);
            }
        }

        if (showHelp) {
            displayHelp();
        }

        if (showVersion) {
            displayVersion();
        }

        if (!loadConfig.isEmpty()) {
            // TODO: Load config from file at path.
            // TODO: Add possibility to load multiple config files at once, load config path should become an array.
        }

        if (!saveConfig.isEmpty()) {
            // TODO: Save config to file.
        }

        if (plugins.length == 0) {
            return Optional.empty();
        }

        return Optional.of(new Configuration(gitPath, Arrays.asList(plugins)));
    }

    /** print an error message to indicate that one of the option asked for is unknown. */
    private static void displayUnknownOption(String option) {
        System.out.println("Unknown option: '" + option + "'!");
        // TODO: Display closest command (use Levenshtein distance between all option names possible).
    }

    /** Print the current version. */
    private static void displayVersion() {
        System.out.println("Version: " + Visulog.class.getAnnotation(Command.class).version());
    }

    /** Print the help message (with the list of options that are available). */
    private static void displayHelp() {
        // TODO: Usage could directly display a simple list of options (similar to how git does it).
        System.out.println("usage: " + Visulog.class.getAnnotation(Command.class).name() + " <path> [options...]");

        System.out.println();

        for (String line : Visulog.class.getAnnotation(Command.class).description()) {
            System.out.println(line);
        }

        System.out.println();

        System.out.println("Options:");
        for (Field field : Visulog.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Option.class)) {
                Option option = field.getAnnotation(Option.class);
                int numberOfNames = option.names().length;

                System.out.print(" \t");

                for (int i = 0; i < numberOfNames; i++) {
                    System.out.print(option.names()[i] + (i == numberOfNames - 1 ? "" : ", "));
                }

                if(!option.usage().isEmpty()) {
                    System.out.print("=" + option.usage());
                }

                System.out.println();

                for (String line : option.description()) {
                    System.out.print(" \t\t");
                    System.out.println(line);
                }
            }

        }
    }
}
