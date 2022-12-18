package com.ibm.rhapsody.rputilities.window;

import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSelector {
    protected RPLog log_ = new RPLog(FileSelector.class);
    protected String lastPath_ = null;

    public FileSelector(String path) {
        lastPath_ = path;
    }

    public String GetOpenDirectoryDialog() {
        String selectPath = null;
        JFileChooser filechooser = null;

        Path pathobj = Paths.get(lastPath_);
        if (Files.exists(pathobj) == true) {
            filechooser = new JFileChooser(lastPath_);
        } else {
            filechooser = new JFileChooser();
        }
        filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int selected = filechooser.showOpenDialog(null);

        if (selected == JFileChooser.APPROVE_OPTION) {
            File file = filechooser.getSelectedFile();
            selectPath = file.getAbsolutePath();

            log_.debug("Approve Selected Target:" + selectPath);
        }

        return selectPath;
    }

}
