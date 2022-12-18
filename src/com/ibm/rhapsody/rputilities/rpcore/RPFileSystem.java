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
     * Get the directory path of the open active project.
     * 
     * @return Absolute path of the active project
     */
    public static String getActiveProjectPath() {
        return activeProjectPath_;
    }

    /**
     * Set the full path of the open active project
     * 
     * @param path Absolute path of the active project
     */
    public static void setActiveProjectPath(String path) {
        activeProjectPath_ = path;
    }

    /**
     * Check if the target path is readable
     * 
     * @param path Paths to be checked
     * @return true is readable. false is not readable.
     */
    public static boolean IsReadable(String path) {
        Path pathobj = Paths.get(path);
        return Files.isReadable(pathobj);
    }

    /**
     * Check if the target path is writable
     * 
     * @param path Paths to be checked
     * @return true is writable. false is not writable.
     */
    public static boolean IsWritable(String path) {
        Path pathobj = Paths.get(path);
        return Files.isWritable(pathobj);
    }

    /**
     * Check if the target path is exist
     * 
     * @param path Paths to be checked
     * @return true is exist. false is not exist.
     */
    public static boolean isExists(String path) {
        Path pathobj = Paths.get(path);
        return Files.exists(pathobj);
    }

    /**
     * Check if the target path is directory
     * 
     * @param path Paths to be checked
     * @return true is directory. false is not directory.
     */
    public static boolean isDirectory(String path) {
        Path pathobj = Paths.get(path);
        return Files.isDirectory(pathobj);
    }

    /**
     * Output date and time as a string in a specified format
     * Default format is "yyyyMMddHHmmssSSS".
     * 
     * @param format Output format; if null, apply default format
     * @return Date/Time String
     */
    public static String CreateDateTimeString(String format) {
        String formatString = format;
        if (formatString == null) {
            formatString = "yyyyMMddHHmmssSSS";
        }

        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern(formatString);
        String formatNowDate = dateformatter.format(nowDate);
        return formatNowDate;
    }

    /**
     * constructor
     */
    public RPFileSystem() {
        super(RPFileSystem.class);
    }

    /**
     * Creates a directory in the specified path.
     * If the directory already exists, do not create the directory.
     * 
     * @param directryPath Path of the directory to be created
     * @return true: directory creation succeeded, false: directory creation failed.
     */
    public boolean CreateDirectory(String directryPath) {
        Path pathobj = Paths.get(directryPath);

        try {
            if (Files.isDirectory(pathobj) != true) {
                debug("CreateDirectory:" + directryPath);
                Files.createDirectory(pathobj);
            }
        } catch (IOException e) {
            error("Create Directory Error" + directryPath, e);
            return false;
        }

        return true;
    }

    /**
     * Deletes files and directories in the specified path.
     * If files exist under the directory, the specified directory is not deleted.
     * 
     * @param filePath File path to be deleted
     * @return true: deletion succeeded, false: deletion failed.
     */
    public boolean Delete(String filePath) {
        if (filePath == null) {
            return false;
        }

        Path pathobj = Paths.get(filePath);
        if (Files.exists(pathobj) != true) {
            return true;
        }

        try {
            int filecount = countFiles(filePath);
            if (filecount < 1) {
                debug("Delete:" + filePath);
                Files.delete(pathobj);
            } else {
                warn("Can't Delete:" + filePath + " isn't empty file:" + filecount);
            }
        } catch (IOException e) {
            error("Delete Error" + filePath, e);
            return false;
        }

        return true;
    }

    /**
     * Counts the number of files under a specified directory.
     * 
     * @param filePath
     * @return
     */
    private int countFiles(String filePath) {
        File dir = new File(filePath);
        int count = countFilesInternal(dir.listFiles());
        return count;
    }

    /**
     * Counts the number of files from the specified file list.
     * Counts normal or hidden files in the file list.
     * A directory in the file list is recursively counted for the number of files
     * under that directory.
     * 
     * @param list file list
     * @return Number of files
     */
    private static int countFilesInternal(File[] list) {
        int file_count = 0;

        if (list == null) {
            return 0;
        }

        for (File f : list) {
            if (f.isDirectory()) {
                file_count += countFilesInternal(f.listFiles());
            } else if (f.isFile()) {
                file_count++;
            } else if (f.isHidden()) {
                file_count++;
            }
        }

        return file_count;
    }

}
