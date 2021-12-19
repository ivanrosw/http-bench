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

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class BenchData {

    private List<Long> responsesMs;

    private Map<String, Long> statuses;

    public BenchData() {
        this.responsesMs = new LinkedList<>();
        this.statuses = new HashMap<>();
    }

    public void addMs(long ms) {
        responsesMs.add(ms);
    }

    public void incrementStatus(String status) {
        if (statuses.containsKey(status)) {
            statuses.put(status, statuses.get(status) + 1);
        } else {
            statuses.put(status, 1L);
        }
    }
}
