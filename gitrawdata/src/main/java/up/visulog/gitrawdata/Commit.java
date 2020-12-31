package up.visulog.gitrawdata;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Class that represent a git commit with data such as id, date, etc
 */
public class Commit implements Comparable<Commit>{

    public final String id;

    public final String date;

    public final String author;

    public final String mail;

    public final String description;

    public final int numberOfLinesAdded;

    public final int numberOfLinesRemoved;

    public final int numberOfFilesChanged;

    /**
     *  Create a new Commit
     *
     * @param id ID of the commit
     * @param author Commit's author (mail)
     * @param date Commit's date
     * @param description Commit's message
     */
    public Commit(String id, String author, String date, String mail, String description, int numberOfLinesAdded, int numberOfLinesRemoved, int numberOfFilesChanged) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.mail = mail;
        this.description = description;
        this.numberOfLinesAdded = numberOfLinesAdded;
        this.numberOfLinesRemoved = numberOfLinesRemoved;
        this.numberOfFilesChanged = numberOfFilesChanged;
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
            Iterable<RevCommit> iterableCommits = git.log().call();
            List<Commit> commits = new ArrayList<>();

            for (RevCommit commit : iterableCommits) {
                commits.add(revCommitToCommit(commit, git));
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
    private static Commit revCommitToCommit(RevCommit rCommit, Git git) {
        PersonIdent author = rCommit.getAuthorIdent();
        String name = author.getName();
        String email = author.getEmailAddress();
        String time = stringOfTime(author.getWhen().getTime());
        int numberOfLinesAdded = 0;
        int numberOfLinesDeleted = 0;
        int numberOfFilesChanged = 0;

        try {
            if (rCommit.getParentCount() > 0) {
                RevCommit parent = new RevWalk(git.getRepository()).parseCommit(rCommit.getParent(0).getId());
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                df.setRepository(git.getRepository());
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs;
                diffs = df.scan(parent.getTree(), rCommit.getTree());
                numberOfFilesChanged = diffs.size();
                for (DiffEntry diff : diffs) {
                    for (Edit edit : df.toFileHeader(diff).toEditList()) {
                        numberOfLinesAdded += edit.getEndB() - edit.getBeginB();
                        numberOfLinesDeleted += edit.getEndA() - edit.getBeginA();
                    }
                }
            }

            return new Commit(rCommit.getId().getName(), name, time, email, rCommit.getFullMessage(), numberOfLinesAdded, numberOfLinesDeleted, numberOfFilesChanged);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
    @Override
    public int compareTo(Commit o) {
        return this.date.compareTo(o.date);
    }
        /**
         * Transforms a time encoded as long into a string with
         * the git log format.
         */
    private static String stringOfTime(long time) {
        var tmp = new SimpleDateFormat("MM/dd/yyyy");
        return tmp.format(time);
    }
}