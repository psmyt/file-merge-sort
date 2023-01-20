package MergePipes;

public interface Pipe {

    static final String POISON = "sdfsfdsdfsdfkjvlcjvxclvjkxlcjvlsdfjksadljfsladjflvurfirhuhfgiuahfiuhf";

    default boolean isPoison(String string) {
        for (int i = 0; i < POISON.length(); i++) {
            if (string.charAt(i) != POISON.charAt(i)) return false;
        }
        return true;
    }

    public String peek();
    public String next();
}
