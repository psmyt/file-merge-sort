package exit;

public class ErrorHandler {

    public static void exitWithMessage(String message, Object... replacements) {
        System.err.println(String.format(message, replacements));
        System.exit(1);
    }
}
