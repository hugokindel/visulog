package up.visulog.gitrawdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Author {
    public final List<String> mails;
    public final List<String> names;

    public Author(String mail, String name) {
        this.mails = new ArrayList<>(Collections.singleton(mail));
        this.names = new ArrayList<>(Collections.singleton(name));
    }

    public Author(List<String> mail, List<String> name) {
        this.mails = mail;
        this.names = name;
    }

    public String getPrimaryMail() {
        return mails.get(0);
    }

    public String getPrimaryName() {
        return names.get(0);
    }

    public boolean is(String mail) {
        return mails.contains(mail);
    }

    @Override
    public String toString() {
        return "Author{" +
                "mail=" + mails +
                ", name=" + names +
                '}';
    }

    public static Author copy(Author author) {
        return new Author(new ArrayList<>(author.mails), new ArrayList<>(author.names));
    }
}
