package validation;

import java.io.File;

public class SourceFile extends File {
    private Order order;

    public SourceFile(String pathname) {
        super(pathname);
    }

    public SourceFile(String pathname, Order order) {
        super(pathname);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
