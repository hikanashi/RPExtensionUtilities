# Plug-in Features

Count the actions in the activity diagram included in the package.

## Configure RPExtensionUtilities.hep

Add the following to RPExtensionUtilities.hep.  
Modify the 1 in each item to match the number of plug-ins/helpers described in the file.  
For example, if `numberOfElements=2`, change `name1` to `name2`.  

```
#REM: Count Activity
name1=RPCountActivity
isPluginCommand1=1
command1=RPExtensionUtilities
applicableTo1=Package,ActivityDiagram
isVisible1=1
DLLServerCompatible1=1
```

## Rhapsody operations

1. Select the package of IBM Rhapsody explorer.
2. Right-click to display menu.
3. Select `RPCountActivity` specified in the hep file.