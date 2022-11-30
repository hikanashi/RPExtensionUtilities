package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeFunction;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeTypedef;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPPackage;

public class RPFunctionImporter extends ARPObject {

    public RPFunctionImporter() {
        super(RPFunctionImporter.class);
    }

    public boolean importAPI(IRPPackage rootPackage, DoxygenTypeFunction function, String currentVersion) {

        if(rootPackage == null) {
            error("Package: is null");
            return false;
        }

        if(function == null) {
            error("Package:"+ rootPackage.getDisplayName() + " function is null");
            return false;
        }

        debug("Function is API:" + function.getType() + " " + function.getName());
        RPStateChartBridge functionBridge = new RPStateChartBridge(function,rootPackage);
        functionBridge.importElement(currentVersion);

        return true;
    }

    public boolean importTypedef(IRPPackage rootPackage, DoxygenTypeTypedef typedef, String currentVersion) {

        if(rootPackage == null) {
            error("Package: is null");
            return false;
        }

        if(typedef == null) {
            error("Package:"+ rootPackage.getDisplayName() + " typedef is null");
            return false;
        }

        // If it contains "(", it is a callback.
        if(typedef.isCallback()) {
            debug("typedef is Callback:" + typedef.getType()+ " " + typedef.getName());
            RPEventBridge eventBridge = new RPEventBridge(typedef,rootPackage);
            eventBridge.importElement(currentVersion);
        }
        else {
            warn("typedef is unkown:" + typedef.getType() + " " + typedef.getName());
        }

        return true;
    }
}


// 指定されたパッケージ以下のイベントかアクティビティ図を取得する
// （SysActがじゃま）
//　今のバージョンのパッケージ以外にchangedもdeleteもついていないアイテムがあればそれがdeleteとする


// イベント
    // 戻り値
    // 引数（タイプは別）
// アクティビティ図
    // ピン　タイプは別登録
    // 戻り値はRETURN名前固定
// タイプ
    // ENUM
    // Typedef
    // Langage
    // Struct
    // Union

// defineはなに？LANGUAGE？作らない？LANGUAGEぐらいしかできない 


// Serarch Item from Base Package
// アイテムがある
// バージョンを確認する

// 今のバージョンと一致
// 変更箇所を上書きする
    // アイテムが一致するか見る
        // 完全一致する
            // １最新バージョンに移動
        // 不一致
            // 

// 今のバージョンよりも新しい（バージョンアップ）
// 今の箇所はchangedに名称変更して、新しく作る

// 今のバージョンよりも古い（バージョンダウン）
// changedを付けたバージョンで検索
    // なければchangedで作る
    // あれば内容を反映


// アイテムがない

// アイテムのモジュールパスを決定する（バージョンパッケージ以下のサブパッケージの構成を決める）

// まずは対象バージョンのパッケージがあるか見る
//  サブパッケージがなければ作る（アイテムを入れるパスまで作る）
//　アイテムを移動なら該当パッケージ以下にアイテムを移動
    // 説明など影響がないところも反映
// アイテム作成なら新規に作る
// タグに適用バージョンを入れておく。（複数定義可能なやつか）
