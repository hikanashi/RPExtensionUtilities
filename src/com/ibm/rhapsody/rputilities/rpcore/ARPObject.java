package com.ibm.rhapsody.rputilities.rpcore;

import java.util.List;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

public abstract class ARPObject {
	/**
	 * log output interface
	 */
	protected RPLog log_ = null;
	/**
	 * Class information of log output source
	 */
	protected Class<?> clazz_ = null;

	/**
	 * Common constructor of Rhapsody processing class
	 * Initialize log output interface
	 * 
	 * @param clazz Class information of log output source
	 */
	protected ARPObject(Class<?> clazz) {
		clazz_ = clazz;
		log_ = new RPLog(clazz);
	}

	/**
	 * Convert an Object to a specified type
	 * 
	 * @param <T> Specified data type
	 * @param obj object being converted
	 * @return Object converted to specified type (null if conversion fails)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getObject(Object obj) {
		try {
			return (T) obj;
		} catch (Exception e) {
			error("getObject Cast Error", e);
			return null;
		}
	}

	/**
	 * Convert a collection to a list of a specified type
	 * 
	 * @param <T>        Specified data type
	 * @param collection Collection to be converted
	 * @return List converted to specified type
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> toList(IRPCollection collection) {
		try {
			return collection.toList();
		} catch (Exception e) {
			error("toList Cast Error", e);
			return null;
		}
	}

	/**
	 * Get the package to which the element belongs
	 * 
	 * @param element Target Element
	 * @return Package Object
	 */
	protected IRPPackage getPackage(IRPModelElement element) {
		for (IRPModelElement checkelement = element;
			checkelement != null;
			checkelement = checkelement.getOwner()) {
			
			if (checkelement instanceof IRPPackage) {
				IRPPackage rpPackage = getObject(checkelement);
				return rpPackage;
			}
		}

		return null;
	}

	/**
	 * Obtains the name of the package to which the element belongs.
	 * If the packages belonging to the element are nested, the package names under
	 * the project are combined with delimiter.
	 * 
	 * @param element   Target Element
	 * @param delimiter delimiter when concatenating package names
	 * @return Package Name
	 */
	protected static String getPackagePath(IRPModelElement element, String delimiter) {
		String packageName = "";

		for (IRPModelElement checkelement = element;
			checkelement != null;
			checkelement = checkelement.getOwner()) {
			
			if (checkelement instanceof IRPProject) {
				break;
			}

			if (!(checkelement instanceof IRPPackage)) {
				continue;
			}

			if (packageName.length() > 0) {
				packageName = checkelement.getDisplayName() + delimiter + packageName;
			} else {
				packageName = checkelement.getDisplayName();
			}
		}

		return packageName;
	}

	/**
	 * Get the path from the project to the target element.
	 * Excluding TopLevel classes.
	 * 
	 * @param element   Target Element
	 * @param delimiter delimiter when concatenating element names
	 * @return path name
	 */
	protected String getPathToProject(IRPModelElement element, String delimiter) {
		String elementPath = "";

		for (IRPModelElement checkelement = element;
			checkelement != null;
			checkelement = checkelement.getOwner()) {

			// Exclude "TopLevel" classes that are automatically generated in between
			if (checkelement instanceof IRPClass) {
				if (checkelement.getName().equals("TopLevel")) {
					continue;
				}
			}

			// Paths under the project
			if (checkelement instanceof IRPProject) {
				break;
			}

			trace("element:" + checkelement.getDisplayName()
					+ " MetaClass:" + checkelement.getMetaClass()
					+ " ClassName:" + checkelement.getClass().getName());

			if (checkelement != element) {
				elementPath = checkelement.getDisplayName() + delimiter + elementPath;
			} else {
				elementPath = checkelement.getDisplayName();
			}
		}

		return elementPath;
	}

	/**
	 * Log an error message
	 * 
	 * @param message error message to output
	 */
	public void error(String message) {
		log_.error(message);
	}

	/**
	 * Log an error message
	 * 
	 * @param message   error message to output
	 * @param exception Occurred exception
	 */
	public void error(String message, Throwable exception) {
		log_.error(message, exception);
	}

	/**
	 * Log a warning message
	 * 
	 * @param message warning message to output
	 */
	public void warn(String message) {
		log_.warn(message);
	}

	/**
	 * Log a warning message
	 * 
	 * @param message   warning message to output
	 * @param exception Occurred exception
	 */
	public void warn(String message, Throwable exception) {
		log_.warn(message, exception);
	}

	/**
	 * Log a infomation message
	 * 
	 * @param message infomation message to output
	 */
	public void info(String message) {
		log_.info(message);
	}

	/**
	 * Log a infomation message
	 * 
	 * @param message   infomation message to output
	 * @param exception Occurred exception
	 */
	public void info(String message, Throwable exception) {
		log_.info(message, exception);
	}

	/**
	 * Log a debug message
	 * 
	 * @param message debug message to output
	 */
	public void debug(String message) {
		log_.debug(message);
	}

	/**
	 * Log a debug message
	 * 
	 * @param message   debug message to output
	 * @param exception Occurred exception
	 */
	public void debug(String message, Throwable exception) {
		log_.debug(message, exception);
	}

	/**
	 * Log a detail debug message
	 * 
	 * @param message detail debug message to output
	 */
	public void trace(String message) {
		log_.trace(message);
	}

	/**
	 * Log a detail debug message
	 * 
	 * @param message   detail debug message to output
	 * @param exception Occurred exception
	 */
	public void trace(String message, Throwable exception) {
		log_.trace(message, exception);
	}

	/**
	 * Log out memory usage
	 * 
	 * @param title Title indicating log output timing
	 */
	public void debugMemory(String title) {
		// System.gc();
		Runtime runtime = Runtime.getRuntime();
		info(String.format("%s total:%d free:%d use:%d",
				title,
				runtime.totalMemory(),
				runtime.freeMemory(),
				runtime.totalMemory() - runtime.freeMemory()));
	}
}
