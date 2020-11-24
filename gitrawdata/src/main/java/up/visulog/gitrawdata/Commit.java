package up.visulog.gitrawdata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;


/**
 * Class that represent a git commit with data such as id, date, etc
 */
public class Commit implements Comparable<Commit>{

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

    /**
     * Parse all commits from the repository at the given path.
     *
     * @param gitPath The path of the repository.
     * @return a list of commits.
     */
    public static List<Commit> parseAllFromRepository(Path gitPath) {
        try {
            Git git = Git.open(new File(gitPath.toAbsolutePath().toString())) ;
            Iterable<RevCommit> iterableCommits = git.log().all().call();
            List<Commit> commits = new ArrayList<>();

            for (RevCommit commit : iterableCommits) {
                commits.add(revCommitToCommit(commit));
            }

            return commits;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Transform a JGit revCommit into a regular Commit object.
     *
     * @param rCommit The commit to transform.
     * @return the commit.
     */
    private static Commit revCommitToCommit(RevCommit rCommit){
        var  author = rCommit.getAuthorIdent();
        var name = author.getName();
        var email = author.getEmailAddress();
        var time = author.getWhen().getTime();
        return new Commit(rCommit.getId().getName(), name + " (" + email+")", stringOfTime(time), rCommit.getFullMessage());
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
     * Transforms a time encoded as long into a string with
     * the git log format.
     */
    private static String stringOfTime(long time) {
        var tmp = new SimpleDateFormat("dd/ww/yyyy");
        return tmp.format(time);
    }
}