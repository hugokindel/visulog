package up.visulog.gitrawdata;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TestCommit {
    @Test
    public void testParseCommit() throws IOException, URISyntaxException {
        var commit = Commit.parseAllFromRepository(Paths.get("./.."));
        String expected = "Commit{id='486d76dbfd24ac65eeeeb16e57ae4fd68c8ecb1c', date='08/27/2020', author='Aldric Degorre (adegorre@irif.fr)', description='Ajout de README.md avec définition des grandes lignes du sujet.'}";
        assertNotNull(commit);
        String result = commit.get(commit.size() - 1).toString();
        assertEquals(expected, result);
    }
}
