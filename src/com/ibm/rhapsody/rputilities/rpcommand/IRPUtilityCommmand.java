package com.ibm.rhapsody.rputilities.rpcommand;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;

abstract class IRPUtilityCommmand extends ARPObject {
    
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
    public <T> T getElement() 
    {
        return getObject(m_element);
    }
}
