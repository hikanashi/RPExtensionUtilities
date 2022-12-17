package com.ibm.rhapsody.rputilities.rpcommand.common;

import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPModelElement;
import java.lang.reflect.Constructor;

public class RPCommandRunner extends ARPObject {
    protected static RPLog slog_ = new RPLog(RPCommandRunner.class);

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
    public static void RunCommand(String utilCommand, IRPModelElement element) {

        String selElemName = new String();
        if (element != null) {
            selElemName = element.getName();
        } else {
            selElemName = "No selected element";
        }

        slog_.debug("OnMenuItemSelect " + utilCommand + " Selected element name is: " + selElemName);

        try {
            String[] commandargs = utilCommand.split("\\\\");

            setLogLevel(commandargs);

            boolean result = invokeCommand(commandargs, element);
            if (result != true) {
                slog_.error("CommandError Menu:" + utilCommand + " Select Item:" + selElemName);
            }

        } catch (Exception e) {
            slog_.error("CommandError Menu:" + utilCommand + " Select Item:" + selElemName, e);
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
     * @param element     element selected at right-clicked (null for multiple
     *                    selections)
     * @return result of command running (true:success false:failure)
     */
    public static boolean invokeCommand(String[] commandargs, IRPModelElement element) {
        if (commandargs.length < 1) {
            slog_.error("name is invaild. Please check .hep file");
            return false;
        }

        String className = IRPUtilityCommmand.class.getPackage().getName() + "." + commandargs[0];
        slog_.debug("className:" + className + " commandargs:" + commandargs.length);

        try {
            Class<?> commandClass = Class.forName(className);
            Constructor<?> constructor = commandClass.getDeclaredConstructor(IRPModelElement.class);
            IRPUtilityCommmand rpcommnad = (IRPUtilityCommmand) constructor.newInstance(element);
            slog_.debug("Create Command:" + className);

            if (rpcommnad == null) {
                slog_.error("className:" + className + " is newInstance fail");
                return false;
            }

            return rpcommnad.command(commandargs);
        } catch (Exception e) {
            slog_.error("CommandError:" + className, e);
            return false;
        }
    }
}
