/**
 * Simple Plug-in sample
 * (c) Copyright IBM 2008
 */
package com.ibm.rhapsody.rputilities;

import com.ibm.rhapsody.rputilities.rpcommand.RPCommandRunner;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.telelogic.rhapsody.core.*;

//@SuppressWarnings("unchecked")
public class RPExtensionUtilities extends RPUserPlugin {
	protected static RPLog slog_ = new RPLog(RPExtensionUtilities.class);


	protected IRPApplication m_rhpApplication = null;

	// called when the plug-in is loaded
	public void RhpPluginInit(final IRPApplication rpyApplication) {
		// keep the application interface for later use
		m_rhpApplication = rpyApplication;
		RPLog.Initialize("RPUtilities", rpyApplication);
		slog_.info("Plugin Load:"+ this.getClass().toString());
	}

	// called when the plug-in menu item under the "Tools" menu is selected	
	public void RhpPluginInvokeItem() {
		slog_.debug("Tools command");
	}

	// called when the plug-in pop-up menu (if applicable) is selected
	public void OnMenuItemSelect(String menuItem) {
		//show the selected element name
		IRPModelElement element = m_rhpApplication.getSelectedElement();
		RPCommandRunner.RunCommand(menuItem, element);
	}

	// called when the plug-in popup trigger (if applicable) fired
	public void OnTrigger(String trigger) {
		//show the trigger string
		//JOptionPane.showMessageDialog(null, "Hello world from SimplePlugin.OnTrigger " + trigger);
		slog_.debug("OnTrigger " + trigger);
	}

	// called when the project is closed - if true is returned the plugin will
	// be unloaded
	public boolean RhpPluginCleanup() {
		slog_.info("Plugin cleanup:"+ this.getClass().toString());
		//cleanup
		RPLog.Finalize();
		m_rhpApplication = null;
		//return true so the plug-in will be unloaded now (on project close)
		return true;
	}

	// called when Rhapsody exits
	public void RhpPluginFinalCleanup() {

	}

}
