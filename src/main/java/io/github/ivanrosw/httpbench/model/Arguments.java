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
package io.github.ivanrosw.httpbench.model;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import io.github.ivanrosw.httpbench.model.converter.TimeUnitConverter;
import io.github.ivanrosw.httpbench.model.converter.HttpMethodConverter;
import io.github.ivanrosw.httpbench.model.converter.UrlConverter;
import lombok.Getter;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
public class Arguments {

    @Parameter(names = { "--help", "-h" }, help = true)
    private boolean help;

    @Parameter(names = { "--url", "-u" }, description = "Url to execute", required = true, converter = UrlConverter.class)
    private URI url;

    @Parameter(names = { "--method", "-m" }, description = "Http method to execute", required = true, converter = HttpMethodConverter.class)
    private HttpMethod method;

    @DynamicParameter(names = { "--header", "-H" }, description = "Headers to transfer. Example: \"-H 'Accept=text/html, application/xhtml+xml'\"")
    private Map<String, String> headers = new HashMap<>();

    @Parameter(names = { "--body", "-b" }, description = "Body to transfer")
    private String body;

    @Parameter(names = { "--thread", "-t" }, description = "Threads count to parallel requesting url")
    private int threads = 1;

    @Parameter(names = { "--requests", "-r" }, description = "Requests count per second by one thread. Total requests per second = threads * requests")
    private int requests = 1;

    @Parameter(names = { "--duration", "-d" }, description = "Duration of test", required = true)
    private long duration;

    @Parameter(names = { "--duration-unit", "-DU" }, description = "Duration unit of test", required = true, converter = TimeUnitConverter.class)
    private TimeUnit durationUnit;

    @Parameter(names = { "--timeout", "-T" }, description = "Timeout of http requests")
    private long timeout = 2;

    @Parameter(names = { "--timeout-unit", "-TU" }, description = "Timeout unit of http requests", converter = TimeUnitConverter.class)
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;

    @Override
    public String toString() {
        return  "Url = " + url + "\n" +
                "Method = " + method + "\n" +
                "Headers = " + headers + "\n" +
                "Body = " + body + "\n" +
                "Threads = " + threads + "\n" +
                "Requests = " + requests + "\n" +
                "Duration = " + duration + "\n" +
                "DurationUnit = " + durationUnit + "\n" +
                "Timeout = " + timeout + "\n" +
                "TimeoutUnit = " + timeoutUnit;
    }
}
