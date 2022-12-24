# summary

For debugging purposes, this document describes how to change the logging settings

## Configure RPExtensionUtilities.hep

Add one of the following to the `name` of each plugin's configuration in RPExtensionUtilities.hep.  
* `LogDebug`:Outputs debug-level logs when executing commands.
* `LogTrace`:Outputs trace-level logs when commands are executed.  
**Warning**
Because of the large amount of trace-level log output, the plugin may take a lot of time to execute.


