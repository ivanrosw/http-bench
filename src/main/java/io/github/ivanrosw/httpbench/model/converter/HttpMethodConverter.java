package io.github.ivanrosw.httpbench.model.converter;

import com.beust.jcommander.IStringConverter;
import io.github.ivanrosw.httpbench.model.HttpMethod;

import java.util.Locale;

public class HttpMethodConverter implements IStringConverter<HttpMethod> {

    @Override
    public HttpMethod convert(String s) {
        String methodStr = s.trim().toUpperCase(Locale.ROOT);
        return HttpMethod.valueOf(methodStr);
    }
}
