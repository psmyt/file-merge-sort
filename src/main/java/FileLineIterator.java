import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class FileLineIterator extends BufferedReader {

    private long markLine = 0;
    private long lineCount = 0;
    Predicate<String> validationStrategy;
    private final Logger log = Logger.getLogger(FileLineIterator.class.getName());

    FileLineIterator(Reader reader, int bufferSize, Predicate<String> validationStrategy) {
        super(reader, bufferSize);
        this.validationStrategy = validationStrategy;
    }

    /**
     * Проверяет остались ли валидные строки в файле, и ставит курсор перед ближайшей из них
     */
    boolean hasValidNext() {
        //TODO придумать как быть с readAheadLimit
        try {
            mark(8192);
            markLine = lineCount;
            String nextLine = readLine();
            lineCount++;
            if (nextLine == null) return false;
            if (validationStrategy.test(nextLine)) {
                reset();
                lineCount = markLine;
                return true;
            }
            else {
                log.warning(String.format("ошибка в строке %s:\n%s", lineCount, nextLine));
                return hasValidNext();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String validNext() {
        try {
            markLine = lineCount;
            mark(8192);
            lineCount++;
            return readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Возвращает курсор к месту последнего вызова next().
     */
    void rollBack() {
        try {
            lineCount = markLine;
            reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void forEachRemaining(Consumer<String> consumer) {
        while (hasValidNext()) {
            consumer.accept(validNext());
        }
    }
}
