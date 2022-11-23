package com.ibm.rhapsody.rputilities.rpcore;

import com.telelogic.rhapsody.core.IRPApplication;

public class RPLog {

	protected static IRPApplication rhpApplication_ = null;
    protected static String title_ = "unknown";
	protected static RPLogLevel level_ = RPLogLevel.INFO;

	protected Class<?> clazz_ = null;


	synchronized public static void Initialize(String title, IRPApplication rpyApplication)
    {
		rhpApplication_ = rpyApplication;
		title_ = title;
	}

	synchronized public static void Finalize()
    {
		rhpApplication_ = null;
		title_ = null;
	}

	/**
	 * set Log level 
	 */
	synchronized public static void setLevel(RPLogLevel level)
	{
		RPLog log = new RPLog(RPLog.class);
		log.info("SET LOGLEVEL:" + level.toString());
		level_ = level;
	}

	public RPLog(Class<?> clazz) {
        clazz_ = clazz;
    }

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public void error(String message)
	{
		loginternal(RPLogLevel.ERROR, message);
	}

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public void error(String message, Throwable exception)
	{
		loginternal( RPLogLevel.ERROR, message, exception);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public void warn(String message)
	{
		loginternal( RPLogLevel.WARN, message);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public void warn(String message, Throwable exception)
	{
		loginternal( RPLogLevel.WARN, message, exception);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public void info(String message)
	{
		loginternal( RPLogLevel.INFO, message);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public void info( String message, Throwable exception)
	{
		loginternal( RPLogLevel.INFO, message, exception);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void debug(String message)
	{
		loginternal( RPLogLevel.DEBUG, message);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void debug(String message, Throwable exception)
	{
		loginternal( RPLogLevel.DEBUG, message, exception);
	}

	/**
	 * Log an Trace message 
	 * @param message, readable debug message
	 */
	public void trace(String message)
	{
        loginternal( RPLogLevel.TRACE, message);
	}

	/**
	 * Log an Trace message 
	 * @param message, readable debug message
	 */
	public void trace(String message, Throwable exception)
	{
		loginternal( RPLogLevel.TRACE, message, exception);
	}


	private void loginternal( RPLogLevel level,String message, Throwable exception) 
	{
		loginternal(level, message);
        if( exception == null )
        {
            return;        
        }

		loginternal( level, exception.toString());

		StackTraceElement[] stacktrace = exception.getStackTrace();
		for(int idx = 0; idx < stacktrace.length; idx++ )
		{
			StackTraceElement stack = stacktrace[idx];
			loginternal( level, "\t at " + stack.toString());
		}
	}

    synchronized private void loginternal(RPLogLevel level, String message) 
	{
		if(level_.toInt() > level.toInt() )
		{
			return;
		}

		String className = "";
		if(clazz_ != null) {
			className = clazz_.getSimpleName() + "\t";
		}
		else {
			className = "<unknown>\t";
		}

        rhpApplication_.writeToOutputWindow(
			title_,
			level.toString() + ","
			+ className + ","
			+ message + "\n");
	}
}
