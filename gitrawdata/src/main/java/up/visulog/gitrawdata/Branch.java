package up.visulog.gitrawdata;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Branch {
    public final Ref branch;
    public final Repo repo;
    public final String id;
    public final String name;

    public Branch(Repo repo, Ref branch, String id, String name) {
        this.repo = repo;
        this.branch = branch;
        this.id = id;
        this.name = name;
    }

    public static List<Branch> parseAll(Repo repo, boolean onlyRemote) {
        try {
            List<Branch> branches = new ArrayList<>();
            ListBranchCommand.ListMode listMode = onlyRemote ? ListBranchCommand.ListMode.REMOTE : ListBranchCommand.ListMode.ALL;
            for (Ref ref : repo.git.branchList().setListMode(listMode).call()) {
                branches.add(parseFromJGit(repo, ref));
            }
            return branches;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Branch parseCurrent(Repo repo) {
        try {
            return parseAll(repo, false).stream().filter(b -> {
                try {
                    return b.name.equals(repo.repository.getFullBranch()) || b.id.equals(repo.repository.getFullBranch());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }).findAny().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Branch parseByName(Repo repo, String name) {
        try {
            return parseAll(repo, false).stream().filter(b -> b.name.equals(name)).findAny().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Branch parseFromJGit(Repo repo, Ref ref) {
        var  id = ref.getObjectId().getName();
        var name = ref.getName();
        return new Branch(repo, ref, id, name);
    }

    @Override
    public String toString() {
        return "Branch{" +
                "branch=" + branch +
                ", repo=" + repo +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
