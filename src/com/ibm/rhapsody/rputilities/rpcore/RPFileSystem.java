package com.ibm.rhapsody.rputilities.rpcore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RPFileSystem extends ARPObject {
    protected static String activeProjectPath_ = null;


    /**
     * 
     */
    public RPFileSystem() {
        super(RPFileSystem.class);
    }

    /**
     * @param directryPath
     * @return
     */
    public boolean IsReadable(String path) {
        Path pathobj = Paths.get(path);
        return Files.isReadable(pathobj);
    }

    /**
     * @param directryPath
     * @return
     */
    public boolean IsWritable(String path) {
        Path pathobj = Paths.get(path);
        return Files.isWritable(pathobj);
    }


    /**
     * @param directryPath
     * @return
     */
    public boolean isExists(String path) {
        Path pathobj = Paths.get(path);
        return Files.exists(pathobj);
    }


    public static String CreateDateTimeString(String format) {
        String formatString = format;
        if(formatString == null)
        {
            formatString = "yyyyMMddHHmmssSSS";
        }

        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter dateformatter =
            DateTimeFormatter.ofPattern(formatString);
        String formatNowDate = dateformatter.format(nowDate);
        return formatNowDate;
    }


    /**
     * @param directryPath
     * @return
     */
    public boolean CreateDirectory(String directryPath) {
        Path pathobj = Paths.get(directryPath);

        try 
        {
            if(Files.isDirectory(pathobj) != true)
            {
                debug("CreateDirectory:" + directryPath );
                Files.createDirectory(pathobj);
            }
        } 
        catch(IOException e) 
        {
            error("Create Directory Error" + directryPath , e);
            return false;
        }

        return true;
    }

    /**
     * @param filePath
     * @return
     */
    public boolean Delete(String filePath) {
        if(filePath == null ) {
            return false;
        }

        Path pathobj = Paths.get(filePath);

        try 
        {
            int filecount = countFiles(filePath);
            if(filecount < 1)
            {
                debug("Delete:" + filePath );
                Files.delete(pathobj);
            }
            else {
                warn("Can't Delete:" + filePath + " isn't empty file:" + filecount );
            }
        } 
        catch(IOException e) 
        {
            error("Delete Error" + filePath , e);
            return false;
        }

        return true;
    }

    /**
     * @param filePath
     * @return
     */
    public int countFiles(String filePath) {
        File dir = new File(filePath);
        int count = countFiles(dir.listFiles());
        return count;
    }


    public static String getActiveProjectPath() {
        return activeProjectPath_;
    }

    public static void setActiveProjectPath(String path) {
        activeProjectPath_ = path;
    }

	/**
	 * @param list
	 * @return
	 */
	private static int countFiles(File[] list) {
        int file_count = 0;

        if(list == null)
        {
            return 0;
        }

		for (File f : list) {
			if (f.isDirectory()) {
				file_count += countFiles(f.listFiles());
			}
            else if (f.isFile()) {
				file_count++;
			} 
            else if (f.isHidden()) {
                file_count++;
            }
		}

        return file_count;
	}
    
}
