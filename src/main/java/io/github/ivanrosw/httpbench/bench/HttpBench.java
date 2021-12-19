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
package io.github.ivanrosw.httpbench.bench;

import io.github.ivanrosw.httpbench.model.Arguments;
import io.github.ivanrosw.httpbench.model.BenchData;
import io.github.ivanrosw.httpbench.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@Slf4j
@AllArgsConstructor
public class HttpBench {

    private static final String LINE_DELIMITER = "---------------------------------------";

    private static final String STATUS_2XX = "2xx";
    private static final String STATUS_OTHER = "other";

    private static final String PERCENTILE_FORMAT = "%-10s | %-11s";
    private static final String STATUS_FORMAT = "%-10s";

    private Arguments arguments;

    public void execute() {
        log.info("Starting benchmark...");
        log.info("Settings: \n{}\n", arguments);

        List<BenchData> benchData = runBench();
        printBenchResult(benchData);
    }

    /**
     * Run bench
     *
     * @return  list with collections of responses ms and statuses counts
     */
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
        } catch (Exception e) {
            log.error("Error while executing bench", e);
            Thread.currentThread().interrupt();
        }

        executorService.shutdown();
        return result;
    }

    /**
     * Convert {@link Arguments#getHeaders()} headers to {@link Header}
     *
     * @return  headers
     */
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

    /**
     * Create {@link RequestConfig} with custom timeout from {@link Arguments#getTimeout()}
     *
     * @return  config for requests
     */
    private RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(arguments.getTimeout(), arguments.getTimeoutUnit())
                .setConnectTimeout(arguments.getTimeout(), arguments.getTimeoutUnit())
                .setResponseTimeout(arguments.getTimeout(), arguments.getTimeoutUnit())
                .build();
    }

    /**
     * Print result of bench
     *
     * @param benchData  result of bench
     */
    private void printBenchResult(List<BenchData> benchData) {
        List<Long> responsesMs = new LinkedList<>();
        benchData.forEach(data -> responsesMs.addAll(data.getResponsesMs()));
        Collections.sort(responsesMs);

        long avg = getAvg(responsesMs);
        long percentile10 = getPercentile(responsesMs, 10);
        long percentile25 = getPercentile(responsesMs, 25);
        long percentile50 = getPercentile(responsesMs, 50);
        long percentile75 = getPercentile(responsesMs, 75);
        long percentile90 = getPercentile(responsesMs, 90);
        long percentile95 = getPercentile(responsesMs, 95);
        long percentile99 = getPercentile(responsesMs, 99);

        Map<String, Long> statuses = calculateStatuses(benchData);

        log.info(LINE_DELIMITER);
        log.info("AVG: {} ms, MIN: {} ms, MAX: {} ms", avg, responsesMs.get(0), responsesMs.get(responsesMs.size() - 1));
        log.info("2xx: {}, other: {}", statuses.get(STATUS_2XX), statuses.get(STATUS_OTHER) );
        log.info(LINE_DELIMITER);
        log.info(String.format(PERCENTILE_FORMAT, "Percentile", "Response ms"));
        log.info(String.format(PERCENTILE_FORMAT, 10, percentile10));
        log.info(String.format(PERCENTILE_FORMAT, 25, percentile25));
        log.info(String.format(PERCENTILE_FORMAT, 50, percentile50));
        log.info(String.format(PERCENTILE_FORMAT, 75, percentile75));
        log.info(String.format(PERCENTILE_FORMAT, 90, percentile90));
        log.info(String.format(PERCENTILE_FORMAT, 95, percentile95));
        log.info(String.format(PERCENTILE_FORMAT, 99, percentile99));
        log.info(LINE_DELIMITER);
        log.info("{} | Count", String.format(STATUS_FORMAT, "Status"));
        for (Map.Entry<String, Long> entry : statuses.entrySet()) {
            if (!entry.getKey().equals(STATUS_2XX) && !entry.getKey().equals(STATUS_OTHER)) {
                log.info("{} | {}", String.format(STATUS_FORMAT, entry.getKey()), entry.getValue());
            }
        }
    }

    /**
     * Calculate avg of responses ms
     *
     * @param responsesMs  all responses ms
     * @return  avg
     */
    private long getAvg(List<Long> responsesMs) {
        long avg = 0;
        for (Long ms : responsesMs) {
            avg += ms;
        }
        avg = avg / responsesMs.size();
        return avg;
    }

    /**
     * Get percentile from responses ms
     *
     * @param responsesMs  sorted responses ms
     * @param percentile   percentile
     * @return             percentile ms
     */
    private long getPercentile(List<Long> responsesMs, double percentile) {
        return responsesMs.get((int) Math.round(percentile / 100.0 * (responsesMs.size() - 1)));
    }

    /**
     * Calculate statuses 2xx and other
     * Collect all statuses from result to one collection
     *
     * @param benchData  result of bench
     * @return           all statuses from result and 2xx, other
     */
    private Map<String, Long> calculateStatuses(List<BenchData> benchData) {
        Map<String, Long> result = new HashMap<>();
        result.put(STATUS_2XX, 0L);
        result.put(STATUS_OTHER, 0L);

        for (BenchData data : benchData) {
            for (Map.Entry<String, Long> entry : data.getStatuses().entrySet()) {
                if (result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), result.get(entry.getKey()) + entry.getValue());
                } else {
                    result.put(entry.getKey(), entry.getValue());
                }

                try {
                    int status = Integer.parseInt(entry.getKey());
                    if (status >= 200 && status < 300) {
                        result.put(STATUS_2XX, result.get(STATUS_2XX) + entry.getValue());
                    } else {
                        result.put(STATUS_OTHER, result.get(STATUS_OTHER) + entry.getValue());
                    }
                } catch (Exception e) {
                    result.put(STATUS_OTHER, result.get(STATUS_OTHER) + entry.getValue());
                }
            }
        }
        return result;
    }
}
