package up.visulog.cli.type;

import up.visulog.cli.annotation.Command;
import up.visulog.cli.annotation.Option;
import up.visulog.cli.util.Parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Runnable {
    /** Option to show the help message. */
    @Option(names = {"-h", "--help"}, description = "Show this help message.")
    protected boolean showHelp;

    /** Option to show the version message. */
    @Option(names = {"-v", "--version"}, description = "Show the current version of this software.")
    protected boolean showVersion;

    /** The value (the part of the command that is not an option). */
    protected String value;

    /** The number of unknown options entered. */
    protected int noUnknowns;

    /** Class constructor. */
    public Runnable() {
        value = "";
        noUnknowns = 0;
        showHelp = false;
        showVersion = false;
    }

    /**
     * Run this command.
     *
     * @param args The arguments.
     * @return the return code.
     */
    public abstract int run(String[] args);

    /**
     * Read every arguments provided and try to see if any option is corresponding to define their values.
     *
     * @param args The arguments.
     * @param classWithArgs The child's class.
     * @param <T> The type of the child class.
     */
    protected <T extends Runnable> void readArguments(String[] args, Class<T> classWithArgs) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(Runnable.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(classWithArgs.getDeclaredFields()));
        fields.removeIf(field -> !field.isAnnotationPresent(Option.class));

        if (args.length == 0) {
            showHelp = true;
        }

        for (String arg : args) {
            if (arg.startsWith("-")) {
                String[] parts = arg.split("=");
                boolean find = false;

                for (Field field : fields) {
                    for (String name : field.getAnnotation(Option.class).names()) {
                        if (parts[0].equals(name)) {
                            find = true;

                            try {
                                field.setAccessible(true);
                                if (field.getType() == boolean.class) {
                                    field.set(this, true);
                                } else {
                                    field.set(this, Parser.parse(parts[1], field.getType()));
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }

                    if (find) {
                        break;
                    }
                }

                if (!find) {
                    displayUnknownOption(parts[0]);
                }
            } else {
                value = arg;
            }
        }

        if (showHelp) {
            displayHelp(classWithArgs, fields);
        }

        if (showVersion) {
            displayVersion(classWithArgs);
        }
    }

    /**
     * Shows the unknown option along with the closest one (if found).
     *
     * @param unknownOption The unknown option.
     */
    private void displayUnknownOption(String unknownOption) {
        System.out.println("Unknown option: '" + unknownOption + "'!");
        noUnknowns++;
    }

    /**
     * Shows the program's version.
     *
     * @param classWithArgs The child's class.
     * @param <T> The type of the child's class.
     */
    private <T extends Runnable> void displayVersion(Class<T> classWithArgs) {
        System.out.println("Version: " + classWithArgs.getAnnotation(Command.class).version());
    }

    /**
     * Shows the program's help message.
     *
     * @param classWithArgs The child's class.
     * @param fields The list of fields to show.
     * @param <T> The type of the child's class.
     */
    private <T extends Runnable> void displayHelp(Class<T> classWithArgs, ArrayList<Field> fields) {
        System.out.println("usage: " + classWithArgs.getAnnotation(Command.class).name() + " [options...]");

        System.out.println();

        for (String line : classWithArgs.getAnnotation(Command.class).description()) {
            System.out.println(line);
        }

        System.out.println();

        System.out.println("Options:");
        for (Field field : fields) {
            Option option = field.getAnnotation(Option.class);
            int numberOfNames = option.names().length;

            System.out.print(" \t");

            for (int i = 0; i < numberOfNames; i++) {
                System.out.print(option.names()[i] + (i == numberOfNames - 1 ? "" : ", "));
            }

            if (!option.usage().isEmpty()) {
                System.out.print("=" + option.usage());
            }

            System.out.println();

            for (String line : option.description()) {
                System.out.print(" \t\t");
                System.out.println(line);
            }
        }
    }

    /** @return if the program will show the help. */
    public boolean willShowHelp() {
        return showHelp;
    }

    /** @return if the program will show the version. */
    public boolean willShowVersion() {
        return showVersion;
    }

    /** @return the value (the part of the command that is not an option). */
    public String getValue() {
        return value;
    }

    /** @return the number of unknown options entered. */
    public int getNoUnknowns() {
        return noUnknowns;
    }
}
