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
package com.addthis.pageracer.framework;

import javax.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import java.nio.file.Path;

import com.addthis.pageracer.configuration.IdentityTransformer;
import com.addthis.pageracer.configuration.Navigator;
import com.addthis.pageracer.configuration.SinglePageNavigator;
import com.addthis.pageracer.configuration.Transformer;
import com.addthis.pageracer.data.NavigationTiming;
import com.addthis.pageracer.data.ResourceTiming;
import com.addthis.pageracer.internal.Manager;
import com.addthis.pageracer.internal.MeasurementTree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Duration;
import org.openqa.selenium.support.ui.Sleeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is the main entry point into the library. It runs a specified
 * series of {@link WebDriver} instructions
 * and records the network performance events from the Chrome developer
 * tools interface (https://developer.chrome.com/devtools/docs/network).
 * The network performance events are recorded to a JSON formatted gzip-compressed
 * output file that can be analyzed and graphed with the provided graph.py
 * python script.
 * <p/>
 * The {@link Navigator} interface
 * specifies what browser operations to execute. The
 * {@link Transformer} interface
 * specifies optional transformations that can be applied to the performance events
 * prior to writing them to the file.
 * <p/>
 * The {@link SinglePageNavigator} class is provided
 * for measuring the network events resulting from loading a single specified webpage.
 * The {@link IdentityTransformer} class is provided
 * if no transformations on the performance events are necessary.
 */
public class PerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(PerformanceTest.class);

    /**
     * Default number of repeated experiments.
     * Can be overridden in the Builder options.
     */
    public static final int DEFAULT_NUMBER_ITERATIONS = 10;

    /**
     * Default number of seconds to wait before retrieving network performance events from the browser.
     * Can be overridden in the Builder options.
     */
    public static final int DEFAULT_PAGE_LOAD_WAIT_SECONDS = 30;

    /**
     * Default option on whether or not to start a new browser in between experiments.
     * Can be overridden in the Builder options.
     */
    public static final boolean DEFAULT_RESET_BETWEEN_ITERATIONS = true;

    /**
     * Default option on whether to conduct the experiments in an incognito browser.
     * Can be overridden in the Builder options.
     */
    public static final boolean DEFAULT_USE_INCOGNITO_BROWSER = true;

    private final Navigator navigator;

    private final int iterations;

    private final int pageLoadWait;

    private final Path outputPath;

    private final ChromeOptions options;

    @Nullable private ChromeDriver driver;

    private final Manager manager;

    private final boolean resetBetweenIterations;

    private PerformanceTest(Navigator navigator, Transformer transformer,
                            Path outputPath, ChromeOptions options, int iterations, int pageLoadWait,
                            boolean resetBetweenIterations, boolean useIncognitoBrowser) {
        checkNotNull(navigator);
        checkNotNull(transformer);
        checkArgument(iterations > 0);
        checkArgument(pageLoadWait >= 0);
        checkNotNull(outputPath);
        if (useIncognitoBrowser) {
            if (options == null) {
                options = new ChromeOptions();
            }
            options.addArguments("--incognito");
        }
        this.manager = new Manager(transformer);
        this.navigator = navigator;
        this.outputPath = outputPath;
        this.options = options;
        this.iterations = iterations;
        this.pageLoadWait = pageLoadWait;
        this.resetBetweenIterations = resetBetweenIterations;
    }

    private void setup() {
        if (driver == null) {
            driver = (options != null) ? new ChromeDriver(options) : new ChromeDriver();
        }
    }

    private List<ResourceTiming> generateResourceTimingEvents() throws IOException {
        String scriptToExecute = "var performance = window.performance || window.mozPerformance || " +
                                 "window.msPerformance || window.webkitPerformance || {}; " +
                                 "var network = performance.getEntries() || {}; return JSON.stringify(network);";
        String netData = driver.executeScript(scriptToExecute).toString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(netData,
                    mapper.getTypeFactory().constructCollectionType(
                            List.class, ResourceTiming.class));
        } catch (IOException ex) {
            log.error("Unable to deserialize JSON results: {}", netData, ex);
            throw ex;
        }
    }

    private NavigationTiming generateNavigationTimingEvent() throws IOException {
        String scriptToExecute = "var network = window.performance.timing || {}; return JSON.stringify(network);";
        String netData = driver.executeScript(scriptToExecute).toString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(netData, NavigationTiming.class);
        } catch (IOException ex) {
            log.error("Unable to deserialize JSON results: {}", netData, ex);
            throw ex;
        }
    }

    private void capture() throws IOException, InterruptedException {
        long timestamp = System.currentTimeMillis();
        Sleeper.SYSTEM_SLEEPER.sleep(new Duration(pageLoadWait, TimeUnit.SECONDS));
        List<ResourceTiming> measurements = generateResourceTimingEvents();
        NavigationTiming navigationTiming = generateNavigationTimingEvent();
        manager.addNavigationTiming(timestamp, navigationTiming);
        for (ResourceTiming measurement : measurements) {
            manager.addMeasurement(timestamp, measurement);
        }
    }

    private void teardown() {
        if (resetBetweenIterations && driver != null) {
            driver.quit();
            driver = null;
        }
    }

    /**
     * Runs a series of experiments and record the results to a file.
     *
     * @throws IOException
     */
    public void run() throws IOException, InterruptedException {

        try {
            for (int i = 0; i < iterations; i++) {
                log.info("Now starting iteration {} of {}", i + 1, iterations);
                try {
                    setup();
                    navigator.navigate(driver);
                    capture();
                } finally {
                    teardown();
                }
            }
        } finally {
            Map<Long, MeasurementTree> measurements = manager.getMeasurements();
            if (measurements.size() > 0) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.enable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
                String results = mapper.writeValueAsString(measurements);
                FileOutputStream fos = new FileOutputStream(outputPath.toFile());
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                try (PrintWriter writer = new PrintWriter(gzos)) {
                    writer.append(results);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private Navigator navigator;

        private Transformer transformer;

        private Path outputPath;

        private int iterations = DEFAULT_NUMBER_ITERATIONS;

        private int pageLoadWait = DEFAULT_PAGE_LOAD_WAIT_SECONDS;

        private boolean resetBetweenIterations = DEFAULT_RESET_BETWEEN_ITERATIONS;

        private boolean useIncognitoBrowser = DEFAULT_USE_INCOGNITO_BROWSER;

        private ChromeOptions options;

        /**
         * Construct a minimal performance test that
         * visits the specified url and writes the results
         * to the output file.
         *
         * @param url        website to visit
         * @param outputPath path to new file for output results
         */
        public Builder(String url, Path outputPath) {
            this.navigator = new SinglePageNavigator(url);
            this.transformer = new IdentityTransformer();
            this.outputPath = outputPath;
        }

        /**
         * Construct a performance test with the specified
         * transformer and navigator.
         *
         * @param navigator   what browser operations to execute
         * @param transformer transformations that can be applied to the performance events
         * @param outputPath  path to new file for output results
         */
        public Builder(Navigator navigator, Transformer transformer, Path outputPath) {
            this.navigator = navigator;
            this.transformer = transformer;
            this.outputPath = outputPath;
        }

        public Builder setTransformer(Transformer transformer) {
            this.transformer = transformer;
            return this;
        }

        public Builder setNavigator(Navigator navigator) {
            this.navigator = navigator;
            return this;
        }

        public Builder setOutputPath(Path outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder setPageLoadWait(int pageLoadWait) {
            this.pageLoadWait = pageLoadWait;
            return this;
        }

        public Builder setOptions(ChromeOptions options) {
            this.options = options;
            return this;
        }

        public Builder setResetBetweenIterations(boolean reset) {
            this.resetBetweenIterations = reset;
            return this;
        }

        public Builder setUseIncognitoBrowser(boolean incognito) {
            this.useIncognitoBrowser = incognito;
            return this;
        }

        public PerformanceTest build() {
            return new PerformanceTest(navigator, transformer, outputPath,
                                       options, iterations, pageLoadWait, resetBetweenIterations, useIncognitoBrowser);
        }
    }
}
