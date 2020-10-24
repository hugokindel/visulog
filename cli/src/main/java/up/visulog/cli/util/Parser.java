package up.visulog.cli.util;

/** This is a parser for options with value passed through the cli. */
public class Parser {
    /**
     * Parse the value of an option.
     *
     * @param value The value (what comes after the '=' character in an option).
     * @param classType The class type of the option.
     * @return the value converted to the class type of the option.
     */
    public static Object parse(String value, Class<?> classType) {
        if (classType == String[].class) {
            return value.split(",");
        } else if (classType == String.class) {
            return value;
        } else if (classType == char.class) {
            return value.charAt(0);
        } else if (classType == boolean.class) {
            return !value.equals("0");
        } else if (classType == int.class) {
            return Integer.valueOf(value);
        } else if (classType == byte.class) {
            return Byte.valueOf(value);
        } else if (classType == short.class) {
            return Short.valueOf(value);
        } else if (classType == Long.class) {
            return Long.valueOf(value);
        } else if (classType == float.class) {
            return Float.valueOf(value);
        } else if (classType == double.class) {
            return Double.valueOf(value);
        }

        return null;
    }
}
