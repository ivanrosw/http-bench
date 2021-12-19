package io.github.ivanrosw.httpbench.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeUtils {

    public static long convertToMs(long time, TimeUnit timeUnit) {
        return timeUnit.toMillis(time);
    }
}
