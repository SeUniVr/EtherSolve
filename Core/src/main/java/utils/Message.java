package utils;

public class Message {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREY = "\u001B[240m";

    public static void printError(String message){
        System.err.println(message);
    }

    public static void printWarning(String message){
        System.err.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    public static void printDebug(String message){
        System.err.println(ANSI_GREY + message + ANSI_RESET);
    }
}
