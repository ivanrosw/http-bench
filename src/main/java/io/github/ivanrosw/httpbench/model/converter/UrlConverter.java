package io.github.ivanrosw.httpbench.model.converter;

import com.beust.jcommander.IStringConverter;
import io.github.ivanrosw.httpbench.model.ArgumentsParseException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URL;

@Slf4j
public class UrlConverter implements IStringConverter<URI> {

    @Override
    public URI convert(String s) {
        try {
            URL url = new URL(s);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (Exception e) {
            throw new ArgumentsParseException("URL parse error", e);
        }
    }
}
