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
