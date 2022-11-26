package com.ibm.rhapsody.rputilities.window;

import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import javax.swing.*;
import java.io.File;

public class FileSelector {
    protected RPLog log_ = new RPLog(FileSelector.class);
    protected String lastPath_ = null;
  
    public FileSelector() {
    }
  
    public String GetOpenDirectoryDialog() {
        JFileChooser filechooser = null;
        if(lastPath_ != null) {
            filechooser = new JFileChooser(lastPath_);
        }
        else {
            filechooser = new JFileChooser();
        }
      
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selected = filechooser.showOpenDialog(null);

        if (selected == JFileChooser.APPROVE_OPTION) {
            File file = filechooser.getSelectedFile();
            lastPath_ = file.getAbsolutePath();

            log_.info("Approve Target:"+ lastPath_);
        }      

        return lastPath_;
    }


}
