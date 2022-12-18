package com.ibm.rhapsody.rputilities.rpcommand;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;
import com.telelogic.rhapsody.core.IRPModelElement;

public abstract class IRPUtilityCommmand extends ARPObject {

    protected IRPModelElement element_ = null;
    protected Properties settings_ = new Properties();

    /**
     * Constructor of Rhapsody utility command class
     * 
     * @param element Elements selected when right-clicked
     */
    protected IRPUtilityCommmand(Class<?> clazz, IRPModelElement element) {
        super(clazz);
        element_ = element;
        loadProperties();
    }

    /**
     * Commands executed when right-clicke
     * 
     * @param argment Menu selected when right-clicked (array of names in hep file
     *                divided by delimiters)
     * @return Result of command execution (true: success false: failure)
     */
    public abstract boolean command(String[] argment);

    /**
     * Get elements selected when right-clicked
     * 
     * @param <T> Type to cast
     * @return Elements selected when right-clicked(null when multiple elements are
     *         selected)
     */
    public <T> T getElement() {
        return getObject(element_);
    }

    protected String getPropertiyFileName() {
        String filename = clazz_.getSimpleName() + ".properties";
        return filename;
    }

    public void setProperty(String name, String value) {
        settings_.setProperty(name, value);
    }

    public String getProperty(String name) {
        return settings_.getProperty(name);
    }

    public void loadProperties() {
        String propfile = getPropertiyFileName();
        if (RPFileSystem.isExists(propfile) == false) {
            return;
        }

        FileInputStream propfilestream = null;

        try {
            propfilestream = new FileInputStream(propfile);
            settings_.load(propfilestream);
        } catch (Exception e) {
            error("loadProperties Error:", e);
        } finally {
            try {
                if (propfilestream != null) {
                    propfilestream.close();
                }
            } catch (Exception e) {
                error("loadProperties Error:", e);
            }
        }

        Set<String> properiynames = settings_.stringPropertyNames();
        for (String name : properiynames) {
            debug("load property name:" + name + " value:" + settings_.getProperty(name, ""));
        }

    }

    public void saveProperties() {
        if (settings_.isEmpty() == true) {
            return;
        }

        FileOutputStream propfilestream = null;
        String propfile = getPropertiyFileName();
        try {
            propfilestream = new FileOutputStream(propfile);
            settings_.store(propfilestream, propfile);
        } catch (Exception e) {
            error("saveProperties Error:", e);
        } finally {
            try {
                if (propfilestream != null) {
                    propfilestream.close();
                }
            } catch (Exception e) {
                error("saveProperties Error:", e);
            }
        }
    }
}
