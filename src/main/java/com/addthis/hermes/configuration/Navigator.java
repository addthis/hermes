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
 * The navigator interface is used to direct the
 * <a href="https://code.google.com/p/selenium/wiki/ChromeDriver">ChromeDriver</a>
 * to perform a series of actions prior to recording
 * the performance of a web page. If multiple pages
 * are navigated by this interface then measurements
 * are stored for the last page that is visited.
 * See the {@link SinglePageNavigator}
 * as an example of visiting a single url.
 */
public interface Navigator {

    public void navigate(ChromeDriver driver);

}
