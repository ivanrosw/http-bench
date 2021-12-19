/*
 * Copyright (C) 2021 Ivan Rosinskii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
