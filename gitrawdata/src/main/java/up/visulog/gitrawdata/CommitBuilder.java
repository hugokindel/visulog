package up.visulog.gitrawdata;

import java.util.Date;

/**
 * Class that creates the Commit (Commit only represent it as an object)
 */
public class CommitBuilder {
    private final String id;
    private String author;
    private Date date;
    private String description;
    private String mergedFrom;

    /**
     * Constructor is needed since id is final
     */
    public CommitBuilder(String id) {
        this.id = id;
    }

    /**
     * Set the athor field (author of the commit)
     * @param author
     */
    public CommitBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the date field (date of the commit)
     * @param date
     */
    public CommitBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    /**
     * Set the description field (message of the commit)
     * @param description
     */
    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set the mergedFrom field (the branch the commit is merged from)
     * @param mergedFrom
     */
    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        return this;
    }

    /**
     * Method to use to create a new commit form :
     * @return a Commit
     */
    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom);
    }
}