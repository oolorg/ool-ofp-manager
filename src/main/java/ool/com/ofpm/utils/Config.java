/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.ofpm.utils;

import org.apache.commons.configuration.Configuration;

/**
 * 設定ファイルの情報を取得するためのインタフェース
 * 
 * @author kurahashi
 * @version 0.1
 * 
 */
public interface Config {
	/**
     * 設定項目に対応する値を取得する
     * 
     * @param key
     *            設定項目名
     * @return 文字列型の値
     */
    String getString(String key);

    /**
     * 設定項目に対応する値を取得する
     * 
     * @param key
     *            設定項目名
     * @param defaultValue
     *            デフォルト値
     * @return 文字列型の値
     */
    String getString(String key, String defaultValue);

    /**
     * 設定項目に対応する値を取得する
     * 
     * @param key
     *            設定項目名
     * @return intの値
     */
    int getInt(String key);

    /**
     * 設定項目に対応する値を取得する
     * 
     * @param key
     *            設定項目名
     * @param defaultValue
     *            デフォルト値
     * @return intの値
     */
    int getInt(String key, int defaultValue);

    /**
     * 設定ファイルの内容を文字列で返す
     * 
     * @return 設定ファイルの内容を示す文字列
     */
    String getContents();

    /**
     * 設定ファイルのオブジェクトを返す
     * 
     * @return 設定ファイルオブジェクト
     */
    Configuration getConfiguration();
}
