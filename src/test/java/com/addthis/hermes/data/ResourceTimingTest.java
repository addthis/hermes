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
package com.addthis.hermes.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ResourceTimingTest {

    @Test
    public void builder() {
        ResourceTiming measurement = new ResourceTiming(
                "name", "entryType", "initiatorType",
                1,2,3,4,5,6,7,8,9,10,11,12,13);
        ResourceTiming.Builder builder = new ResourceTiming.Builder(measurement);
        ResourceTiming copy = builder.build();
        assertNotSame(measurement, copy);
        assertEquals(measurement, copy);
    }

}
