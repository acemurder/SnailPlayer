package com.ride.snailplayer.net.exception;

/**
 * @author Stormouble
 * @since 2017/5/24.
 */

public class SnailPlayerApiException extends RuntimeException {

    public SnailPlayerApiException(String message) {
        super(message);
    }

    public SnailPlayerApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public SnailPlayerApiException(Throwable cause) {
        super(cause);
    }
}
