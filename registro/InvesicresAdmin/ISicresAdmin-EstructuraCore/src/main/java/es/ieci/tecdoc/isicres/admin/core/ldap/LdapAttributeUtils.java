package es.ieci.tecdoc.isicres.admin.core.ldap;

import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class LdapAttributeUtils {
    private static final Logger logger = Logger.getLogger((Class)LdapAttributeUtils.class);
    private static final String KEY_ACTIVEDIRECTORY_GUID = "AD_GUID";
    private static final String KEY_IPLANET_GUID = "IP_GUID";
    private static final String KEY_OPENLDAP_GUID = "OL_GUID";
    private static final String KEY_ACTIVEDIRECTORY_GUID_GROUP = "AD_GUID_GROUP";
    private static final String KEY_IPLANET_GUID_GROUP = "IP_GUID_GROUP";
    private static final String KEY_OPENLDAP_GUID_GROUP = "OL_GUID_GROUP";
    private static String fileName = "ldap.properties";
    private static Properties props = new Properties();

    public static String getLdapAttributeActiveDirectoryGUID() {
        String key = (String)props.get("AD_GUID");
        if (key == null) {
            key = "objectGUID";
            logger.error((Object)("Error obteniendo el guid para active directory del fichero: " + fileName));
        }
        return key;
    }

    public static String getLdapAttributeIplanetGUID() {
        String key = (String)props.get("IP_GUID");
        if (key == null) {
            key = "nsuniqueid";
            logger.error((Object)("Error obteniendo el guid para iplanet del fichero: " + fileName));
        }
        return key;
    }

    public static String getLdapAttributeOpenLdapGUID() {
        String key = (String)props.get("OL_GUID");
        if (key == null) {
            key = "uidnumber";
            logger.error((Object)("Error obteniendo el guid para open ldap del fichero: " + fileName));
        }
        return key;
    }

    public static String getLdapAttributeGroupActiveDirectoryGUID() {
        String key = (String)props.get("AD_GUID_GROUP");
        if (key == null) {
            key = "objectGUID";
            logger.error((Object)("Error obteniendo el guid para active directory del fichero: " + fileName));
        }
        return key;
    }

    public static String getLdapAttributeGroupIplanetGUID() {
        String key = (String)props.get("IP_GUID_GROUP");
        if (key == null) {
            key = "nsuniqueid";
            logger.error((Object)("Error obteniendo el guid para iplanet del fichero: " + fileName));
        }
        return key;
    }

    public static String getLdapAttributeGroupOpenLdapGUID() {
        String key = (String)props.get("OL_GUID_GROUP");
        if (key == null) {
            key = "uidnumber";
            logger.error((Object)("Error obteniendo el guid para open ldap del fichero: " + fileName));
        }
        return key;
    }

    static {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            props.load(is);
            is.close();
        }
        catch (Exception e) {
            logger.error((Object)"Error inicializando configuraci\u00f3n de b\u00fasqueda en LDAP", (Throwable)e);
        }
    }
}
