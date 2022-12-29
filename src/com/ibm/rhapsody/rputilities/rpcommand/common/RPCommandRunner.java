package com.ibm.rhapsody.rputilities.rpcommand.common;

import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPApplication;
import com.telelogic.rhapsody.core.IRPModelElement;
import java.lang.reflect.Constructor;

public class RPCommandRunner extends ARPObject {

    public RPCommandRunner() {
        super(RPCommandRunner.class);
    }

    /**
     * Run a command from the selected menu.
     * The command is the name of the selected menu, divided by /.
     * 
     * @param utilCommand Name of the selected menu
     * @param element     element selected when right-clicked (null when multiple
     *                    selections are made)
     */
    public void RunCommand(String utilCommand, IRPApplication application) {

        String versionString = System.getProperty("java.version");
        String version[] = versionString.split("\\.");
        debug("Java Version:" + versionString);

        if( version.length < 1) {
            error("Unable to use plugin due to incorrect version. version:" + versionString);
            return;
        }

        int majorVersion = Integer.parseInt(version[0]);
        if(majorVersion != 17) {
            error("Non-supported version:" + versionString + ". Use JDK 17.");
            error("Download JDK at https://projects.eclipse.org/projects/adoptium");
            error("After installing JDK, please change JavaLocation in Rhapsody.ini.");
            error("Storage location of Rhapsody.ini C:\\ProgramData\\IBM\\Rhapsody\\9.0.1x64\\rhapsody.ini");
            error("Before modification of Rhapsody.ini:JavaLocation=C:\\Program Files\\IBM\\Rhapsody\\9.0.1\\jdk\\jre");
            error("After changing Rhapsody.ini (in case of JDK 17.0.5.8-hotspot): JavaLocation=C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.5.8-hotspot\\");
            return;
        }

        IRPModelElement element = application.getSelectedElement();
        String selElemName = new String();
        if (element != null) {
            selElemName = element.getName();
        } else {
            selElemName = "No selected element";
        }

        debug("OnMenuItemSelect " + utilCommand + " Selected element name is: " + selElemName);

        try {
            String[] commandargs = utilCommand.split("\\\\");

            setLogLevel(commandargs);

            boolean result = invokeCommand(commandargs, application);
            if (result != true) {
                error("CommandError Menu:" + utilCommand + " Select Item:" + selElemName);
            }

        } catch (Exception e) {
            error("CommandError Menu:" + utilCommand + " Select Item:" + selElemName, e);
        }

    }

    /**
     * Set the level of log output when the command is executed.
     * Change the level of log output when the following are included in a command.
     * - LogDebug : Outputs debug level logs.
     * - LogTrace : Outputs trace-level logs.
     * 
     * @param commandargs
     */
    public static void setLogLevel(String[] commandargs) {
        for (int index = 0; index < commandargs.length; index++) {
            if (commandargs[index].equals("LogDebug")) {
                RPLog.setLevel(RPLogLevel.DEBUG);
            } else if (commandargs[index].equals("LogTrace")) {
                RPLog.setLevel(RPLogLevel.TRACE);
            }
        }
    }

    /**
     * Runs a command
     * Create an instance with the head of the command as the execution class name,
     * and run the command method of the created instance.
     * By specifying and running all commands including the first command in the
     * command method,
     * it is possible to This enables switching the operation within the command
     * method.
     * 
     * @param commandargs command array
     * @param application     element selected at right-clicked (null for multiple
     *                    selections)
     * @return result of command running (true:success false:failure)
     */
    public boolean invokeCommand(String[] commandargs, IRPApplication application) {
        if (commandargs.length < 1) {
            error("name is invaild. Please check .hep file");
            return false;
        }

        String className = IRPUtilityCommmand.class.getPackage().getName() + "." + commandargs[0];
        debug("className:" + className + " commandargs:" + commandargs.length);

        try {
            Class<?> commandClass = Class.forName(className);
            Constructor<?> constructor = commandClass.getDeclaredConstructor(IRPApplication.class);
            IRPUtilityCommmand rpcommnad = (IRPUtilityCommmand) constructor.newInstance(application);
            debug("Create Command:" + className);

            if (rpcommnad == null) {
                error("className:" + className + " is newInstance fail");
                return false;
            }

            return rpcommnad.command(commandargs);
        } catch (Exception e) {
            error("CommandError:" + className, e);
            return false;
        }
    }
}
