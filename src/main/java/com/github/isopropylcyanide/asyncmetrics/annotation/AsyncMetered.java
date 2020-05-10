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
 * An annotation for marking a method of an annotated object that returns asynchronously as metered.
 * <p>
 * Given a method like this:
 * <pre><code>
 *     {@literal @}AsyncMetered(name = "meterName")
 *     public CompletableFuture get(String name) {
 *         return CompletableFuture.completedFuture("Hello " + name);
 *     }
 * </code></pre>
 * A meter for the defining class with the name {@code meterName} will be created and each time the
 * {@code #get(String)} method is invoked and the future is successfully resolved,
 * the meter's counter will be incremented. If method throws an exception, nothing will be marked.
 * Note: If {@code meterName} is not specified, the name of the method will be used to mark
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface AsyncMetered {

    /**
     * @return The name of the meter.
     */
    String name() default "";

    /**
     * @return The suffix which will be appended for the name of the metric
     */
    String suffix() default "meter";
}
