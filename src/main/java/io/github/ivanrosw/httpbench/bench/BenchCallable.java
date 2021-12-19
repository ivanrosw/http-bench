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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.util.concurrent.Callable;

@Slf4j
@Setter
public class BenchCallable implements Callable<BenchData> {

    private Arguments arguments;
    private Header[] headers;
    private RequestConfig requestConfig;

    private long endTestMs;

    /**
     * Execute http requests and collect data: responses ms and statuses counts
     * Execute every second {@link Arguments#getRequests()} requests
     *
     * @return  object with responses ms and statuses counts
     */
    @Override
    public BenchData call() throws Exception {
        BenchData benchData = new BenchData();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            int requestsCount = 0;
            long currentIteration = System.currentTimeMillis();
            long nextIteration = currentIteration;

            while (System.currentTimeMillis() < endTestMs) {
                if (System.currentTimeMillis() >= nextIteration) {
                    currentIteration = nextIteration;
                    nextIteration = currentIteration + 1000;
                    requestsCount = 0;
                }

                if (requestsCount < arguments.getRequests()) {
                    long now = System.currentTimeMillis();
                    executeRequest(httpClient, benchData);
                    benchData.addMs(System.currentTimeMillis() - now);
                    requestsCount++;
                } else {
                    long timeToNextIteration = nextIteration - System.currentTimeMillis();
                    if (timeToNextIteration > 0) Thread.sleep(timeToNextIteration);
                }
            }
        } catch (Exception e) {
            log.error("Unexpected bench error", e);
            Thread.currentThread().interrupt();
        }

        return benchData;
    }

    /**
     * Execute http request and write status to benchData
     *
     * @param httpClient  client to execute request
     * @param benchData   object to write status
     */
    private void executeRequest(CloseableHttpClient httpClient, BenchData benchData) {
        HttpUriRequestBase httpUriRequestBase = new HttpUriRequestBase(arguments.getMethod().name(), arguments.getUrl());
        httpUriRequestBase.setConfig(requestConfig);
        if (headers != null) httpUriRequestBase.setHeaders(headers);
        if (arguments.getBody() != null) httpUriRequestBase.setEntity(new StringEntity(arguments.getBody()));

        try (CloseableHttpResponse response = httpClient.execute(httpUriRequestBase)) {
            benchData.incrementStatus(String.valueOf(response.getCode()));
        } catch (ConnectTimeoutException | HttpHostConnectException e) {
            if (log.isTraceEnabled()) log.trace("Execute request timeout", e);
            benchData.incrementStatus("408");
        } catch (HttpResponseException e) {
            if (log.isTraceEnabled()) log.trace("Execute request error", e);
            benchData.incrementStatus(String.valueOf(e.getStatusCode()));
        } catch (Exception e) {
            if (log.isTraceEnabled()) log.trace("Execute request unknown error", e);
            benchData.incrementStatus("unknown");
        }
    }
}
