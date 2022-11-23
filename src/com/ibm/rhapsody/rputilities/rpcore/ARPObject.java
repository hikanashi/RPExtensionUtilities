package com.ibm.rhapsody.rputilities.rpcore;

import java.util.List;

import com.telelogic.rhapsody.core.IRPClass;
import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPProject;

public abstract class ARPObject {
	protected RPLog log_ = null;

    protected ARPObject(Class<?> clazz) {
        log_ = new RPLog(clazz);
    }

    /**
     * Objectを指定の型に変換する
     * @param <T> 指定のデータ型
     * @param obj 変換対象オブジェクト
     * @return 指定の型に変換されたオブジェクト(変換失敗時はnull)
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(Object obj) {
        try {
            return (T)obj;
        } catch(Exception e) {
            error("getObject Cast Error", e);
            return null;            
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> toList(IRPCollection collection) {
        try {
            return collection.toList();
        } catch(Exception e) {
            error("toList Cast Error", e);
            return null;            
        }
    }

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public void error(String message)
	{
		log_.error(message);
	}

	/**
	 * Log an error message 
	 * @param message, readable info message
	 */
	public void error(String message, Throwable exception)
	{
		log_.error( message, exception);
	}


	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public void warn(String message)
	{
		log_.warn( message);
	}

	/**
	 * Log an warning message 
	 * @param message, readable info message
	 */
	public void warn(String message, Throwable exception)
	{
		log_.warn( message, exception);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public void info(String message)
	{
		log_.info( message);
	}

    /**
	 * Log an information message 
	 * @param message, readable info message
	 */
	public void info(String message, Throwable exception)
	{
		log_.info( message, exception);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void debug(String message)
	{
		log_.debug( message);
	}

    /**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void debug(String message, Throwable exception)
	{
		log_.debug( message, exception);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void trace(String message)
	{
        log_.trace( message);
	}

	/**
	 * Log an Debug message 
	 * @param message, readable debug message
	 */
	public void trace(String message, Throwable exception)
	{
		log_.trace( message, exception);
	}

    protected static String getPackageName(IRPModelElement element, String delimiter)
    {
        String packageName = "";

        for(IRPModelElement checkelement = element;
			checkelement != null;
			checkelement = checkelement.getOwner())
        {
            if( checkelement instanceof IRPProject) {
				break;
			}

            if( !(checkelement instanceof IRPPackage) ) {
				continue;
			}

            if(packageName.length() > 0)
            {
                packageName = checkelement.getDisplayName() + delimiter + packageName;
            }
            else
            {
                packageName = checkelement.getDisplayName();
            }
        }

        return packageName;
    }

	protected String getPathToProject(IRPModelElement element, String delimiter)
    {
        String elementPath = "";

        for(IRPModelElement checkelement = element;
			checkelement != null;
			checkelement = checkelement.getOwner())
        {
			if( checkelement instanceof IRPClass) {
				if(checkelement.getName().equals("TopLevel"))
				{
					continue;
				}
            }

            if( checkelement instanceof IRPProject) {
                break;
            }

			trace("element:"+ checkelement.getDisplayName()
				+ " MetaClass:" + checkelement.getMetaClass()
				+ " ClassName:" + checkelement.getClass().getName());

            if(checkelement != element)
            {
                elementPath = checkelement.getDisplayName() + delimiter + elementPath;
            }
            else
            {
                elementPath = checkelement.getDisplayName();
            }
        }

        return elementPath;
    } 

}
