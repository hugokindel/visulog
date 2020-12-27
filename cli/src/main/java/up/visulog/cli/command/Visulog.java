package up.visulog.cli.command;

import up.visulog.analyzer.Analyzer;
import up.visulog.analyzer.AnalyzerResult;
import up.visulog.cli.annotation.Command;
import up.visulog.cli.annotation.Option;
import up.visulog.cli.type.Runnable;
import up.visulog.config.Configuration;
import up.visulog.webgen.Webgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This calls the other modules and print out an HTML div representation
 * with the result of the Configuration make from command line arguments.
 */
@Command(name = "visulog", version = "0.1.0", description = "Tool for analysis and visualization of git logs.")
public class Visulog extends Runnable {
    /** Option to define which plugins to use. */
    @Option(names = {"-p", "--plugins"}, description = "Add a plugin (by name) to run.", usage = "<plugin>,...")
    protected String[] plugins;

    /** Option to load a config file with specified path. */
    @Option(names = {"-l", "--load-config"}, description = "Load a configuration file which contains a list of plugins to run.", usage = "<path>")
    protected String loadConfig;

    /** Option to save config of this instance to a file to specified path. */
    @Option(names = {"-l", "--save-config"}, description = "Save the configuration file of this command call.")
    protected String saveConfig;

    /** Option to output the list of plugins that can be used. */
    @Option(names = {"--list-plugins"}, description = "Output the list of plugins that can be used.")
    protected boolean listPlugins;


    /** Class constructor. */
    public Visulog() {
        super();
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
        readArguments(args, Visulog.class);

        Path gitPath;

        if (listPlugins) {
            try {
                List<String> listClasses = Analyzer.getPluginsList();
                StringBuilder list = new StringBuilder();

                for (int i = 0; i < listClasses.size(); i++) {
                    list.append(i == 0 ? "" : ",").append(listClasses.get(i));
                }

                System.out.println(list.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
        
        if (value.isEmpty()) {
            gitPath = FileSystems.getDefault().getPath(".");
        } else {
            gitPath = FileSystems.getDefault().getPath(value);
        }

        if (!loadConfig.isEmpty()) {
            // TODO: Add possibility to load multiple config files at once, load config path should become an array.
            File f = new File(loadConfig);
            if(f.exists() && !f.isDirectory()) {
                StringBuilder contentBuilder = new StringBuilder();

                try (Stream<String> stream = Files.lines( Paths.get(loadConfig), StandardCharsets.UTF_8))
                {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                String content = contentBuilder.toString().replace("\n", "").replace("\r", "");
                List<String> options = new ArrayList<>();
                options.addAll(Arrays.asList(content.split(" ")));
                options.addAll(Arrays.asList(args));
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).contains("-l=") || options.get(i).contains("--load-config=")) {
                        options.remove(i);
                        i--;
                    }
                }

                loadConfig = "";


                return run(options.toArray(new String[0]));
            }
        }

        if (!saveConfig.isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(saveConfig));
                File configFile = new File(saveConfig);
                FileWriter fileWriter = new FileWriter(configFile);
                StringBuilder fileContent = new StringBuilder();
                for (String arg : args) {
                    if (!arg.contains("--s=") && !arg.contains("--save-config=")) {
                        fileContent.append(arg).append(" ");
                    }
                }
                fileWriter.write(fileContent.substring(0, fileContent.length() - 1));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Optional<Configuration> config = plugins.length == 0 ? Optional.empty() : Optional.of(new Configuration(gitPath, Arrays.asList(plugins)));

        if (config.isPresent()) {
            Analyzer analyzer = new Analyzer(config.get());
            AnalyzerResult results = analyzer.computeResults();
            new Webgen(results,analyzer.getConfig().getPluginNames()).getFile(gitPath);
        }

        return 0;
    }
}