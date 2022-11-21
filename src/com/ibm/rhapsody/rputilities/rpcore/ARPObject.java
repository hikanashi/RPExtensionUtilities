package com.ibm.rhapsody.rputilities.rpcore;

import java.util.List;

import com.telelogic.rhapsody.core.IRPCollection;
import com.telelogic.rhapsody.core.IRPModelElement;

@SuppressWarnings("unchecked")
abstract public class ARPObject {
    /**
     * Objectを指定の型に変換する
     * @param <T> 指定のデータ型
     * @param obj 変換対象オブジェクト
     * @return 指定の型に変換されたオブジェクト(変換失敗時はnull)
     */
    public static <T> T getObject(Object obj) {
        try {
            return (T)obj;
        } catch(Exception e) {
            RPLog.logException("getObject Cast Error", e);
            return null;            
        }
    }

    public static <T> List<T> toList(IRPCollection collection) {
        try {
            return collection.toList();
        } catch(Exception e) {
            RPLog.logException("toList Cast Error", e);
            return null;            
        }
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

}
