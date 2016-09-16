# Dynatrace Ant Plugin [![Build Status](https://travis-ci.org/Dynatrace/Dynatrace-Ant-Plugin.svg?branch=master)](https://travis-ci.org/Dynatrace/Dynatrace-Ant-Plugin)


The automation plugin enables FULL Automation of Dynatrace by leveraging the REST interfaces of the Dynatrace AppMon Server. The automation plugin includes Ant tasks to execute the following actions on the Dynatrace AppMon Server:
* Activate Configuration: Activates a configuration within a system profile
* Clear Session: Clears the live session
* Enable/Disable Profile
* Get Agent Information: Either returns the number of connected agents or specific information about a single agent
* Create Memory/Thread Dumps: Triggers memory or thread dumps for a specific connected agent
* Reanalyze Stored Sessions: Triggers business transaction analysis of a stored session
* Restart Server/Collector
* Start/Stop Session Recording: Returns the actual recorded session name
* Start Test: returns testrun id, allowing to inject it into Dynatrace agent parameters
* Sensor placement into agent
* Store pure paths

#### Table of Contents

* [Installation](#installation)  
 * [Prerequisites](#prerequisites)
 * [Manual Installation](#manual_installation)
* [Configuration](#configuration)
* [Available Ant tasks](#tasks)  
* [Additional Resources](#resources)

## <a name="installation"></a>Installation

### <a name="prerequisites"></a>Prerequisites

* Dynatrace Application Monitoring version: 6.3+
* Ant 1.9+

Find further information in the [Dynatrace community](https://community.dynatrace.com/community/display/DL/Automation+Library+%28Ant,+Ant%29+for+Dynatrace).

### <a name="manual_installation"></a>Manual Installation

* Download the [latest plugin]() and extract it into the `lib` folder in your project
* There you can find dtTaskDefs.xml (defines all Ant Task) and ant.plugin-6.5.0.jar (the actual automation library)
Have a look at build.xml in the example directory as a sample on how to call the ant tasks

## Building
In order to build the plugin, Gradle environment is needed to be configured in your system. Then you should be able to build package by executing `gradlew build`. Jar file with tasks definition XML should be available it `build/dist` folder

## <a name="configuration"></a>Configuration
A full example can be seen in the pom.xml as part of the project available in `examples` folder.

## <a name="tasks"></a>Available Ant tasks
Description of Available Ant Tasks

#### Server Management
* DtSensorPlacement - Performs a Hot Sensor Placement
* DtGetAgentInfo - Returns information about a connected Agent
* DtEnableProfile - Enables or disables a System Profile
* DtActivateConfiguration - Activates a Configuration of a System Profile
* DtRestartServer - Restarts a dynaTrace Server
* DtRestartCollector - Restarts a collector
* DtStorePurePaths - Store current Live Session

#### Session Management
* DtClearSession - Clears the Live Session of a System Profile
* DtReanalyzeSession - Reanalyzes a stored session
* DtStartRecording - Starts session recording for a specified system profile
* DtStopRecording - Stops session recording for a specified system profile

#### Test Management
* DtStartTest - Sets meta data information for the Test Automation Feature and provides the DtStartTest.testRunId necessary to support parallel builds. The DtStartTest.testRunId value needs to be passed to the agent instrumenting the JVM that's executing the tests.
Resource Dumps
* DtMemoryDump - Creates a Memory Dump for an agent
* DtThreadDump - Creates a Thread Dump on an agent

## <a name="resources"></a>Additional Resources
- [Automation Library (Ant, Ant) for Dynatrace](https://community.dynatrace.com/community/display/DL/Automation+Library+%28Ant,+Ant%29+for+Dynatrace)
- [Test Automation and Ant](https://community.dynatrace.com/community/display/DOCDT63/Test+Automation+and+Ant)

- [Continuous Delivery & Test Automation](https://community.dynatrace.com/community/pages/viewpage.action?pageId=215161284)
- [Capture Performance Data from Tests](https://community.dynatrace.com/community/display/DOCDT63/Capture+Performance+Data+from+Tests)
- [Integrate Dynatrace in Continous Integration Builds](https://community.dynatrace.com/community/display/DOCDT63/Integrate+Dynatrace+in+Continuous+Integration+Builds)

