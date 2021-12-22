# Http-Bench

Benchmark for services

Returns percentile of responses time

### Getting started

Compile project
```
mvn clean package
```

Run project with settings, for example:
```
java -jar HttpBench.jar -u 'http://localhost:8450/' -m GET -DU HOURS -d 1 -r 1600 -t 4
```
It runs benchmark for 1 hour with 4 threads and 1600 GET requests each thread to url http://localhost:8450/ 

To show available settings
```
java -jar HttpBench.jar --help
```
