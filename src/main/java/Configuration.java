
import Validation.Order;
import Validation.SourceFile;
import Validation.ValidationStrategy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ErrorHandler.ErrorHandler.exitWithMessage;
import static Validation.Order.ASCENDING;
import static Validation.Order.DESCENDING;
import static java.lang.Character.isDigit;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

class Configuration {

    public static Predicate<String> STRING_VALIDATOR = s -> !s.contains(" ");
    public static Comparator<String> STRING_COMPARATOR = String::compareTo;

    public static Predicate<String> NUMERIC_VALIDATOR = Configuration::numericValidator;

    public static boolean numericValidator(String str) {
        if (str.isEmpty()) return false;
        if (str.equals("0")) return true;
        char firstSymbol = str.charAt(0);
        if (firstSymbol == '-') return str.charAt(1) != '0' && numericValidator(str.substring(1));
        if (firstSymbol == '0' && str.length() > 1) return false;
        return IntStream.range(0, str.length()).allMatch(i -> isDigit(str.charAt(i)));
    }

    public static Comparator<String> NUMERIC_COMPARATOR = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    static Set<String> SUPPORTED_PARAMS = Set.of("-a", "-d", "-s", "-i");

    static Map<Set<String>, ValidationStrategy> VALIDATION_CHART = Map.of(
            Set.of("-a", "-s"), new ValidationStrategy(STRING_COMPARATOR, STRING_VALIDATOR, ASCENDING),
            Set.of("-s"), new ValidationStrategy(STRING_COMPARATOR, STRING_VALIDATOR, ASCENDING),
            Set.of("-d", "-s"), new ValidationStrategy(STRING_COMPARATOR.reversed(), STRING_VALIDATOR, DESCENDING),
            Set.of("-a", "-i"), new ValidationStrategy(NUMERIC_COMPARATOR, NUMERIC_VALIDATOR, ASCENDING),
            Set.of("-i"), new ValidationStrategy(NUMERIC_COMPARATOR, NUMERIC_VALIDATOR, ASCENDING),
            Set.of("-d", "-i"), new ValidationStrategy(NUMERIC_COMPARATOR.reversed(), NUMERIC_VALIDATOR, DESCENDING));

    ValidationStrategy validationStrategy;
    List<SourceFile> files;

    public Configuration(String[] args) {
        this.validationStrategy = resolveValidationStrategy(args);
        this.files = processFileNames(args);
    }

    public static ValidationStrategy resolveValidationStrategy(String[] args) {
        List<String> params = getParams(args);
        validate(params);
        return VALIDATION_CHART.get(new HashSet<>(params));
    }

    private static List<String> getParams(String[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith("-"))
                .distinct()
                .collect(toList());
    }

    private static void validate(List<String> params) {
        if (!(params.contains("-i") || params.contains("-s")))
            exitWithMessage("должен присутствовать хотя бы один из параметров: -s или -i");

        if (params.contains("-a") && params.contains("-d"))
            exitWithMessage("противоречащие параметры : -a и -d");

        if (params.contains("-i") && params.contains("-s"))
            exitWithMessage("противоречащие параметры : -i и -s");

        List<String> badParams = params.stream()
                .filter(param -> !SUPPORTED_PARAMS.contains(param))
                .collect(toList());

        if (!badParams.isEmpty()) exitWithMessage(
                "неподдерживаемые параметры запуска: %s",
                String.join(", ", badParams));

    }

    public static List<SourceFile> processFileNames(String[] args) {
        List<String> names = Arrays
                .stream(args)
                .filter(x -> !(x.startsWith("-")))
                .collect(toList());

        if (names.size() < 3) exitWithMessage("укажите 3 или более файлов");

        checkIfSourceFilesExist(names);

        String outputFile = names.get(0);
        try {
            createOutputFile(Path.of(outputFile));
        } catch (InvalidPathException e) {
            exitWithMessage("убедитесь в корректности указанного пути файла: " + outputFile);
        }

        List<SourceFile> files = toFiles(names, args);
        if (files.size() < 2) exitWithMessage("недостаточно источников для начала сортировки");
        return files;
    }

    private static void checkIfSourceFilesExist(List<String> names) {
        List<String> notFound = names.stream()
                .skip(1)
                .filter(name -> !Files.exists(Path.of(name)))
                .collect(toList());

        if (!notFound.isEmpty())
            exitWithMessage("файл(ы) не найден(ы): %s",
                    String.join(", ", notFound));
    }

    private static List<SourceFile> toFiles(List<String> names, String[] args) {
        List<SourceFile> files = names.stream()
                .skip(1)
                .map(name -> toFile(name, args))
                .filter(file -> !isNull(file))
                .collect(Collectors.toList());
        files.add(0, new SourceFile(names.get(0)));
        return files;
    }

    private static SourceFile toFile(String name, String[] args) {
        try (FileReader fileReader = new FileReader(name);
             BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            Comparator<String> comparator = getParams(args).contains("-i") ? NUMERIC_COMPARATOR : STRING_COMPARATOR;
            while (true) {
                String line1 = bufferedReader.readLine();
                String line2 = bufferedReader.readLine();
                try {
                    if (comparator.compare(line1, line2) == 0) continue;
                    return comparator.compare(line1, line2) > 0 ?
                            new SourceFile(name, Order.DESCENDING) : new SourceFile(name, ASCENDING);
                } catch (ClassCastException e) {
                    System.out.printf("ошибка в файле %s, одна из строк имеет неверный формат: %s, %s%n",
                            name, line1, line2);
                    continue;
                } catch (NullPointerException e) {
                    System.out.printf("не удалось оперделить направление чтения источника %s%n", name);
                    System.out.printf("%s будет удален из списка источников%n", name);
                    return null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createOutputFile(Path outputFile) {
        if (Files.exists(outputFile)) {
            System.out.printf(
                    "файл %s уже существует, вы уверены что хотите перезаписать? (y/n)%n",
                    outputFile.getFileName());
            try (Scanner scanner = new Scanner(System.in)) {
                String response;
                while (!(response = scanner.next()).equals("y")) {
                    if (response.equals("n")) System.exit(0);
                }
            }
        } else try {
            Files.createFile(outputFile);
        } catch (IOException e) {
            exitWithMessage("не удалось создать файл %s", outputFile.getFileName());
        }
    }
}
