package com.ibm.rhapsody.rputilities.rpcore;

import com.telelogic.rhapsody.core.IRPApplication;

public class RPLog {

	protected IRPApplication m_rhpApplication = null;
    protected String m_Title = "unknown";
	protected RPLogLevel m_Level = RPLogLevel.INFO;

	protected static RPLog m_Object = null;


	public static void Initialize(String title, IRPApplication rpyApplication)
    {
		m_Object = new RPLog(title,rpyApplication); 
	}

	public static void Finalize()
    {
		m_Object = null; 
	}
	
	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public static void error(Class<?> clazz,String message)
	{
		sloginternal(clazz,RPLogLevel.ERROR, message);
	}

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public static void error(Class<?> clazz,String message, Throwable exception)
	{
		sloginternal(clazz, RPLogLevel.ERROR, message, exception);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public static void warn(Class<?> clazz,String message)
	{
		sloginternal(clazz, RPLogLevel.WARN, message);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public static void warn(Class<?> clazz,String message, Throwable exception)
	{
		sloginternal(clazz, RPLogLevel.WARN, message, exception);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public static void info(Class<?> clazz,String message)
	{
		sloginternal(clazz, RPLogLevel.INFO, message);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public static void info(Class<?> clazz, String message, Throwable exception)
	{
		sloginternal(clazz, RPLogLevel.INFO, message, exception);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void debug(Class<?> clazz,String message)
	{
		sloginternal(clazz, RPLogLevel.DEBUG, message);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void debug(Class<?> clazz,String message, Throwable exception)
	{
		sloginternal(clazz, RPLogLevel.DEBUG, message, exception);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void trace(Class<?> clazz,String message)
	{
        sloginternal(clazz, RPLogLevel.TRACE, message);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void trace(Class<?> clazz,String message, Throwable exception)
	{
		sloginternal(clazz, RPLogLevel.TRACE, message, exception);
	}

	/**
	 * set Log Debug mode 
	 */
	public static void setLevel(RPLogLevel level)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.loginternal(RPLog.class, RPLogLevel.INFO, 
			"SET LOGLEVEL:" + level.toString());

		m_Object.m_Level = level;

	}

	protected static void sloginternal(Class<?> clazz,RPLogLevel level,String message)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.loginternal(clazz,level,message);
	}

	protected static void sloginternal(Class<?> clazz,RPLogLevel level,String message, Throwable exception)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.loginternal(clazz,level,message,exception);
	}

	protected RPLog(String title, IRPApplication rpyApplication)
    {

        this.m_Title = title;
        this.m_rhpApplication = rpyApplication;
	}

	private void loginternal(Class<?> clazz, RPLogLevel level,String message, Throwable exception) 
	{
		loginternal(clazz, level, message);
        if( exception == null )
        {
            return;        
        }

		loginternal(clazz, level, exception.toString());

		StackTraceElement[] stacktrace = exception.getStackTrace();
		for(int idx = 0; idx < stacktrace.length; idx++ )
		{
			StackTraceElement stack = stacktrace[idx];
			loginternal(clazz, level, "\t at " + stack.toString());
		}
	}

    private void loginternal(Class<?> clazz, RPLogLevel level, String message) 
	{
		if(m_Level.toInt() > level.toInt() )
		{
			return;
		}

		String className = "";
		if(clazz != null) {
			className = clazz.getSimpleName() + "\t";
		}
		else {
			className = "<static>\t";
		}

        this.m_rhpApplication.writeToOutputWindow(
			this.m_Title,
			level.toString() + ","
			+ className + ","
			+ message + "\n");
	}
}
