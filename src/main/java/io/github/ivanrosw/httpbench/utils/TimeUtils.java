package io.github.ivanrosw.httpbench.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static long convertToMs(long time, TimeUnit timeUnit) {
        return timeUnit.toMillis(time);
    }
}
