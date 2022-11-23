package com.ibm.rhapsody.rputilities.rpcore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RPFileSystem extends ARPObject {
    
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
    public boolean CreateDirectory(String directryPath) {
        Path p = Paths.get(directryPath);

        try 
        {
            if(Files.isDirectory(p) != true)
            {
                Files.createDirectory(p);
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
        Path p = Paths.get(filePath);
        try 
        {
            if(countFiles(filePath) < 1)
            {
                Files.delete(p);
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
