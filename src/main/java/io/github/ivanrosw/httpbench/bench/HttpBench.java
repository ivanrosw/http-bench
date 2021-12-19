package io.github.ivanrosw.httpbench.bench;

import io.github.ivanrosw.httpbench.model.Arguments;
import io.github.ivanrosw.httpbench.model.BenchData;
import io.github.ivanrosw.httpbench.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@Slf4j
@AllArgsConstructor
public class HttpBench {

    private Arguments arguments;

    public void execute() {
        log.info("Starting benchmark...");
        log.info("Settings: \n{}\n", arguments);

        List<BenchData> benchData = runBench();
        calculatePercentile(benchData);
    }

    private List<BenchData> runBench() {
        List<BenchData> result = new ArrayList<>(arguments.getThreads());
        ExecutorService executorService = Executors.newFixedThreadPool(arguments.getThreads());

        Header[] headers = createHeaders();
        RequestConfig requestConfig = createRequestConfig();
        long endTestMs = System.currentTimeMillis() + TimeUtils.convertToMs(arguments.getDuration(), arguments.getDurationUnit());

        try {
            List<FutureTask<BenchData>> tasks = new ArrayList<>(arguments.getThreads());

            for (int i = 0; i < arguments.getThreads(); i++) {
                BenchCallable benchCallable = new BenchCallable();
                benchCallable.setArguments(arguments);
                benchCallable.setHeaders(headers);
                benchCallable.setRequestConfig(requestConfig);
                benchCallable.setEndTestMs(endTestMs);

                FutureTask<BenchData> futureTask = new FutureTask<>(benchCallable);
                tasks.add(futureTask);
                executorService.execute(futureTask);
            }

            for (FutureTask<BenchData> futureTask : tasks) {
                result.add(futureTask.get());
            }

            int requests = 0;
            for (BenchData data : result) {
                requests += data.getResponsesMs().size();
            }

            //todo remove log
            log.info(result.get(0).toString());
            log.info(String.valueOf(requests));
        } catch (Exception e) {
            log.error("Error while executing bench", e);
        }

        executorService.shutdown();
        return result;
    }

    private Header[] createHeaders() {
        Header[] headers = null;
        if (arguments.getHeaders() != null && !arguments.getHeaders().isEmpty()) {
            headers = new Header[arguments.getHeaders().size()];
            int headerIndex = 0;
            for (Map.Entry<String, String> entry : arguments.getHeaders().entrySet()) {
                headers[headerIndex] = new BasicHeader(entry.getKey(), entry.getValue());
                headerIndex++;
            }
        }
        return headers;
    }

    private RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(arguments.getTimeout(), arguments.getTimeoutUnit())
                .setConnectTimeout(arguments.getTimeout(), arguments.getTimeoutUnit())
                .setResponseTimeout(arguments.getTimeout(), arguments.getTimeoutUnit())
                .build();
    }

    private void calculatePercentile(List<BenchData> benchData) {

    }
}
