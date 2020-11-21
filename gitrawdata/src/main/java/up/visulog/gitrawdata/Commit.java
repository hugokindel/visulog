package up.visulog.gitrawdata;

import java.io.IOException;
import java.text.SimpleDateFormat;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;


/**
 * Class that represent a git commit with data such as id, date, etc
 */
public class Commit {

    public final String id;
    public final String date; // TODO : String format for date is a temporary solution

    /** Author of the commit. */
    public final String author;

    /** Written description of the commit. */
    public final String description;


    /**
     *  Create a new Commit
     *
     * @param id ID of the commit
     * @param author Commit's author (mail)
     * @param date Commit's date
     * @param description Commit's message
     */
    public Commit(String id, String author, String date, String description) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Parses a log item and outputs a commit object. Exceptions will
     * be thrown in case the input does not have the proper format.
     */
    public static Commit parse (Repository repo, AnyObjectId id) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        try (RevWalk walk = new RevWalk(repo)) {
            RevCommit rCommit = walk.parseCommit(id);
            walk.dispose();
            return commitOfRevCommit(id, rCommit);
        }
    }


    /**
     * Transform a JGit revCommit into a regular Commit object.
     */
    public static Commit commitOfRevCommit (AnyObjectId id, RevCommit rCommit){
        var  author = rCommit.getAuthorIdent();
        var name = author.getName();
        var email = author.getEmailAddress();
        var time = author.getWhen().getTime();
        return new Commit(id.getName(), name + " (" + email+")", stringOfTime(time), rCommit.getFullMessage());
    }

    /**
     * Transforms a time encoded as long into a string with
     * the git log format.
     */
    static String stringOfTime(long time) {
        var tmp = new SimpleDateFormat("dd : ww : yyyy");
        return tmp.format(time);
    }


}
