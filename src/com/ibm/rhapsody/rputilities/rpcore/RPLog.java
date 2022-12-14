package com.ibm.rhapsody.rputilities.rpcore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.telelogic.rhapsody.core.IRPApplication;

public class RPLog {
	protected static IRPApplication rhpApplication_ = null;
	protected static String title_ = "unknown";
	protected static RPLogLevel level_ = RPLogLevel.INFO;

	protected Class<?> clazz_ = null;
	protected Logger logger_ = null;

	/**
	 * @param title
	 * @param rpyApplication
	 */
	synchronized public static void Initialize(String title, IRPApplication rpyApplication) {
		rhpApplication_ = rpyApplication;
		title_ = title;
	}

	/**
	 * 
	 */
	synchronized public static void Finalize() {
		rhpApplication_ = null;
		title_ = null;
	}

	synchronized public static IRPApplication getApplication() {
		return rhpApplication_;
	}

	/**
	 * @param level
	 */
	synchronized public static void setLevel(RPLogLevel level) {
		RPLog log = new RPLog(RPLog.class);
		log.info("SET LOGLEVEL:" + level.toString());
		level_ = level;
	}

	/**
	 * @param clazz
	 */
	public RPLog(Class<?> clazz) {
		clazz_ = clazz;
		logger_ = LogManager.getLogger(clazz_);
	}

	/**
	 * Log an error message
	 * 
	 * @param message readable info message
	 */
	public void error(String message) {
		logger_.error(message);
		loginternal(RPLogLevel.ERROR, message);
	}

	/**
	 * Log an error message
	 * 
	 * @param message   readable info messageslog_
	 * @param exception Occurred exception
	 */
	public void error(String message, Throwable exception) {
		logger_.error(message,exception);
		loginternal(RPLogLevel.ERROR, message, exception);
	}

	/**
	 * Log an warning message
	 * 
	 * @param message readable info message
	 */
	public void warn(String message) {
		logger_.warn(message);
		loginternal(RPLogLevel.WARN, message);
	}

	/**
	 * Log an warning message
	 * 
	 * @param message   readable info message
	 * @param exception Occurred exception
	 */
	public void warn(String message, Throwable exception) {
		logger_.warn(message, exception);
		loginternal(RPLogLevel.WARN, message, exception);
	}

	/**
	 * Log an information message
	 * 
	 * @param message readable info message
	 */
	public void info(String message) {
		logger_.info(message);
		loginternal(RPLogLevel.INFO, message);
	}

	/**
	 * Log an information message
	 * 
	 * @param message   readable info message
	 * @param exception Occurred exception
	 */
	public void info(String message, Throwable exception) {
		logger_.info(message, exception);
		loginternal(RPLogLevel.INFO, message, exception);
	}

	/**
	 * Log an Debug message
	 * 
	 * @param message   readable debug message
	 * @param exception Occurred exception
	 */
	public void debug(String message) {
		logger_.debug(message);
		loginternal(RPLogLevel.DEBUG, message);
	}

	/**
	 * Log an Debug message
	 * 
	 * @param message   readable debug message
	 * @param exception Occurred exception
	 */
	public void debug(String message, Throwable exception) {
		logger_.debug(message, exception);
		loginternal(RPLogLevel.DEBUG, message, exception);
	}

	/**
	 * Log an Trace message
	 * 
	 * @param message   readable debug message
	 * @param exception Occurred exception
	 */
	public void trace(String message) {
		logger_.trace(message);
		loginternal(RPLogLevel.TRACE, message);
	}

	/**
	 * Log an Trace message
	 * 
	 * @param message   readable debug message
	 * @param exception Occurred exception
	 */
	public void trace(String message, Throwable exception) {
		logger_.trace(message, exception);
		loginternal(RPLogLevel.TRACE, message, exception);
	}

	/**
	 * @param level
	 * @param message
	 * @param exception
	 */
	private void loginternal(RPLogLevel level, String message, Throwable exception) {
		loginternal(level, message);
		logexception(level, exception);
	}

	/**
	 * @param level
	 * @param exception
	 */
	private void logexception(RPLogLevel level, Throwable exception) {
		if (exception == null) {
			return;
		}

		loginternal(level, "<Exception>" + exception.getClass().getName());
		if (exception.getMessage() != null) {
			loginternal(level, "<ExceptionMessage>" + exception.getMessage());
		}

		StackTraceElement[] stacktrace = exception.getStackTrace();
		for (int idx = 0; idx < stacktrace.length; idx++) {
			StackTraceElement stack = stacktrace[idx];
			loginternal(level, "\t at " + stack.toString());
		}

		logexception(level, exception.getCause());

	}

	/**
	 * @param level
	 * @param message
	 */
	synchronized private void loginternal(RPLogLevel level, String message) {
		if (level_.toInt() > level.toInt()) {
			return;
		}

		String className = "";
		if (clazz_ != null) {
			className = clazz_.getSimpleName() + "\t";
		} else {
			className = "<unknown>\t";
		}

		rhpApplication_.writeToOutputWindow(
				title_,
				level.toString() + ","
						+ className + ","
						+ message + "\n");
	}
}
