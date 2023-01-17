import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class BufferedReaderLineIterator extends BufferedReader {

    Predicate<String> validationStrategy;
    private final Logger log = Logger.getLogger("BufferedReaderLineIterator");

    BufferedReaderLineIterator(Reader reader, int bufferSize, Predicate<String> validationStrategy) {
        super(reader, bufferSize);
        this.validationStrategy = validationStrategy;
    }

    boolean hasNext() {
        //TODO придумать как быть с readAheadLimit
        try {
            mark(8192);
            //TODO если строка пропускается из-за валидации
            if (readLine() == null) return false;
            else {
                reset();
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String next() {
        try {
            mark(8192);
            String next = readLine();
            if (validationStrategy.test(next)) return next;
            else {
                log.warning("ошибка в строке \n" + next);
                return next();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Возвращает курсор к месту последнего вызова next().
     */
    void rollBack() {
        try {
            reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void forEachRemaining(Consumer<String> consumer) {
        while (hasNext()) {
            consumer.accept(next());
        }
    }
}
