## Example project with Dynatrace Ant Plugin

This project contains example usage of the Dynatrace Ant Plugin.

### Running project

To build and run project using Ant use normal target with additional call.

Run example by
```
ant startTestWithAntPlugin
```
In the example project target **startTestWithAntPlugin** depends on normal Ant target **test-compile** and call **DtStartTest** plugin task.

You can also run explicity one Dynatrace Ant Pluging task. To do this, you create normal ant target with plugin task call. In the example all target, expect first four, are made this way.

Run explicity plugin task by `ant DtRestartServer`, `ant DtStopRecording` etc.

## <a name="resources"></a>Additional Resources
- [Test Automation and Ant](https://community.dynatrace.com/community/display/DOCDT63/Test+Automation+and+Ant)