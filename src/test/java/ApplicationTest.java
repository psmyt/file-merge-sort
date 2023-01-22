import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {


    @AfterEach
    public void deleteResultFile() {
        try {
            Files.delete(Path.of("src/test/resources/result"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void appTest() throws IOException, InterruptedException {
        Application.main(new String[]{"-i",
                "src/test/resources/result",
                "src/test/resources/file1",
                "src/test/resources/file2",
                "src/test/resources/file3"});
        Path output = Path.of("src/test/resources/result");
        Path file1 = Path.of("src/test/resources/file1");
        Path file2 = Path.of("src/test/resources/file2");
        Path file3 = Path.of("src/test/resources/file3");
        List<String> expected = Stream.of(Files.readAllLines(file1), Files.readAllLines(file2), Files.readAllLines(file3))
                .flatMap(Collection::stream)
                .filter(Configuration.NUMERIC_VALIDATOR)
                .sorted(Configuration.NUMERIC_COMPARATOR)
                .collect(Collectors.toList());
        assertEquals(expected, Files.readAllLines(output));
    }

    @Test
    public void paramsTest() throws IOException, InterruptedException {
        Application.main(new String[]{"-i",
                "src/test/resources/result",
                "src/test/resources/file1",
                "src/test/resources/file2",
                "src/test/resources/file3"});
    }
}