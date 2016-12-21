
package es.ieci.tecdoc.isicres.admin.core.ldap;

import es.ieci.tecdoc.isicres.admin.core.collections.IeciTdShortTextArrayList;
import es.ieci.tecdoc.isicres.admin.core.exception.IeciTdException;
import org.apache.log4j.Logger;

public final class LdapBasicFns
{  
   private static final Logger logger = Logger.getLogger((Class)LdapBasicFns.class);

   private LdapBasicFns()
   {
   }
   
   public static String findEntryDn(LdapConnection conn, 
                                    String start, int scope, 
                                    String filter)
                        throws Exception
   {
      if (logger.isDebugEnabled()) {
         logger.debug((Object)"findEntryDn(LdapConnection, String, int, String) - start");
         logger.debug((Object)("connection url: [" + conn.getUrl() + "]"));
         logger.debug((Object)("start: [" + start + "]"));
         logger.debug((Object)("filter: [" + filter + "]"));
         logger.debug((Object)("scope: [" + scope + "]"));
      }      

      String     dn       = null;
      LdapSearch search   = null;
      String[]   retAttrs = {};
      int        maxCount = 1;
      
      try
      {
         
         search = new LdapSearch();
         
         search.initialize(conn, start, scope, filter, retAttrs, maxCount);
         
         search.execute();
         
         if (!search.next())
         {
	    logger.error((Object)("No se ha encontrado la b\u00fasuqeda ldap con la url [" + conn.getUrl() + "] y el nodo [" + start + "]"));
            throw new IeciTdException(LdapError.EC_NOT_FOUND,
                                      LdapError.EM_NOT_FOUND);
         }
         
         dn = search.getEntryDn();
         if (logger.isDebugEnabled()) {
            logger.debug((Object)("Se ha encontrado el nodo [" + dn + "]"));
         }
         
         search.release();
                          
      }
      catch (Exception e)
      {
	 logger.error("findEntryDn(LdapConnection, String, int, String)", e);
         LdapSearch.ensureRelease(search, e);
      }
      
      return dn;
      
   }

   public static String findGroupGuid(LdapConnection conn, String start, int scope, String filter) throws Exception {
      return LdapBasicFns.findEntryGuid(conn, start, scope, filter, false);
   }
   public static String findEntryGuid(LdapConnection conn, String start, int scope, String filter) throws Exception {
      return LdapBasicFns.findEntryGuid(conn, start, scope, filter, true);
   }
   
   public static String findEntryGuid(LdapConnection conn, 
                                      String start, int scope,
                                      String filter, boolean isEntryUser)
                        throws Exception
   {
      
      String     guid     = null;
      LdapSearch search   = null;
      String     guidAn;      
      String[]   retAttrs;
      int        maxCount = 1;
      Object     guidAv;
      
      guidAn = isEntryUser ? LdapAttribute.getGuidAttributeName(conn) : LdapAttribute.getGuidGroupAttributeName(conn);

      retAttrs = new String[] {guidAn};
      
      try
      {
         
         search = new LdapSearch();
         
         search.initialize(conn, start, scope, filter, retAttrs, maxCount);
         
         search.execute();
         
         if (!search.next())
         {
            throw new IeciTdException(LdapError.EC_NOT_FOUND,
                                      LdapError.EM_NOT_FOUND);
         }
         
         guidAv = search.getAttributeValue(guidAn);
         
         search.release();
         
         guid = formatGuid(conn, guidAv);
                          
      }
      catch (Exception e)
      {
         LdapSearch.ensureRelease(search, e);
      }
      
      return guid;
      
   }
   
   public static IeciTdShortTextArrayList findEntryDns(LdapConnection conn, 
                                                       String start, int scope, 
                                                       String filter)
                                          throws Exception
   {
      
      IeciTdShortTextArrayList dns      = null;
      String                   dn;
      LdapSearch               search   = null;
      String[]                 retAttrs = {};
      int                      maxCount = 0;
      
      try
      {
         
         dns = new IeciTdShortTextArrayList();
         
         search = new LdapSearch();
         
         search.initialize(conn, start, scope, filter, retAttrs, maxCount);
         
         search.execute();
         
         while (search.next())
         {
       
            dn = search.getEntryDn();
            
            dns.add(dn);
            
         }                  
         
         search.release();
                          
      }
      catch (Exception e)
      {
         LdapSearch.ensureRelease(search, e);
      }
      
      return dns;
      
   }
   
   public static IeciTdShortTextArrayList findEntryGuids(LdapConnection conn, 
                                                        String start, int scope,
                                                         String filter)
                                          throws Exception
   {
      
      IeciTdShortTextArrayList guids = null;
      String                   guid;
      LdapSearch               search   = null;
      String                   guidAn;      
      String[]                 retAttrs;
      int                      maxCount = 0;
      Object                   guidAv;
      
      guidAn   = LdapAttribute.getGuidGroupAttributeName(conn);
      retAttrs = new String[] {guidAn};
      
      try
      {

         guids = new IeciTdShortTextArrayList();

         search = new LdapSearch();
         
         search.initialize(conn, start, scope, filter, retAttrs, maxCount);
         
         search.execute();
         
         while (search.next())
         {
         
            guidAv = search.getAttributeValue(guidAn);
            guid   = formatGuid(conn, guidAv);
            
            guids.add(guid);
         
         }         
         
         search.release();         
                          
      }
      catch (Exception e)
      {
         LdapSearch.ensureRelease(search, e);
      }
      
      return guids;
      
   }
   
   public static IeciTdShortTextArrayList findEntryAttributeValues(
                                          LdapConnection conn, String dn,
                                          String attrName)
                                          throws Exception
   {
      
      IeciTdShortTextArrayList vals   = null;
      LdapSearch               search = null;
      String                   start, filter;
      int                      scope;
      String[]                 retAttrs;
      int                      maxCount = 0;
      int                      count , i;
      String                   val;
      
      start    = conn.getBaseRPath(dn);
      scope    = LdapScope.BASE;
      filter   = LdapFilter.getNullFilter(conn);
      retAttrs = new String[] {attrName};
      
      try
      {
         
         vals = new IeciTdShortTextArrayList();
         
         search = new LdapSearch();
         
         search.initialize(conn, start, scope, filter, retAttrs, maxCount);
         
         search.execute();
         
         if (!search.next())
         {
            throw new IeciTdException(LdapError.EC_NOT_FOUND,
                                      LdapError.EM_NOT_FOUND);
         }
         
         count = search.getAttributeValueCount(attrName);
         
         for (i = 0; i < count; i++)
         {
            
            val = search.getAttributeValue(attrName, i).toString();
            
            vals.add(val);
            
         }
         
         search.release();
                          
      }
      catch (Exception e)
      {
         LdapSearch.ensureRelease(search, e);
      }
            
      return vals;
      
   }
   
   public static String formatGuid(LdapConnection conn, Object guidObj)
   {
      
      String guidStr = null;
      int    engine;
      
      engine = conn.getEngine();
      
      if (engine == LdapEngine.ACTIVE_DIRECTORY)
         guidStr = formatAdGuid((byte[]) guidObj);
      else
         guidStr = guidObj.toString();
      
      return guidStr;
      
   }
   
   public static String formatGuid(LdapConnection conn, String sguid)
   {
      
      String fguid = null;
      int    engine;
      
      engine = conn.getEngine();
      
      if (engine == LdapEngine.ACTIVE_DIRECTORY)
         fguid = formatAdGuid(sguid);
      else
         fguid = sguid;
      
      return fguid;
            
   }
   
   public static String getEntryRdn(String dn)
   {      
      String rdn;
      int    idx;
      
      idx = dn.indexOf(",");
      
      if (idx == -1)  
         rdn = dn;
      else
      {
         rdn = dn.substring(0, idx);        
      }
      
      return rdn;
      
   }
   
   // **************************************************************************
   
   private static String formatAdGuid(byte[] guidObj)
   {
      
      String guidStr = "";
      int    len, i;
      String byteStr;
      
      len = guidObj.length;
      
      for (i = 0; i < len; i++)
      {
         
         byteStr = Integer.toHexString(guidObj[i] & 0xff);
         if (byteStr.length() == 1)
            byteStr = "0" + byteStr;
         
         guidStr = guidStr + byteStr;
         
      }
      
      return guidStr;
      
   }
   
   private static String formatAdGuid(String sguid)
   {
      
      String fguid = "";      
      int    len, i;
      
      len = sguid.length();
      
      for (i = 0; i < len; i = i + 2)
      {
         fguid = fguid + "\\" + sguid.charAt(i) + sguid.charAt(i + 1);
      }
         
      return fguid;
       
   }
   
} // class
