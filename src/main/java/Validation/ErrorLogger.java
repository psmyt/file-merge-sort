package Validation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ErrorLogger implements Runnable {

    private final AtomicBoolean stop = new AtomicBoolean(false);

    private final BlockingQueue<String> source = new LinkedBlockingQueue<>(100);

    private final String logFilePath;

    public ErrorLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public BlockingQueue<String> getErrorQueue() {
        return source;
    }

    @Override
    public void run() {
        try (FileWriter fileWriter = new FileWriter(logFilePath, true);
             BufferedWriter writer = new BufferedWriter(fileWriter, 64)
        ) {
            while (!stop.get()) {
                String next = source.poll(1, TimeUnit.SECONDS);
                if (next != null) {
                    writer.write(next);
                    writer.newLine();
                }
            }
            while (!source.isEmpty()) {
                writer.write(source.take());
                if (!source.isEmpty()) writer.newLine();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void finishJob() {
        stop.set(true);
    }
}
