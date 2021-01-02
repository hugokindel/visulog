package up.visulog.cli.command;

import up.visulog.analyzer.Analyzer;
import up.visulog.analyzer.AnalyzerResult;
import up.visulog.cli.annotation.Command;
import up.visulog.cli.annotation.Option;
import up.visulog.cli.type.Runnable;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Branch;
import up.visulog.gitrawdata.Repo;
import up.visulog.gitrawdata.Util;
import up.visulog.webgen.Webgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This calls the other modules and print out an HTML div representation
 * with the result of the Configuration make from command line arguments.
 */
@Command(name = "cli", version = "1.0.0", description = "Tool for analysis and visualization of git logs.")
public class Visulog extends Runnable {
    /** Option to define which plugins to use. */
    @Option(names = {"-p", "--plugins"}, description = "Add a plugin (by name) to run.", usage = "<plugin>,...")
    protected String[] plugins;

    /** Option to load a config file with specified path. */
    @Option(names = {"--load"}, description = "Load a configuration file which contains a list of plugins to run.", usage = "<path>")
    protected String loadConfig;

    /** Option to save config of this instance to a file to specified path. */
    @Option(names = {"--save"}, description = "Save the configuration file of this command call.", usage = "<path>")
    protected String saveConfig;

    /** Option to specify to not automatically open the result in the browser. */
    @Option(names = {"--dont-open"}, description = "Specifies that we should not automatically launches the result in the browser.")
    protected boolean dontOpen;

    /** Option to add a custom CSS file. */
    @Option(names = {"--css"}, description = "Add a custom CSS file to the generated HTML results.", usage = "<path>,...")
    protected String[] cssToAdd;

    /** Option to select a specific branch to inspect. */
    @Option(names = {"-b", "--branch"}, description = "Select a specific branch to inspect.", usage = "<name>")
    protected String branchToSearch;

    /** Option to select a specific branch to inspect. */
    @Option(names = {"--start"}, description = "Select a specific date to start inspection.", usage = "<dd/MM/yyyy>")
    protected String timeStart;

    /** Option to select a specific branch to inspect. */
    @Option(names = {"--end"}, description = "Select a specific branch to inspect.", usage = "<dd/MM/yyyy>")
    protected String timeEnd;

    /** Option to select a specific branch to inspect. */
    @Option(names = {"--blacklist"}, description = "Select all mails to exclude from authors (aliases will be excluded too).", usage = "<mail>,...")
    protected String[] mailBlacklist;

    /** Option to select a specific branch to inspect. */
    @Option(names = {"--whitelist"}, description = "Select all authors mails to allow in search.", usage = "<mail>,...")
    protected String[] mailWhitelist;

    @Option(names = {"--alias"}, description = "Sets all mail aliases for an author (can be called an infinite number of time, must starts with the author's name followed by all aliases).", usage = "<Name_Of_Author,mail,...>")
    protected Function<String, Void> aliases;

    @Option(names = {"--format"}, description = "Defines a format to use for every date formatting (including --start and --end commands).", usage = "<dd/MM/yyyy>")
    protected String format;

    protected List<Author> authorsWithAliases;

    /** Class constructor. */
    public Visulog() {
        super();

        plugins = new String[0];
        loadConfig = "";
        saveConfig = "";
        dontOpen = false;
        cssToAdd = null;
        branchToSearch = "";
        timeStart = "";
        timeEnd = "";
        authorsWithAliases = new ArrayList<>();
        mailBlacklist = new String[0];
        mailWhitelist = new String[0];
        format = "dd/MM/yyyy";
        aliases = x -> {
            String[] array = x.split(",");
            String name = Arrays.stream(array).findFirst().get().replace("_", " ");
            String[] mails = Arrays.copyOfRange(array, 1, array.length);
            authorsWithAliases.add(new Author(Arrays.asList(mails), Collections.singletonList(name)));
            return null;
        };
    }

    /**
     * Run the command.
     *
     * @param args The command line arguments.
     * @return the error code.
     */
    public int run(String[] args) {
        if (!readArguments(args, Visulog.class)) {
            return 1;
        }

        if (showHelp || showVersion) {
            return 0;
        }

        Path gitPath;

        if (value == null || value.isEmpty()) {
            gitPath = FileSystems.getDefault().getPath(".");
        } else {
            gitPath = FileSystems.getDefault().getPath(value);
        }

        if (!loadConfig.isEmpty()) {
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
                    if (options.get(i).contains("--load=")) {
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
                    if (!arg.contains("--save=")) {
                        fileContent.append(arg).append(" ");
                    }
                }
                fileWriter.write(fileContent.substring(0, fileContent.length() - 1));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!Util.doesRepoExists(gitPath.toAbsolutePath().toString())) {
            return 1;
        }

        if (!branchToSearch.isEmpty() && !Util.doesBranchExists(gitPath.toAbsolutePath().toString(), branchToSearch)) {
            return 1;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date start;
        Date end;

        if (timeStart.isEmpty()) {
            start = new Date(0);
        } else {
            try {
                start = formatter.parse(timeStart);
            } catch (Exception e) {
                System.out.println("Invalid start timestamp!");
                return 1;
            }
        }

        if (timeEnd.isEmpty()) {
            end = new Date();
        } else {
            try {
                end = formatter.parse(timeEnd);
            } catch (Exception e) {
                System.out.println("Invalid end timestamp!");
                return 1;
            }
        }

        Repo repo = Repo.parse(gitPath.toAbsolutePath().toString());
        Branch branch;

        if (branchToSearch.isEmpty()) {
            branch = Branch.parseCurrent(repo);
        } else {
            branch = Branch.parseByName(repo, branchToSearch);
        }

        Optional<Configuration> config = plugins.length == 0 ? Optional.empty() : Optional.of(new Configuration(repo, branch, authorsWithAliases, Arrays.asList(plugins), start, end, Arrays.asList(mailBlacklist), Arrays.asList(mailWhitelist), format));

        if (config.isPresent()) {
            Analyzer analyzer = new Analyzer(config.get());
            AnalyzerResult results = analyzer.computeResults();
            new Webgen(results,analyzer.getConfig().pluginNames).getFile(gitPath, !dontOpen, cssToAdd);
        }

        return 0;
    }
}