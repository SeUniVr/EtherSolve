package utils;

/**
 * Util class to print errors, warning and debug messages
 */
public class Message {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static boolean printDebug = true;

    /**
     * Prints an error to the stderr
     * @param message printed error
     */
    public static void printError(String message){
        System.err.println(message);
    }

    /**
     * Prints a warning to stdout with a yellow color
     * @param message printed warning
     */
    public static void printWarning(String message){
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    /**
     * Prints a message as debug to stdout if the debug option is active
     * @param message
     */
    public static void printDebug(String message){
        if (printDebug)
            System.out.println(message);
    }

    /**
     * Set the debug level
     * @param printDebug true: do print, false: don't print
     */
    public static void setPrintDebug(boolean printDebug) {
        Message.printDebug = printDebug;
    }
}
