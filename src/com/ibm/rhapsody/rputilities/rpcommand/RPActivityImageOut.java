package com.ibm.rhapsody.rputilities.rpcommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ibm.rhapsody.rputilities.rpcore.RPActivitiyFacade;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.telelogic.rhapsody.core.IRPActivityDiagram;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;
import com.telelogic.rhapsody.core.IRPStatechart;

class RPActivityImageOut extends IRPUtilityCommmand {
    
    protected String   m_ImageDirectory = null;
    protected final String IMAGEOUTDIRECTRY_PREFIX = "ActivityImage"; 
    protected final String IMAGEOUTFILE_PREFIX = "act"; 
    protected final String IMAGEOUT_FORMAT = "JPG"; 
    protected final int NEED_IMAGEMAP = 0; 

    /**
     * アクティビティ図画像出力クラス
     * @param element 右クリック時に選択された要素
     */
    public RPActivityImageOut(IRPModelElement element) 
    {
        super(element);
    }


    /* 
     * 選択されたパッケージのアクティビティ画像を出力する
     * @see com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) 
    {
        boolean result = false;
        IRPModelElement element = getElement();
        if(element == null)
        {
            RPLog.Info("name[" + argment[0] + "] is need select element.\n"
                + "Please select one Element.");
            return false;
        }

        RPLog.Info("Activitiy Image Out Start:" + element.getDisplayName());

        if(element instanceof IRPPackage)
        {
            IRPPackage rppackage = getElement();
            result = ImageOutActivity(rppackage);
        }
        else if(element instanceof IRPFlowchart)
        {
            IRPFlowchart rpActivity = getElement();
            result = ImageOutStateChart(rpActivity);
        }
        else
        {
            RPLog.Info("select element["+ element.getDisplayName() 
                + "]("+ element.getClass().toString() + ") is not target element. ");
        }

        RPLog.Info("Activitiy Image Out End:" 
            + (m_ImageDirectory != null ? m_ImageDirectory : "--None--"));

        return result;
    }
    /**
     * 選択されたパッケージ以下のアクティビティ図の画像を出力する
     * @param rppackage 選択されたパッケージ
     * @return 画像出力結果
     */
    protected boolean ImageOutActivity(IRPPackage rppackage) 
    {
        RPLog.Debug("Package:" + rppackage.getDisplayName()
                + " ImageOut Activity");
        
        boolean result = false;

        //List<Object> activityCollection = rppackage.getBehavioralDiagrams().toList();
        List<IRPFlowchart> activityCollection = RPActivitiyFacade.CollectActivity(rppackage,1);
        for(IRPFlowchart rpflowchart : activityCollection)
        {
            result = ImageOutStateChart(rpflowchart);
            if( result != true )
            {
                return false;
            }
        }

        return true;
    }


    protected boolean ImageOutStateChart(IRPStatechart chart) 
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


        String imagepath = GetImageFilePath(chart,IMAGEOUT_FORMAT);
        IRPCollection diagrammap = null;
        chart.getPictureAs(imagepath,IMAGEOUT_FORMAT,NEED_IMAGEMAP,diagrammap);

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

        try 
        {
            Path p = Paths.get(directryPath);
            Files.createDirectory(p);
        } 
        catch(IOException e) 
        {
            RPLog.logException("Create Directory Error" + directryPath , e);
            return false;
        }

        m_ImageDirectory = directryPath;
        return true;
    }


    protected String GetImageFilePath(IRPStatechart chart, String imageFormat) 
    {
        if( chart == null )
        {
            return "";
        }

        String filePath = m_ImageDirectory + "/"
             + IMAGEOUTFILE_PREFIX + "_" + getFileName(chart,imageFormat);

        return filePath;
    }

    protected static String getFileName(IRPModelElement element, String imageFormat)
    {
        String fileName = imageFormat.toLowerCase();
        IRPModelElement checkelement = element;

        while(checkelement != null)
        {
            if( checkelement.getIsOfMetaClass("Project") == 1 )
            {
                break;
            }

            if(checkelement != element)
            {
                fileName = checkelement.getDisplayName() + "_" + fileName;
            }
            else
            {
                fileName = checkelement.getDisplayName() + "." + fileName;
            }

            checkelement = checkelement.getOwner();
        }

        return fileName;
    } 
}
