package io.github.ivanrosw.httpbench;

import com.beust.jcommander.JCommander;
import io.github.ivanrosw.httpbench.bench.HttpBench;
import io.github.ivanrosw.httpbench.model.Arguments;

public class HttpBenchMain {

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder().addObject(arguments).build();
        jCommander.parse(args);

        if (arguments.isHelp()) {
            jCommander.usage();
            System.exit(0);
        }

        HttpBench httpBench = new HttpBench(arguments);
        httpBench.execute();
    }
}
