package com.ride.util.common;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Stormouble
 * @since 2017/5/23.
 */

public final class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException("IOUtils can't be initialized");
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
                //ignored the exception
            }
        }
    }
}
