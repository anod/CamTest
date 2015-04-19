package info.anodsplace.camtest.utils;

import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {
    private static final int FORTY_KILOBYTES = 1024 * 40;

    private final long size;
    private final Listener progressListener;
    private long progress, lastUpdate = 0;
    private final InputStream inputStream;
    private boolean closed = false;

    public interface Listener {
        void onProgress(long progress,long size);
    }

    public ProgressInputStream(InputStream inputStream, long size, Listener listner) {
        this.size = size;
        this.inputStream = inputStream;
        this.progressListener = listner;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (closed) throw new IOException("already closed");
        closed = true;
    }

    @Override
    public int read() throws IOException {
        int read = inputStream.read();
        if (read > 0)
            progress += 1;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate, size);
        return read;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        if (count > 0)
            progress += count;
        lastUpdate = maybeUpdateDisplay(progress, lastUpdate, size);
        return count;
    }

    private long maybeUpdateDisplay(long progress, long lastUpdate, long size) {
        if (progress - lastUpdate > FORTY_KILOBYTES) {
            lastUpdate = progress;
            progressListener.onProgress(progress, size);
        }
        return lastUpdate;
    }
}