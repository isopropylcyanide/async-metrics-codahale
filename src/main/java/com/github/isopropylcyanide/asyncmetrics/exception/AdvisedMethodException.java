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
package com.github.isopropylcyanide.asyncmetrics.exception;

/**
 * Every time the invocation of an asynchronous method returns an error, an exception of type
 * {@link AdvisedMethodException} will be thrown containing the root throwable.
 */
public class AdvisedMethodException extends RuntimeException {

    public AdvisedMethodException(String methodName, Throwable throwable) {
        super(String.format("Exception in advised method: [%s] : [%s]", methodName, throwable.getMessage()), throwable);
    }
}
