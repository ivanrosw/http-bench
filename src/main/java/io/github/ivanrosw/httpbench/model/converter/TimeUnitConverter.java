package io.github.ivanrosw.httpbench.model.converter;

import com.beust.jcommander.IStringConverter;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUnitConverter implements IStringConverter<TimeUnit> {

    @Override
    public TimeUnit convert(String s) {
        String unitStr = s.trim().toUpperCase(Locale.ROOT);
        return TimeUnit.valueOf(unitStr);
    }
}
