package up.visulog.gitrawdata;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;

import java.io.File;

public class Util {
    public static boolean doesRepoExists(String path) {
        try {
            Git.open(new File(path));
        } catch (Exception e) {
            System.out.println("This path is not a valid repository!");
            return false;
        }

        return true;
    }

    public static boolean doesBranchExists(String path, String branch) {
        try {
            if (Git.open(new File(path)).branchList().setListMode(ListBranchCommand.ListMode.REMOTE)
                    .call().stream().noneMatch(b -> (branch).equals(b.getName()))) {
                System.out.println("This branch is not part of the repository!");
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }
}
