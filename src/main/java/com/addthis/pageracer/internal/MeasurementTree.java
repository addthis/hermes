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

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.addthis.pageracer.data.NavigationTiming;
import com.addthis.pageracer.data.ResourceTiming;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasurementTree {

    @JsonProperty
    private final Map<String, MeasurementTree> categories;

    @JsonProperty
    private final List<ResourceTiming> measurements;

    @JsonProperty
    @Nullable
    private NavigationTiming navigation;

    public MeasurementTree() {
        categories = new HashMap<>();
        measurements = new ArrayList<>();
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public MeasurementTree(
            @JsonProperty("categories") Map<String, MeasurementTree> categories,
            @JsonProperty("measurements") List<ResourceTiming> measurements,
            @JsonProperty("navigation") NavigationTiming navigation) {
        this.categories = categories;
        this.measurements = measurements;
        this.navigation = navigation;
    }

    public void addMeasurement(ResourceTiming measurement, String... names) {
        if (names.length == 0) {
            measurements.add(measurement);
        } else {
            String headName = names[0];
            String[] tailNames = Arrays.copyOfRange(names, 1, names.length);
            MeasurementTree category = categories.get(headName);
            if (category == null) {
                category = new MeasurementTree();
                categories.put(headName, category);
            }
            category.addMeasurement(measurement, tailNames);
        }
    }

    public void setNavigation(NavigationTiming navigation) {
        this.navigation = navigation;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("categories", Joiner.on('\n').withKeyValueSeparator(" : ").join(categories))
                      .add("measurements", Joiner.on(",\n").join(measurements))
                      .add("navigation", navigation)
                      .toString();
    }
}
