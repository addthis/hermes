#!/usr/bin/env python2

# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import gzip
import json
import matplotlib
import numpy
import pylab
import reportlab.platypus
import sys

if len(sys.argv) != 6 :
    print ""
    print "USAGE: graph.py [categories] [input] [output] [ylimit|0] [outlier|0]"
    print ""
    print "[categories] comma seperated categories or \"\" for all measurements"
    print "[input] path to input measurements file"
    print "[output] output directory for writing graphs"
    print "[ylimit] optional upper bound on durations graph or 0."
    print "[outlier] optional number of standard deviations to remove or 0."
    print ""
    exit(1)

categories = sys.argv[1]
inputFile = sys.argv[2]
outputDir = sys.argv[3]
maxDuration = float(sys.argv[4])
stdOutlier = int(sys.argv[5])

if len(categories) > 0:
    categories = categories.split(",")

def reject_outliers(data, m = 2):
    u = numpy.mean(data)
    s = numpy.std(data)
    filtered = [e for e in data if (u - 2 * s < e < u + 2 * s)]
    return filtered

# Rename all the keys to numbered indices
# and return a mapping of numbers to keys
def renameKeys(results):
    legend = {}
    keys = results['start'].keys()
    keys.sort(key = lambda x: numpy.median(results['start'][x]))
    for i, key in enumerate(keys):
        legend[i] = key
        results['start'][i] = results['start'][key]
        results['end'][i] = results['end'][key]
        results['duration'][i] = results['duration'][key]
        del results['start'][key]
        del results['end'][key]
        del results['duration'][key]
    return legend

def renameNavigation(navigation):
    legend = {}
    keys = navigation.keys()
    keys.sort(key = lambda x: numpy.median(navigation[x]))
    for i, key in enumerate(keys):
        legend[i + 1] = key
        navigation[i] = navigation[key]
        del navigation[key]
    return legend

def addCategory(subset):
    global results
    for event in subset:
        name = event['name']
        start = event['startTime']
        end = event['responseEnd']
        if name not in results['start']:
            results['start'][name] = []
            results['end'][name] = []
            results['duration'][name] = []
        results['start'][name].append(start)
        results['end'][name].append(end)
        results['duration'][name].append(end - start)

def addNavigationTiming(navtimings):
    global navigation
    start = navtimings['navigationStart']
    for key, value in navtimings.items():
        if key != 'navigationStart' and value > 0:
            duration = value - start
            if key not in navigation:
                navigation[key] = []
            navigation[key].append(duration)

def addRecursiveCategories(experiments):
    if 'categories' in experiments:
        for category in experiments['categories']:
            addRecursiveCategories(experiments['categories'][category])
    addCategory(experiments['measurements'])

handle = gzip.open(inputFile, 'rb')
data = json.load(handle)
handle.close()
results = {'start': {}, 'end': {}, 'duration': {}}
navigation = {}

for experiments in data.itervalues():
    addNavigationTiming(experiments['navigation'])
    if len(categories) == 0:
        addRecursiveCategories(experiments)
    else:
        for category in categories:
            cnames = category.split('/')
            for cname in cnames:
                experiments = experiments['categories'][cname]
        addRecursiveCategories(experiments)

legend = renameKeys(results)
navLabels = renameNavigation(navigation)

if stdOutlier > 0:
    for key in results:
        for col in results[key]:
            results[key][col] = reject_outliers(results[key][col],
                stdOutlier)

pylab.figure(1, figsize=(12, 6))
subplot = pylab.subplot(111)
subplot.axes.yaxis.set_minor_locator(matplotlib.ticker.AutoMinorLocator(4))
pylab.boxplot(results['start'], whis = 0)
pylab.xlabel('Event ID (see legend)')
pylab.ylabel('Time (msec)')
pylab.title('Beginning of Events')

pylab.figure(2, figsize=(12, 6))
subplot = pylab.subplot(111)
subplot.axes.yaxis.set_minor_locator(matplotlib.ticker.AutoMinorLocator(4))
pylab.boxplot(results['end'], whis = 0)
pylab.xlabel('Event ID (see legend)')
pylab.ylabel('Time (msec)')
pylab.title('End of Events')

pylab.figure(3, figsize=(12, 6))
pylab.boxplot(results['duration'], whis = 0)
pylab.xlabel('Event ID (see legend)')
pylab.ylabel('Time (msec)')
pylab.title('Duration of Events')
if maxDuration > 0:
    pylab.ylim([0, maxDuration])
subplot = pylab.subplot(111)
subplot.axes.yaxis.set_minor_locator(matplotlib.ticker.AutoMinorLocator(4))

pylab.figure(4, figsize=(12, 6))
pylab.boxplot(navigation, whis = 0)
pylab.xticks(navLabels.keys(), navLabels.values(), rotation=270)
pylab.xlabel('Event')
pylab.ylabel('Time (msec)')
pylab.title('Navigation Timing API')
if maxDuration > 0:
    pylab.ylim([0, maxDuration])
subplot = pylab.subplot(111)
subplot.axes.yaxis.set_minor_locator(matplotlib.ticker.AutoMinorLocator(4))

tableData = [['id', 'event']]

pylab.figure(1)
pylab.savefig(outputDir + "/beginning.pdf", bbox_inches='tight')

pylab.figure(2)
pylab.savefig(outputDir + "/end.pdf", bbox_inches='tight')

pylab.figure(3)
pylab.savefig(outputDir + "/duration.pdf", bbox_inches='tight')

pylab.figure(4)
pylab.savefig(outputDir + "/navigation.pdf", bbox_inches='tight')

for key, value in legend.iteritems():
    tableData.append([key + 1, value[8:80]])

doc = reportlab.platypus.SimpleDocTemplate(outputDir + "/legend.pdf")
elements = []
t=reportlab.platypus.Table(tableData)
elements.append(t)
doc.build(elements)

