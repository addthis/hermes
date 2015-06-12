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
package com.addthis.hermes.configuration;

import org.openqa.selenium.chrome.ChromeDriver;

/**
 * This implementation of the {@link Navigator}
 * interface visits a specified web page. The page
 * racer will record page loading measurements for
 * the page that is specified.
 */
public class SinglePageNavigator implements Navigator {

    private final String url;

    public SinglePageNavigator(String url) {
        this.url = url;
    }

    @Override
    public void navigate(ChromeDriver driver) {
        driver.get(url);
    }

}
