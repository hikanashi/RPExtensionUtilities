package com.ibm.rhapsody.rputilities.rpcommand.Imageout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.rpcore.RPActivityFacade;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStatechart;

public class RPActivityImageOut extends IRPUtilityCommmand {
    
    protected String   m_ImageDirectory = null;
    protected final String IMAGEOUTDIRECTRY_PREFIX = "ActivityImage"; 
    protected final String IMAGEOUTFILE_PREFIX = "act"; 
    protected final String IMAGEOUT_DEFAULT_FORMAT = "JPG"; 
    protected final int NEED_IMAGEMAP = 0; 

    /**
     * アクティビティ図画像出力クラス
     * @param element 右クリック時に選択された要素
     */
    public RPActivityImageOut(IRPModelElement element) 
    {
        super(RPActivityImageOut.class,element);
    }


    /* 
     * 選択されたパッケージのアクティビティ画像を出力する
     * @see com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) 
    {
        boolean result = false;
        IRPModelElement element = getElement();
        String imageFormat = IMAGEOUT_DEFAULT_FORMAT;
        if(element == null)
        {
            error("name[" + argment[0] + "] is need select element.\n"
                + "Please select one Element.");
            return false;
        }

        if(argment.length > 1)
        {
            imageFormat = argment[1];
        }

        info("Activitiy Image Out Start:" + element.getDisplayName() + "ImageFormat:" + imageFormat);

        UpdateImageProperty(element);

        if(element instanceof IRPPackage)
        {
            IRPPackage rppackage = getElement();
            result = ImageOutActivity(rppackage, imageFormat);
        }
        else if(element instanceof IRPFlowchart)
        {
            IRPFlowchart rpActivity = getElement();
            result = ImageOutStateChart(rpActivity, imageFormat);
        }
        else
        {
            info("select element["+ element.getDisplayName() 
                + "]("+ element.getClass().toString() + ") is not target element. ");
        }

        info("Activitiy Image Out End:" 
            + (m_ImageDirectory != null ? m_ImageDirectory : "--None--"));

        if(result != true) {
            DeleteImageDirectory();
        }

        return result;
    }


    /**
     * Update properties to prevent shrinking when outputting images
     * @param element target element
     * @return Property setting results(true:success false:failure)
     */
    protected boolean UpdateImageProperty(IRPModelElement element) 
    {
        if(element == null)
        {
            return false;
        }

        IRPProject rpProject = element.getProject();
        if(rpProject == null)
        {
            return false;
        }

        String propertyKey = "General.Graphics.ExportedDiagramScale";
        String updateValue = "NoPagination";

        String currentValue = rpProject.getPropertyValue(propertyKey);
        debug("before Project:" + rpProject.getDisplayName()
            + " Value:"+ currentValue);
        
        if(currentValue.equals(updateValue)) {
            debug("Property is already updated.");
            return true;
        }

        rpProject.setPropertyValue(propertyKey,updateValue);

        debug("after Project:" + rpProject.getDisplayName()
            + " Value:"+ rpProject.getPropertyValue(propertyKey));

        return true;
    }



    /**
     * 選択されたパッケージ以下のアクティビティ図の画像を出力する
     * @param rppackage 選択されたパッケージ
     * @return 画像出力結果
     */
    protected boolean ImageOutActivity(IRPPackage rppackage, String imageFormat) 
    {
        debug("Package:" + rppackage.getDisplayName()
                + " ImageOut Activity");
        
        boolean result = false;
        RPActivityFacade activityFacade = new RPActivityFacade();
        //List<Object> activityCollection = rppackage.getBehavioralDiagrams().toList();
        List<IRPFlowchart> activityCollection = activityFacade.CollectActivity(rppackage,1);
        for(IRPFlowchart rpflowchart : activityCollection)
        {
            result = ImageOutStateChart(rpflowchart, imageFormat);
            if( result != true )
            {
                return false;
            }
        }

        return true;
    }


    protected boolean ImageOutStateChart(IRPStatechart chart, String imageFormat) 
    {
        if(chart == null )
        {
            return false;
        }

        if( m_ImageDirectory == null )
        {
            boolean result = CreateImageDirectory(chart);
            if(result != true)
            {
                return false;
            }
        }

        String imagepath = GetImageFilePath(chart,imageFormat);
        IRPCollection diagrammap = null;

        info("Imageout:" + chart.getDisplayName()
                + " Path:" + imagepath);

        int image_number = chart.getPictureAs(imagepath,imageFormat,NEED_IMAGEMAP,diagrammap).getCount();
        if(image_number < 1)
        {
            error("Create Image Fail. file:" + imagepath );
            error("Check Image format in RPExtensionUtilities.hep and check directory permission,");
            return false;
        }

        return true;
    }

    protected boolean CreateImageDirectory(IRPStatechart chart) 
    {
        if( chart == null )
        {
            return false;
        }

        IRPProject rpProject = chart.getProject();
        
        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter dateformatter =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formatNowDate = dateformatter.format(nowDate);
        
        String directryPath = rpProject.getCurrentDirectory()
            + "/" + IMAGEOUTDIRECTRY_PREFIX + "_" + formatNowDate;

        RPFileSystem filesystem = new RPFileSystem();
        if( filesystem.CreateDirectory(directryPath) ) {
            m_ImageDirectory = directryPath;
            return true;
        } 
        else {
            error("Create Directory Error" + directryPath);
            return false;
        }
    }

    protected void DeleteImageDirectory() 
    {
        if( m_ImageDirectory == null )
        {
            return;
        }

        RPFileSystem filesystem = new RPFileSystem();
        filesystem.Delete(m_ImageDirectory);
    }

    protected String GetImageFilePath(IRPStatechart chart, String imageFormat) 
    {
        if( chart == null )
        {
            return "";
        }

        String filePath = m_ImageDirectory + "/" + IMAGEOUTFILE_PREFIX + "_" 
                            + getPathToProject(chart,"_") + "." + imageFormat.toLowerCase();

        return filePath;
    }
}
