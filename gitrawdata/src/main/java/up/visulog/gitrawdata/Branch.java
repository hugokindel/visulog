package up.visulog.gitrawdata;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Branch {
    public final String id;
    public final String name;

    public Branch(String id, String name ){
        this.id = id;
        this.name=name;

    }
    public static List<Branch> parseAllBranchFromRepository(Path gitPath) {
        try {
            Git git = Git.open(new File(gitPath.toAbsolutePath().toString())) ;
            List<Ref> call = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
            List<Branch> branch = new ArrayList<>();
            for (Ref ref : call) {
                branch.add(revBranchToBranch(ref));
            }
            return branch;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Branch revBranchToBranch(Ref ref) {
        var  id = ref.getObjectId().getName();
        var name = ref.getName();
        return new Branch(id , name);
    }
    public String toString(){
        return "Branch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
