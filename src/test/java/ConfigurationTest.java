import Configuration.Configuration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @ParameterizedTest
    @CsvSource(value = {"-1:-300", "1900:190", "13424:0", "1:-34234532", "2323143424332445:23"}, delimiter = ':')
    void numericComparatorTest(String str1, String str2) {
        assertEquals(1, Configuration.compareStringsAsNumbers(str1, str2));
        assertEquals(-1, Configuration.compareStringsAsNumbers(str2, str1));
    }

}