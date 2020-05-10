## Async Metrics Using Guice AOP
![Travis (.org)](https://img.shields.io/travis/isopropylcyanide/async-metrics-codahale)
![Codecov](https://img.shields.io/codecov/c/github/isopropylcyanide/async-metrics-codahale)
![Maven Central](https://img.shields.io/maven-central/v/com.github.isopropylcyanide/async-codahale-metrics)
![GitHub](https://img.shields.io/github/license/isopropylcyanide/async-metrics-codahale?color=blue)

Leverage Codahale metrics (or any Metric Registry for that matter) to provide metrics on methods that complete asynchronously. The library requires `Guice AOP` to work (bundled by default)

## Maven Artifacts

This project is available on Maven Central. To add it to your project you can add the following dependency to your
`pom.xml`:

```xml
    <dependency>
        <groupId>com.github.isopropylcyanide</groupId>
        <artifactId>async-codahale-metrics</artifactId>
        <version>1.0</version>
     </dependency>
```
        
## Features

| Annotation | Codahale Equivalent |
| ------------- | ------------- |
| @AsyncTimed |  @Timed |
| @AsyncMetered | @Meter |
| @AsyncExceptionMetered | @ExceptionMetered |


## Usage

- Initialise the Module in your Guice Injector

```java    
    MetricRegistry registry = new MetricRegistry();
    Guice.createInjector(new AsyncMetricsModule(metricRegistry));    
}
```

- Annotate the required methods with the required annotation

```java
@AsyncTimed
@AsyncExceptionMetered
CompletableFuture<Integer> asyncMethodThatCompletesNormally() {
    return CompletableFuture
            .supplyAsync(() -> StringUtils.split(getClass().getName(), "."))
           .thenApply(s -> s.length);
}
```

```java
@AsyncMetered
CompletableFuture<String> asyncMethodThatCompletesNormally() {
    return CompletableFuture.supplyAsync(() -> "Hello World");
}
```

## Why another metrics library?
We wish to leverage [`Dropwizard metrics`](https://github.com/dropwizard/metrics/tree/4.1-development/metrics-annotation/src/main/java/com/codahale/metrics/annotation) for async callbacks. The likes of `@Timed` or `@ExceptionMetered` for methods that return `Futures` or `CompletableFutures` or `Callbacks` 

These annotation for marking a method of an annotated object make the code much more readable and separate the unnecessary boilerplate of marking metrics from the business logic

```
@Timed                                          @Timed
@ExceptionMetered                               @ExceptionMetered                              
public X getX() {                               public Future<X> getX() {
    //business logic                                //business logic that might execute asynchronously
    return x;                                       return Future.of(x)        
}                                               }
     
  Works as expected                             Doesn't work as expected
```

However, the second approach won't produce correct results as if the calling thread dispatches the work to another thread in the pool then this method execution completes without waiting for the result. Ideally, we want to mark our metrics and figure out a way to do meta stuff once the callback resolves either successfully or exceptionally. That is why this library as created.


## Enhancements
- Feel free to extend this to mark any `custom metrics`. All you need is an annotation and the corresponding aspects
- Instead of `Codahale Metric Registry` it can be extended to any registry.
- Instead of `completable future`, it can be any random callback. The only requirement is of a hook to execute action post callback resolution that doesn't block.


## Testing
- Create a driver class with a new metric registry
- Install the module
- Set up a local Console reporter

```java
ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
       .convertRatesTo(TimeUnit.SECONDS)
       .convertDurationsTo(TimeUnit.MILLISECONDS)
       .build();
   reporter.start(1, TimeUnit.SECONDS);
```

## Wiki
https://medium.com/@aman_garg/leveraging-async-metrics-using-aspects-81838b9b887e

## Support

Please file bug reports and feature requests in [GitHub issues](https://github.com/isopropylcyanide/async-metrics-codahale/issues).


## License

Copyright (c) 2012-2020 Aman Garg

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.







