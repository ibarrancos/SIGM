package com.ieci.tecdoc.isicres.desktopweb.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public class ValidationRBUtil {
    private static final String RB_NAME = "validation";
    private static Logger _logger = Logger.getLogger((Class)ValidationRBUtil.class);
    private ResourceBundle rb = null;
    private static Map resourceBundles = new HashMap(3);

    public ValidationRBUtil(Locale locale) {
        this.rb = ResourceBundle.getBundle("validation", locale);
    }

    public static synchronized ValidationRBUtil getInstance(Locale locale) {
        ValidationRBUtil rbUtil = null;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (resourceBundles.containsKey(locale)) {
            rbUtil = (ValidationRBUtil)resourceBundles.get(locale);
        } else {
            rbUtil = new ValidationRBUtil(locale);
            resourceBundles.put(locale, rbUtil);
        }
        return rbUtil;
    }

    public String getProperty(String key) {
        return this.getProperty(key, "@@" + key + "@@");
    }

    public String getProperty(String key, String defaultValue) {
        String result = defaultValue;
        try {
            result = this.rb.getString(key);
        }
        catch (MissingResourceException mrE) {
            // empty catch block
        }
        return result;
    }
}
