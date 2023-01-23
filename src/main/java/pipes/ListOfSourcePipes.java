package pipes;

import java.util.ArrayList;

public class ListOfSourcePipes extends ArrayList<SourcePipe> implements AutoCloseable {

    @Override
    public void close() {
        this.forEach(item -> {
            try {
                item.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
