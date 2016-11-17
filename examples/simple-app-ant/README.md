## Example project with Dynatrace Ant Plugin

This project contains example usage of the Dynatrace Ant Plugin.

### Running project

To build and run project using Ant use normal target with additional call.

Run example `ant startTestWithAntPlugin`

In the example project target `startTestWithAntPlugin` depends on normal Ant target e.g `test-compile` and call plugin task by `DtStartTest` element.

You can also run explicity one Dynatrace Ant Pluging task. To do this, you create normal ant target with only job to call plugin task. In the example all target, expect first four, are made this way.

Run explicity plugin task `ant DtRestartServer`, `ant DtReanalyzeSession` etc.

## <a name="resources"></a>Additional Resources
- [Test Automation and Ant](https://community.dynatrace.com/community/display/DOCDT63/Test+Automation+and+Ant)