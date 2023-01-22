
import Validation.ValidationStrategy;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ErrorHandler.ErrorHandler.exitWithMessage;

class Configuration {

    static Comparator<String> STRING_COMPARATOR = (s1, s2) -> 0;

    static Comparator<String> NUMERIC_COMPARATOR = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    static Predicate<String> NUMERIC_VALIDATOR = str -> {
        if (str.isEmpty()) return false;
        if (!Character.isDigit(str.charAt(0)) || str.charAt(0) == '0') return false;
        return !str.chars()
                .anyMatch(ch -> !Character.isDigit((char) ch));
    };
    static Predicate<String> STRING_VALIDATOR = s -> true;
    static Set<String> SUPPORTED_PARAMS = Set.of("-a", "-d", "-s", "-i");

    static Map<Set<String>, ValidationStrategy> VALIDATION_CHART = Map.of(
            Set.of("-a", "-s"), new ValidationStrategy(STRING_COMPARATOR, STRING_VALIDATOR),
            Set.of("-s"), new ValidationStrategy(STRING_COMPARATOR, STRING_VALIDATOR),
            Set.of("-d", "-s"), new ValidationStrategy(STRING_COMPARATOR.reversed(), STRING_VALIDATOR),
            Set.of("-a", "-i"), new ValidationStrategy(NUMERIC_COMPARATOR, NUMERIC_VALIDATOR),
            Set.of("-i"), new ValidationStrategy(NUMERIC_COMPARATOR, NUMERIC_VALIDATOR),
            Set.of("-d", "-i"), new ValidationStrategy(NUMERIC_COMPARATOR.reversed(), NUMERIC_VALIDATOR));

    private ValidationStrategy validationStrategy;

    private List<String> fileNames;

    Configuration(String[] args) {
        fileNames = processFileNames(args);
        validationStrategy = resolveValidationStrategy(args);
    }

    private ValidationStrategy resolveValidationStrategy(String[] args) {
        List<String> params = getParams(args);
        validate(params);
        return VALIDATION_CHART.get(new HashSet<>(params));
    }

    public ValidationStrategy getValidationStrategy() {
        return validationStrategy;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    private static List<String> getParams(String[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith("-"))
                .distinct()
                .collect(Collectors.toList());
    }

    private void validate(List<String> params) {
        Supplier<Stream<String>> badParams = () -> params
                .stream()
                .filter(param -> param.startsWith("-"))
                .filter(param -> !SUPPORTED_PARAMS.contains(param));

        if (badParams.get().findAny().isPresent()) exitWithMessage(
                String.format("неподдерживаемые параметры запуска: %s",
                        badParams.get().collect(Collectors.joining(", "))));

        if (params.contains("-a") && params.contains("-d"))
            exitWithMessage("противоречащие параметры : -a и -d");

        if (params.contains("-i") && params.contains("-s"))
            exitWithMessage("противоречащие параметры : -i и -s");
    }

    private List<String> processFileNames(String[] args) {
        Supplier<Stream<String>> names = () -> Arrays
                .stream(args)
                .filter(x -> !(x.startsWith("-")));

        if (names.get().count() < 3) exitWithMessage("укажите 3 или более файлов");

        Path outputFile = Path.of(names.get().limit(1).findFirst().get()); // выше проверено что стрим не пустой...
        validateOutputFile(outputFile);

        Stream<String> notFound = names.get().skip(1).filter(name -> !Files.exists(Path.of(name)));

        if (notFound.findAny().isPresent())
            exitWithMessage(String.format("файл(ы) не найден(ы): %s",
                    notFound.collect(Collectors.joining(", "))));

        return names.get().collect(Collectors.toList());

    }

    private static void validateOutputFile(Path outputFile) {
        if (Files.exists(outputFile)) {
            System.out.printf(
                    "файл %s уже существует, вы уверены что хотите перезаписать? (y/n)",
                    outputFile.getFileName());
            try {
                while (true) {
                    int response = System.in.read();
                    if (response == 'n') System.exit(0);
                    if (response == 'y') break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else try {
            Files.createFile(outputFile);
        } catch (IOException e) {
            exitWithMessage("не получилось создать файл %s", outputFile.getFileName());
        }
    }
}
