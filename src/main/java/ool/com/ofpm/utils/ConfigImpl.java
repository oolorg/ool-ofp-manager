/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.ofpm.utils;

import java.io.StringWriter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * 設定ファイルの読み込みを行うクラス。
 *
 * @author kurahashi
 * @version 0.1
 */
public class ConfigImpl implements Config {

    private static final Logger logger = Logger.getLogger(ConfigImpl.class);

    private Configuration config = null;

    /**
     * 設定ファイルを読み込む。
     *
     * @throws RuntimeException
     *             設定ファイルの読み込みに失敗(RuntimeExceptionで返す)
     */
    public ConfigImpl() {
        this(Definition.DEFAULT_PROPERTIY_FILE);
    }

    /**
     * 設定ファイルを指定してインスタンスを生成する
     *
     * @param config
     *            設定ファイル
     */
    public ConfigImpl(String config) {
        try {
            this.config = new PropertiesConfiguration(config);
        } catch (ConfigurationException e) {
            String message = "failed to read config file.";
            logger.error(message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * 設定オブジェクトを指定してインスタンスを生成する
     *
     * @param config
     *            設定ファイル
     */
    public ConfigImpl(Configuration config) {
        this.config = config;
    }

    @Override
    public String getString(String key) {
        String value = getConfiguration().getString(key);
        return value;
    }

    @Override
    public String getString(String key, String defaultValue) {
        try {
            String value = getConfiguration().getString(key, defaultValue);
            return value;
        } catch (ConversionException e) {
            return defaultValue;
        }
    }

    @Override
    public int getInt(String key) {
        return getConfiguration().getInt(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        try {
            int value = getConfiguration().getInt(key, defaultValue);
            return value;
        } catch (ConversionException e) {
            return defaultValue;
        }
    }

    @Override
    public String getContents() {
        PropertiesConfiguration prop = new PropertiesConfiguration();
        prop.append(this.config);
        StringWriter sw = new StringWriter();
        try {
            prop.save(sw);
        } catch (ConfigurationException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    /**
     * 設定ファイルへの参照を提供するConfigurationオブジェクトを返す。
     *
     * @return 設定ファイルへの参照を提供するConfigurationオブジェクト
     */
    @Override
    public Configuration getConfiguration() {
        return this.config;
    }
}
