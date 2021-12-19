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
