package com.ibm.rhapsody.rputilities.rpcommand;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPModelElement;
import java.lang.reflect.Constructor;

public class RPCommandRunner extends ARPObject {
	protected static RPLog slog_ = new RPLog(RPCommandRunner.class);
    public RPCommandRunner() 
    {
        super(RPCommandRunner.class);
    }

    /**
     * 選択されたメニューからコマンドを実行する。
     * 選択メニュー名を/で分割したものをコマンドとする。
     * @param utilCommand 選択されたメニュー名
     * @param element 右クリック時に選択された要素(複数選択時はnull)
     */
    public static void RunCommand(String utilCommand, IRPModelElement element)
    {

		String selElemName = new String();
		if(element != null){
			selElemName = element.getName();			
		} else {
			selElemName = "No selected element";
		}

		slog_.debug("OnMenuItemSelect " + utilCommand + " Selected element name is: " + selElemName);

        try {
            String[] commandargs = utilCommand.split("\\\\");
            
            setLogLevel(commandargs);
        
            boolean result = invokeCommand(commandargs, element);
            if(result != true) 
            {
                slog_.error("CommandError Menu:" + utilCommand + " Select Item:" + selElemName);     
            }
    
        } catch(Exception e) {
            slog_.error("CommandError Menu:" + utilCommand + " Select Item:" + selElemName, e);       
        }

    }

    /**
     * コマンド実行時のログ出力レベルを設定する。<br>
     * コマンドの中に、下記が含まれる場合、ログ出力のレベルを変更する。<br>
     *   - LogDebug：デバッグレベルのログを出力する<br>
     *   - LogTrace：トレースレベルのログを出力する<br>
     * @param commandargs
     */
    public static void setLogLevel(String[] commandargs)
    {
        for(int index=0; index < commandargs.length; index++)
        {
            if(commandargs[index].equals("LogDebug"))
            {
                RPLog.setLevel(RPLogLevel.DEBUG);
            }
            else if(commandargs[index].equals("LogTrace"))
            {
                RPLog.setLevel(RPLogLevel.TRACE);           
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
            slog_.error("name is invaild. Please check .hep file");
            return false;
        }

        String className =  IRPUtilityCommmand.class.getPackage().getName() + "." + commandargs[0];
        slog_.debug("className:"+ className + " commandargs:"+ commandargs.length);
        
        try {
            Class<?> commandClass = Class.forName(className);
            Constructor<?> constructor = commandClass.getDeclaredConstructor(IRPModelElement.class);
            IRPUtilityCommmand rpcommnad = (IRPUtilityCommmand) constructor.newInstance(element);
            slog_.debug("Create Command:"+ className);

            if(rpcommnad == null)
            {
                slog_.error("className:"+ className + " is newInstance fail");
                return false;
            }

            return rpcommnad.command(commandargs);
        } 
        catch(Exception e) 
        {
            slog_.error("CommandError:"+ className, e);
            return false;       
        }
    }
}
