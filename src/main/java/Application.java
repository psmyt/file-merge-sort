import MergePipes.Factory;
import MergePipes.Pipe;

import java.util.Comparator;
import java.util.List;

public class Application {

    static Comparator<String> comparator;
    static Factory factory;

    public static void main(String[] args) {
        System.out.println("fdfd");
    }

    public static Pipe assemblePipes(List<Pipe> sources) {
        while (sources.size() > 1) {
            Pipe sourceA = sources.remove(0);
            Pipe sourceB = sources.remove(0);
            sources.add(factory.threeWayMergePipeInstance(sourceA, sourceB));
        }
        return sources.get(0);
    }

}
