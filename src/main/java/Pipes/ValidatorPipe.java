package Pipes;

import Validation.ValidationStatus;
import Validation.ValidationStrategy;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import static Validation.ValidationStatus.*;

public class ValidatorPipe implements SourcePipe, AutoCloseable {

    final Predicate<String> validator;
    final Comparator<String> comparator;
    private final SourcePipe source;
    private final Queue<String> log;

    private long lineCounter = 0;

    String previousValidLine;

    ValidatorPipe(ValidationStrategy validationStrategy, SourcePipe source, BlockingQueue<String> log) {
        validator = validationStrategy.getValidator();
        comparator = validationStrategy.getComparator();
        this.source = source;
        this.log = log;
    }

    public ValidationStatus validate(String line) {
        if (line == null) return VALID; // null - конец файла, обрабатывается далее.
        if (previousValidLine != null) {
            return validator.test(line) && comparator.compare(line, previousValidLine) >= 0 ? VALID :
                    validator.test(line) ? OUT_OF_ORDER : INVALID;
        } else {
            if (validator.test(line)) {
                previousValidLine = line;
                return VALID;
            } else return INVALID;
        }
    }

    @Override
    public String peek() {
        String nextLine = source.peek();
        ValidationStatus status = validate(nextLine);
        if (status == VALID) return nextLine;
        else {
            logErrorMessage(nextLine, status);
            return next();
        }
    }

    @Override
    public String next() {
        String nextLine = source.next();
        lineCounter++;
        ValidationStatus status = validate(nextLine);
        if (status == VALID) {
            previousValidLine = nextLine;
            return nextLine;
        } else {
            logErrorMessage(nextLine, status);
            return nextValid();
        }
    }

    private String nextValid() {
        ValidationStatus status = null;
        do {
            String nextLine = source.next();
            lineCounter++;
            logErrorMessage(nextLine, status);
        }
        while ((status = validate(source.peek())) != VALID);
        String nextLine = source.next();
        previousValidLine = nextLine;
        return nextLine;
    }

    private void logErrorMessage(String line, ValidationStatus status) {
        log.add(String.format("ошибка в строке %s файла %s: %s\n строка: %s",
                lineCounter,
                source.getName(),
                status == INVALID ? "неверный формат" : outOfOrderMessage(),
                line));
    }

    private String outOfOrderMessage() {
        return String.format("нарушен порядок сортировки (последнее валидное значение - %s)", previousValidLine);
    }

    @Override
    public void close() throws Exception {
        source.close();
    }

    @Override
    public String getName() {
        return null;
    }
}
