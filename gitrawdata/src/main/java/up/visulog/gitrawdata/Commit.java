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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Class that represent a git commit with data such as id, date, etc
 */
public class Commit {
    public final Branch branch;
    public final RevCommit commit;
    public final String id;
    public final Date date;
    public final Author author;
    public final String description;
    public final int numberOfLinesAdded;
    public final int numberOfLinesRemoved;
    public final int numberOfFilesChanged;
    public final String format;

    /**
     *  Create a new Commit
     *
     * @param id ID of the commit
     * @param author Commit's author (mail)
     * @param date Commit's date
     * @param description Commit's message
     */
    public Commit(Branch branch, RevCommit commit, String id, Author author, Date date, String description, int numberOfLinesAdded, int numberOfLinesRemoved, int numberOfFilesChanged, String format) {
        this.branch = branch;
        this.commit = commit;
        this.id = id;
        this.author = author;
        this.date = date;
        this.description = description;
        this.numberOfLinesAdded = numberOfLinesAdded;
        this.numberOfLinesRemoved = numberOfLinesRemoved;
        this.numberOfFilesChanged = numberOfFilesChanged;
        this.format = format;
    }

    public static List<Commit> parseAllFromRepo(Repo repo, Date start, Date end, List<Author> aliases, List<String> mailBlacklist, List<String> mailWhitelist, String format) {
        try {
            List<Commit> commits = new ArrayList<>();
            for (RevCommit commit : repo.git.log().call()) {
                commits.add(parseFromJGit(Branch.parseCurrent(repo), commit, repo.git, aliases, mailBlacklist, mailWhitelist, format));
            }
            return filterByDate(commits, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Commit> parseAllFromBranch(Branch branch, Date start, Date end, List<Author> aliases, List<String> exclusionList, List<String> mailWhitelist, String format) {
        try {
            List<Commit> commits = new ArrayList<>();
            for (RevCommit rCommit : branch.repo.git.log().add(branch.repo.repository.resolve(branch.name)).call()) {
                Commit commit = parseFromJGit(branch, rCommit, branch.repo.git, aliases, exclusionList, mailWhitelist, format);
                if (commit != null) {
                    commits.add(commit);
                }
            }
            return filterByDate(commits, start, end);
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
    private static Commit parseFromJGit(Branch branch, RevCommit rCommit, Git git, List<Author> aliases, List<String> mailBlacklist, List<String> mailWhitelist, String format) {
        PersonIdent personIdent = rCommit.getAuthorIdent();
        String id = rCommit.getId().getName();
        Author author;
        Optional<Author> aliasAuthor = aliases.stream().filter(e -> e.is(personIdent.getEmailAddress())).findAny();
        author = aliasAuthor.map(Author::copy).orElseGet(() -> new Author(personIdent.getEmailAddress(), personIdent.getName()));

        if (mailBlacklist.contains(author.getPrimaryMail()) || (mailWhitelist.size() > 0 && mailWhitelist.stream().noneMatch(author::is))) {
            return null;
        }

        Date time = personIdent.getWhen();
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

            return new Commit(branch, rCommit, id, author, time, rCommit.getFullMessage(), numberOfLinesAdded, numberOfLinesDeleted, numberOfFilesChanged, format);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat(format).format(date);
    }

    @Override
    public String toString() {
        return "Commit{" +
                "branch=" + branch +
                ", commit=" + commit +
                ", id='" + id + '\'' +
                ", date=" + date +
                ", author=" + author +
                ", description='" + description + '\'' +
                ", numberOfLinesAdded=" + numberOfLinesAdded +
                ", numberOfLinesRemoved=" + numberOfLinesRemoved +
                ", numberOfFilesChanged=" + numberOfFilesChanged +
                '}';
    }

    private static List<Commit> filterByDate(List<Commit> commits, Date start, Date end) {
        return commits.stream().filter(c -> c.date.compareTo(start) > 0).filter(c -> c.date.compareTo(new Date(end.getTime() + 86400000)) < 0).collect(Collectors.toList());
    }
}