package com.ibm.rhapsody.rputilities;

import com.ibm.rhapsody.rputilities.rpcommand.RPCommandRunner;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.telelogic.rhapsody.core.*;

public class RPExtensionUtilities extends RPUserPlugin {
	// protected static RPLog slog_ = new RPLog(RPExtensionUtilities.class);
	protected static IRPApplication rpApplication_ = null;


	/**
	 * get Rhapsody Application
	 * @return Rhapsody Application
	 */
	public static IRPApplication getApplication() {
		return rpApplication_;
	}

	/* 
	 * called when the plug-in is loaded
	 * @see com.telelogic.rhapsody.core.RPUserPlugin#RhpPluginInit(com.telelogic.rhapsody.core.IRPApplication)
	 */
	public void RhpPluginInit(final IRPApplication rpApplication) {
		// keep the application interface for later use
		rpApplication_ = rpApplication;
		RPLog.Initialize("RPUtilities", rpApplication);
		
		if(rpApplication_ != null) {
			IRPProject rpproject = rpApplication_.activeProject();
			if(rpproject != null) {
				RPFileSystem.setActiveProjectPath(rpproject.getCurrentDirectory());
			}
		}

		// slog_.info("Plugin Load:"+ this.getClass().toString());
	}

	
	/* 
	 * called when the plug-in menu item under the "Tools" menu is selected	
	 * @see com.telelogic.rhapsody.core.RPUserPlugin#RhpPluginInvokeItem()
	 */
	public void RhpPluginInvokeItem() {
		// slog_.debug("Tools command");
	}

	/* 
	 * called when the plug-in pop-up menu (if applicable) is selected
	 * @see com.telelogic.rhapsody.core.RPUserPlugin#OnMenuItemSelect(java.lang.String)
	 */
	public void OnMenuItemSelect(String menuItem) {
		//show the selected element name
		IRPModelElement element = rpApplication_.getSelectedElement();
		RPCommandRunner.RunCommand(menuItem, element);
	}

	/* 
	 * called when the plug-in popup trigger (if applicable) fired
	 * @see com.telelogic.rhapsody.core.RPUserPlugin#OnTrigger(java.lang.String)
	 */
	public void OnTrigger(String trigger) {
		//show the trigger string
		//JOptionPane.showMessageDialog(null, "Hello world from SimplePlugin.OnTrigger " + trigger);
		// slog_.debug("OnTrigger " + trigger);
	}

	/* called when the project is closed 
	 * - if true is returned the plugin will be unloaded
	 * @see com.telelogic.rhapsody.core.RPUserPlugin#RhpPluginCleanup()
	 */
	public boolean RhpPluginCleanup() {
		// slog_.info("Plugin cleanup:"+ this.getClass().toString());
		//cleanup
		RPLog.Finalize();
		rpApplication_ = null;
		//return true so the plug-in will be unloaded now (on project close)
		return true;
	}

	/* 
	 * called when Rhapsody exits
	 * @see com.telelogic.rhapsody.core.RPUserPlugin#RhpPluginFinalCleanup()
	 */
	public void RhpPluginFinalCleanup() {

	}

}
