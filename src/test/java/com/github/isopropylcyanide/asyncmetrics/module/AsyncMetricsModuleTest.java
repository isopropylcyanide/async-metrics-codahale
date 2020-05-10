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

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AsyncMetricsModuleTest {

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private Injector injector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        injector = Guice.createInjector(new ApplicationModule(metricRegistry));
    }

    @Test
    public void testModuleBindsTheInterceptorsCorrectlyAndCorrectlyTimes() throws ExecutionException, InterruptedException {
        AsyncClient client = injector.getInstance(AsyncClient.class);
        assertEquals("Success", client.getStringFromUpStream().get());

        SortedMap<String, Timer> timers = metricRegistry.getTimers();
        assertEquals(1, timers.size());

        Map.Entry<String, Timer> timerEntry = timers.entrySet().iterator().next();
        assertEquals(1, timerEntry.getValue().getCount());

        SortedMap<String, Meter> meters = metricRegistry.getMeters();
        assertEquals(1, meters.size());
        Map.Entry<String, Meter> meterEntry = meters.entrySet().iterator().next();

        assertEquals(1, meterEntry.getValue().getCount());
    }

    @Test
    public void testModuleBindsTheInterceptorsCorrectlyAndCorrectlyTimesExceptionMeter() {
        AsyncClient client = injector.getInstance(AsyncClient.class);
        try {
            client.getIntegerFromUpStream().get();

        } catch (Exception ex) {
            SortedMap<String, Meter> meters = metricRegistry.getMeters();
            assertEquals(1, meters.size());
            Map.Entry<String, Meter> meterEntry = meters.entrySet().iterator().next();

            assertTrue(meterEntry.getValue().getMeanRate() > 0);
            assertEquals(1, meterEntry.getValue().getCount());
        }
    }
}
