package up.visulog.cli.command;

import org.junit.Test;
import static org.junit.Assert.*;

/** This is a unit test class to test the makeConfigFromCommandLineArgs task. */
public class TestVisulog {
    /** Test if the configuration is created. */
    @Test
    public void testArgumentParser() {
        var config1 = new Visulog().makeConfigFromCommandLineArgs(new String[]{".", "--plugins=countCommits"});
        assertTrue(config1.isPresent());
    }

    /** Test if the program works with good arguments. */
    @Test
    public void testProgramWithRealArguments() {
        Visulog visulog = new Visulog();
        visulog.makeConfigFromCommandLineArgs(new String[]{".", "--plugins=countCommits"});
        assertEquals(1, visulog.plugins.length);
    }

    /** Test if the program works with no arguments (help showed). */
    @Test
    public void testProgramWithNoArguments() {
        Visulog visulog = new Visulog();
        visulog.makeConfigFromCommandLineArgs(new String[] { });
        assertTrue(visulog.showHelp);
    }

    /** Test if tje program works with unknown arguments. */
    @Test
    public void testProgramWithUnknownArguments() {
        Visulog visulog = new Visulog();
        visulog.makeConfigFromCommandLineArgs(new String[] { "--testUnknown" });
        assertEquals(1, visulog.noUnknowns);
    }
}