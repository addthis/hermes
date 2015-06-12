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
package com.addthis.hermes.framework;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.addthis.hermes.configuration.IdentityTransformer;
import com.addthis.hermes.configuration.SinglePageNavigator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Example of loading a url for N iterations and recording the
 * network performance results to a file.
 */
public class Main {

    /**
     * Generate the command line options that will be parsed.
     *
     * @return command line options.
     */
    @SuppressWarnings("static-access")
    private static Options createOptions() {
        Option help = Option.builder("h").longOpt("help")
                                   .desc("print this message").build();
        Option url = Option.builder("u").longOpt("url")
                                  .hasArg().desc("url of the website to test. Required!")
                                  .required().type(String.class).build();
        Option outfile = Option.builder("o").argName("filename").longOpt("output")
                                      .hasArg().desc("filename and path of output. Required!")
                                      .required().build();
        Option iterations = Option.builder().argName("N").longOpt("iterations")
                                         .hasArg().desc("number of iterations. Default is " +
                                                        PerformanceTest.DEFAULT_NUMBER_ITERATIONS)
                                         .type(Integer.class).build();
        Option incognito = Option.builder().argName("true|false").longOpt("incognito")
                                        .hasArg().desc("use incognito browser. Default is " +
                                                       PerformanceTest.DEFAULT_USE_INCOGNITO_BROWSER)
                                        .type(Boolean.class).build();
        Option wait = Option.builder().argName("N").longOpt("wait")
                                   .hasArg().desc("number of seconds to wait. Default is " +
                                                  PerformanceTest.DEFAULT_PAGE_LOAD_WAIT_SECONDS)
                                   .type(Integer.class).build();
        Option reset = Option.builder().argName("true|false").longOpt("reset")
                                    .hasArg().desc("Close and reopen browser each iteration. Default is " +
                                                   PerformanceTest.DEFAULT_RESET_BETWEEN_ITERATIONS)
                                    .type(Boolean.class).build();
        Options options = new Options();
        options.addOption(help);
        options.addOption(url);
        options.addOption(outfile);
        options.addOption(iterations);
        options.addOption(incognito);
        options.addOption(reset);
        options.addOption(wait);
        return options;
    }

    /**
     * Print the command line options help message and exit application.
     */
    @SuppressWarnings("static-access")
    private static void showHelpMessage(String[] args, Options options) {
        Options helpOptions = new Options();
        helpOptions.addOption(Option.builder("h").longOpt("help")
                                    .desc("print this message").build());
        try {
            CommandLine helpLine = new DefaultParser().parse(helpOptions, args, true);
            if (helpLine.hasOption("help") || args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("page-racer", options);
                System.exit(0);
            }
        } catch (ParseException ex) {
            System.err.println("Parsing failed.  Reason: " + ex.getMessage());
            System.exit(1);
        }
    }

    /**
     * The main entry point into the application.
     *
     * @param args see "-h" or "--h" for command line arguments
     * @throws IOException             if output files cannot be written
     * @throws InterruptedException    if thread is interrupted while sleeping
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        Options options = createOptions();
        showHelpMessage(args, options);
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException ex) {
            System.err.println("Parsing failed.  Reason: " + ex.getMessage());
            System.exit(1);
        }

        String url = line.getOptionValue("url");
        String outfile = line.getOptionValue("output");
        Path writePath = Paths.get(outfile);

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }

        int iterations = Integer.parseInt(line.getOptionValue("iterations",
                                                              Integer.toString(
                                                                      PerformanceTest.DEFAULT_NUMBER_ITERATIONS)));

        int wait = Integer.parseInt(line.getOptionValue("wait",
                                                        Integer.toString(
                                                                PerformanceTest.DEFAULT_PAGE_LOAD_WAIT_SECONDS)));

        boolean reset = Boolean.parseBoolean(
                line.getOptionValue("reset",
                                    Boolean.toString(PerformanceTest.DEFAULT_RESET_BETWEEN_ITERATIONS)));

        boolean incognito = Boolean.parseBoolean(
                line.getOptionValue("incognito",
                                    Boolean.toString(PerformanceTest.DEFAULT_USE_INCOGNITO_BROWSER)));

        if (Files.exists(writePath)) {
            System.err.println("ERROR: The output file already exists " + outfile);
            System.exit(1);
        }

        IdentityTransformer configuration = new IdentityTransformer();
        SinglePageNavigator navigator = new SinglePageNavigator(url);
        PerformanceTest performanceTest = new PerformanceTest.Builder(navigator, configuration, writePath)
                .setIterations(iterations)
                .setUseIncognitoBrowser(incognito)
                .setPageLoadWait(wait)
                .setResetBetweenIterations(reset)
                .build();

        performanceTest.run();
    }

}
