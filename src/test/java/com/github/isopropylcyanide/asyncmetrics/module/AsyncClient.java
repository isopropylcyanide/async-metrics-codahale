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
package com.github.isopropylcyanide.asyncmetrics.module;

import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncExceptionMetered;
import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncMetered;
import com.github.isopropylcyanide.asyncmetrics.annotation.AsyncTimed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class AsyncClient {

    @AsyncTimed
    @AsyncMetered
    CompletableFuture<String> getStringFromUpStream() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
                return "Success";

            } catch (InterruptedException ignored) {
                return "Fail";
            }
        });
    }

    @AsyncExceptionMetered
    CompletableFuture<Integer> getIntegerFromUpStream() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
                throw new RuntimeException("Failed");

            } catch (InterruptedException e) {
                return 0;
            }
        });
    }
}
