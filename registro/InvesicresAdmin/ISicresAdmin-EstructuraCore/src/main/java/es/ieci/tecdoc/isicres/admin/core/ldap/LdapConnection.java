
package es.ieci.tecdoc.isicres.admin.core.ldap;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.AuthenticationException;

import es.ieci.tecdoc.isicres.admin.core.exception.IeciTdException;
import org.apache.commons.lang.StringUtils;

import java.util.Hashtable;

public final class LdapConnection
{
   public static final String LDAP_VERSION_2 = "2";
   public static final String LDAP_VERSION_3 = "3";
   private InitialDirContext m_initDirCtx;  
   private int               m_engine;       
   private int               m_provider;
   private String            m_url;
   private boolean           m_pool;
   private int               m_poolTimeout;

   public LdapConnection()
   {
      m_initDirCtx = null;
      m_engine     = 0;      
      m_provider   = 0;
   }

   public void open(int engine, int provider, String url, String userDn, String userPwd, boolean pool, int poolTimeout) throws Exception {
      String urlEncoded;
      this.open(engine, provider, url, userDn, userPwd, pool, poolTimeout, "2");
   }

   /*
    
    engine: ActiveDirectory, iPlanet, ...
    provider: Sun, ...
    url: "ldap://host:port/baseDn"
    poolTimeout: en milisegundos. 
    
   */
   
   public void open(int engine, int provider, String url,
                    String userDn, String userPwd,
                    boolean pool, int poolTimeout, String ldapVersion)
                    throws Exception
   {
      
      Hashtable env;      
      
      checkEngine(engine);
      m_engine = engine;
      
      checkProvider(provider);
      m_provider = provider;

      String urlEncoded = this.encodeURL(url);
      m_url         = urlEncoded;
      m_pool        = pool;
      m_poolTimeout = poolTimeout;
            
      env = new Hashtable();
         
      env.put(Context.INITIAL_CONTEXT_FACTORY, getInitCtxFry(provider));
      env.put(Context.PROVIDER_URL, urlEncoded);
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      env.put(Context.SECURITY_PRINCIPAL, userDn);
      env.put(Context.SECURITY_CREDENTIALS, userPwd);
      
      env.put("java.naming.ldap.version", ldapVersion);
      
      if (engine == LdapEngine.ACTIVE_DIRECTORY)
         env.put("java.naming.ldap.attributes.binary", "objectGUID");
      
      configurePool(provider, env, pool, poolTimeout);
         
      try
      {
         m_initDirCtx = new InitialDirContext(env);
      }
      catch (AuthenticationException e)
      {
         throw new IeciTdException(LdapError.EC_INVALID_AUTH_SPEC, 
                                   LdapError.EM_INVALID_AUTH_SPEC);
      }
      
   }

   private String encodeURL(String url) {
      String urlEncoded = url.replaceAll(" ", "%20");
      return urlEncoded;
   }
   
   public void open(LdapConnCfg cfg) throws Exception
   {
      
      if (StringUtils.isBlank(cfg.getLdapVersion())) {
         open(cfg.getEngine(), cfg.getProvider(), cfg.getUrl(), cfg.getUser(), cfg.getPwd(), cfg.getPool(), cfg.getPoolTimeOut());
      } else {
         open(cfg.getEngine(), cfg.getProvider(), cfg.getUrl(), cfg.getUser(), cfg.getPwd(), cfg.getPool(), cfg.getPoolTimeOut(), cfg.getLdapVersion());
      }
      
   }      
   
   public void close() throws Exception
   {      

      if (m_initDirCtx != null)
      {
         m_initDirCtx.close();
         m_initDirCtx = null;                              
      }
 
   } 

   public static void ensureClose(LdapConnection conn, Exception exc)
                      throws Exception
   {
      
      try
      {
         if (conn != null) conn.close();
         throw exc;
      }
      catch (Exception e)
      {
         throw exc;
      }
      
   }

   public InitialDirContext getInitDirContext()
   {
      return m_initDirCtx;
   }

   public int getEngine()
   {
      return m_engine;
   }

   public int getProvider()
   {
      return m_provider;
   }

   public String getUrl()
   {
      return m_url;
   }

   public boolean getPool()
   {
      return m_pool;
   }

   public int getPoolTimeout()
   {
      return m_poolTimeout;
   }
   
   public String getBaseDn() throws Exception
   {
      return m_initDirCtx.getNameInNamespace();
   }
      
   public String getBaseRPath(String dn) throws Exception
   {
      
      String brp = null;
      String baseDn;
      int    dnl, bdnl;
      
      baseDn = m_initDirCtx.getNameInNamespace();
      
      if ((dn!=null)&&(baseDn!=null)&&(dn.equals(baseDn)))
    	  return "";
      
      dn     = dn.toLowerCase();
      baseDn = baseDn.toLowerCase();
      
      dnl  = dn.length();
      bdnl = baseDn.length();
      
      if (dn.lastIndexOf(baseDn) == dnl - bdnl)
         brp = dn.substring(0, dnl - bdnl - 1);
      else
      {
         throw new IeciTdException(LdapError.EC_INVALID_DN, 
                                   LdapError.EM_INVALID_DN);         
      }
      
      return brp;
      
   }
   
   // **************************************************************************
   
   private static void checkEngine(int engine) throws Exception
   {
      
      if (engine == LdapEngine.ACTIVE_DIRECTORY) return;
      if (engine == LdapEngine.I_PLANET) return;
      if (engine == LdapEngine.OPENLDAP) return;
      
      throw new IeciTdException(LdapError.EC_INVALID_ENGINE, 
                                LdapError.EM_INVALID_ENGINE);
      
   }
   
   private static void checkProvider(int provider) throws Exception
   {
      
      if (provider == LdapProvider.SUN) return;
      
      throw new IeciTdException(LdapError.EC_INVALID_PROVIDER, 
                                LdapError.EM_INVALID_PROVIDER);
      
   }
   
   private static String getInitCtxFry(int provider)
   {
      
      String fry = null;
      
      if (provider == LdapProvider.SUN)
         fry = "com.sun.jndi.ldap.LdapCtxFactory";
      
      return fry;
      
   }
   
   private static void configurePool(int provider, Hashtable env, 
                                     boolean pool, int poolTimeout)
   {
      
      if (!pool) return;
      
      if (provider == LdapProvider.SUN)
      {
         env.put("com.sun.jndi.ldap.connect.pool", "true");
         env.put("com.sun.jndi.ldap.connect.pool.timeout", 
                 Integer.toString(poolTimeout));         
      }
         
   }
   
} // class
