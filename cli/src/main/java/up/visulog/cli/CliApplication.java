package up.visulog.cli;

import up.visulog.cli.command.Visulog;

/** This is main class of the cli program. */
public class CliApplication {
    /** Start of the program. */
    public static void main(String[] args) {
        // Exit with the error code returned by the Visulog command.
        System.exit(new Visulog().run(args));
    }
}
