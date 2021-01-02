package up.visulog.cli.type;

import up.visulog.cli.annotation.Command;
import up.visulog.cli.annotation.Option;
import up.visulog.cli.util.Parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

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
    protected <T extends Runnable> boolean readArguments(String[] args, Class<T> classWithArgs) {
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
                                } else if (parts.length == 1) {
                                    System.out.println("Option '" + name + "' called with no values when one was expected (please use the -h command).");
                                    return false;
                                } else if (field.getType() == Function.class) {
                                    ((Function<String, Void>)field.get(this)).apply(parts[1]);
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
                    displayUnknownOption(parts[0], fields);
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

        return true;
    }

    /**
     * Shows the unknown option along with the closest one (if found).
     *
     * @param unknownOption The unknown option.
     * @param fields The fields to search in.
     */
    private void displayUnknownOption(String unknownOption, ArrayList<Field> fields) {
        int distance = -1;
        String nearest = "";

        for (Field field : fields) {
            Option option = field.getAnnotation(Option.class);

            for (String name : option.names()) {
                int optionDistance = calculateLevenshteinDistance(unknownOption, name);

                if (distance == -1 || optionDistance < distance) {
                    distance = optionDistance;
                    nearest = name;
                }
            }
        }

        System.out.println("Unknown option '" + unknownOption + "'!");

        if (!nearest.isEmpty()) {
            System.out.println("Did you mean '" + nearest + "'?");
        }

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

    /**
     * Calculate the levenshtein distance.
     * Implementation from: http://rosettacode.org/wiki/Levenshtein_distance#Java
     *
     * @param s1 The first string to compare.
     * @param s2 The second string to compare.
     * @return the distance between s1 and s2.
     */
    public static int calculateLevenshteinDistance(String s1, String s2) {
        if(s1.length() == 0) {
            return s2.length();
        } else if(s2.length() == 0) {
            return s1.length();
        }

        if(s1.charAt(0) == s2.charAt(0)) {
            return calculateLevenshteinDistance(s1.substring(1), s2.substring(1));
        }

        int a = calculateLevenshteinDistance(s1.substring(1), s2.substring(1));
        int b = calculateLevenshteinDistance(s1, s2.substring(1));
        int c = calculateLevenshteinDistance(s1.substring(1), s2);

        if(a > b) {
            a = b;
        }

        if(a > c) {
            a = c;
        }

        return a + 1;
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
