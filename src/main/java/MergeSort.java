import org.apache.commons.io.output.WriterOutputStream;

import java.io.*;
import java.util.*;

public class MergeSort {

    public static List<String> merge(ListIterator<String> listIterator1,
                                     ListIterator<String> listIterator2,
                                     Comparator<String> comparator
    ) {
        List<String> result = new ArrayList<>();
        while (listIterator1.hasNext()) {
            String a = listIterator1.next();
            while (listIterator2.hasNext()) {
                String b = listIterator2.next();
                if (comparator.compare(a, b) >= 0) {
                    result.add(b);
                } else {
                    listIterator2.previous();
                    break;
                }
            }
            result.add(a);
        }
        listIterator2.forEachRemaining(result::add);
        return result;
    }

    public static List<String> mergeFiles(FileLineIterator lineIterator1,
                                          FileLineIterator lineIterator2,
                                          Comparator<String> comparator
    ) {
        List<String> result = new ArrayList<>();
        while (lineIterator1.hasValidNext()) {
            String a = lineIterator1.validNext();
            while (lineIterator2.hasValidNext()) {
                String b = lineIterator2.validNext();
                if (comparator.compare(a, b) >= 0) {
                    result.add(b);
                } else {
                    lineIterator2.rollBack();
                    break;
                }
            }
            result.add(a);
        }
        lineIterator2.forEachRemaining(result::add);
        return result;
    }

    public static void mergeInputStreams(FileLineIterator lineIterator1,
                                         FileLineIterator lineIterator2,
                                         BufferedWriter output,
                                         Comparator<String> comparator
    ) throws IOException {
        while (lineIterator1.hasValidNext()) {
            String a = lineIterator1.validNext();
            while (lineIterator2.hasValidNext()) {
                String b = lineIterator2.validNext();
                if (comparator.compare(a, b) >= 0) {
                    output.append(b).append(System.lineSeparator());
                } else {
                    lineIterator2.rollBack();
                    break;
                }
            }
            output.append(a).append(System.lineSeparator());
        }
        while (lineIterator2.hasValidNext()) {
            output.append(lineIterator2.validNext())
                    .append(System.lineSeparator());
        }
        output.close();
    }
}
