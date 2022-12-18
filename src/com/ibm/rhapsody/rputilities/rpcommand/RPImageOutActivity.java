package com.ibm.rhapsody.rputilities.rpcommand;

import java.util.List;

import com.ibm.rhapsody.rputilities.rpcore.RPActivityFacade;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStatechart;

public class RPImageOutActivity extends IRPUtilityCommmand {
    protected static final String IMAGEOUTDIRECTRY_PREFIX = "ActivityImage";
    protected static final String IMAGEOUTFILE_PREFIX = "act";
    protected static final String IMAGEOUT_DEFAULT_FORMAT = "JPG";
    protected static final int NEED_IMAGEMAP = 0;

    protected String imageDirectory_ = null;

    /**
     * Activity Diagram Image Output Class
     * 
     * @param element Elements selected when right-clicked
     */
    public RPImageOutActivity(IRPModelElement element) {
        super(RPImageOutActivity.class, element);
    }

    /*
     * Outputs an activity image for the selected package
     * 
     * @see
     * com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) {
        boolean result = false;
        IRPModelElement element = getElement();
        String imageFormat = IMAGEOUT_DEFAULT_FORMAT;
        if (element == null) {
            error("name[" + argment[0] + "] is need select element.\n"
                    + "Please select one Element.");
            return false;
        }

        if (argment.length > 1) {
            imageFormat = argment[1];
        }

        info("Activitiy Image Out Start:" + element.getDisplayName() + "ImageFormat:" + imageFormat);

        UpdateImageProperty(element);

        if (element instanceof IRPPackage) {
            IRPPackage rppackage = getElement();
            result = ImageOutActivity(rppackage, imageFormat);
        } else if (element instanceof IRPFlowchart) {
            IRPFlowchart rpActivity = getElement();
            result = ImageOutStateChart(rpActivity, imageFormat);
        } else {
            info("select element[" + element.getDisplayName()
                    + "](" + element.getClass().toString() + ") is not target element. ");
        }

        info("Activitiy Image Out End:"
                + (imageDirectory_ != null ? imageDirectory_ : "--None--"));

        if (result != true) {
            DeleteImageDirectory();
        }

        return result;
    }

    /**
     * Update properties to prevent shrinking when outputting images
     * 
     * @param element target element
     * @return Property setting results(true:success false:failure)
     */
    protected boolean UpdateImageProperty(IRPModelElement element) {
        if (element == null) {
            return false;
        }

        IRPProject rpProject = element.getProject();
        if (rpProject == null) {
            return false;
        }

        String propertyKey = "General.Graphics.ExportedDiagramScale";
        String updateValue = "NoPagination";

        String currentValue = rpProject.getPropertyValue(propertyKey);
        debug("before Project:" + rpProject.getDisplayName()
                + " Value:" + currentValue);

        if (currentValue.equals(updateValue)) {
            debug("Property is already updated.");
            return true;
        }

        rpProject.setPropertyValue(propertyKey, updateValue);

        debug("after Project:" + rpProject.getDisplayName()
                + " Value:" + rpProject.getPropertyValue(propertyKey));

        return true;
    }

    /**
     * Outputs an image of the activity diagram below the selected package
     * 
     * @param rppackage   selected package
     * @param imageFormat Output image format
     * @return Image output result(true:success false:failure)
     */
    protected boolean ImageOutActivity(IRPPackage rppackage, String imageFormat) {
        debug("Package:" + rppackage.getDisplayName()
                + " ImageOut Activity");

        boolean result = false;
        RPActivityFacade activityFacade = new RPActivityFacade();
        // List<Object> activityCollection = rppackage.getBehavioralDiagrams().toList();
        List<IRPFlowchart> activityCollection = activityFacade.CollectActivity(rppackage, 1);
        for (IRPFlowchart rpflowchart : activityCollection) {
            result = ImageOutStateChart(rpflowchart, imageFormat);
            if (result != true) {
                return false;
            }
        }

        return true;
    }

    /**
     * Outputs an image of the specified activity diagram
     * 
     * @param chart       Activity diagram for output target
     * @param imageFormat Output image format
     * @return Image output result(true:success false:failure)
     */
    protected boolean ImageOutStateChart(IRPStatechart chart, String imageFormat) {
        if (chart == null) {
            return false;
        }

        if (imageDirectory_ == null) {
            boolean result = CreateImageDirectory(chart);
            if (result != true) {
                return false;
            }
        }

        String imagepath = GetImageFilePath(chart, imageFormat);
        IRPCollection diagrammap = null;

        info("Imageout:" + chart.getDisplayName()
                + " Path:" + imagepath);

        int image_number = chart.getPictureAs(imagepath, imageFormat, NEED_IMAGEMAP, diagrammap).getCount();
        if (image_number < 1) {
            error("Create Image Fail. file:" + imagepath);
            error("Check Image format in RPExtensionUtilities.hep and check directory permission,");
            return false;
        }

        return true;
    }

    /**
     * Generate a directory to output images to
     * 
     * @param chart Activity diagram for output target
     * @return Result of directory generation(true:success false:failure)
     */
    protected boolean CreateImageDirectory(IRPStatechart chart) {
        if (chart == null) {
            return false;
        }

        IRPProject rpProject = chart.getProject();
        RPFileSystem filesystem = new RPFileSystem();
        String formatNowDate = RPFileSystem.CreateDateTimeString(null);

        String directryPath = rpProject.getCurrentDirectory()
                + "/" + IMAGEOUTDIRECTRY_PREFIX + "_" + formatNowDate;

        if (filesystem.CreateDirectory(directryPath)) {
            imageDirectory_ = directryPath;
            return true;
        } else {
            error("Create Directory Error" + directryPath);
            return false;
        }
    }

    /**
     * Delete directory
     * If directory is empty, delete the generated directory.
     */
    protected void DeleteImageDirectory() {
        if (imageDirectory_ == null) {
            return;
        }

        RPFileSystem filesystem = new RPFileSystem();
        filesystem.Delete(imageDirectory_);
    }

    /**
     * Get the absolute path of the output image of the specified activity diagram.
     * 
     * @param chart       Activity diagram for output target
     * @param imageFormat Output image format
     * @return Result of getting absolute path(true:success false:failure)
     */
    protected String GetImageFilePath(IRPStatechart chart, String imageFormat) {
        if (chart == null) {
            return "";
        }

        String filePath = imageDirectory_ + "/" + IMAGEOUTFILE_PREFIX + "_"
                + getPathToProject(chart, "_") + "." + imageFormat.toLowerCase();

        return filePath;
    }
}
