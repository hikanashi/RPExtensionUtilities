# Plug-in Features

## Summary
Generate method activity diagrams and callback events using as input an XML file generated by doxygen based on C source code.

Detects activity diagrams that use modified or deleted APIs.  


## Structure
The structure to be generated is as follows
```
[target-package] Right click to select
   [version name] from user specified
      Module (more than 1) from doxygen
         * Activity diagram created from doxygen methods 
         * Events created from doxygen callbacks
         * DataType and ValueType created from doxygen types
```
Allow users to specify version when importing XML files.  
The imported and generated APIs and data types should be placed under the latest imported version of the package.

## Changes and Deletions
If a method or callback argument is changed, the element with the older version is renamed as follows
```
   changed_[version_name]_API_name
   deleted_[version name]_API name
```
If an element with the same name already exists in the same version,  
the name is not changed and the change is reflected.

## Tags to be assigned to the element
Added/Modified/Deleted API and data types should be given the following tags.  
* `FirstApplicableVersion`: Set the first imported version.
* `UnavailableStartVersion`: Set the version that was changed or deleted.
* `Excluded`: Set the target to be excluded from modification or deletion on import.   
  **Note**
  This tag must be assigned by the user.


# Configure RPExtensionUtilities.hep

Add the following to RPExtensionUtilities.hep.  
Modify the 1 in each item to match the number of plug-ins/helpers described in the file.  
For example, if `numberOfElements=2`, change `name1` to `name2`.

```
#REM: Doxygen Import
name1=RPDoxygenXML\Import
isPluginCommand1=1
command1=RPExtensionUtilities
applicableTo1=Package
isVisible1=1
DLLServerCompatible1=1
```

```
#REM: Check Unavailable Activities
name2=RPDoxygenXML\Check Unavailable Activities
isPluginCommand2=1
command2=RPExtensionUtilities
applicableTo2=Package,Project
isVisible2=1
DLLServerCompatible2=1
```

# Rhapsody operations

## Import from Doxygen XML to Rhapsody

1. Select the package of IBM Rhapsody explorer.
2. Right-click to display menu.
3. Select `RPDoxygenXML` -> `Import`
4. Click the Select button and select the directory where the Doxygen XML files were output.
   1. If you select an XML output directory, any APIs not included in the XML of that directory will be considered deleted.
   2. If you specify an XML file, any APIs not included in the XML will not be considered deleted.
5. Specify the import version.
6. Specify the target to import as a model by checking the checkboxes
7. Click the Import button to execute the import.
8. The import status will be displayed in the output window.
9. A popup notifies you when the import is finished.

## Check Unavailable Activities

1. Select the package of IBM Rhapsody explorer.
2. Right-click to display menu.
3. Select `RPDoxygenXML` -> `Check Unavailable Activities`
4. The results are output to the Output window.