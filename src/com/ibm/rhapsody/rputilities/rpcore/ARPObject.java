package com.ibm.rhapsody.rputilities.rpcore;

import java.util.List;

import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;

public abstract class ARPObject {
    protected Class<?> m_clazz = ARPObject.class;


    protected ARPObject(Class<?> clazz) {
        m_clazz = clazz;
    }

    /**
     * Objectを指定の型に変換する
     * @param <T> 指定のデータ型
     * @param obj 変換対象オブジェクト
     * @return 指定の型に変換されたオブジェクト(変換失敗時はnull)
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(Object obj) {
        try {
            return (T)obj;
        } catch(Exception e) {
            RPLog.error(null,"getObject Cast Error", e);
            return null;            
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(IRPCollection collection) {
        try {
            return collection.toList();
        } catch(Exception e) {
            RPLog.error(null,"toList Cast Error", e);
            return null;            
        }
    }

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public void error(String message)
	{
		RPLog.error(m_clazz,message);
	}

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public void error(String message, Throwable exception)
	{
		RPLog.error(m_clazz, message, exception);
	}

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public static void serror(String message)
	{
		RPLog.error(null,message);
	}

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public static void serror(String message, Throwable exception)
	{
		RPLog.error(null, message, exception);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public void warn(String message)
	{
		RPLog.warn(m_clazz, message);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public void warn(String message, Throwable exception)
	{
		RPLog.warn(m_clazz, message, exception);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public static void swarn(String message)
	{
		RPLog.warn(null, message);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public static void swarn(String message, Throwable exception)
	{
		RPLog.warn(null, message, exception);
	}
    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public void info(String message)
	{
		RPLog.info(m_clazz, message);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public void info(String message, Throwable exception)
	{
		RPLog.info(m_clazz, message, exception);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public static void sinfo(String message)
	{
		RPLog.info(null, message);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public static void sinfo(String message, Throwable exception)
	{
		RPLog.info(null, message, exception);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void debug(String message)
	{
		RPLog.debug(m_clazz, message);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void debug(String message, Throwable exception)
	{
		RPLog.debug(m_clazz, message, exception);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void sdebug(String message)
	{
		RPLog.debug(null, message);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void sdebug(String message, Throwable exception)
	{
		RPLog.debug(null, message, exception);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void trace(String message)
	{
        RPLog.trace(m_clazz, message);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void trace(String message, Throwable exception)
	{
		RPLog.trace(m_clazz, message, exception);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void strace(String message)
	{
        RPLog.trace(null, message);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public static void strace(String message, Throwable exception)
	{
		RPLog.trace(null, message, exception);
	}

    protected static String getPackageName(IRPModelElement element)
    {
        String packageName = "-";
        IRPModelElement checkelement = element;

        while(checkelement != null)
        {
            if( checkelement.getIsOfMetaClass("Package") == 1 )
            {
                packageName = checkelement.getDisplayName();
                break;
            }
            
            checkelement = checkelement.getOwner();
        }

        return packageName;
    }

	protected static String getPathToProject(IRPModelElement element, String delimiter)
    {
        String elementPath = "";
        IRPModelElement checkelement = element;

        while(checkelement != null)
        {
            if( checkelement.getIsOfMetaClass("Project") == 1 )
            {
                break;
            }

            if(checkelement != element)
            {
                elementPath = checkelement.getDisplayName() + delimiter + elementPath;
            }
            else
            {
                elementPath = checkelement.getDisplayName();
            }

            checkelement = checkelement.getOwner();
        }

        return elementPath;
    } 

}
