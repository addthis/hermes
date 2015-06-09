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
package com.addthis.pageracer.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.addthis.pageracer.configuration.Transformer;
import com.addthis.pageracer.data.NavigationTiming;
import com.addthis.pageracer.data.ResourceTiming;

public class Manager {

    private final Transformer transformer;

    private final Map<Long, MeasurementTree> data;

    public Manager(Transformer transformer) {
        this.transformer = transformer;
        this.data = new HashMap<>();
    }

    public void addNavigationTiming(long timestamp, NavigationTiming navigationTiming) {
        MeasurementTree measurements = data.get(timestamp);
        if (measurements == null) {
            measurements = new MeasurementTree();
            data.put(timestamp, measurements);
        }
        measurements.setNavigation(navigationTiming);
    }

    public void addMeasurement(long timestamp, ResourceTiming measurement) {
        String name = measurement.getName();
        Pattern[] ignorePatterns = transformer.getIgnorePatterns();
        Pattern[] searchPatterns = transformer.getSearchPatterns();
        String[] replaceStrings = transformer.getReplacementStrings();
        for (Pattern ignorePattern : ignorePatterns) {
            if (ignorePattern.matcher(name).find()) {
                return;
            }
        }
        for (int i = 0; i < searchPatterns.length; i++) {
            name = searchPatterns[i].matcher(name).replaceFirst(replaceStrings[i]);
        }
        ResourceTiming modified = new ResourceTiming.Builder(measurement).setName(name).build();
        String[] categories = transformer.generateCategories(modified);
        MeasurementTree measurements = data.get(timestamp);
        if (measurements == null) {
            measurements = new MeasurementTree();
            data.put(timestamp, measurements);
        }
        measurements.addMeasurement(modified, categories);
    }

    /**
     * Returns a reference to the current measurements.
     *
     * @return reference to the current measurements.
     */
    public Map<Long, MeasurementTree> getMeasurements() {
        // TODO: Is it worth implementing deep copy for this feature?
        return data;
    }

    @Override
    public String toString() {
        return Arrays.toString(data.entrySet().toArray());
    }

}
