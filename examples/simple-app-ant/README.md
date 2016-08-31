## Example project with Dynatrace Ant Plugin

This project contains example usage of the Dynatrace Ant Plugin.

### Prerequisites

Ant plugin should be built or installed before running this project

### Running project

To build and install project using Ant, simply execute: `mvn install`.
To run tests with injected agent, execute: `mvn test`

### Running tasks

In order to run any Dynatrace Ant Plugin task, `ant dynaTrace:dtAutomation:YOUR_GOAL_NAME`. (e.g. `mvn dynaTrace:dtAutomation:enableProfile`).
Every task usage is presented in `pom.xml` and `pom-start-test.xml' files.
