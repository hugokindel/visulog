package up.visulog.cli.command;

import org.junit.Test;
import static org.junit.Assert.*;

/** This is a unit test class to test the makeConfigFromCommandLineArgs task. */
public class TestVisulog {
    /** Test if the configuration is created. */
    @Test
    public void testArgumentParser() {
        Visulog visulog = new Visulog();
        visulog.run(new String[] {".", "--plugins=CountCommitsPerAuthor"});
        assertEquals(".", visulog.getValue());
        assertEquals("CountCommitsPerAuthor", visulog.plugins[0]);
    }

    /** Test if the program works with good arguments. */
    @Test
    public void testProgramWithRealArguments() {
        Visulog visulog = new Visulog();
        visulog.run(new String[] {"--plugins=CountCommitsPerAuthor,CountLinesPerAuthor"});
        assertEquals("CountCommitsPerAuthor", visulog.plugins[0]);
        assertEquals("CountLinesPerAuthor", visulog.plugins[1]);

    }

    /** Test if the program works with no arguments (help showed). */
    @Test
    public void testProgramWithNoArguments() {
        Visulog visulog = new Visulog();
        visulog.run(new String[] {});
        assertTrue(visulog.willShowHelp());
    }

    /** Test if tje program works with unknown arguments. */
    @Test
    public void testProgramWithUnknownArguments() {
        Visulog visulog = new Visulog();
        visulog.run(new String[] {"--testWithThis"});
        assertEquals(1, visulog.getNoUnknowns());
    }
}