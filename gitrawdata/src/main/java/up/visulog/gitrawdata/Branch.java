package up.visulog.gitrawdata;

import java.util.List;

public class Branch {
    public final String id;
    public final String date;
    public final List<String> author;
    public final List<Commit> commit;


    public Branch(String id, String date, List<String> author, List<Commit> commit ){
        this.id = id;
        this.date = date;
        this.author = author;
        this.commit = commit;
    }


}
