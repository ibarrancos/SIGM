package ieci.tecdoc.sgm.registropresencial.autenticacion;

import com.ieci.tecdoc.idoc.decoder.dbinfo.LDAPDef;
import org.apache.log4j.Logger;

import com.ieci.tecdoc.isicres.session.security.SecuritySession;
import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.exception.ValidationException;

import es.ieci.tecdoc.fwktd.core.spring.configuration.jdbc.datasource.MultiEntityContextHolder;

public class Login {

	private static final Logger log = Logger.getLogger((Class)Login.class);

	public static String login(User user, String entidad)
			throws SecurityException, ValidationException {
		String sessionID = SecuritySession.login(user.getUserName(), user
				.getPassword(), user.getLocale(), entidad);

		//seteamos al thread local
		MultiEntityContextHolder.setEntity(entidad);

		return sessionID;
	}

	public static boolean isAuthenticationLdap(String entidad) {
	    boolean result = false;
	    try {
	        LDAPDef ldapDef = SecuritySession.ldapInfo((String)entidad);
	        if (ldapDef.getLdapEngine() != 0) {
	            result = true;
	        }
	    }
	    catch (Exception e) {
	        log.warn((Object)"Se han encontrado problemas para obtener la pol\u00edtica de autenticaci\u00f3n", (Throwable)e);
	    }
	    return result;
	}
}
