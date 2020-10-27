package up.visulog.cli.command;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** This is a unit test class to test the makeConfigFromCommandLineArgs task. */
public class TestVisulog {
    /**
     TODO: One can also add integration tests here:
     - Run the whole program with some valid options and look whether the output has a valid format.
     - Run the whole program with bad command and see whether something that looks like help is printed.
     */
    @Test
    public void testArgumentParser() {
        var config1 = new Visulog().makeConfigFromCommandLineArgs(new String[]{".", "--plugins=countCommits"});
        assertTrue(config1.isPresent());

        var config2 = new Visulog().makeConfigFromCommandLineArgs(new String[] {"--nonExistingOption"});
        assertFalse(config2.isPresent());

        var config3 = new Visulog().makeConfigFromCommandLineArgs(new String[]{".", "-h"});
        assertFalse(config3.isPresent());
    }
}
