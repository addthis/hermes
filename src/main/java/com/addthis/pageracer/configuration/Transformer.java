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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.addthis.pageracer.data.ResourceTiming;

/**
 * This interface allows transformations to be performed on the names of measurements prior
 * to the measurements being saved to disk. These transformations can be useful if, for example,
 * assets with random URLs are retrieved on each page load and you which to collapse these
 * URLs onto a common name. Additionally this interface allows for the separation
 * of measurements into categories for further analysis.
 * The {@link IdentityTransformer} implementation is an instantiation of
 * this interface where no transformation are performed.
 */
public interface Transformer {

    /**
     * Returns an array of regular expression patterns. If these patterns
     * match against the name of the asset loaded (the url) with a subset match
     * {@link Matcher#find()} then the measurement is skipped
     * altogether.
     *
     * @return array of search patterns to ignore
     */
    public Pattern[] getIgnorePatterns();

    /**
     * Returns an array of regular expression patterns. These patterns are applied
     * sequentially to the name of the asset loaded (the url) with the transformation
     * {@code pattern.matcher(url).replaceFirst(replacement)}. The length of this
     * array and the length of the {@link Transformer#getReplacementStrings()}
     * array must be identical.
     *
     * @return array of search patterns for search and replace
     */
    public Pattern[] getSearchPatterns();

    /**
     * See description of {@link Transformer#getSearchPatterns()}
     *
     * @return array of search strings for search and replace
     */
    public String[] getReplacementStrings();

    /**
     * This method can optionally assign a category to an input
     * measurement. Categories can be nested. For example,
     * the return value {@code ["foo", "bar"]} translates
     * to the category "bar" that is a child of the top
     * level category "foo". Return an array of length 0
     * to specify no categories.
     *
     * @param measurement input measurement
     * @return category for the measurement
     */
    String[] generateCategories(ResourceTiming measurement);
}
