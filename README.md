### async-metrics-codahale
Leverage Codahale metrics (or any Metric Registry for that matter) to provide metrics on methods that complete asynchronously.

---
#### Problem at hand
We wish to leverage DropWizard metrics for async callbacks. The likes of *@Timed* or *@ExceptionMetered* for methods that return Futures or CompletableFutures or Callbacks.

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

However, the second approach won't produce correct results as if the calling thread dispatches the work to another thread in the pool then this method execution completes without waiting for the result. Ideally, we want to mark our metrics and figure out a way to do meta stuff once the callback resolves either successfully or exceptionally.

The obvious way to deal this is as follows.

```
@Inject
private MetricRegistry metricRegistry
 
private Meter exceptionMeter;
private Timer timer;
 
public Future<X> getX(){
    timer.start()
 
    //business logic that might execute asynchronously
    x = Future.of(x)
        .thenAccept(result -> timer.stop())
        .onFailure(error -> exceptionMeter.mark())
    return x;
}
```

Thus the problem is to subtly mark the metrics for these futures once they resolve without polluting the business logic. Somewhere along the likes of Annotation based solution

---

#### Usage

- Initialise the Module in your Guice Injector

```
public static void main(){
    ...
    MetricRegistry registry = new MetricRegistry();
    Guice.createInjector(new AspectsModule(metricRegistry));
    ...
}
```

- Annotate the required methods with the required annotation

```
@AsyncTimed
@AsyncExceptionMetered
CompletableFuture<Integer> asyncMethodThatCompletesNormally() {
    return CompletableFuture.supplyAsync(() -> StringUtils.split(getClass().getName(), "."))
           .thenApply(s -> s.length);
}
```
---

| Annotation | Codahale Equivalent |
| ------------- | ------------- |
| @AsyncTimed |  @Timed |
| @AsyncMetered | @Meter |
| @AsyncExceptionMetered | @ExceptionMetered |

---

#### Enhancements
- Feel free to extend this to mark any custom metrics. All you need is an Annotation and the corresponding aspects
- Instead of Codahale Metric Registry it can be extended to any registry
- Instead of completable future, it can be any random callback. It's just that there should be a hook to execute action post callback resolution that doesn't block.

---
#### Testing
- Create a driver class with a new metric registry
- Install the module
- Set up a local Console reporter
```
ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
       .convertRatesTo(TimeUnit.SECONDS)
       .convertDurationsTo(TimeUnit.MILLISECONDS)
       .build();
   reporter.start(1, TimeUnit.SECONDS);
```










