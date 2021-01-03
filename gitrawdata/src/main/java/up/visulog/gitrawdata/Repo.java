package up.visulog.gitrawdata;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.util.List;

public class Repo {
    public final Git git;
    public final Repository repository;
    public final String path;

    public Repo(Git git, Repository repository, String path) {
        this.git = git;
        this.repository = repository;
        this.path = path;
    }

    public static Repo parse(String path) {
        try {
            Git git = Git.open(new File(path));
            return new Repo(git, git.getRepository(), path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "git=" + git +
                ", repository=" + repository +
                ", path='" + path + '\'' +
                '}';
    }
}
