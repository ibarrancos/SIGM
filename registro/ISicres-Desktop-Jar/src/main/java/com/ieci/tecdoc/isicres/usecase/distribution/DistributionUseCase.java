package com.ieci.tecdoc.isicres.usecase.distribution;

import com.ieci.tecdoc.common.entity.AxSfEntity;
import com.ieci.tecdoc.common.exception.BookException;
import com.ieci.tecdoc.common.exception.DistributionException;
import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.exception.SessionException;
import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.common.invesdoc.Idocarchdet;
import com.ieci.tecdoc.common.invesicres.ScrDistreg;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.common.isicres.DistributionResults;
import com.ieci.tecdoc.common.isicres.ScrDistRegResults;
import com.ieci.tecdoc.common.isicres.SessionInformation;
import com.ieci.tecdoc.common.keys.IDocKeys;
import com.ieci.tecdoc.common.utils.Configurator;
import com.ieci.tecdoc.common.utils.ScrRegStateByLanguage;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.session.book.BookSession;
import com.ieci.tecdoc.isicres.session.distribution.DistributionSession;
import com.ieci.tecdoc.isicres.session.folder.FolderSession;
import com.ieci.tecdoc.isicres.session.security.SecuritySession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.distribution.xml.DistributionFields;
import com.ieci.tecdoc.isicres.usecase.distribution.xml.DistributionSearchFields;
import com.ieci.tecdoc.isicres.usecase.distribution.xml.XMLDistributionList;
import com.ieci.tecdoc.utils.HibernateUtil;
import com.ieci.tecdoc.utils.Validator;
import com.ieci.tecdoc.utils.cache.CacheBag;
import com.ieci.tecdoc.utils.cache.CacheFactory;
import gnu.trove.THashMap;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class DistributionUseCase {
    private static Logger _logger = Logger.getLogger((Class)DistributionUseCase.class);
    private static final String ORACLE = "ORACLE";
    private static final String SQLSERVER = "SQLSERVER";
    private static final String POSTGRESQL = "POSTGRESQL";
    private static final String DB2 = "DB2";
    private static final String GUION = "-";
    private static final String ALMOHADILLA = "#";
    private static final String PUNTO_COMA = ";";
    private static final String COMILLA = "'";
    private static final String COMA = ",";
    private static final String PER_CENT = "%";
    private static final String PAR_IZQ = "(";
    private static final String PAR_DER = ")";
    private static final String OR = " OR ";
    private static final String IS_NULL = "is null";
    private static final String ESPACIO = " ";
    private static final String TO_CONVERT_DATE = "convert(datetime";
    private static final String TO_CONVERT_DATE_FORMAT = "0";
    private static final String TO_DATE = "to_date(";
    private static final String TO_TIMESTAMP = "to_timestamp(";
    private static final String TO_CHAR = "to_char(";
    private static final String TIME_INITIAL = " 00:00:00";
    private static final String TIME_FINAL = " 23:59:59";
    private static final String DATE_FORMAT = "DD-MM-YYYY HH24:MI:SS";
    private static final String DATE_FORMAT_DB2 = "YYYY-MM-DD HH24:MI:SS";
    private static final String MONTH_FORMAT = "MM";
    private static final String YEAR_FORMAT = "YYYY";
    private static final String CONTENT = " LIKE ";
    private static final String MONTH = "MONTH(";
    private static final String YEAR = "YEAR(";
    private static final String SCRORGS = " IN (SELECT ID FROM SCR_ORGS WHERE CODE LIKE ";
    private static final String SCRORGSCONNECT = " IN (SELECT ID FROM SCR_ORGS WHERE TYPE <> 0 START WITH CODE= ";
    private static final String BYPRIOR = " CONNECT BY PRIOR ID = ID_FATHER) ";
    private static final String SCRCA = " IN (SELECT ID FROM SCR_CA WHERE CODE LIKE ";
    private static final String TYPE = " AND TYPE <> 0)";

    public Document getDistribution(UseCaseConf useCaseConf, int state, int firstRow, int typeDist, String distWhere, String regWhere, String orderBy) throws ValidationException, DistributionException, SessionException, BookException, SecurityException {
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("getDistribution state [" + state + "] firstRow [" + firstRow + "] typeDist [" + typeDist + "]"));
        }
        Integer timeout = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getTimeOut());
        Integer[] distPerms = SecuritySession.getScrDistPermission((String)useCaseConf.getSessionID());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        boolean isOfficeAsoc = Boolean.valueOf(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Distribution/DistributionOficAsoc"));
        DistributionResults distributionResults = DistributionSession.getDistribution((String)useCaseConf.getSessionID(), (int)state, (int)firstRow, (int)Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageDistributionMinutaSize")), (int)typeDist, (String)distWhere, (String)regWhere, (boolean)isOfficeAsoc, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap(), (String)orderBy);
        Map booksReg = this.descompBookIDRegs(distributionResults);
        HashMap<String, AxSf> axsfs = new HashMap<String, AxSf>();
        HashMap vldAttribMap = new HashMap();
	for (Iterator it03 = booksReg.keySet().iterator(); it03.hasNext();) {
	    Integer bookID=(Integer) it03.next();
            if (bookID == 0 || booksReg.get(bookID) == null) continue;
            BookSession.openBook((String)useCaseConf.getSessionID(), (Integer)bookID, (String)useCaseConf.getEntidadId());
            Validator.validate_String_NotNull_LengthMayorZero((String)useCaseConf.getSessionID(), (String)"session");
            Validator.validate_Integer((Integer)bookID, (String)"book");
            AxSf axsf = null;
            Transaction tran = null;
            try {
                Session session = HibernateUtil.currentSession((String)useCaseConf.getEntidadId());
                tran = session.beginTransaction();
                CacheBag cacheBag = CacheFactory.getCacheInterface().getCacheEntry(useCaseConf.getSessionID());
                if (!cacheBag.containsKey((Object)bookID)) {
                    throw new BookException("bookexception.error_book_not_open");
                }
                THashMap bookInformation = (THashMap)cacheBag.get((Object)bookID);
                if (!cacheBag.containsKey((Object)bookID)) {
                    throw new BookException("bookexception.error_book_not_open");
                }
                Idocarchdet idoc = (Idocarchdet)bookInformation.get((Object)IDocKeys.IDOCARCHDET_FLD_DEF_ASOBJECT);
                List regs = (List)booksReg.get(bookID);
                AxSfEntity axSfEntity = null;
                Iterator iter = regs.iterator();
                while (iter.hasNext()) {
                    axSfEntity = new AxSfEntity();
                    Integer fdrid = (Integer)iter.next();
                    axsf = FolderSession.getBookFolder((Session)session, (THashMap)bookInformation, (String)useCaseConf.getSessionID(), (Integer)bookID, (int)fdrid, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (Idocarchdet)idoc, (AxSfEntity)axSfEntity, vldAttribMap);
                    String key = bookID + "_" + fdrid;
                    axsfs.put(key, axsf);
                }
                HibernateUtil.commitTransaction((Transaction)tran);
                continue;
            }
            catch (BookException bE) {
                HibernateUtil.rollbackTransaction((Transaction)tran);
                throw bE;
            }
            catch (SessionException sE) {
                HibernateUtil.rollbackTransaction((Transaction)tran);
                throw sE;
            }
            catch (Exception e) {
                HibernateUtil.rollbackTransaction((Transaction)tran);
                throw new BookException("bookexception.cannot_find_registers");
            }
            finally {
                HibernateUtil.closeSession((String)useCaseConf.getEntidadId());
                continue;
            }
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("distributionResults.getTotalSize():" + distributionResults.getTotalSize()));
            _logger.debug((Object)("distributionResults.getIdocarchhdr():" + distributionResults.getIdocarchhdr()));
            _logger.debug((Object)("axsfs:" + axsfs));
            _logger.debug((Object)("distributionResults:" + (Object)distributionResults));
        }
        int paso = Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageDistributionMinutaSize"));
        List listScrDistRegResults = this.getScrDistRegResults(useCaseConf, distributionResults, axsfs);
        return XMLDistributionList.getXMLDistributionList(firstRow, paso, distributionResults.getTotalSize(), useCaseConf.getLocale(), listScrDistRegResults, typeDist, timeout, distPerms, distributionResults.getActualDate(), sessionInformation);
    }

    public Map descompBookIDRegs(DistributionResults distributionResults) {
        HashMap<Integer, List> booksReg = new HashMap<Integer, List>();
        ArrayList<Integer> regs = new ArrayList<Integer>();
        Iterator it = distributionResults.getBooks().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            StringTokenizer tokenizer = new StringTokenizer(key, "_");
            Integer bookID = new Integer(tokenizer.nextToken());
            int fdrid = Integer.parseInt(tokenizer.nextToken());
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("bookID:" + bookID + "fdrid:" + fdrid));
            }
            if (booksReg.containsKey(bookID)) {
                List aux = (List)booksReg.get(bookID);
                aux.add(new Integer(fdrid));
                booksReg.put(bookID, aux);
                continue;
            }
            regs = new ArrayList();
            regs.add(new Integer(fdrid));
            booksReg.put(bookID, regs);
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("bookID:" + booksReg));
        }
        return booksReg;
    }

    public Document acceptDistribution(UseCaseConf useCaseConf, List dis, int state, int firstRow, int typeDist, Integer bookId, String distWhere, String regWhere, String orderBy) throws ValidationException, DistributionException, SessionException, BookException, SecurityException {
        ArrayList<Integer> createPermBooks = null;
        HashMap<Integer, String> createPermBooksInfo = null;
        if (bookId == 0) {
            List<ScrRegStateByLanguage> inList = BookSession.getInBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            boolean canCreate = false;
            createPermBooks = new ArrayList<Integer>();
            createPermBooksInfo = new HashMap<Integer, String>();
            for (ScrRegStateByLanguage scrRegStateByLanguage : inList) {
                Integer id = scrRegStateByLanguage.getIdocarchhdrId();
                String name = scrRegStateByLanguage.getIdocarchhdrName();
                BookSession.openBook((String)useCaseConf.getSessionID(), (Integer)id, (String)useCaseConf.getEntidadId());
                canCreate = SecuritySession.canCreate((String)useCaseConf.getSessionID(), (Integer)id);
                if (!canCreate) continue;
                createPermBooks.add(id);
                createPermBooksInfo.put(id, name);
            }
        }
        List<ScrDistreg> archidFdr = null;
        Object result = null;
        int maxResults = Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageDistributionMinutaSize"));
        Integer launchDistOutRegister = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getDistSRegister());
        boolean isOfficeAsoc = Boolean.valueOf(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Distribution/DistributionOficAsoc"));
        DistributionResults distributionResults = DistributionSession.getDistribution((String)useCaseConf.getSessionID(), (int)state, (int)firstRow, (int)maxResults, (int)typeDist, (String)distWhere, (String)regWhere, (boolean)isOfficeAsoc, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap(), (String)orderBy);
        try {
            result = DistributionSession.acceptDistribution((String)useCaseConf.getSessionID(), (List)dis, (int)state, (int)firstRow, (int)maxResults, (int)typeDist, (Integer)bookId, createPermBooks, (DistributionResults)distributionResults, (String)distWhere, (String)regWhere, (Integer)launchDistOutRegister, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            if (result instanceof List) {
                archidFdr = (List)result;
                Document document = null;
                return document;
            }
            archidFdr = (List)((Map)result).get(new Integer(-1));
            Document document = XMLDistributionList.getXMLDistributionVldBooks(createPermBooksInfo);
            return document;
        }
        finally {
            if (!(archidFdr == null || archidFdr.isEmpty())) {
                for (ScrDistreg distReg2 : archidFdr) {
                    FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)new Integer(distReg2.getIdArch()), (int)distReg2.getIdFdr(), (String)useCaseConf.getEntidadId());
                }
            }
        }
    }

    public Document acceptDistributionEx(UseCaseConf useCaseConf, List dis, int state, int firstRow, int typeDist, Integer bookId, String distWhere, String regWhere, String orderBy) throws ValidationException, DistributionException, SessionException, BookException, SecurityException {
        ArrayList<Integer> createPermBooks = null;
        HashMap<Integer, String> createPermBooksInfo = null;
        if (bookId == 0) {
            List<ScrRegStateByLanguage> inList = BookSession.getInBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            boolean canCreate = false;
            createPermBooks = new ArrayList<Integer>();
            createPermBooksInfo = new HashMap<Integer, String>();
            for (ScrRegStateByLanguage scrRegStateByLanguage : inList) {
                Integer id = scrRegStateByLanguage.getIdocarchhdrId();
                String name = scrRegStateByLanguage.getIdocarchhdrName();
                BookSession.openBook((String)useCaseConf.getSessionID(), (Integer)id, (String)useCaseConf.getEntidadId());
                canCreate = SecuritySession.canCreate((String)useCaseConf.getSessionID(), (Integer)id);
                if (!canCreate) continue;
                createPermBooks.add(id);
                createPermBooksInfo.put(id, name);
            }
        }
        List<ScrDistreg> archidFdr = null;
        Object result = null;
        int maxResults = Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageDistributionMinutaSize"));
        Integer launchDistOutRegister = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getDistSRegister());
        boolean isOfficeAsoc = Boolean.valueOf(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Distribution/DistributionOficAsoc"));
        DistributionResults distributionResults = DistributionSession.getDistribution((String)useCaseConf.getSessionID(), (int)state, (int)firstRow, (int)maxResults, (int)typeDist, (String)distWhere, (String)regWhere, (boolean)isOfficeAsoc, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap(), (String)orderBy);
        try {
            result = DistributionSession.acceptDistribution((String)useCaseConf.getSessionID(), (List)dis, (int)state, (int)firstRow, (int)maxResults, (int)typeDist, (Integer)bookId, createPermBooks, (DistributionResults)distributionResults, (String)distWhere, (String)regWhere, (Integer)launchDistOutRegister, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            if (result instanceof List) {
                archidFdr = (List)result;
                Document document = null;
                return document;
            }
            archidFdr = (List)((Map)result).get(new Integer(-1));
            Document document = XMLDistributionList.getXMLDistributionVldBooks(createPermBooksInfo);
            return document;
        }
        finally {
            if (!(archidFdr == null || archidFdr.isEmpty())) {
                for (ScrDistreg distReg2 : archidFdr) {
                    FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)new Integer(distReg2.getIdArch()), (int)distReg2.getIdFdr(), (String)useCaseConf.getEntidadId());
                }
            }
        }
    }

    public void rejectDistribution(String sessionId, String idEntidad, boolean isLdap, Locale locale, List dis, int state, int firstRow, int typeDist, String remarks, String distWhere, String regWhere) throws ValidationException, DistributionException, SessionException, BookException {
        UseCaseConf useCaseConf = new UseCaseConf();
        useCaseConf.setEntidadId(idEntidad);
        useCaseConf.setSessionID(sessionId);
        useCaseConf.setUseLdap(isLdap);
        useCaseConf.setLocale(locale);
        this.rejectDistribution(useCaseConf, dis, state, firstRow, typeDist, remarks, distWhere, regWhere, null);
    }

    public void rejectDistribution(UseCaseConf useCaseConf, List dis, int state, int firstRow, int typeDist, String remarks, String distWhere, String regWhere, String orderBy) throws ValidationException, DistributionException, SessionException, BookException {
        List<ScrDistreg> archidFdr = new ArrayList();
        int maxResults = Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageDistributionMinutaSize"));
        try {
            archidFdr = DistributionSession.rejectDistribution((String)useCaseConf.getSessionID(), (List)dis, (String)remarks, (int)state, (int)firstRow, (int)maxResults, (int)typeDist, (String)distWhere, (String)regWhere, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap(), (String)orderBy);
        }
        finally {
            for (ScrDistreg distReg2 : archidFdr) {
                FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)new Integer(distReg2.getIdArch()), (int)distReg2.getIdFdr(), (String)useCaseConf.getEntidadId());
            }
        }
    }

    public void saveDistribution(UseCaseConf useCaseConf, List dis, int state, int firstRow, int typeDist, String distWhere, String regWhere, String orderBy, String remarks) throws ValidationException, DistributionException, SessionException, BookException {
        List<ScrDistreg> archidFdr = new ArrayList();
        int maxResults = Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageDistributionMinutaSize"));
        try {
            archidFdr = DistributionSession.saveDistribution((String)useCaseConf.getSessionID(), (List)dis, (int)state, (int)firstRow, (int)maxResults, (int)typeDist, (String)distWhere, (String)regWhere, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap(), (String)orderBy, (String)remarks);
        }
        finally {
            for (ScrDistreg distReg2 : archidFdr) {
                FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)new Integer(distReg2.getIdArch()), (int)distReg2.getIdFdr(), (String)useCaseConf.getEntidadId());
            }
        }
    }

    public void changeDistribution(UseCaseConf useCaseConf, List dis, int state, int firstRow, int typeDist, String code) throws ValidationException, DistributionException, SessionException, BookException {
        Integer launchDistOutRegister = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getDistSRegister());
        Integer canDestWithoutList = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getCanChangeDestWithoutList());
        DistributionSession.changeDistribution((String)useCaseConf.getSessionID(), (List)dis, (String)code, (int)typeDist, (Integer)launchDistOutRegister, (Integer)canDestWithoutList, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap());
    }

    public String createDistribution(UseCaseConf useCaseConf, Integer bookId, List<Integer> listIdsRegister, Integer userType, Integer userId, String messageForUser) throws BookException, DistributionException, ValidationException, SessionException {
        try {
            String string = DistributionSession.createDistribution((String)useCaseConf.getSessionID(), (Integer)bookId, (List)listIdsRegister, (Integer)userType, (Integer)userId, (String)messageForUser, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            return string;
        }
        finally {
            for (Integer folderID2 : listIdsRegister) {
                FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)folderID2, (String)useCaseConf.getEntidadId());
            }
        }
    }

    public String saveRemarks(UseCaseConf useCaseConf, int id, String remarks) throws ValidationException, DistributionException, SessionException, BookException {
        List<ScrDistreg> archidFdr = new ArrayList();
        try {
            String xml;
            archidFdr = DistributionSession.saveRemarks((String)useCaseConf.getSessionID(), (int)id, (String)remarks, (String)useCaseConf.getEntidadId());
            Document xmlDocument = XMLDistributionList.getXMLSaveRemarks(id, remarks, useCaseConf.getLocale());
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
            try {
                xmlWriter.write(xmlDocument);
            }
            catch (IOException e) {
                throw new ValidationException("validationFieldValue");
            }
            String string = xml = writer.toString();
            return string;
        }
        finally {
            for (ScrDistreg distReg2 : archidFdr) {
                FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)new Integer(distReg2.getIdArch()), (int)distReg2.getIdFdr(), (String)useCaseConf.getEntidadId());
            }
        }
    }

    public Document getDistributionSearch(UseCaseConf useCaseConf, int typeDist) throws Exception {
        String dataBaseType = DistributionSession.getDataBaseType((String)useCaseConf.getSessionID());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        return XMLDistributionList.getXMLDistributionSearchList(typeDist, useCaseConf.getLocale(), dataBaseType, sessionInformation.getCaseSensitive());
    }

    public String getValidateDistributionSearch(UseCaseConf useCaseConf, Integer typeDist, String distWhere, String regWhere) throws Exception {
        Object whereDist = null;
        Object whereReg = null;
        String dataBaseType = DistributionSession.getDataBaseType((String)useCaseConf.getSessionID());
        whereDist = !distWhere.equals("") ? this.getSearchCriteria(useCaseConf, (String)distWhere, 1, typeDist, useCaseConf.getLocale(), dataBaseType) : distWhere;
        whereReg = !regWhere.equals("") ? this.getSearchCriteria(useCaseConf, (String)regWhere, 2, typeDist, useCaseConf.getLocale(), dataBaseType) : regWhere;
        Document xmlDocument = XMLDistributionList.getXMLDistributionClausuleWhere(whereDist, whereReg, useCaseConf.getLocale());
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(xmlDocument);
        }
        catch (IOException e) {
            throw new ValidationException("validationFieldValue");
        }
        String xml = writer.toString();
        return xml;
    }

    private Object getSearchCriteria(UseCaseConf useCaseConf, String distRegWhere, int searchType, Integer typeDist, Locale locale, String dataBaseType) throws Exception {
        DistributionSearchFields distributionSearchFields = null;
        DistributionFields distributionFields = null;
        distributionSearchFields = new DistributionSearchFields(new Integer(searchType), typeDist, locale, dataBaseType);
        List fieldSearch = distributionSearchFields.getResult();
        StringTokenizer tokens = new StringTokenizer(distRegWhere, "#");
        String token = null;
        String token1 = null;
        String result = "";
        Integer validation = null;
        Integer type = null;
        Map operators = null;
        String operator = null;
        String value = null;
        String fieldName = null;
        ArrayList<String> validationError = new ArrayList<String>();
        int index = 0;
        String operatorNoValidate = RBUtil.getInstance(locale).getProperty("query.begin.by.value");
        String operatorDepend = RBUtil.getInstance(locale).getProperty("query.depend.of.value");
        while (tokens.hasMoreTokens()) {
            token = tokens.nextToken();
            StringTokenizer tokens1 = new StringTokenizer(token, ";");
            while (tokens1.hasMoreTokens()) {
                token1 = tokens1.nextToken();
                for (int i = 0; i < fieldSearch.size(); ++i) {
                    distributionFields = (DistributionFields)fieldSearch.get(i);
                    if (!distributionFields.getFieldName().equals(token1)) continue;
                    validation = distributionFields.getFieldValidation();
                    type = distributionFields.getFieldType();
                    operators = distributionFields.getOperators();
                    fieldName = token1;
                    break;
                }
                operator = tokens1.nextToken();
                value = tokens1.nextToken();
                if (!((validation != 1 || operator.equals(operatorNoValidate)) && validation != 9 && validation != 10 && validation != 6)) {
                    Integer id = DistributionSession.validateQueryDistribution((String)useCaseConf.getSessionID(), (String)value, (Integer)validation, (String)useCaseConf.getEntidadId());
                    if (id != null) {
                        if (validation == 9 || validation == 10) {
                            value = new Long(id | 1073741824).toString();
                        }
                        if (!(validation != 1 || operator.equals(operatorDepend))) {
                            value = new Integer(id).toString();
                        }
                        if (validation == 6) {
                            value = new Integer(id).toString();
                        }
                    } else {
                        validationError.add(fieldName);
                        return validationError;
                    }
                }
                if (index == 0) {
                    result = this.getWhere(fieldName, type, operator, value, operators, locale, dataBaseType);
                    continue;
                }
                result = result + " AND " + this.getWhere(fieldName, type, operator, value, operators, locale, dataBaseType);
            }
            ++index;
        }
        if (typeDist == 0 && searchType == 0) {
            result = result + " AND (STATE != 6)";
        }
        return result;
    }

    private String getWhere(String fieldName, Integer type, String operator, String value, Map<String,String> operators, Locale locale, String dataBaseType) throws Exception {
        String conversionOperator = null;
        StringBuffer buffer = new StringBuffer();
        for (String key : operators.keySet()) {
            String aux = key.substring(1, key.length());
            if (!aux.equals(operator)) continue;
            conversionOperator = (String)operators.get(key);
            break;
        }
        if (type == 5) {
            buffer.append(fieldName);
            buffer.append(conversionOperator);
            buffer.append(value);
        }
        if (type == 0) {
            if (conversionOperator == "=" || conversionOperator == "<>" || conversionOperator == ">" || conversionOperator == "<" || conversionOperator == ">=" || conversionOperator == "<=") {
                if (conversionOperator == "<>") {
                    buffer.append("(");
                    buffer.append(fieldName);
                    buffer.append(" ");
                    buffer.append("is null");
                    buffer.append(" OR ");
                    buffer.append(fieldName);
                    buffer.append(conversionOperator);
                    buffer.append("'");
                    buffer.append(value);
                    buffer.append("'");
                    buffer.append(")");
                } else {
                    buffer.append(fieldName);
                    buffer.append(conversionOperator);
                    buffer.append("'");
                    buffer.append(value);
                    buffer.append("'");
                }
            } else if (conversionOperator.equals("LIKE1")) {
                buffer.append(fieldName);
                buffer.append(" LIKE ");
                buffer.append("'");
                buffer.append(value);
                buffer.append("%");
                buffer.append("'");
            } else if (conversionOperator.equals("LIKE2")) {
                buffer.append(fieldName);
                buffer.append(" LIKE ");
                buffer.append("'");
                buffer.append("%");
                buffer.append(value);
                buffer.append("'");
            } else {
                buffer.append(fieldName);
                buffer.append(" LIKE ");
                buffer.append("'");
                buffer.append("%");
                buffer.append(value);
                buffer.append("%");
                buffer.append("'");
            }
        }
        if (type == 2) {
            List monthYear = null;
            if (conversionOperator.equals("MOUNTH") || conversionOperator.equals("YEAR")) {
                monthYear = this.getMonthYear(value);
            }
            if (conversionOperator == ">=AND<=") {
                buffer.append(this.getWhereByDataBaseRest(1, fieldName, value, dataBaseType));
            } else if (conversionOperator.equals("<OR>")) {
                buffer.append(this.getWhereByDataBaseRest(2, fieldName, value, dataBaseType));
            } else if (conversionOperator.equals(">23")) {
                buffer.append(this.getWhereByDataBaseRest(3, fieldName, value, dataBaseType));
            } else if (conversionOperator.equals("<00")) {
                buffer.append(this.getWhereByDataBaseRest(4, fieldName, value, dataBaseType));
            } else if (conversionOperator.equals("MOUNTH")) {
                buffer.append(this.getWhereByDataBaseMonthYear(1, fieldName, dataBaseType, monthYear));
            } else if (conversionOperator.equals("YEAR")) {
                buffer.append(this.getWhereByDataBaseMonthYear(2, fieldName, dataBaseType, monthYear));
            }
        }
        if (type == 1) {
            if (conversionOperator == "=") {
                buffer.append(fieldName);
                buffer.append(conversionOperator);
                buffer.append(value);
            } else if (conversionOperator == "<>") {
                buffer.append("(");
                buffer.append(fieldName);
                buffer.append(" ");
                buffer.append("is null");
                buffer.append(" OR ");
                buffer.append(fieldName);
                buffer.append(conversionOperator);
                buffer.append(value);
                buffer.append(")");
            } else if (conversionOperator == "SCR_ORGS") {
                buffer.append(fieldName);
                buffer.append(" IN (SELECT ID FROM SCR_ORGS WHERE CODE LIKE ");
                buffer.append("'");
                buffer.append("%");
                buffer.append(value);
                buffer.append("%");
                buffer.append("'");
                buffer.append(" AND TYPE <> 0)");
            } else if (conversionOperator == "SCR_ORGS_CONNECT") {
                buffer.append(fieldName);
                buffer.append(" IN (SELECT ID FROM SCR_ORGS WHERE TYPE <> 0 START WITH CODE= ");
                buffer.append("'");
                buffer.append(value);
                buffer.append("'");
                buffer.append(" CONNECT BY PRIOR ID = ID_FATHER) ");
            } else {
                buffer.append(fieldName);
                buffer.append(" IN (SELECT ID FROM SCR_CA WHERE CODE LIKE ");
                buffer.append("'");
                buffer.append("%");
                buffer.append(value);
                buffer.append("%");
                buffer.append("'");
                buffer.append(")");
            }
        }
        return buffer.toString();
    }

    private List getMonthYear(String value) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        String month = value.substring(value.indexOf("-") + 1, value.lastIndexOf("-"));
        String year = value.substring(value.lastIndexOf("-") + 1, value.length());
        result.add(month);
        result.add(year);
        return result;
    }

    private String getWhereByDataBaseMonthYear(int type, String fieldName, String dataBaseType, List monthYear) throws Exception {
        StringBuffer buffer = new StringBuffer();
        if (dataBaseType.equals("ORACLE") || dataBaseType.equals("POSTGRESQL")) {
            if (type == 1) {
                buffer.append("to_char(");
                buffer.append(fieldName);
                buffer.append(",");
                buffer.append("'");
                buffer.append("MM");
                buffer.append("'");
                buffer.append(")");
                buffer.append("=");
                buffer.append("'");
                buffer.append((String)monthYear.get(0));
                buffer.append("'");
                buffer.append(" AND ");
                buffer.append("to_char(");
                buffer.append(fieldName);
                buffer.append(",");
                buffer.append("'");
                buffer.append("YYYY");
                buffer.append("'");
                buffer.append(")");
                buffer.append("=");
                buffer.append("'");
                buffer.append((String)monthYear.get(1));
                buffer.append("'");
            } else {
                buffer.append("to_char(");
                buffer.append(fieldName);
                buffer.append(",");
                buffer.append("'");
                buffer.append("YYYY");
                buffer.append("'");
                buffer.append(")");
                buffer.append("=");
                buffer.append("'");
                buffer.append((String)monthYear.get(1));
                buffer.append("'");
            }
        }
        if (dataBaseType.equals("SQLSERVER") || dataBaseType.equals("DB2")) {
            if (type == 1) {
                buffer.append("MONTH(");
                buffer.append(fieldName);
                buffer.append(")");
                buffer.append("=");
                buffer.append(new Integer((String)monthYear.get(0)).toString());
                buffer.append(" AND ");
                buffer.append("YEAR(");
                buffer.append(fieldName);
                buffer.append(")");
                buffer.append("=");
                buffer.append(new Integer((String)monthYear.get(1)).toString());
            } else {
                buffer.append("YEAR(");
                buffer.append(fieldName);
                buffer.append(")");
                buffer.append("=");
                buffer.append(new Integer((String)monthYear.get(1)).toString());
            }
        }
        return buffer.toString();
    }

    private String getWhereByDataBaseRest(int type, String fieldName, String value, String dataBaseType) throws Exception {
        StringBuffer buffer = new StringBuffer();
        if (dataBaseType.equals("ORACLE")) {
            if (type == 1) {
                buffer.append(fieldName);
                buffer.append(">=").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
                buffer.append(" AND ");
                buffer.append(fieldName);
                buffer.append("<=").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
            }
            if (type == 2) {
                buffer.append("(");
                buffer.append(fieldName);
                buffer.append("<").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
                buffer.append(" OR ");
                buffer.append(fieldName);
                buffer.append(">").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")").append(")");
            }
            if (type == 3) {
                buffer.append(fieldName);
                buffer.append(">").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
            }
            if (type == 4) {
                buffer.append(fieldName);
                buffer.append("<").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
            }
        }
        if (dataBaseType.equals("SQLSERVER")) {
            if (type == 1) {
                buffer.append(fieldName);
                buffer.append(">=").append("convert(datetime").append(",").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("0").append(")");
                buffer.append(" AND ");
                buffer.append(fieldName);
                buffer.append("<=").append("convert(datetime").append(",").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("0").append(")");
            }
            if (type == 2) {
                buffer.append("(");
                buffer.append(fieldName);
                buffer.append("<").append("convert(datetime").append(",").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("0").append(")");
                buffer.append(" OR ");
                buffer.append(fieldName);
                buffer.append(">").append("convert(datetime").append(",").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("0").append(")").append(")");
            }
            if (type == 3) {
                buffer.append(fieldName);
                buffer.append(">").append("convert(datetime").append(",").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("0").append(")");
            }
            if (type == 4) {
                buffer.append(fieldName);
                buffer.append("<").append("convert(datetime").append(",").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("0").append(")");
            }
        }
        if (dataBaseType.equals("POSTGRESQL")) {
            if (type == 1) {
                buffer.append(fieldName);
                buffer.append(">=").append("to_timestamp(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
                buffer.append(" AND ");
                buffer.append(fieldName);
                buffer.append("<=").append("to_timestamp(").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
            }
            if (type == 2) {
                buffer.append("(");
                buffer.append(fieldName);
                buffer.append("<").append("to_timestamp(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
                buffer.append(" OR ");
                buffer.append(fieldName);
                buffer.append(">").append("to_timestamp(").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")").append(")");
            }
            if (type == 3) {
                buffer.append(fieldName);
                buffer.append(">").append("to_timestamp(").append("'");
                buffer.append(value);
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
            }
            if (type == 4) {
                buffer.append(fieldName);
                buffer.append("<").append("to_timestamp(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("DD-MM-YYYY HH24:MI:SS").append("'").append(")");
            }
        }
        if (dataBaseType.equals("DB2")) {
            if (type == 1) {
                buffer.append(fieldName);
                buffer.append(">=").append("to_date(").append("'");
                buffer.append(this.getDateFormated(value));
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("YYYY-MM-DD HH24:MI:SS").append("'").append(")");
                buffer.append(" AND ");
                buffer.append(fieldName);
                buffer.append("<=").append("to_date(").append("'");
                buffer.append(this.getDateFormated(value));
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("YYYY-MM-DD HH24:MI:SS").append("'").append(")");
            }
            if (type == 2) {
                buffer.append("(");
                buffer.append(fieldName);
                buffer.append("<").append("to_date(").append("'");
                buffer.append(this.getDateFormated(value));
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("YYYY-MM-DD HH24:MI:SS").append("'").append(")").append(" OR ");
                buffer.append(fieldName);
                buffer.append(">").append("to_date(").append("'");
                buffer.append(this.getDateFormated(value));
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("YYYY-MM-DD HH24:MI:SS").append("'").append(")").append(")");
            }
            if (type == 3) {
                buffer.append(fieldName);
                buffer.append(">").append("to_date(").append("'");
                buffer.append(this.getDateFormated(value));
                buffer.append(" 23:59:59").append("'").append(",").append("'").append("YYYY-MM-DD HH24:MI:SS").append("'").append(")");
            }
            if (type == 4) {
                buffer.append(fieldName);
                buffer.append("<").append("to_date(").append("'");
                buffer.append(value);
                buffer.append(" 00:00:00").append("'").append(",").append("'").append("YYYY-MM-DD HH24:MI:SS").append("'").append(")");
            }
        }
        return buffer.toString();
    }

    private List getScrDistRegResults(UseCaseConf useCaseConf, DistributionResults distributionResults, Map axsfs) throws ValidationException, DistributionException {
        Map distType = distributionResults.getDistType();
        TreeMap treemap = new TreeMap(new XMLDistributionList.IntegerComparator());
        treemap.putAll(distributionResults.getResults());
        ArrayList<ScrDistRegResults> listScrDistRegResults = new ArrayList<ScrDistRegResults>();
	for (Iterator it03 = treemap.keySet().iterator(); it03.hasNext();) {
	    Integer distId=(Integer) it03.next();
            Map aux = (Map)treemap.get(distId);
            if (aux == null || aux.size() <= 0) continue;
            String id = (String)aux.keySet().iterator().next();
            ScrDistreg distReg = (ScrDistreg)aux.get(id);
            Integer dType = (Integer)distType.get(distReg.getId());
            String sourceDesc = DistributionSession.getOrigDestDescription((String)useCaseConf.getSessionID(), (ScrDistreg)distReg, (boolean)true, (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap());
            String targetDesc = DistributionSession.getOrigDestDescription((String)useCaseConf.getSessionID(), (ScrDistreg)distReg, (boolean)false, (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap());
            AxSf axsf = (AxSf)axsfs.get("" + distReg.getIdArch() + "_" + distReg.getIdFdr());
            Object idocarch = distributionResults.getIdocarchhdr().get(new Integer(distReg.getIdArch()));
            String targetActualDist = null;
            if (distReg.getState() == 5) {
                targetActualDist = DistributionSession.getDestinoActualDistribucion((String)useCaseConf.getSessionID(), (Integer)distReg.getId(), (String)useCaseConf.getEntidadId());
            }
            ScrDistRegResults scrDistRegResult = new ScrDistRegResults(distReg, axsf, idocarch, sourceDesc, targetDesc, dType, targetActualDist);
            listScrDistRegResults.add(scrDistRegResult);
        }
        return listScrDistRegResults;
    }

    protected String getDateFormated(String formatedField) {
        String where = formatedField;
        String day = where.substring(0, 2);
        String month = where.substring(3, 5);
        String year = where.substring(6, 10);
        StringBuffer buffer = new StringBuffer();
        buffer.append(year);
        buffer.append("-");
        buffer.append(month);
        buffer.append("-");
        buffer.append(day);
        return buffer.toString();
    }

    public void redistributionDistribution(UseCaseConf useCaseConf, List dis, int typeDist, String messageForUser, Integer userType, Integer userId) throws ValidationException, DistributionException, SessionException, BookException {
        Integer canDestWithoutList = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getCanChangeDestWithoutList());
        DistributionSession.redistributionDistribution((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (List)dis, (Integer)userId, (int)typeDist, (Integer)canDestWithoutList, (String)messageForUser, (Integer)userType, (boolean)useCaseConf.getUseLdap());
    }
}
