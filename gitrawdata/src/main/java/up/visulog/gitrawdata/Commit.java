package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Class that represent a git commit with data such as id, date, etc
 */
public class Commit {

    public final String id;
    public final Date date;
    public final String author;
    public final String description;
    public final String mergedFrom;

    /**
     *  Create a new Commit
     *
     * @param id            ID of the commit
     * @param author        Commit's author (mail)
     * @param date          Commit's date
     * @param description   Commit's message
     * @param mergedFrom    Branch the commit is mergedFrom
     */
    public Commit(String id, String author, Date date, String description, String mergedFrom) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.description = description;
        this.mergedFrom = mergedFrom;
    }

    /**
     *  Generate a List of Commit from the git lo command
     *
     * @param gitPath   Command used (git log for the moment)
     * @return          a list of Commit
     * @throws          IOException is can't read the git.log file
     */
    public static List<Commit> parseLogFromCommand(Path gitPath) {
        return parseLog(parseCommand(gitPath, "git", "log", "--date=format:%d/%m/%Y"));
    }

    /**
     * Create a buffer from any git commands
     *
     * @param path      Path to the git.log file (if needed)
     * @param command   Used git command
     * @return          A BufferedReader of the 'command'
     * @throws          RuntimeException if the command can't be run
     */
    public static BufferedReader parseCommand(Path path, String... command) {
        ProcessBuilder builder = new ProcessBuilder(command).directory(path.toFile());

        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running \"" + command + "\"", e);
        }
        InputStream is = process.getInputStream();
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * Read a Buffer and convert it into a List of Commit
     * @param reader    the BufferedReader if the git command
     * @return          a List of Commit
     */
    public static List<Commit> parseLog(BufferedReader reader) {
        var result = new ArrayList<Commit>();
        Optional<Commit> commit = parseCommit(reader);
        while (commit.isPresent()) {
            result.add(commit.get());
            commit = parseCommit(reader);
        }
        return result;
    }

    /**
     * Parses a log item and outputs a commit object. Exceptions will be thrown in case the input does not have the proper format.
     * Returns an empty optional if there is nothing to parse anymore.
     */
    public static Optional<Commit> parseCommit(BufferedReader input) {
        try {

            var line = input.readLine();
            if (line == null) return Optional.empty(); // if no line can be read, we are done reading the buffer
            var idChunks = line.split(" ");
            if (!idChunks[0].equals("commit")) parseError();
            var builder = new CommitBuilder(idChunks[1]);

            line = input.readLine();
            while (!line.isEmpty()) {
                var colonPos = line.indexOf(":");
                var fieldName = line.substring(0, colonPos);
                var fieldContent = line.substring(colonPos + 1).trim();
                switch (fieldName) {
                    case "Author":
                        builder.setAuthor(fieldContent);
                        break;
                    case "Merge":
                        builder.setMergedFrom(fieldContent);
                        break;
                    case "Date":
                        builder.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(fieldContent));
                        break;
                    default: // TODO: warn the user that some field was ignored
                        System.out.println("A field was ignored,please fill it");
                }
                line = input.readLine(); //prepare next iteration
                if (line == null) parseError(); // end of stream is not supposed to happen now (commit data incomplete)
            }

            // now read the commit message per se
            var description = input
                    .lines() // get a stream of lines to work with
                    .takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one (commits are separated by empty lines). Remark: commit messages are indented with spaces, so any blank line in the message contains at least a couple of spaces.
                    .map(String::trim) // remove indentation
                    .reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
            builder.setDescription(description);
            return Optional.of(builder.createCommit());
        } catch (IOException | ParseException e) {
            parseError();
        }
        return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
    }

    /**
     * Helper function for generating parsing exceptions. This function *always* quits on an exception. It *never* returns.
     */
    private static void parseError() {
        throw new RuntimeException("Wrong commit format.");
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "") + //TODO: find out if this is the only optional field
                ", date='" + date + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
