package Pipes;

import Validation.ErrorLogger;
import Validation.ValidationStatus;
import Validation.ValidationStrategy;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import static Validation.ValidationStatus.*;

public class ValidatorPipe implements SourcePipe, AutoCloseable {

    final Predicate<String> validator;
    final Comparator<String> comparator;
    private final SourcePipe source;
    private final BlockingQueue<String> log;

    private long lineCounter = 0;

    String previousValidLine;

    ValidatorPipe(ValidationStrategy validationStrategy, SourcePipe source, ErrorLogger logger) {
        validator = validationStrategy.getValidator();
        comparator = validationStrategy.getComparator();
        this.source = source;
        this.log = logger.getErrorQueue();
    }

    public ValidationStatus validate(String line) {
        if (line == null) return VALID; // null - сигнализирует конец файла, обрабатывается в SortingPipe.
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
            peekTillNextValid();
            return source.peek();
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
            peekTillNextValid();
            String nextValid = source.next();
            lineCounter++;
            previousValidLine = nextValid;
            return nextValid;
        }
    }

    private void peekTillNextValid() {
        String nextLine;
        while (validate(nextLine = source.peek()) != VALID) {
            logErrorMessage(nextLine, validate(nextLine));
            lineCounter++;
            source.next();
        }
    }

    private void logErrorMessage(String line, ValidationStatus status) {
        try {
            log.put(String.format("ошибка в строке %s файла %s: %s\n строка: %s",
                    lineCounter,
                    source.getName(),
                    status == INVALID ? "неверный формат" : outOfOrderMessage(),
                    line));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        return source.getName();
    }
}
