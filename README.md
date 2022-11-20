# RPExtensionUtilities

RPExtensionUtilities is a Java plugin framework for IBM Rhapsody.

## Installation

Copy the RPExtensionUtilities directory of Package under the profile directory of IBM Rhapsody.

The RPExtensionUtilities directory of the package contains the following files.
- RPExtensionUtilities.jar
- RPExtensionUtilities.sbsx
- RPExtensionUtilities.hep


For example, in Windows 10, the profile directory of IBM Rhapsody is located under `C:\Program Files\IBM\Rhapsody\9.0.1\Share\Profiles`


## Count Action of Actiby
Count the actions in the activity diagram included in the package.

### Configure RPExtensionUtilities.hep

Set `RPActivityCount/[SwimlaneName]` to the name of the swimlane whose actions you want to count in the activity diagram.
The following is an example of counting the swimlane name "XXX".

```
#REM: Count Activity for packages
name3=RPActivityCount/XXX
isPluginCommand3=1
command3=RPExtensionUtilities
applicableTo3=Package
isVisible3=1
DLLServerCompatible3=1
```

### Rhapsody operations

1. Select the package of IBM Rhapsody explorer.
2. Right-click to display menu.
3. Select `RPActivityCount/[SwimlaneName]` specified in the hep file.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)