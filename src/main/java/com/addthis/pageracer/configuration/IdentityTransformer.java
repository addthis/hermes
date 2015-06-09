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
package com.addthis.pageracer.configuration;

import java.util.regex.Pattern;

import com.addthis.pageracer.data.ResourceTiming;

public class IdentityTransformer implements Transformer {
    private static final Pattern[] EMPTY_PATTERNS = new Pattern[0];
    private static final String[] EMPTY_STRINGS = new String[0];

    @Override
    public Pattern[] getSearchPatterns() {
        return EMPTY_PATTERNS;
    }

    @Override
    public String[] getReplacementStrings() {
        return EMPTY_STRINGS;
    }

    @Override
    public Pattern[] getIgnorePatterns() {
        return EMPTY_PATTERNS;
    }

    @Override
    public String[] generateCategories(ResourceTiming measurement) {
        return EMPTY_STRINGS;
    }
}
