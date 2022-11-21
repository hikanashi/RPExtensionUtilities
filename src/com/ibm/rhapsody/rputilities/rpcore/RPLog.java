package com.ibm.rhapsody.rputilities.rpcore;

import com.telelogic.rhapsody.core.IRPApplication;

public class RPLog {

	protected IRPApplication m_rhpApplication = null;
    protected String m_Title = "unknown";
    protected boolean m_isDebug = false;
	protected boolean m_isDetail = false;

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
	 * log an exception
	 */
	public static void logException(String message, Throwable exception)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.log(message, exception);
	}
	
	/**
	 * Log an unexpected exception 
	 */
	public static void logException(Throwable exception)
	{
		logException("Unexpected Exception", exception);
	}
	
    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public static void Info(String message)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.log(message);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void Debug(String message)
	{
		if(m_Object == null)
		{
			return;
		}

        if(m_Object.m_isDebug)
		{
            m_Object.log("<DEBUG>"+ message);
        }
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void Detail(String message)
	{
		if(m_Object == null)
		{
			return;
		}

        if(m_Object.m_isDetail)
		{
            m_Object.log("<DETAIL>"+ message);
        }
	}

	/**
	 * set Log Debug mode 
	 */
	public static void setDebug(boolean mode)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.m_isDebug = mode;

	}

	/**
	 * set Log Detail mode 
	 */
	public static void setDetail(boolean mode)
	{
		if(m_Object == null)
		{
			return;
		}

		m_Object.m_isDetail = mode;
		if(m_Object.m_isDetail)
		{
			m_Object.m_isDebug = true;
		}
	}

	protected RPLog(String title, IRPApplication rpyApplication)
    {

        this.m_Title = title;
        this.m_rhpApplication = rpyApplication;
	}

	private void log(String message, Throwable exception) 
	{
		log(message);
        if( exception == null )
        {
            return;        
        }

		log(exception.toString());

		StackTraceElement[] stacktrace = exception.getStackTrace();
		for(int idx = 0; idx < stacktrace.length; idx++ )
		{
			StackTraceElement stack = stacktrace[idx];
			log("\t at " + stack.toString());
		}
	}

    private void log(String message) 
	{
        this.m_rhpApplication.writeToOutputWindow(
			this.m_Title,
			message + "\n");
	}
}
