package com.ibm.rhapsody.rputilities;

import com.telelogic.rhapsody.core.*;
import java.lang.reflect.*;

@SuppressWarnings("unchecked")
abstract class IRPUtilityCommmand {
    
    protected IRPModelElement   m_element = null;


    /**
     * Rhapsodyユーティリティコマンドクラスのコンストラクタ
     * @param element 右クリック時に選択された要素
     */
    public IRPUtilityCommmand(IRPModelElement element) {
        m_element = element;
    }


    
    /**
     * 右クリック時に実行されるコマンド
     * @param argment 右クリック時に選択されたメニュー(hepファイルに記載されたnameをデリミタで分割した配列)
     * @return コマンド実行結果(true:成功 false:失敗)
     */
    abstract boolean command(String[] argment);
 
    /**
     * 右クリック時に選択された要素を取得する
     * @param <T> キャストしたい型
     * @return 右クリック時に選択された要素(要素を複数選択時はnull)
     */
    public <T> T getElement() {
        try {
            return (T)m_element;
        } catch(Exception e) {
            RPLog.logException("getElement Cast Error", e);
            return null;            
        }
    }

    /**
     * Objectを指定の型に変換する
     * @param <T> 指定のデータ型
     * @param obj 変換対象オブジェクト
     * @return 指定の型に変換されたオブジェクト(変換失敗時はnull)
     */
    public <T> T getObject(Object obj) {
        try {
            return (T)obj;
        } catch(Exception e) {
            RPLog.logException("getObject Cast Error", e);
            return null;            
        }
    }



    /**
     * 選択されたメニューからコマンドを実行する。
     * 選択メニュー名を/で分割したものをコマンドとする。
     * @param utilCommand 選択されたメニュー名
     * @param element 右クリック時に選択された要素(複数選択時はnull)
     */
    public static void RunCommand(String utilCommand, IRPModelElement element)
    {
        String[] commandargs = utilCommand.split("/");

        setLogLevel(commandargs);
        
        boolean result = invokeCommand(commandargs, element);
        if(result != true) 
        {
            RPLog.Info("CommandError Menu:" + utilCommand);     
        }

    }


    /**
     * コマンド実行時のログ出力レベルを設定する。<br>
     * コマンドの中に、下記が含まれる場合、ログ出力のレベルを変更する。<br>
     *   - LogDebug：デバッグレベルのログを出力する<br>
     *   - LogDetail：デバッグ、詳細レベルのログを出力する<br>
     * @param commandargs
     */
    public static void setLogLevel(String[] commandargs)
    {
        for(int index=0; index < commandargs.length; index++)
        {
            if(commandargs[index].equals("LogDebug"))
            {
                RPLog.Info("Set RPLog Debug mode");
                RPLog.setDebug(true);
            }
            else if(commandargs[index].equals("LogDetail"))
            {
                RPLog.Info("Set RPLog Detail mode");
                RPLog.setDetail(true);                
            }
        }
    }


    /**
     * コマンドを実行する。
     * コマンドの先頭を実行クラス名としてインスタンスを生成し、生成したインスタンスのcommandメソッドを実行する。
     * commandメソッドには、コマンドの先頭を含む全てのコマンドを指定して実行することで、
     * commandメソッド内で動作を切り替える事をを可能とする。
     * @param commandargs コマンド配列
     * @param element 右クリック時に選択された要素（複数選択はnull)
     * @return コマンド実行結果(true:成功 false:失敗)
     */
    public static boolean invokeCommand(String[] commandargs, IRPModelElement element)
    {
        if( commandargs.length < 1 )
        {
            RPLog.Info("name is invaild. Please check .hep file");
            return false;
        }

        String className =  "com.ibm.rhapsody.rputilities." + commandargs[0];
        RPLog.Debug("className:"+ className + " commandargs:"+ commandargs.length);
        
        try {
            Class<?> commandClass = Class.forName(className);
            Constructor<?> constructor = commandClass.getDeclaredConstructor(IRPModelElement.class);
            IRPUtilityCommmand rpcommnad = (IRPUtilityCommmand) constructor.newInstance(element);
            RPLog.Debug("Create Command:"+ className);

            if(rpcommnad == null)
            {
                RPLog.Info("className:"+ className + " is newInstance fail");
                return false;
            }

            return rpcommnad.command(commandargs);
        } 
        catch(Exception e) 
        {
            RPLog.logException("CommandError:"+ className, e);
            return false;       
        }
    }

}
