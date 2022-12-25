# RPExtensionUtilities

RPExtensionUtilities is a Java plugin framework for IBM Rhapsody.

# Installation

### Installing the Java Environment

1. Download JDK 17 or higher at https://projects.eclipse.org/projects/adoptium
2. Install the downloaded JDK
3. Change JavaLocation in Rhapsody.ini.  
Storage location of Rhapsody.ini  
C:\ProgramData\IBM\Rhapsody\9.0.1x64\rhapsody.ini  
* Before modification of Rhapsody.ini:  
JavaLocation=C:\Program Files\IBM\Rhapsody\9.0.1\jdk\jre    
* After changing Rhapsody.ini (in case of JDK 17.0.5.8-hotspot):  
 JavaLocation=C:\Program Files\Eclipse Adoptium\jdk-17.0.5.8-hotspot


### Installing Plug-in

Copy the RPExtensionUtilities directory of Package under the profile directory of IBM Rhapsody.

The RPExtensionUtilities directory of the package contains the following files.
- RPExtensionUtilities.jar
- RPExtensionUtilities.sbsx
- RPExtensionUtilities.hep


For example, in Windows 10, the profile directory of IBM Rhapsody is located under
 `C:\Program Files\IBM\Rhapsody\9.0.1\Share\Profiles`

## Inclusion in the project

Please follow the steps below to add a profile.  
`File(F)` -> `Add profiles to the model` -> select `RPExtensionUtilities.sbsx`

Adding a profile enables the plug-in.

## Documentation of individual features

See under doc directory.

[RPCountActivity](doc/RPCountActivity.md)  
[RPImageOutActivity](doc/RPImageOutActivity.md)  
[RPDoxygenXML](doc/RPDoxygenXML.md)  

## Debug Configuration

[DebugConfiguration](doc/DebugConfiguration.md)  

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)