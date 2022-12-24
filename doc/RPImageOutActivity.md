# Plug-in Features

Outputs an activity diagram under the specified package as an image file.  
The output destination of the image file is the same directory as the project file of the active project.  

## Configure RPExtensionUtilities.hep

Add the following to RPExtensionUtilities.hep.  
Modify the 1 in each item to match the number of plug-ins/helpers described in the file.  
For example, if `numberOfElements=2`, change `name1` to `name2`.  
Multiple image formats can be supported by changing the JPG portion below.  

```
#REM: Image Out Activity. format is EMF, BMP, JPEG, JPG, TIFF.
name1=RPImageOutActivity\JPG
isPluginCommand1=1
command1=RPExtensionUtilities
applicableTo1=Package,ActivityDiagram
isVisible1=1
DLLServerCompatible1=1
```

## Rhapsody operations

1. Select the package of IBM Rhapsody explorer.
2. Right-click to display menu.
3. Select `RPImageOutActivity` -> `JPG` (image format name)