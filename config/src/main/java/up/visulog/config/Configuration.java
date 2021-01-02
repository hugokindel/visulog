package up.visulog.config;

import up.visulog.gitrawdata.Author;
import up.visulog.gitrawdata.Branch;
import up.visulog.gitrawdata.Repo;

import java.util.Date;
import java.util.List;

/**
 * Represent configuration from a git command and some plugins.
 */
public class Configuration {
    public final Repo repo;

    public final Branch branch;

    public final List<Author> aliases;

    public final List<String> pluginNames;

    public final Date start;

    public final Date end;

    public final List<String> mailBlacklist;

    public final List<String> mailWhitelist;

    public final String format;

    public Configuration(Repo repo, Branch branch, List<Author> aliases, List<String> pluginNames, Date start, Date end, List<String> mailBlacklist, List<String> mailWhitelist, String format) {
        this.repo = repo;
        this.branch = branch;
        this.aliases = aliases;
        this.pluginNames = List.copyOf(pluginNames);
        this.start = start;
        this.end = end;
        this.mailBlacklist = mailBlacklist;
        this.mailWhitelist = mailWhitelist;
        this.format = format;
    }
}
