package io.github.ivanrosw.httpbench.model.converter;

import com.beust.jcommander.IStringConverter;
import io.github.ivanrosw.httpbench.model.DurationMeasure;

import java.util.Locale;

public class DurationMeasureConverter implements IStringConverter<DurationMeasure> {

    @Override
    public DurationMeasure convert(String s) {
        String measureStr = s.trim().toUpperCase(Locale.ROOT);
        return DurationMeasure.valueOf(measureStr);
    }
}
