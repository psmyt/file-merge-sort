package Pipes.FileReaders;

import Pipes.FileReaders.BufferedLineReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class BufferedReaderAdapter extends BufferedReader implements BufferedLineReader {

    public BufferedReaderAdapter(Reader in) {
        super(in);
    }

    @Override
    public String nextLine() {
        try {
            return this.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
