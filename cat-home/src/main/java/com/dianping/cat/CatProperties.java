package com.dianping.cat;

import java.io.*;
import java.util.Properties;

/**
 * Created by allen on 17/2/22.
 */
public class CatProperties {

    private static final String PROPERTIES_PATH = "/data/appdatas/cat/cat.properties";
    private static CatProperties instance = new CatProperties();
    private Properties property = null;


    private CatProperties() {
        InputStream in = null;
        InputStreamReader reader = null;
        try {
            in = new FileInputStream(new File(PROPERTIES_PATH));
            reader = new InputStreamReader(in, "UTF-8");
            property = new Properties();
            property.load(reader);
        } catch (IOException e) {
            Cat.logError(e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    Cat.logError(e);
                }
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Cat.logError(e);
                }
            }
        }
    }

    public static CatProperties getInstance() {
        return instance;
    }

    public String getProperty(String key) {
        return property.getProperty(key);
    }
}
