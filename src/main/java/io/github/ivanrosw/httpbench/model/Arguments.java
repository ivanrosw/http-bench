package io.github.ivanrosw.httpbench.model;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import io.github.ivanrosw.httpbench.model.converter.DurationMeasureConverter;
import io.github.ivanrosw.httpbench.model.converter.HttpMethodConverter;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
public class Arguments {

    @Parameter(names = { "--help", "-h" }, help = true)
    private boolean help;

    @Parameter(names = { "--url", "-u" }, description = "Url to execute", required = true)
    private String url;

    @Parameter(names = { "--method", "-m" }, description = "Http method to execute", required = true, converter = HttpMethodConverter.class)
    private HttpMethod method;

    @DynamicParameter(names = { "--header", "-H" }, description = "Headers to transfer. Example: \"-H 'Accept=text/html, application/xhtml+xml'\"")
    private Map<String, String> headers = new HashMap<>();

    @Parameter(names = { "--body", "-b" }, description = "Body to transfer")
    private String body;

    @Parameter(names = { "--thread", "-t" }, description = "Threads count of parallel requesting url")
    private int threads = 1;

    @Parameter(names = { "--requests", "-r" }, description = "Requests count per second by one thread. Total requests per second = threads * requests")
    private int requests = 1;

    @Parameter(names = { "--duration", "-d" }, description = "Duration of test", required = true)
    private long duration;

    @Parameter(names = { "--measure", "-M" }, description = "Duration measure of test", required = true, converter = DurationMeasureConverter.class)
    private DurationMeasure durationMeasure;
}
