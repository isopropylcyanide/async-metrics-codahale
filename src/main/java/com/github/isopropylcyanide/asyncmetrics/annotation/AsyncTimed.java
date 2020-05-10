/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.isopropylcyanide.asyncmetrics.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking a method of an annotated object that returns asynchronously as timed.
 * <p>
 * Given a method like this:
 * <pre><code>
 *     {@literal @}AsyncTimed(name = "timerName")
 *     public CompletableFuture get(String name) {
 *         return CompletableFuture.completedFuture("Hello " + name);
 *     }
 * </code></pre>
 * A timer for the defining class with the name {@code timerName} will be created and each time the
 * {@code #get(String)} method is invoked and the future is successfully resolved,
 * the method's execution will be timed. If method throws an exception, nothing will be timed.
 * Note: If {@code timerName} is not specified, the name of the method will be used to time
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface AsyncTimed {

    /**
     * @return The name of the timer.
     */
    String name() default "";

    /**
     * @return The suffix which will be appended for the name of the metric
     */
    String suffix() default "timer";
}
