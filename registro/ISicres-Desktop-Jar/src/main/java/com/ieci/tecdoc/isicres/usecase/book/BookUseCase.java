package com.ieci.tecdoc.isicres.usecase.book;

import com.ieci.tecdoc.common.compulsa.vo.ISicresCreateCompulsaVO;
import com.ieci.tecdoc.common.conf.UserConf;
import com.ieci.tecdoc.common.entity.dao.AbstractDBEntityDAO;
import com.ieci.tecdoc.common.exception.AttributesException;
import com.ieci.tecdoc.common.exception.BookException;
import com.ieci.tecdoc.common.exception.DistributionException;
import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.exception.SessionException;
import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.common.invesdoc.Idocarchdet;
import com.ieci.tecdoc.common.invesdoc.Idocfmt;
import com.ieci.tecdoc.common.invesicres.ScrCaaux;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.invesicres.ScrOrg;
import com.ieci.tecdoc.common.invesicres.ScrRegstate;
import com.ieci.tecdoc.common.isicres.AxDoch;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.common.isicres.AxSfIn;
import com.ieci.tecdoc.common.isicres.AxSfOut;
import com.ieci.tecdoc.common.isicres.AxSfQuery;
import com.ieci.tecdoc.common.isicres.AxSfQueryField;
import com.ieci.tecdoc.common.isicres.AxSfQueryResults;
import com.ieci.tecdoc.common.isicres.AxXf;
import com.ieci.tecdoc.common.isicres.FieldSearchAvanced;
import com.ieci.tecdoc.common.isicres.RowQuerySearchAdvanced;
import com.ieci.tecdoc.common.isicres.SaveOrigDocDataDocInput;
import com.ieci.tecdoc.common.isicres.SaveOrigDocDatasInput;
import com.ieci.tecdoc.common.isicres.SearchOperator;
import com.ieci.tecdoc.common.isicres.SessionInformation;
import com.ieci.tecdoc.common.isicres.ValidationResultCode;
import com.ieci.tecdoc.common.utils.Configurator;
import com.ieci.tecdoc.common.utils.ScrRegStateByLanguage;
import com.ieci.tecdoc.idoc.decoder.form.FCtrlDef;
import com.ieci.tecdoc.idoc.decoder.form.FDlgDef;
import com.ieci.tecdoc.idoc.decoder.form.FPageDef;
import com.ieci.tecdoc.idoc.decoder.form.FormFormat;
import com.ieci.tecdoc.idoc.decoder.query.QCtrlDef;
import com.ieci.tecdoc.idoc.decoder.query.QDlgDef;
import com.ieci.tecdoc.idoc.decoder.query.QSelectDef;
import com.ieci.tecdoc.idoc.decoder.query.QueryFormat;
import com.ieci.tecdoc.idoc.decoder.table.TColDef;
import com.ieci.tecdoc.idoc.decoder.table.TDlgDef;
import com.ieci.tecdoc.idoc.decoder.table.TableFormat;
import com.ieci.tecdoc.idoc.decoder.validation.idocarchdet.FFldDef;
import com.ieci.tecdoc.idoc.decoder.validation.idocarchdet.FieldFormat;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrField;
import com.ieci.tecdoc.isicres.audit.helper.ISicresAuditHelper;
import com.ieci.tecdoc.isicres.context.ISicresBeansProvider;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.session.attributes.AttributesSession;
import com.ieci.tecdoc.isicres.session.book.BookSession;
import com.ieci.tecdoc.isicres.session.distribution.DistributionSession;
import com.ieci.tecdoc.isicres.session.folder.FolderAsocSession;
import com.ieci.tecdoc.isicres.session.folder.FolderFileSession;
import com.ieci.tecdoc.isicres.session.folder.FolderHistSession;
import com.ieci.tecdoc.isicres.session.folder.FolderSession;
import com.ieci.tecdoc.isicres.session.security.SecuritySession;
import com.ieci.tecdoc.isicres.session.utils.UtilsSessionEx;
import com.ieci.tecdoc.isicres.session.validation.ValidationSessionEx;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.book.util.BookUseCaseAsocRegsUtil;
import com.ieci.tecdoc.isicres.usecase.book.xml.AsocRegsResults;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLAsocRegsFdr;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLAsocRegsResults;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLAsocRegsSearch;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLAsocRegsValidate;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLBook;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLBookTree;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLBooks;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLDistReg;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLFrmPersistFld;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLOrigDocFdr;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLQueryBook;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLTableBook;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLUpdHisReg;
import com.ieci.tecdoc.isicres.usecase.validationlist.ValidationListUseCase;
import com.ieci.tecdoc.isicres.usecase.validationlist.xml.XMLValidationCode;
import es.ieci.tecdoc.isicres.terceros.util.InteresadosDecorator;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class BookUseCase
implements Keys {
    private static Logger _logger = Logger.getLogger((Class)BookUseCase.class);
    private static final String PUNTO_COMA = ";";
    private static final String AMPERSAN = "#";
    private static final String BARRA = "|";
    private static final String AND = " && ";
    private static final String SPACES = " ";
    private static final String PORCIENTO = "%";
    private static final int MASK_PERM_QUERY = 1;
    private static final int MASK_PERM_CREATE = 2;
    private static final int MASK_PERM_MODIFY = 4;
    private static final String COMPARE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private ValidationListUseCase validationUseCase = new ValidationListUseCase();

    public Date getDBDateServer(UseCaseConf useCaseConf) throws ValidationException, BookException, SessionException {
        return BookSession.getDBDateServer((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId());
    }

    public void saveCompulFiles(ISicresCreateCompulsaVO compulsa) throws ValidationException, BookException, SessionException {
        String label = RBUtil.getInstance(compulsa.getLocale()).getProperty("label.compul.documents");
        FolderFileSession.saveCompulFiles((ISicresCreateCompulsaVO)compulsa, (String)label);
    }

    public AxSf getBookFolder(UseCaseConf useCaseConf, Integer bookID, int fdrid) throws ValidationException, BookException, SessionException {
        return FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookID, (int)fdrid, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
    }

    public boolean lockFolder(UseCaseConf useCaseConf, Integer bookID, int fdrid) throws ValidationException, BookException, SessionException {
        return FolderSession.lockFolder((String)useCaseConf.getSessionID(), (Integer)bookID, (int)fdrid, (String)useCaseConf.getEntidadId());
    }

    public Document getUserConfig(UseCaseConf useCaseConf, Integer bookId) throws ValidationException, BookException, SessionException, SecurityException {
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        Document doc = null;
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (boolean)false, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        try {
            doc = XMLBookTree.createXMLUserConfig(useCaseConf, bookId, usrConf, useCaseConf.getLocale());
        }
        catch (Exception e) {
            throw new BookException("bookexception.cannot_load_persist_fields");
        }
        return doc;
    }

    public Document getUserConfig(UseCaseConf useCaseConf, Integer bookId, boolean nuevoRegistro) throws ValidationException, BookException, SessionException, SecurityException {
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        Document doc = null;
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (boolean)false, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)nuevoRegistro);
        try {
            doc = XMLBookTree.createXMLUserConfig(useCaseConf, bookId, usrConf, useCaseConf.getLocale());
        }
        catch (Exception e) {
            throw new BookException("bookexception.cannot_load_persist_fields");
        }
        return doc;
    }

    public String saveUserConfig(UseCaseConf useCaseConf, Integer bookId, String unitCode, Integer unitType) throws ValidationException, BookException, SessionException, SecurityException {
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        String xml = null;
        Document doc = null;
        BookSession.saveUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (String)unitCode, (Integer)unitType, (AxSf)axsfQ, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (boolean)false, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        try {
            doc = XMLBookTree.createXMLUserConfig(useCaseConf, bookId, usrConf, useCaseConf.getLocale());
        }
        catch (Exception e) {
            throw new BookException("bookexception.cannot_load_persist_fields");
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(doc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.cannot_save_persist_fields");
        }
        xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public String saveUserConfig(UseCaseConf useCaseConf, Integer bookId, String fields, String params) throws ValidationException, BookException, SessionException, SecurityException {
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        String xml = null;
        Document doc = null;
        BookSession.saveUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (String)fields, (String)params, (AxSf)axsfQ, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (boolean)false, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        try {
            doc = XMLBookTree.createXMLUserConfig(useCaseConf, bookId, usrConf, useCaseConf.getLocale());
        }
        catch (Exception e) {
            throw new BookException("bookexception.cannot_load_persist_fields");
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(doc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.cannot_save_persist_fields");
        }
        xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public Document getBookTree(UseCaseConf useCaseConf, Integer bookId, boolean readOnly, long folderPId, int folderId, int row, String url, boolean openFolderDtr, Locale locale) throws ValidationException, BookException, SessionException, SecurityException {
        Idocarchdet idocarchdet = BookSession.getIdocarchdetMisc((String)useCaseConf.getSessionID(), (Integer)bookId);
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)folderId, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        axsf.setFormat(axsfQ.getFormat());
        ScrRegstate scrregstate = BookSession.getBook((String)useCaseConf.getSessionID(), (Integer)bookId);
        boolean permShowDocuments = SecuritySession.permisionShowDocuments((String)useCaseConf.getSessionID(), (AxSf)axsf);
        List docs = null;
        if (permShowDocuments || openFolderDtr) {
            docs = FolderFileSession.getBookFolderDocsWithPages((String)useCaseConf.getSessionID(), (Integer)bookId, (int)folderId, (String)useCaseConf.getEntidadId());
        }
        boolean isBookAdmin = SecuritySession.isBookAdmin((String)useCaseConf.getSessionID(), (Integer)bookId);
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (boolean)false, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        String archiveName = BookSession.getBookName((String)useCaseConf.getSessionID(), (Integer)bookId, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        int bookType = this.getTypeBook(axsf);
        if (!readOnly) {
            readOnly = !FolderSession.lockFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)folderId, (String)useCaseConf.getEntidadId());
            String estado = axsf.getAttributeValueAsString("fld6");
            readOnly = this.validateStateFolderIfClosedOrCancel(estado);
        }
        Document doc = XMLBookTree.createXMLBookTree(useCaseConf, bookId, scrregstate, axsf, idocarchdet, readOnly, folderPId, folderId, row, docs, url, bookType, isBookAdmin, locale, 1, sessionInformation, usrConf, archiveName);
        ISicresAuditHelper.auditarAccesoRegistro((String)useCaseConf.getSessionID(), (Integer)bookId, (int)folderId, (AxSf)axsf, (String)archiveName, (int)bookType);
        return doc;
    }

    public boolean validateStateFolderIfClosedOrCancel(String estado) {
        int numEstado;
        boolean result = false;
        if (StringUtils.isNotEmpty((String)estado) && ((numEstado = new Integer(estado).intValue()) == 5 || numEstado == 4)) {
            result = true;
        }
        return result;
    }

    public int getTypeBook(AxSf axsf) {
        int bookType = 0;
        if (axsf instanceof AxSfIn) {
            bookType = 1;
        } else if (axsf instanceof AxSfOut) {
            bookType = 2;
        }
        return bookType;
    }

    public String getDocName(UseCaseConf useCaseConf, Integer bookId, int folderId, String id) throws ValidationException, BookException, SessionException {
        List docs = FolderFileSession.getBookFolderDocsWithPages((String)useCaseConf.getSessionID(), (Integer)bookId, (int)folderId, (String)useCaseConf.getEntidadId());
        String docName = null;
        if (!(docs == null || docs.isEmpty())) {
            AxDoch axdoch = null;
            Iterator it = docs.iterator();
            while (it.hasNext() && docName == null) {
                axdoch = (AxDoch)it.next();
                if (!Integer.toString(axdoch.getId()).equals(id)) continue;
                docName = axdoch.getName();
            }
        }
        return docName;
    }

    public Document getEmptyBookTree(UseCaseConf useCaseConf, Integer bookId, boolean readOnly, long folderPId, int folderId, int row, String url, Locale locale, boolean nuevoRegistro) throws ValidationException, BookException, SessionException, SecurityException {
        Idocarchdet idocarchdet = BookSession.getIdocarchdetMisc((String)useCaseConf.getSessionID(), (Integer)bookId);
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        ScrRegstate scrregstate = BookSession.getBook((String)useCaseConf.getSessionID(), (Integer)bookId);
        boolean isBookAdmin = SecuritySession.isBookAdmin((String)useCaseConf.getSessionID(), (Integer)bookId);
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (boolean)false, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (boolean)nuevoRegistro);
        String archiveName = BookSession.getBookName((String)useCaseConf.getSessionID(), (Integer)bookId, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        int bookType = this.getTypeBook(axsfQ);
        Document doc = XMLBookTree.createXMLBookTree(useCaseConf, bookId, scrregstate, axsfQ, idocarchdet, readOnly, folderPId, folderId, row, null, url, bookType, isBookAdmin, locale, 0, sessionInformation, usrConf, archiveName);
        return doc;
    }

    public Document getBookFolderInitialPage(UseCaseConf useCaseConf, Integer bookId, int fldid, int page, boolean readOnly) throws ValidationException, AttributesException, SecurityException, BookException, SessionException, Exception {
        return this.getBookFolderPage(useCaseConf, bookId, fldid, 0, readOnly);
    }

    public Document getBookFolderInitialPageFromCopy(UseCaseConf useCaseConf, Integer bookId, int fldid, int page, boolean readOnly) throws ValidationException, AttributesException, SecurityException, BookException, SessionException, Exception {
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        Map extendedValues = new HashMap();
        String origen = null;
        String destino = null;
        String additionalInfo = null;
        int updatePer = 0;
        int addPer = 0;
        int updateProField = 0;
        int setRegisterDate = 0;
        AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        axsf.setFormat(axsfQ.getFormat());
        ArrayList<String> toRemove = new ArrayList<String>();

        for (Iterator it03 = axsf.getAttributesNames().iterator(); it03.hasNext();) {
            String key2 = (String) it03.next();
            try {
                if (Integer.parseInt(key2.substring(3, key2.length())) >= 7) continue;
                toRemove.add(key2);
            }
            catch (NumberFormatException e) {
                toRemove.add(key2);
            }
        }
        Iterator it = toRemove.iterator();
        while (it.hasNext()) {
            axsf.removeAttribute((String)it.next());
        }
        axsf.setFld5(null);
        extendedValues = this.getValidationFields(axsf, useCaseConf, bookId, page);
        if (!readOnly) {
            int[] perms = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookId);
            addPer = perms[0];
            updatePer = perms[1];
            updateProField = perms[4];
            setRegisterDate = perms[2];
        }
        origen = this.validationUseCase.getScrOrgName(useCaseConf, axsf.getFld7(), axsf.getFld7Name());
        destino = this.validationUseCase.getScrOrgName(useCaseConf, axsf.getFld8(), axsf.getFld8Name());
        ScrCaaux scr = null;
        if (axsf instanceof AxSfIn && axsf.getAttributeValueAsString("fld16") != null) {
            scr = UtilsSessionEx.getScrCaaux((String)useCaseConf.getSessionID(), (Integer)bookId, (String)axsf.getAttributeValueAsString("fld16"), (String)useCaseConf.getEntidadId());
        } else if (axsf instanceof AxSfOut && axsf.getAttributeValueAsString("fld12") != null) {
            scr = UtilsSessionEx.getScrCaaux((String)useCaseConf.getSessionID(), (Integer)bookId, (String)axsf.getAttributeValueAsString("fld12"), (String)useCaseConf.getEntidadId());
        }
        additionalInfo = "";
        if (scr != null) {
            additionalInfo = scr.getDatosAux();
        }
        axsf.setAttributeValue("fld9", (Object)this.getDest(useCaseConf, bookId, fldid));
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)bookId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        Document doc = XMLBook.createXMLBook(readOnly, updatePer, addPer, updateProField, setRegisterDate, axsf, fieldFormat, bookId, page, useCaseConf.getLocale(), extendedValues, origen, destino, additionalInfo, sessionInformation);
        return doc;
    }

    public Document getBookFolderPage(UseCaseConf useCaseConf, Integer bookId, int fldid, int page, boolean readOnly) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, Exception {
        int[] perms;
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        Map extendedValues = new HashMap();
        String origen = null;
        String destino = null;
        String additionalInfo = null;
        int updatePer = 0;
        int addPer = 0;
        int updateProField = 0;
        int updateRegisterDate = 0;
        AxSf axsf = null;
        if (fldid != -1) {
            axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            axsf.setFormat(axsfQ.getFormat());
            axsf.setLenFields(axsfQ.getLenFields());
            extendedValues = this.getValidationFields(axsf, useCaseConf, bookId, page);
            if (!readOnly) {
                boolean bl = readOnly = !FolderSession.lockFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)useCaseConf.getEntidadId());
            }
            if (!readOnly) {
                perms = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookId);
                addPer = perms[0];
                updatePer = perms[1];
                updateProField = perms[4];
                updateRegisterDate = perms[3];
            }
            origen = this.validationUseCase.getScrOrgName(useCaseConf, axsf.getFld7(), axsf.getFld7Name());
            destino = this.validationUseCase.getScrOrgName(useCaseConf, axsf.getFld8(), axsf.getFld8Name());
            ScrCaaux scr = null;
            if (axsf instanceof AxSfIn && axsf.getAttributeValueAsString("fld16") != null) {
                scr = UtilsSessionEx.getScrCaaux((String)useCaseConf.getSessionID(), (Integer)bookId, (String)axsf.getAttributeValueAsString("fld16"), (String)useCaseConf.getEntidadId());
            } else if (axsf instanceof AxSfOut && axsf.getAttributeValueAsString("fld12") != null) {
                scr = UtilsSessionEx.getScrCaaux((String)useCaseConf.getSessionID(), (Integer)bookId, (String)axsf.getAttributeValueAsString("fld12"), (String)useCaseConf.getEntidadId());
            }
            additionalInfo = "";
            if (scr != null) {
                additionalInfo = scr.getDatosAux();
            }
            axsf.setAttributeValue("fld9", (Object)this.getDest(useCaseConf, bookId, fldid));
        } else {
            axsf = axsfQ;
            perms = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookId);
            addPer = perms[0];
            updatePer = perms[1];
            updateProField = perms[4];
            updateRegisterDate = perms[2];
            extendedValues = this.getValidationFields(axsf, useCaseConf, bookId, page);
        }
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)bookId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        Document doc = XMLBook.createXMLBook(readOnly, updatePer, addPer, updateProField, updateRegisterDate, axsf, fieldFormat, bookId, page, useCaseConf.getLocale(), extendedValues, origen, destino, additionalInfo, sessionInformation);
        return doc;
    }

    public Document getBooks(UseCaseConf useCaseConf) throws ValidationException, BookException, SessionException, DistributionException {
        Integer alarmOnRejected;
        int sizeNewDist = 0;
        int sizeRejectedDist = 0;
        Integer alarmOnNewDist = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getAlarmOnNewDist());
        if (alarmOnNewDist != 0) {
            sizeNewDist = DistributionSession.getNewFolderDistByDeptId((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId());
        }
        if ((alarmOnRejected = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getAlarmOnRejected())) != 0) {
            sizeRejectedDist = DistributionSession.getRejectedFolderDistByDeptId((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId());
        }
        List inList = BookSession.getInBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        List outList = BookSession.getOutBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        Document doc = XMLBooks.createXMLBooks(inList, outList, useCaseConf.getLocale(), sizeNewDist, sizeRejectedDist, sessionInformation);
        return doc;
    }

    public Map translateQueryParams(UseCaseConf useCaseConf, Integer bookId, Map params) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        HashMap<Integer, Object> idsToValidate = new HashMap<Integer, Object>();
        HashMap<Integer, Integer> controlsMemo = new HashMap<Integer, Integer>();
        Map result = new HashMap();
        AxSf axsfQ = this.getQueryFormat(useCaseConf.getSessionID(), bookId, useCaseConf.getEntidadId());
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        Collection formatterFields = formatter.getDlgDef().getCtrldefs().values();
        int fldid = 0;
        int id = 0;
        for (Iterator it03 = formatterFields.iterator(); it03.hasNext();) {
            QCtrlDef ctrlDef2 = (QCtrlDef) it03.next();
            fldid = ctrlDef2.getFldId();
            id = ctrlDef2.getId();
            if (!ctrlDef2.getName().startsWith("IDOC_EDIT") || params.get(Integer.toString(id)) == null || params.get(Integer.toString(id)).toString().length() <= 0) continue;
            try {
                AxSfQueryField field = new AxSfQueryField();
                field.setFldId("fld" + ctrlDef2.getFldId());
                field.setValue(this.getValue(axsfQ, field, params, ctrlDef2, null, useCaseConf.getLocale()));
                field.setOperator(this.translateOperator(this.getValue(params, Integer.toString(ctrlDef2.getRelCtrlId())), useCaseConf.getLocale()));
                if (fldid == 5) {
                    idsToValidate.put(new Integer(fldid), field.getValue());
                    controlsMemo.put(new Integer(fldid), new Integer(id));
                }
                if (!(fldid != 7 && fldid != 8 || field.getOperator().equals("dde"))) {
                    idsToValidate.put(new Integer(fldid), field.getValue());
                    controlsMemo.put(new Integer(fldid), new Integer(id));
                }
                if (axsfQ instanceof AxSfIn) {
                    if (fldid != 13 && fldid != 16) continue;
                    idsToValidate.put(new Integer(fldid), field.getValue());
                    controlsMemo.put(new Integer(fldid), new Integer(id));
                    continue;
                }
                if (fldid == 12) {
                    idsToValidate.put(new Integer(fldid), field.getValue());
                }
                controlsMemo.put(new Integer(fldid), new Integer(id));
            }
            catch (Exception e) {}
        }
        if (!idsToValidate.isEmpty()) {
            result = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idsToValidate, (String)useCaseConf.getEntidadId());
        }
        return result;
    }

    public String validateQueryParam(UseCaseConf useCaseConf, Integer bookId, int fldId, String code) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        ValidationResultCode validationResultCode = AttributesSession.validateAndReturnValidationCode((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldId, (String)code, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        Document xmlDocument = XMLValidationCode.createXMLValidationCode(validationResultCode, useCaseConf.getLocale());
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

    public List validateQueryParams(UseCaseConf useCaseConf, Integer bookId, Map params) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        ArrayList<Integer> result = new ArrayList<Integer>();
        HashMap<Integer, Object> idsToValidate = new HashMap<Integer, Object>();
        HashMap<Integer, Integer> controlsMemo = new HashMap<Integer, Integer>();
        Locale locale = useCaseConf.getLocale();
        if (locale == null) {
            locale = new Locale("es");
        }
        AxSf axsfQ = this.getQueryFormat(useCaseConf.getSessionID(), bookId, useCaseConf.getEntidadId());
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        Collection formatterFields = formatter.getDlgDef().getCtrldefs().values();
        int fldid = 0;
        int id = 0;
        for (Iterator it03 = formatterFields.iterator(); it03.hasNext();) {
            QCtrlDef ctrlDef2 = (QCtrlDef) it03.next();
            String value;
            fldid = ctrlDef2.getFldId();
            id = ctrlDef2.getId();
            if (!ctrlDef2.getName().startsWith("IDOC_EDIT") || (value = this.getValue(params, Integer.toString(id))) == null || value.toString().length() <= 0) continue;
            try {
                AxSfQueryField field = new AxSfQueryField();
                field.setFldId("fld" + ctrlDef2.getFldId());
                field.setValue(this.getValue(axsfQ, field, params, ctrlDef2, null, useCaseConf.getLocale()));
                field.setOperator(this.translateOperator(this.getValue(params, Integer.toString(ctrlDef2.getRelCtrlId())), useCaseConf.getLocale()));
                if (field.getValue().getClass().equals(ArrayList.class) && ((List)field.getValue()).size() != 2 && !field.getOperator().equals("|")) {
                    result.add(new Integer(id));
                }
                if ((fldid == 2 || fldid == 4) && field.getValue().getClass().equals(Date.class)) {
                    String auxDate = this.getValue(params, Integer.toString(id)).toString();
                    SimpleDateFormat shortFormatter = new SimpleDateFormat(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("date.shortFormat"));
                    shortFormatter.setLenient(false);
                    try {
                        if (auxDate.length() > 10) {
                            result.add(new Integer(id));
                        }
                        shortFormatter.parse(auxDate);
                        if (shortFormatter.getCalendar().get(1) < 1970) {
                            result.add(new Integer(id));
                        }
                    }
                    catch (Exception e) {
                        result.add(new Integer(id));
                    }
                }
                if (!(fldid != 5 && fldid != 7 && fldid != 8 || field.getValue().getClass().equals(List.class))) {
                    idsToValidate.put(new Integer(fldid), field.getValue());
                    controlsMemo.put(new Integer(fldid), new Integer(id));
                }
                if (axsfQ instanceof AxSfIn) {
                    if (!(fldid != 13 && fldid != 16 || field.getValue().getClass().equals(List.class))) {
                        idsToValidate.put(new Integer(fldid), field.getValue());
                        controlsMemo.put(new Integer(fldid), new Integer(id));
                    }
                    if (fldid <= 17 || AttributesSession.getExtendedValidationFieldValue((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)field.getValue().toString(), (String)locale.getLanguage(), (String)useCaseConf.getEntidadId()) != null) continue;
                    result.add(new Integer(id));
                    continue;
                }
                if (!(fldid != 12 || field.getValue().getClass().equals(List.class))) {
                    idsToValidate.put(new Integer(fldid), field.getValue());
                    controlsMemo.put(new Integer(fldid), new Integer(id));
                }
                if (fldid <= 13 || AttributesSession.getExtendedValidationFieldValue((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)field.getValue().toString(), (String)locale.getLanguage(), (String)useCaseConf.getEntidadId()) != null) continue;
                result.add(new Integer(id));
            }
            catch (Exception e) {
                result.add(new Integer(id));
            }
        }
        if (!idsToValidate.isEmpty()) {
            List aux = AttributesSession.validateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idsToValidate, (boolean)false, (String)useCaseConf.getEntidadId());
            for (Iterator it03 = aux.iterator(); it03.hasNext();) {
            	Integer auxFldid2 = (Integer) it03.next();
                result.add((Integer)controlsMemo.get(auxFldid2));
            }
        }
        return result;
    }

    public List validateTableTextQueryParams(UseCaseConf useCaseConf, Integer bookId, Map params, List badCtrls) throws ValidationException, BookException, AttributesException, SessionException, SecurityException, ParseException {
        AxSf axsfQ = BookSession.getQueryFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        Collection formatterFields = formatter.getDlgDef().getCtrldefs().values();
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Iterator it04 = formatterFields.iterator(); it04.hasNext();) {
            QCtrlDef ctrlDef = (QCtrlDef) it04.next();
            int fldid = ctrlDef.getFldId();
            int id = ctrlDef.getId();
            if (!badCtrls.contains(new Integer(id))) continue;
            String value = this.getValue(params, Integer.toString(id));
            if (!ctrlDef.getName().startsWith("IDOC_EDIT") || value == null || value.toString().length() <= 0) continue;
            try {
                AxSfQueryField field = new AxSfQueryField();
                field.setFldId("fld" + ctrlDef.getFldId());
                field.setValue(this.getValue(axsfQ, field, params, ctrlDef, null, useCaseConf.getLocale()));
                field.setOperator(this.translateOperator(this.getValue(params, Integer.toString(ctrlDef.getRelCtrlId())), useCaseConf.getLocale()));
                if (axsfQ instanceof AxSfIn) {
                    if (fldid <= 17 || !AttributesSession.getExtendedValidationFieldValueForQuery((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)field.getOperator(), (String)useCaseConf.getEntidadId())) continue;
                    result.add(new Integer(id));
                    continue;
                }
                if (fldid <= 13 || !AttributesSession.getExtendedValidationFieldValueForQuery((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)field.getOperator(), (String)useCaseConf.getEntidadId())) continue;
                result.add(new Integer(id));
            }
            catch (Exception e) {
                if (badCtrls.contains(new Integer(id))) continue;
                badCtrls.add(new Integer(id));
            }
        }
        if (!result.isEmpty()) {
            block3 : for (Integer idResult : result) {
                Iterator iter = badCtrls.iterator();
                while (iter.hasNext()) {
                    Integer idBadCtrls = (Integer)iter.next();
                    if (idBadCtrls.intValue() != idResult.intValue()) continue;
                    iter.remove();
                    continue block3;
                }
            }
        }
        return badCtrls;
    }

    public void validateOfficeCode(UseCaseConf useCaseConf, String code) throws SessionException, ValidationException {
        ValidationSessionEx.validateOfficeCode((String)useCaseConf.getSessionID(), (String)code, (String)useCaseConf.getEntidadId());
    }

    public int openTableResults(UseCaseConf useCaseConf, Integer bookId, Map params, Map translations, Integer reportOption, String listOrder) throws ValidationException, BookException, SessionException, SecurityException {
        AxSf axsfQ = BookSession.getQueryFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        AxSfQuery axsfQuery = new AxSfQuery();
        axsfQuery.setOrderBy(listOrder);
        axsfQuery.setBookId(bookId);
        axsfQuery.setPageSize(Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageTableResultsSize")));
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        Collection formatterFields = formatter.getDlgDef().getCtrldefs().values();
        for (Iterator it04 = formatterFields.iterator(); it04.hasNext();) {
            QCtrlDef ctrlDef2 = (QCtrlDef) it04.next();
            String value;
            if (!ctrlDef2.getName().startsWith("IDOC_EDIT") || (value = this.getValue(params, Integer.toString(ctrlDef2.getId()))) == null || value.toString().length() <= 0) continue;
            try {
                AxSfQueryField field = new AxSfQueryField();
                field.setFldId("fld" + ctrlDef2.getFldId());
                field.setValue(this.getValue(axsfQ, field, params, ctrlDef2, translations, useCaseConf.getLocale()));
                field.setOperator(this.translateOperator(this.getValue(params, Integer.toString(ctrlDef2.getRelCtrlId())), useCaseConf.getLocale()));
                axsfQuery.addField(field);
            }
            catch (ParseException e) {
                _logger.fatal((Object)"Error al parsear", (Throwable)e);
            }
        }
        if (formatter.getSelectDef().getType() == 3) {
            axsfQuery.setSelectDefWhere2(formatter.getSelectDef().getWhere2());
        }
        ArrayList<Integer> bookIds = new ArrayList<Integer>();
        if (reportOption != 0) {
            if (BookSession.isInBook((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId())) {
                List<ScrRegStateByLanguage> inList = BookSession.getInBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
                for (ScrRegStateByLanguage scrregstate : inList) {
                    if (reportOption != 2 && scrregstate.getScrregstateState() != 0) continue;
                    bookIds.add(scrregstate.getIdocarchhdrId());
                }
            } else {
                List<ScrRegStateByLanguage> outList = BookSession.getOutBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
                for (ScrRegStateByLanguage scrregstate : outList) {
                    if (reportOption != 2 && scrregstate.getScrregstateState() != 0) continue;
                    bookIds.add(scrregstate.getIdocarchhdrId());
                }
            }
        }
        if (axsfQuery.getPageSize() <= 0) {
            throw new BookException("bookexception.error_page_size");
        }
        int size = FolderSession.openRegistersQuery((String)useCaseConf.getSessionID(), (AxSfQuery)axsfQuery, bookIds, (Integer)reportOption, (String)useCaseConf.getEntidadId());
        return size;
    }

    public void closeTableResults(UseCaseConf useCaseConf, Integer bookId) throws ValidationException, BookException, SessionException, SecurityException {
        BookSession.closeBook((String)useCaseConf.getSessionID(), (Integer)bookId);
    }

    public Document getLastRegisterForUser(UseCaseConf useCaseConf, Integer bookID) throws AttributesException, ValidationException, BookException, SessionException, SecurityException {
        AxSf axsf = BookSession.getTableFormat((String)useCaseConf.getSessionID(), (Integer)bookID, (String)useCaseConf.getEntidadId());
        Integer autoDist = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getAutoDist());
        Integer[] distPerms = SecuritySession.getScrDistPermission((String)useCaseConf.getSessionID());
        boolean distributionManualBookOut = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-Server/Distribution/DistributionManualBookOut");
        if (axsf instanceof AxSfOut && !distributionManualBookOut) {
            distPerms[5] = new Integer(0);
        }
        int perm = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookID)[4];
        boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)bookID);
        boolean canOpenReg = SecuritySession.canOpenCloseReg((String)useCaseConf.getSessionID());
        boolean canOperationIR = SecuritySession.canOperationIR((String)useCaseConf.getSessionID());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        AxSfQueryResults queryResults = FolderSession.getLastRegisterForUser((String)useCaseConf.getSessionID(), (Integer)bookID, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        if (queryResults.getTotalQuerySize() > 0) {
            Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)bookID);
            FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
            this.assignValidationFields(axsf, queryResults, useCaseConf, bookID);
            return XMLTableBook.createXMLTable(axsf, queryResults, useCaseConf.getLocale(), autoDist, distPerms[5], canModify && perm == 1, canOpenReg, canOperationIR, sessionInformation.getCaseSensitive(), null, fieldFormat);
        }
        return null;
    }

    public int getFrmNavigateFolderResults(UseCaseConf useCaseConf, Integer archiveId, Integer index) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        AxSfQueryResults queryResults = FolderSession.navigateToRowRegistersQuery((String)useCaseConf.getSessionID(), (Integer)archiveId, (int)index, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (String)null);
        AxSf axsf = null;
        axsf = (AxSf)queryResults.getResults().iterator().next();
        return Integer.parseInt(axsf.getAttributeValue("fdrid").toString());
    }

    public Document getTableResults(UseCaseConf useCaseConf, Integer archiveId, String navigationType, String orderByTable) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        AxSf axsf = BookSession.getTableFormat((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        Integer autoDist = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getAutoDist());
        Integer[] distPerms = SecuritySession.getScrDistPermission((String)useCaseConf.getSessionID());
        boolean distributionManualBookOut = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-Server/Distribution/DistributionManualBookOut");
        if (axsf instanceof AxSfOut && !distributionManualBookOut) {
            distPerms[5] = new Integer(0);
        }
        int perm = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)archiveId)[4];
        boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean canOpenReg = SecuritySession.canOpenCloseReg((String)useCaseConf.getSessionID());
        boolean canOperationIR = SecuritySession.canOperationIR((String)useCaseConf.getSessionID());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        AxSfQueryResults queryResults = FolderSession.navigateRegistersQuery((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)navigationType, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (String)orderByTable);
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)archiveId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        this.assignValidationFields(axsf, queryResults, useCaseConf, archiveId);
        return XMLTableBook.createXMLTable(axsf, queryResults, useCaseConf.getLocale(), autoDist, distPerms[5], canModify && perm == 1, canOpenReg, canOperationIR, sessionInformation.getCaseSensitive(), orderByTable, fieldFormat);
    }

    public Document getTableResultsForRow(UseCaseConf useCaseConf, Integer archiveId, int row, String orderByTable) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        AxSf axsf = BookSession.getTableFormat((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        Integer autoDist = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getAutoDist());
        Integer[] distPerms = SecuritySession.getScrDistPermission((String)useCaseConf.getSessionID());
        if (axsf instanceof AxSfOut) {
            distPerms[5] = new Integer(0);
        }
        int perm = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)archiveId)[4];
        boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean canOpenReg = SecuritySession.canOpenCloseReg((String)useCaseConf.getSessionID());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        AxSfQueryResults queryResults = FolderSession.navigateToRowRegistersQuery((String)useCaseConf.getSessionID(), (Integer)archiveId, (int)row, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (String)orderByTable);
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)archiveId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        this.assignValidationFields(axsf, queryResults, useCaseConf, archiveId);
        return XMLTableBook.createXMLTable(axsf, queryResults, useCaseConf.getLocale(), autoDist, distPerms[5], canModify && perm == 1, canOpenReg, sessionInformation.getCaseSensitive(), orderByTable, fieldFormat);
    }

    public Document getQueryFormatReports(UseCaseConf useCaseConf, Integer archiveId, long archivePId, long fdrqrypid, int opcion) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        this.openBook(useCaseConf, archiveId);
        Document doc = null;
        int perms = 0;
        String dataBaseType = null;
        AxSf axsf = BookSession.getQueryFormat((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)archiveId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        String archiveName = BookSession.getBookName((String)useCaseConf.getSessionID(), (Integer)archiveId, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)archiveId, (AxSf)axsf, (boolean)true, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        try {
            dataBaseType = BookSession.getDataBaseType((String)useCaseConf.getSessionID());
        }
        catch (Exception e) {
            // empty catch block
        }
        ArrayList<Integer> bookIds = new ArrayList<Integer>();
        if (axsf instanceof AxSfIn) {
            List<ScrRegStateByLanguage> inList = BookSession.getInBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            for (ScrRegStateByLanguage scrRegStateByLanguage : inList) {
                if (opcion != 2 && scrRegStateByLanguage.getScrregstateState() != 0) continue;
                bookIds.add(scrRegStateByLanguage.getIdocarchhdrId());
            }
        } else {
            List<ScrRegStateByLanguage> outList = BookSession.getOutBooks((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            for (ScrRegStateByLanguage scrRegStateByLanguage : outList) {
                if (opcion != 2 && scrRegStateByLanguage.getScrregstateState() != 0) continue;
                bookIds.add(scrRegStateByLanguage.getIdocarchhdrId());
            }
        }
        Map fieldsNotEqual = BookSession.compareBooks((String)useCaseConf.getSessionID(), bookIds, (Integer)archiveId, (String)useCaseConf.getEntidadId());
        perms = this.getPermisosLibro(useCaseConf, archiveId);
        boolean isBookAdmin = SecuritySession.isBookAdmin((String)useCaseConf.getSessionID(), (Integer)archiveId);
        List validationFields = AttributesSession.getExtendedValidationFieldsForBook((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        doc = XMLQueryBook.createXMLQueryFormat(axsf, fieldFormat, archiveId, archivePId, fdrqrypid, archiveName, useCaseConf.getLocale(), isBookAdmin, perms, validationFields, fieldsNotEqual, 0, usrConf, dataBaseType, sessionInformation);
        return doc;
    }

    public int getPermisosLibro(UseCaseConf useCaseConf, Integer archiveId) throws ValidationException, SecurityException {
        int perms = 0;
        boolean CanQuery = SecuritySession.canQuery((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean CanCreate = SecuritySession.canCreate((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)archiveId);
        perms+=CanQuery ? 1 : 0;
        perms+=CanCreate ? 2 : 0;
        return perms+=canModify ? 4 : 0;
    }

    public Document getQueryFormat(UseCaseConf useCaseConf, Integer archiveId, long archivePId, long fdrqrypid) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        this.openBook(useCaseConf, archiveId);
        int sizeIncompletRegiters = 0;
        String dataBaseType = null;
        Integer alarmOnIncompletRegiters = new Integer(BookSession.invesicresConf((String)useCaseConf.getEntidadId()).getAlarmOnIncompleteReg());
        if (alarmOnIncompletRegiters != 0) {
            sizeIncompletRegiters = FolderSession.getIncompletRegisterSize((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        }
        try {
            dataBaseType = BookSession.getDataBaseType((String)useCaseConf.getSessionID());
        }
        catch (Exception e) {
            // empty catch block
        }
        Document doc = null;
        int perms = 0;
        AxSf axsf = BookSession.getQueryFormat((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)archiveId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        boolean CanQuery = SecuritySession.canQuery((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean CanCreate = SecuritySession.canCreate((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)archiveId);
        boolean isBookAdmin = SecuritySession.isBookAdmin((String)useCaseConf.getSessionID(), (Integer)archiveId);
        List validationFields = AttributesSession.getExtendedValidationFieldsForBook((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        String archiveName = BookSession.getBookName((String)useCaseConf.getSessionID(), (Integer)archiveId, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        UserConf usrConf = BookSession.getUserConfig((String)useCaseConf.getSessionID(), (Integer)archiveId, (AxSf)axsf, (boolean)true, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        perms+=CanQuery ? 1 : 0;
        perms+=CanCreate ? 2 : 0;
        doc = XMLQueryBook.createXMLQueryFormat(axsf, fieldFormat, archiveId, archivePId, fdrqrypid, archiveName, useCaseConf.getLocale(), isBookAdmin, perms+=canModify ? 4 : 0, validationFields, null, sizeIncompletRegiters, usrConf, dataBaseType, sessionInformation);
        return doc;
    }

    public void openBook(UseCaseConf useCaseConf, Integer bookID) throws ValidationException, BookException, SessionException {
        BookSession.openBook((String)useCaseConf.getSessionID(), (Integer)bookID, (String)useCaseConf.getEntidadId());
    }

    public void closeBook(UseCaseConf useCaseConf, Integer bookID) throws ValidationException, BookException, SessionException {
        BookSession.closeBook((String)useCaseConf.getSessionID(), (Integer)bookID);
    }

    public void closeFolder(UseCaseConf useCaseConf, Integer bookID, int fdrid) throws ValidationException, BookException, SessionException {
        FolderSession.closeFolder((String)useCaseConf.getSessionID(), (Integer)bookID, (int)fdrid, (String)useCaseConf.getEntidadId());
    }

    public Document getDtrFdrResults(UseCaseConf useCaseConf, Integer bookID, int folderID) throws BookException, SessionException, ValidationException {
        List list = DistributionSession.getDtrFdrResults((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap());
        return XMLDistReg.createXMLDistReg(list, bookID, folderID, useCaseConf.getLocale());
    }

    public Document getDtrFdrResultsWithRemarkDistribution(UseCaseConf useCaseConf, Integer bookID, int folderID) throws BookException, SessionException, ValidationException {
        List list = DistributionSession.getDtrFdrResults((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (String)useCaseConf.getEntidadId(), (boolean)useCaseConf.getUseLdap());
        String bookName = BookSession.getBookName((String)useCaseConf.getSessionID(), (Integer)bookID, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        String numReg = axsf.getAttributeValueAsString("fld1");
        return XMLDistReg.createXMLDistRegWithRemarkDistribution(list, bookID, folderID, useCaseConf.getLocale(), bookName, numReg);
    }

    public Document getUpdHisFdrResults(UseCaseConf useCaseConf, Integer bookID, int folderId, AxSf axsf, String num_reg) throws BookException, SessionException, ValidationException {
        List list = FolderHistSession.getUpdHisFdrResults((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (Integer)bookID, (int)folderId, (AxSf)axsf, (String)num_reg, (String)useCaseConf.getEntidadId());
        return XMLUpdHisReg.createXMLUpdHisReg(list, bookID, folderId, num_reg, useCaseConf.getLocale());
    }

    public Document getOrigDocFdrBad(UseCaseConf useCaseConf, int openType, Integer bookID, int folderID, List datas) throws BookException, SessionException, SecurityException, ValidationException {
        int canAdd = 0;
        int canDel = 0;
        if (openType == 1) {
            canAdd = 1;
            canDel = 1;
        } else {
            boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)bookID);
            boolean IsBookAdmin = SecuritySession.isBookAdmin((String)useCaseConf.getSessionID(), (Integer)bookID);
            int[] perms = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookID);
            canAdd = perms[4] == 1 && canModify ? 1 : 0;
            canDel = IsBookAdmin ? 1 : 0;
        }
        List list = datas;
        AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        int type = 1;
        if (axsf instanceof AxSfOut) {
            type = 2;
        }
        return XMLOrigDocFdr.createXMLOrigDocFdr(list, type, canAdd, canDel, axsf, useCaseConf.getLocale(), true);
    }

    public Document getOrigDocFdr(UseCaseConf useCaseConf, int openType, Integer bookID, int folderID) throws BookException, SessionException, SecurityException, ValidationException {
        int canAdd = 0;
        int canDel = 0;
        if (openType == 1) {
            canAdd = 1;
            canDel = 1;
        } else {
            boolean canModify = SecuritySession.canModify((String)useCaseConf.getSessionID(), (Integer)bookID);
            boolean IsBookAdmin = SecuritySession.isBookAdmin((String)useCaseConf.getSessionID(), (Integer)bookID);
            int[] perms = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookID);
            canAdd = perms[4] == 1 && canModify ? 1 : 0;
            canDel = IsBookAdmin ? 1 : 0;
        }
        List list = FolderFileSession.getOrigDocFdr((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (String)useCaseConf.getEntidadId());
        AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        int type = 1;
        if (axsf instanceof AxSfOut) {
            type = 2;
        }
        return XMLOrigDocFdr.createXMLOrigDocFdr(list, type, canAdd, canDel, axsf, useCaseConf.getLocale(), false);
    }

    public Document getAsocRegsFdr(UseCaseConf useCaseConf, Integer bookID, int folderID) throws BookException, SessionException, SecurityException, ValidationException {
        Object[] result = FolderAsocSession.getAsocRegsFdr((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        Map idocs = (Map)result[0];
        Map axsfs = (Map)result[1];
        Map axsfPrim = (Map)result[2];
        return XMLAsocRegsFdr.createXMLAsocRegsFdr(idocs, axsfs, axsfPrim, useCaseConf.getLocale());
    }

    public Document getAsocRegsSearch(UseCaseConf useCaseConf) throws Exception {
        String dataBaseType = BookSession.getDataBaseType((String)useCaseConf.getSessionID());
        SessionInformation sessionInformation = BookSession.getSessionInformation((String)useCaseConf.getSessionID(), (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        return XMLAsocRegsSearch.getXMLAsocRegsSearchList(useCaseConf.getLocale(), dataBaseType, sessionInformation.getCaseSensitive());
    }

    public String getValidateAsocRegsSearch(UseCaseConf useCaseConf, String regWhere, Integer inicio, String registerBook, Integer folderId, Integer archiveId) throws Exception {
        Integer bookIdSelect = new Integer(registerBook);
        String dataBaseType = BookSession.getDataBaseType((String)useCaseConf.getSessionID());
        BookSession.openBook((String)useCaseConf.getSessionID(), (Integer)bookIdSelect, (String)useCaseConf.getEntidadId());
        AxSf axsfQ = BookSession.getQueryFormat((String)useCaseConf.getSessionID(), (Integer)bookIdSelect, (String)useCaseConf.getEntidadId());
        String orderBy = new String("FLD1 ASC");
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        AxSfQuery axSfQuery = BookUseCaseAsocRegsUtil.getAsocRegsSearchCriteria(useCaseConf, regWhere, useCaseConf.getLocale(), dataBaseType, bookIdSelect, formatter);
        if (axSfQuery.getPageSize() <= 0) {
            throw new BookException("bookexception.error_page_size");
        }
        int size = FolderSession.openRegistersQuery((String)useCaseConf.getSessionID(), (AxSfQuery)axSfQuery, (List)null, (Integer)new Integer(0), (String)useCaseConf.getEntidadId());
        AxSfQueryResults queryResults = null;
        boolean isRegCurrentAsoc = false;
        if (size > 0) {
            queryResults = FolderSession.navigateRegistersQuery((String)useCaseConf.getSessionID(), (Integer)bookIdSelect, (String)"QueryAll", (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId(), (String)orderBy);
            if (BookUseCaseAsocRegsUtil.isAsocReg(useCaseConf, folderId, archiveId)) {
                isRegCurrentAsoc = true;
                queryResults = BookUseCaseAsocRegsUtil.filterRegsResultsByAsocRegs(queryResults, useCaseConf, bookIdSelect);
            }
        }
        List listaRegs = null;
        if (queryResults != null) {
            listaRegs = BookUseCaseAsocRegsUtil.getAsocRegsResults(queryResults.getResults(), queryResults.getBookId(), useCaseConf.getLocale());
            if (!isRegCurrentAsoc) {
                listaRegs = BookUseCaseAsocRegsUtil.filterRegsResultByCurrent(listaRegs, archiveId, folderId);
            }
        }
        Document doc = XMLAsocRegsResults.createXMLAsocRegsResult(listaRegs, inicio, size);
        Document xmlDoc = XMLAsocRegsResults.createXMLValidateAsocRegsResult(doc, "SearchAsocRegs", inicio, size, useCaseConf.getLocale(), registerBook, regWhere);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(xmlDoc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.asocreg.validated.search");
        }
        String xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public String getValidatedAsocRegsBook(UseCaseConf useCaseConf, String bookIdString) throws Exception {
        String code = "10";
        try {
            Integer bookId = new Integer(bookIdString);
            if (!BookUseCaseAsocRegsUtil.isBookOpen(useCaseConf, bookId)) {
                code = "5";
            }
        }
        catch (Exception ex) {
            code = "5";
        }
        Document doc = XMLAsocRegsValidate.createXMLAsocRegsValidate(code, "");
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(doc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.asocreg.validated.selected");
        }
        String xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public String getValidateAsocRegsSelected(UseCaseConf useCaseConf, String asocRegsSelected, Integer folderId, Integer archiveId) throws BookException, SessionException, ValidationException {
        String code = "-1";
        if (BookUseCaseAsocRegsUtil.isAsocReg(useCaseConf, folderId, archiveId)) {
            code = "0";
        } else {
            String codeAux = BookUseCaseAsocRegsUtil.getAsocRegsSelectedCode(asocRegsSelected, useCaseConf);
            if (codeAux != null && codeAux.length() > 0) {
                code = codeAux;
            }
        }
        Document doc = XMLAsocRegsValidate.createXMLAsocRegsValidate(code, asocRegsSelected);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(doc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.asocreg.validated.selected");
        }
        String xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public String getPrimaryAsocRegsSelected(UseCaseConf useCaseConf, String asocRegsSelected) throws BookException {
        List listaRegsSelected = BookUseCaseAsocRegsUtil.getAsocRegsResults(asocRegsSelected, useCaseConf.getLocale());
        Document doc = XMLAsocRegsResults.createXMLAsocRegsResult(listaRegsSelected, 0, 15);
        Document xmlDoc = XMLAsocRegsResults.createXMLValidateAsocRegsResult(doc, "SearchAsocRegs", 0, 15, useCaseConf.getLocale(), " ", " ");
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(xmlDoc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.asocreg.validated.search");
        }
        String xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public String saveAsocRegs(UseCaseConf useCaseConf, String asocRegsSelected, String primaryReg, int code, Integer bookId, Integer folderId) throws BookException {
        Document doc = null;
        String resultCode = "10";
        List listaRegs;
        List listaRegs2;
        switch (code) {
            case 0: {
                try {
                    Integer[] primario = BookUseCaseAsocRegsUtil.getAsocRegPrimario(useCaseConf, bookId, folderId);
                    if (primario == null || (listaRegs2 = BookUseCaseAsocRegsUtil.getAsocRegsResults(asocRegsSelected, useCaseConf.getLocale())) == null || listaRegs2.isEmpty()) break;
                    BookUseCaseAsocRegsUtil.saveAsocRegsSec(useCaseConf, listaRegs2, primario[0], primario[1]);
                }
                catch (Exception e) {
                    resultCode = "-1";
                }
                break;
            }
            case 1: {
                try {
                    Integer[] primario;
                    listaRegs = BookUseCaseAsocRegsUtil.getAsocRegsResults(asocRegsSelected, useCaseConf.getLocale());
                    if (listaRegs == null || listaRegs.isEmpty() || (primario = BookUseCaseAsocRegsUtil.getAsocRegPrimario(useCaseConf, listaRegs)) == null) break;
                    BookUseCaseAsocRegsUtil.saveAsocRegsSec(useCaseConf, primario[0], primario[1], bookId, folderId);
                }
                catch (Exception e) {
                    resultCode = "-1";
                }
                break;
            }
            case 2: {
                try {
                    AsocRegsResults regCurrent = new AsocRegsResults(bookId, folderId, "", "", "");
                    listaRegs2 = BookUseCaseAsocRegsUtil.getAsocRegsResults(asocRegsSelected, useCaseConf.getLocale());
                    if (listaRegs2 == null) {
                        listaRegs2 = new ArrayList();
                    }
                    if (listaRegs2.isEmpty()) break;
                    Integer[] primario = BookUseCaseAsocRegsUtil.getAsocRegPrimario(useCaseConf, listaRegs2);
                    listaRegs2 = BookUseCaseAsocRegsUtil.getNoAsocRegsResults(useCaseConf, listaRegs2);
                    listaRegs2.add(regCurrent);
                    if (primario == null) break;
                    BookUseCaseAsocRegsUtil.saveAsocRegsSec(useCaseConf, listaRegs2, primario[0], primario[1]);
                }
                catch (Exception e) {
                    resultCode = "-1";
                }
                break;
            }
            case 3: {
                try {
                    listaRegs = BookUseCaseAsocRegsUtil.getAsocRegsResults(asocRegsSelected, useCaseConf.getLocale());
                    if (listaRegs == null || listaRegs.isEmpty()) break;
                    AsocRegsResults asocRegsResults = (AsocRegsResults)listaRegs.get(0);
                    BookUseCaseAsocRegsUtil.saveAsocRegsSec(useCaseConf, asocRegsResults.getBookId(), asocRegsResults.getFolderId(), bookId, folderId);
                }
                catch (Exception e) {
                    resultCode = "-1";
                }
                break;
            }
            case 4: {
                try {
                    List<AsocRegsResults> listaPrimaryRegs = BookUseCaseAsocRegsUtil.getAsocRegsResults(primaryReg, useCaseConf.getLocale());
                    if (listaPrimaryRegs == null || listaPrimaryRegs.isEmpty()) break;
                    AsocRegsResults asocRegsResultsPrimary = (AsocRegsResults)listaPrimaryRegs.get(0);
                    List<AsocRegsResults> listaRegs3 = BookUseCaseAsocRegsUtil.getAsocRegsResults(asocRegsSelected, useCaseConf.getLocale());
                    if ((listaRegs3 = BookUseCaseAsocRegsUtil.filterRegsResultByCurrent(listaRegs3, asocRegsResultsPrimary.getBookId(), asocRegsResultsPrimary.getFolderId())) == null) {
                        listaRegs3 = new ArrayList<AsocRegsResults>();
                    }
                    AsocRegsResults regCurrent = new AsocRegsResults(bookId, folderId, "", "", "");
                    listaRegs3.add(regCurrent);
                    BookUseCaseAsocRegsUtil.saveAsocRegsSec(useCaseConf, listaRegs3, asocRegsResultsPrimary.getBookId(), asocRegsResultsPrimary.getFolderId());
                    break;
                }
                catch (Exception e) {
                    resultCode = "-1";
                    break;
                }
            }
        }
        doc = XMLAsocRegsValidate.createXMLAsocRegsValidate(resultCode, asocRegsSelected);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(doc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.asocreg.validated.selected");
        }
        String xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public String deleteAsocRegs(UseCaseConf useCaseConf, Integer bookId, Integer folderId) throws Exception {
        BookUseCaseAsocRegsUtil.deleteAsocRegsSec(useCaseConf, bookId, folderId);
        Document doc = XMLAsocRegsValidate.createXMLAsocRegsValidate("10", "");
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter((Writer)writer, format);
        try {
            xmlWriter.write(doc);
        }
        catch (IOException e) {
            throw new BookException("bookexception.asocreg.validated.selected");
        }
        String xml = writer.toString();
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)xml);
        }
        return xml;
    }

    public Integer vldInterSave(UseCaseConf useCaseConf, String strDoc, String strName, String strApe1, String strApe2, int strTipoDoc, String strDirecciones, String strDireccionesTel, int strTipoPer, Integer idPerson) throws ValidationException, SecurityException, AttributesException, BookException, SessionException {
        if (idPerson == 0) {
            int id = UtilsSessionEx.addScrPfiPjur((String)useCaseConf.getSessionID(), (String)strDoc, (String)strName, (String)strApe1, (String)strApe2, (int)strTipoDoc, (String)strDirecciones, (String)strDireccionesTel, (int)strTipoPer, (Integer)idPerson, (String)useCaseConf.getEntidadId());
            return new Integer(id);
        }
        UtilsSessionEx.updateScrPfiPjur((String)useCaseConf.getSessionID(), (String)strDoc, (String)strName, (String)strApe1, (String)strApe2, (int)strTipoDoc, (String)strDirecciones, (String)strDireccionesTel, (int)strTipoPer, (Integer)idPerson, (String)useCaseConf.getEntidadId());
        return idPerson;
    }

    public Document saveOrigDocs(UseCaseConf useCaseConf, Integer bookID, Integer folderID, String datas, Integer openType) throws ValidationException, SecurityException, AttributesException, BookException, SessionException {
        SaveOrigDocDatasInput datasInput = new SaveOrigDocDatasInput(datas);
        datasInput.analize();
        FolderFileSession.saveOrigDoc((String)useCaseConf.getSessionID(), (Integer)bookID, (int)folderID, (SaveOrigDocDatasInput)datasInput, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        boolean badProc = false;
        ArrayList<SaveOrigDocDataDocInput> datasOutput = new ArrayList<SaveOrigDocDataDocInput>();
	for (Iterator it03 = datasInput.getDocs().values().iterator(); it03.hasNext();) {
            SaveOrigDocDataDocInput doc2 = (SaveOrigDocDataDocInput) it03.next();
            datasOutput.add(doc2);
            if (doc2.getProcId() >= 0) continue;
            badProc = true;
        }
        if (badProc) {
            return this.getOrigDocFdrBad(useCaseConf, openType, bookID, folderID, datasOutput);
        }
        return this.getOrigDocFdr(useCaseConf, openType, bookID, folderID);
    }

    public void savePersistFields(UseCaseConf useCaseConf, String cadena) throws ValidationException, SecurityException, AttributesException, BookException, SessionException {
        BookSession.savePersistFields((String)useCaseConf.getSessionID(), (String)cadena, (String)useCaseConf.getEntidadId());
    }

    public String getPersistFields(UseCaseConf useCaseConf, Integer bookId) throws ValidationException, SecurityException, AttributesException, BookException, SessionException {
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        return BookSession.getPersistFields((String)useCaseConf.getSessionID(), (Integer)bookId, (AxSf)axsfQ, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
    }

    public Document getFieldsForPersistence(UseCaseConf useCaseConf, Integer bookID) throws ValidationException, SecurityException, AttributesException, BookException, SessionException {
        int updateDate = SecuritySession.getScrPermission((String)useCaseConf.getSessionID(), (Integer)bookID)[2];
        boolean includeDate = false;
        if (updateDate == 1) {
            includeDate = true;
        }
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookID, (String)useCaseConf.getEntidadId());
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)bookID);
        return XMLFrmPersistFld.createXMLFrmPersistFld(idocarchdet, bookID, axsfQ, includeDate, useCaseConf.getLocale());
    }

    public byte[] getFile(UseCaseConf useCaseConf, Integer bookId, Integer regId, Integer docId, Integer pageId) throws ValidationException, BookException, SessionException {
        return FolderFileSession.getFile((String)useCaseConf.getSessionID(), (Integer)bookId, (Integer)regId, (Integer)pageId, (String)useCaseConf.getEntidadId());
    }

    public List validateFolder(UseCaseConf useCaseConf, Integer bookId, int fdrid, List files, List atts, Map documents) throws ValidationException, SecurityException, AttributesException, BookException, SessionException {
        Locale locale;
        HashMap<Integer, Integer> ctrlIds = new HashMap<Integer, Integer>();
	for (Iterator it03 = atts.iterator(); it03.hasNext();) {
	    FlushFdrField flushFdrField22 = (FlushFdrField) it03.next();
            ctrlIds.put(new Integer(flushFdrField22.getFldid()), new Integer(flushFdrField22.getCtrlid()));
        }
        AxSf axsfQ = BookSession.getFormFormat((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)bookId);
        Date dbDate = BookSession.getDBDateServer((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId());
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        Date dateMaxRegClose = BookSession.getMaxDateRegClose((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId(), (Integer)bookId);
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)fieldFormat);
        }
        if ((locale = useCaseConf.getLocale()) == null) {
            locale = new Locale("es");
        }
        HashMap<Integer, String> idsToValidate = new HashMap<Integer, String>();
        ArrayList<Integer> preResult = new ArrayList<Integer>();
        ArrayList<Integer> result = new ArrayList<Integer>();
        SimpleDateFormat longFormatter = new SimpleDateFormat(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("date.longFormat"));
        longFormatter.setLenient(false);
        SimpleDateFormat shortFormatter = new SimpleDateFormat(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("date.shortFormat"));
        shortFormatter.setLenient(false);
        boolean dateError = fdrid != -1;
        AttributesSession.getExtendedValidationFieldsForBook((String)useCaseConf.getSessionID(), (Integer)bookId, (String)useCaseConf.getEntidadId());
	for (Iterator it03 = atts.iterator(); it03.hasNext();) {
	    FlushFdrField flushFdrField22 = (FlushFdrField) it03.next();
            if (flushFdrField22.getValue() == null || flushFdrField22.getValue().equals("")) continue;
            if (flushFdrField22.getFldid() == 2) {
                try {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug((Object)"****************FORMATEANDO FECHAS***************************");
                        _logger.debug((Object)("longFormatter: " + RBUtil.getInstance(useCaseConf.getLocale()).getProperty("date.longFormat")));
                        _logger.debug((Object)("value: " + flushFdrField22.getValue()));
                    }
                    Date date = null;
                    if (flushFdrField22.getValue().length() > 19) {
                        dateError = true;
                    } else {
                        date = longFormatter.parse(flushFdrField22.getValue());
                    }
                    if (_logger.isDebugEnabled()) {
                        _logger.debug((Object)("date: " + date));
                        _logger.debug((Object)("date.after(dbDate): " + date.after(dbDate)));
                        _logger.debug((Object)("longFormatter.getCalendar().get(Calendar.YEAR): " + longFormatter.getCalendar().get(1)));
                    }
                    if (date.after(dbDate) || longFormatter.getCalendar().get(1) < 1970 || shortFormatter.getCalendar().get(1) > 2040) {
                        dateError = true;
                    } else if (!this.validateDateRegisterWithDateMaxClose(date, dateMaxRegClose)) {
                        _logger.warn((Object)("Error en la validacion de Fecha Registro: La fecha del registro [" + date + "] es anterior a la fecha maxima de cierre [" + dateMaxRegClose + "] de registros"));
                        dateError = true;
                    } else {
                        dateError = false;
                    }
                    if (!_logger.isDebugEnabled()) continue;
                    _logger.debug((Object)"*******************************************");
                }
                catch (Exception e) {
                    dateError = true;
                }
                continue;
            }
            if (flushFdrField22.getFldid() == 11 && axsfQ instanceof AxSfIn) {
                try {
                    Integer.parseInt(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("book.fld11." + flushFdrField22.getValue()));
                }
                catch (Exception e) {
                    preResult.add(new Integer(flushFdrField22.getFldid()));
                }
                continue;
            }
            if (flushFdrField22.getFldid() == 4) {
                try {
                    if (flushFdrField22.getValue().length() > 10) {
                        new Exception("Fecha mal formada");
                    }
                    shortFormatter.parse(flushFdrField22.getValue());
                }
                catch (Exception e) {
                    preResult.add(new Integer(flushFdrField22.getFldid()));
                }
                continue;
            }
            if (flushFdrField22.getFldid() == 5 || flushFdrField22.getFldid() == 7 || flushFdrField22.getFldid() == 8) {
                idsToValidate.put(new Integer(flushFdrField22.getFldid()), flushFdrField22.getValue());
                continue;
            }
            if (axsfQ instanceof AxSfIn && flushFdrField22.getFldid() == 12) {
                try {
                    if (flushFdrField22.getValue().length() > 10) {
                        new Exception("Fecha mal formada");
                    }
                    shortFormatter.parse(flushFdrField22.getValue());
                    if (shortFormatter.getCalendar().get(1) >= 1970 && shortFormatter.getCalendar().get(1) <= 2040) continue;
                    preResult.add(new Integer(flushFdrField22.getFldid()));
                }
                catch (Exception e) {
                    preResult.add(new Integer(flushFdrField22.getFldid()));
                }
                continue;
            }
            if (axsfQ instanceof AxSfIn && (flushFdrField22.getFldid() == 13 || flushFdrField22.getFldid() == 16)) {
                idsToValidate.put(new Integer(flushFdrField22.getFldid()), flushFdrField22.getValue());
                continue;
            }
            if (axsfQ instanceof AxSfOut && flushFdrField22.getFldid() == 12) {
                idsToValidate.put(new Integer(flushFdrField22.getFldid()), flushFdrField22.getValue());
                continue;
            }
            if (axsfQ instanceof AxSfIn && flushFdrField22.getFldid() == 14 || axsfQ instanceof AxSfOut && flushFdrField22.getFldid() == 10) continue;
            if (axsfQ instanceof AxSfIn && flushFdrField22.getFldid() > 17) {
                if (axsfQ.getProposedExtendedFields().contains(new Integer(flushFdrField22.getFldid()))) continue;
                if (AttributesSession.getExtendedValidationFieldValue((String)useCaseConf.getSessionID(), (Integer)bookId, (int)flushFdrField22.getFldid(), (String)flushFdrField22.getValue(), (String)locale.getLanguage(), (String)useCaseConf.getEntidadId()) != null) {
                    this.checkValue(flushFdrField22, shortFormatter, longFormatter, fieldFormat, preResult);
                    continue;
                }
                preResult.add(new Integer(flushFdrField22.getFldid()));
                continue;
            }
            if (axsfQ instanceof AxSfOut && flushFdrField22.getFldid() > 13) {
                if (axsfQ.getProposedExtendedFields().contains(new Integer(flushFdrField22.getFldid()))) continue;
                if (AttributesSession.getExtendedValidationFieldValue((String)useCaseConf.getSessionID(), (Integer)bookId, (int)flushFdrField22.getFldid(), (String)flushFdrField22.getValue(), (String)locale.getLanguage(), (String)useCaseConf.getEntidadId()) != null) {
                    this.checkValue(flushFdrField22, shortFormatter, longFormatter, fieldFormat, preResult);
                    continue;
                }
                preResult.add(new Integer(flushFdrField22.getFldid()));
                continue;
            }
            if (flushFdrField22.getFldid() == 6) continue;
            this.checkValue(flushFdrField22, shortFormatter, longFormatter, fieldFormat, preResult);
        }
        if (ctrlIds.get(new Integer(2)) == null) {
            Date date = null;
            AxSf axSf = null;
            if (fdrid != -1) {
                axSf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fdrid, (Locale)locale, (String)useCaseConf.getEntidadId());
                if (axSf.getAttributeValue("fld2") != null) {
                    date = (Date)axSf.getAttributeValue("fld2");
                }
                if (!(this.validateDateRegisterWithDateMaxClose(date, dateMaxRegClose) || date == null)) {
                    FCtrlDef fechaRegistro = this.getCtrlDefByFldId(axsfQ, 2);
                    _logger.warn((Object)("Error en la validacion de Fecha Registro: La fecha del registro [" + date + "] es anterior a la fecha maxima de cierre [" + dateMaxRegClose + "] de registros"));
                    result.add(new Integer(fechaRegistro.getId()));
                }
            }
        }
        if (dateError) {
            preResult.add(new Integer(2));
        }
        if (!idsToValidate.isEmpty()) {
            preResult.addAll(AttributesSession.validateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idsToValidate, (boolean)true, (String)useCaseConf.getEntidadId()));
        }
        for (Integer f : preResult) {
            Integer c = (Integer)ctrlIds.get(f);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("A\u00f1adiendo error en : " + f + " para el control :" + c));
            }
            if (c == null) continue;
            result.add(c);
        }
        return result;
    }

    public FCtrlDef getCtrlDefByFldId(AxSf axsfQ, int fldId) {
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("getCtrlDefByFldId: " + fldId));
        }
        FormFormat formFormat = new FormFormat(axsfQ.getFormat().getData());
        TreeMap pages = formFormat.getDlgDef().getPagedefs();
	for (Iterator it03 = pages.values().iterator(); it03.hasNext();) {
	    FPageDef pageDef = (FPageDef) it03.next();

            TreeMap ctrls = pageDef.getCtrldefs();
	    for (Iterator it04 = ctrls.values().iterator(); it04.hasNext();) {
	        FCtrlDef ctrlDef2 = (FCtrlDef) it04.next();
                if (ctrlDef2.getFldId() != fldId) continue;
                if (_logger.isDebugEnabled()) {
                    _logger.debug((Object)("getCtrlDefByFldId FldId: " + fldId + " ctrlDef: " + ctrlDef2.toString()));
                }
                return ctrlDef2;
            }
        }
        return null;
    }

    public int saveOrUpdateFolder(String idEntidad, String sessionId, Locale locale, Integer bookId, int fldid, List files, List atts, List inter, Map documents) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, ParseException {
        boolean updateDate = false;
        Integer modifySystemDate = new Integer(BookSession.invesicresConf((String)idEntidad).getModifySystemDate());
        Integer launchDistOutRegister = new Integer(BookSession.invesicresConf((String)idEntidad).getDistSRegister());
        if (modifySystemDate == 1) {
            updateDate = true;
        }
        AxSf axsfQ = BookSession.getFormFormat((String)sessionId, (Integer)bookId, (String)idEntidad);
        HashMap<Integer, String> idsToTranslate = new HashMap<Integer, String>();
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)sessionId, (Integer)bookId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        for (Iterator it03 = atts.iterator(); it03.hasNext();) {
            FlushFdrField flushFdrField22 = (FlushFdrField) it03.next();
            if (flushFdrField22.getFldid() == 5 || flushFdrField22.getFldid() == 7 || flushFdrField22.getFldid() == 8) {
                idsToTranslate.put(new Integer(flushFdrField22.getFldid()), flushFdrField22.getValue());
            }
            if (axsfQ instanceof AxSfIn && (flushFdrField22.getFldid() == 13 || flushFdrField22.getFldid() == 16)) {
                idsToTranslate.put(new Integer(flushFdrField22.getFldid()), flushFdrField22.getValue());
            }
            if (!(axsfQ instanceof AxSfOut) || flushFdrField22.getFldid() != 12) continue;
            idsToTranslate.put(new Integer(flushFdrField22.getFldid()), flushFdrField22.getValue());
        }
        Map translatedIds = AttributesSession.translateFixedValuesForSaveOrUpdate((String)sessionId, (Integer)bookId, idsToTranslate, (String)idEntidad);
        AxSf newAxSF = null;
        if (axsfQ instanceof AxSfIn) {
            newAxSF = new AxSfIn();
            newAxSF.setLiteralBookType(RBUtil.getInstance(locale).getProperty("bookusecase.node.inBook.name"));
        } else {
            newAxSF = new AxSfOut();
            newAxSF.setLiteralBookType(RBUtil.getInstance(locale).getProperty("bookusecase.node.outBook.name"));
        }
        for (Iterator it03 = translatedIds.keySet().iterator(); it03.hasNext();) {
            Integer id2 = (Integer) it03.next(); 
            newAxSF.addAttributeName("fld" + id2.toString());
            newAxSF.addAttributeValue("fld" + id2.toString(), translatedIds.get(id2));
        }
        SimpleDateFormat longFormatter = new SimpleDateFormat(RBUtil.getInstance(locale).getProperty("date.longFormat"));
        SimpleDateFormat shortFormatter = new SimpleDateFormat(RBUtil.getInstance(locale).getProperty("date.shortFormat"));
        for (Iterator it03 = atts.iterator(); it03.hasNext();) {
            FlushFdrField flushFdrField22 = (FlushFdrField) it03.next();
            if (flushFdrField22.getFldid() == 17 && axsfQ instanceof AxSfIn) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
            }
            if ((flushFdrField22.getFldid() == 14 || flushFdrField22.getFldid() == 15) && axsfQ instanceof AxSfIn) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
            }
            if ((flushFdrField22.getFldid() == 10 || flushFdrField22.getFldid() == 11) && axsfQ instanceof AxSfOut) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
            }
            if (flushFdrField22.getFldid() == 13 && axsfQ instanceof AxSfOut) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
            }
            if (flushFdrField22.getFldid() == 10 && axsfQ instanceof AxSfIn) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
            }
            if (flushFdrField22.getFldid() == 11 && axsfQ instanceof AxSfIn) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                if (flushFdrField22.getValue() != null) {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)new BigDecimal(RBUtil.getInstance(locale).getProperty("book.fld11." + flushFdrField22.getValue())));
                } else {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
                }
            }
            if (flushFdrField22.getFldid() == 2) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                if (flushFdrField22.getValue() != null) {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)longFormatter.parse(flushFdrField22.getValue()));
                } else {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
                }
            }
            if (flushFdrField22.getFldid() == 4) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                if (flushFdrField22.getValue() != null) {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)shortFormatter.parse(flushFdrField22.getValue()));
                } else {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
                }
            }
            if (axsfQ instanceof AxSfIn && flushFdrField22.getFldid() == 12) {
                newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
                if (flushFdrField22.getValue() != null) {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)shortFormatter.parse(flushFdrField22.getValue()));
                } else {
                    newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)flushFdrField22.getValue());
                }
            }
            this.getExtendsFields(axsfQ, fieldFormat, flushFdrField22, (AxSf)newAxSF, longFormatter, shortFormatter);
            if (flushFdrField22.getFldid() != 6 || flushFdrField22.getValue() == null) continue;
            newAxSF.addAttributeName("fld" + flushFdrField22.getFldid());
            newAxSF.addAttributeValue("fld" + flushFdrField22.getFldid(), (Object)new Integer(flushFdrField22.getValue()));
        }
        newAxSF.setLocale(locale);
        if (fldid == -1) {
            return FolderSession.createNewFolder((String)sessionId, (Integer)bookId, (AxSf)newAxSF, (List)inter, (Integer)launchDistOutRegister, (Locale)locale, (String)idEntidad);
        }
        FolderSession.updateFolder((String)sessionId, (Integer)bookId, (int)fldid, (AxSf)newAxSF, (List)inter, (Map)documents, (boolean)updateDate, (Integer)launchDistOutRegister, (Locale)locale, (String)idEntidad);
        return fldid;
    }

    public boolean validateDateRegisterWithDateMaxClose(Date date, Date dateMaxRegClose) throws BookException {
        boolean result = true;
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)"Entra a validar fecha registro: validateDateRegisterWithDateMaxClose()");
        }
        SimpleDateFormat compareFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (dateMaxRegClose != null && date != null) {
            String strDateReg = compareFormatter.format(date);
            String strDateMaxClose = compareFormatter.format(dateMaxRegClose);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("Fecha del Registro: [" + strDateReg + "] - Fecha Maxima de Cierre : [" + strDateMaxClose + "]"));
            }
            result = strDateReg.compareTo(strDateMaxClose) > 0;
        }
        return result;
    }

    public void getExtendsFields(AxSf axsfQ, FieldFormat fieldFormat, FlushFdrField flushFdrField, AxSf newAxSF, SimpleDateFormat longFormatter, SimpleDateFormat shortFormatter) {
        AxXf extendFiel = null;
        if (axsfQ instanceof AxSfIn && flushFdrField.getFldid() > 17 || axsfQ instanceof AxSfOut && flushFdrField.getFldid() > 13) {
            if (axsfQ.getProposedExtendedFields().contains(new Integer(flushFdrField.getFldid())) && flushFdrField.getValue() == null) {
                flushFdrField.setValue("");
            }
            if (axsfQ.getProposedExtendedFields().contains(new Integer(flushFdrField.getFldid()))) {
                if (axsfQ instanceof AxSfIn && flushFdrField.getFldid() == 18) {
                    AxXf axxf = BookUseCase.fromFlushFdrFieldToAxXF(flushFdrField);
                    newAxSF.setAxxf(axxf);
                }
                extendFiel = BookUseCase.fromFlushFdrFieldToAxXF(flushFdrField);
                newAxSF.getExtendedFields().put(new Integer(extendFiel.getFldId()), extendFiel);
            } else {
                newAxSF.addAttributeName("fld" + flushFdrField.getFldid());
                newAxSF.addAttributeValue("fld" + flushFdrField.getFldid(), this.convertValue(flushFdrField, shortFormatter, longFormatter, fieldFormat));
            }
        }
    }

    public static AxXf fromFlushFdrFieldToAxXF(FlushFdrField flushFdrField) {
        AxXf result = null;
        result = new AxXf();
        result.setFdrId(flushFdrField.getCtrlid());
        result.setFldId(flushFdrField.getFldid());
        result.setId(flushFdrField.getFldid());
        result.setText(flushFdrField.getValue());
        return result;
    }

    public int saveOrUpdateFolder(UseCaseConf useCaseConf, Integer bookId, int fldid, List files, List atts, List inter, Map documents) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, ParseException {
        String idEntidad = useCaseConf.getEntidadId();
        Locale locale = useCaseConf.getLocale();
        String sessionId = useCaseConf.getSessionID();
        return this.saveOrUpdateFolder(idEntidad, sessionId, locale, bookId, fldid, files, atts, inter, documents);
    }

    public String updateFieldOrg(UseCaseConf useCaseConf, Integer bookId, Integer fldid, String code, List<Integer> listIdsRegister) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, ParseException {
        FlushFdrField field = new FlushFdrField();
        field.setFldid(fldid.intValue());
        field.setValue(code);
        ArrayList<FlushFdrField> atts = new ArrayList<FlushFdrField>(1);
        atts.add(field);
        ScrOrg scrOrg = BookSession.preUpdateFieldOrg((String)useCaseConf.getSessionID(), (Integer)bookId, (String)code, (List)listIdsRegister, (String)useCaseConf.getEntidadId());
        for (Integer folderID2 : listIdsRegister) {
            this.saveOrUpdateFolder(useCaseConf, bookId, folderID2, null, atts, null, null);
        }
        List list = BookSession.postUpdateFieldOrg((String)useCaseConf.getSessionID(), (Integer)bookId, (List)listIdsRegister, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        StringBuffer result = new StringBuffer();
        result.append(this.getScrOrgDescription(scrOrg));
        result.append("#");
        result.append(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("book.fld6.0"));
        result.append("#");
        Iterator it = list.iterator();
        while (it.hasNext()) {
            result.append(it.next().toString());
            result.append("_");
        }
        return result.toString();
    }

    public void getPermsRegisterDistPen(UseCaseConf useCaseConf, Integer bookId, Integer fdrid) throws BookException, DistributionException, ValidationException, SessionException {
        AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fdrid, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        String regNumberString = (String)axsf.getAttributeValue("fld1");
        AxSfQuery axsfQuery = new AxSfQuery();
        axsfQuery.setBookId(bookId);
        axsfQuery.setPageSize(Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageTableResultsSize")));
        AxSfQueryField field = new AxSfQueryField();
        field.setFldId("fld1");
        field.setValue((Object)regNumberString);
        field.setOperator("=");
        axsfQuery.addField(field);
        boolean perms = false;
        int distResults = 0;
        distResults = DistributionSession.getDistribution((String)useCaseConf.getSessionID(), (int)1, (Integer)bookId, (Integer)fdrid, (String)useCaseConf.getEntidadId());
        perms = distResults != 0;
        if (!perms) {
            throw new BookException("bookexception.error_cannot_show_register");
        }
    }

    private String getScrOrgDescription(ScrOrg org) {
        String text = "";
        if (org != null) {
            if (Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/QueryResultsTableRepresentation/AdministrativeUnits/@code")) {
                text = org.getCode();
            } else if (Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/QueryResultsTableRepresentation/AdministrativeUnits/@abbreviation")) {
                text = org.getAcron();
            } else if (Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/QueryResultsTableRepresentation/AdministrativeUnits/@name")) {
                text = org.getName();
            }
            if (text == null) {
                text = "";
            }
        }
        return text;
    }

    private String translateOperator(String operator, Locale locale) {
        String result = null;
        if (operator.equals(RBUtil.getInstance(locale).getProperty("query.equal.text.value"))) {
            result = "=";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.not.equal.text.value"))) {
            result = "!=";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.greater.text.value"))) {
            result = ">";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.greater.equal.text.value"))) {
            result = ">=";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.lesser.text.value"))) {
            result = "<";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.lesser.equal.text.value"))) {
            result = "<=";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.between.text.value"))) {
            result = "..";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.like.text.value"))) {
            result = "%";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.or.text.value"))) {
            result = "|";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.abc.text.value"))) {
            result = "Abc";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.in.and.text.value"))) {
            result = "C&";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.in.or.text.value"))) {
            result = "c|";
        } else if (operator.equals(RBUtil.getInstance(locale).getProperty("query.depend.of.value"))) {
            result = "dde";
        }
        return result;
    }

    private String translateOperator(int idOperator, Locale locale) {
        String result = null;
        if (idOperator == 1) {
            result = "=";
        } else if (idOperator == 2) {
            result = "!=";
        } else if (idOperator == 4) {
            result = ">";
        } else if (idOperator == 8) {
            result = ">=";
        } else if (idOperator == 16) {
            result = "<";
        } else if (idOperator == 32) {
            result = "<=";
        } else if (idOperator == 64) {
            result = "..";
        } else if (idOperator == 128) {
            result = "%";
        } else if (idOperator == 256) {
            result = "|";
        } else if (idOperator == 512) {
            result = "Abc";
        } else if (idOperator == 1024) {
            result = "C&";
        } else if (idOperator == 2048) {
            result = "c|";
        }
        return result;
    }

    private String translateNexo(String nexo) {
        String result = AbstractDBEntityDAO.AND;
        if (StringUtils.isNotEmpty((String)nexo)) {
            if (nexo.equals("or")) {
                result = AbstractDBEntityDAO.OR;
            } else if (nexo.equals("and")) {
                result = AbstractDBEntityDAO.AND;
            }
        }
        return result;
    }

    private Object getValue(AxSf axsfQ, AxSfQueryField field, Map params, QCtrlDef ctrlDef, Map translations, Locale locale) throws ParseException {
        String param = this.getValue(params, Integer.toString(ctrlDef.getId()));
        String operator = this.getValue(params, Integer.toString(ctrlDef.getRelCtrlId()));
        if (translations != null && translations.containsKey(new Integer(ctrlDef.getFldId()))) {
            return translations.get(new Integer(ctrlDef.getFldId()));
        }
        if (operator.equals(RBUtil.getInstance(locale).getProperty("query.or.text.value")) || operator.equals(RBUtil.getInstance(locale).getProperty("query.between.text.value"))) {
            StringTokenizer tokenizer = new StringTokenizer(param, ";");
            ArrayList<Object> list = new ArrayList<Object>(tokenizer.countTokens());
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                list.add(this.getValue(axsfQ, field, tokenizer.nextToken(), ctrlDef, locale));
                ++i;
            }
            return list;
        }
        if (operator.equals(RBUtil.getInstance(locale).getProperty("query.like.text.value"))) {
            String aux = param;
            if (StringUtils.startsWith((String)param, (String)"%") || StringUtils.endsWith((String)param, (String)"%")) {
                _logger.debug((Object)("Realizando la busqueda simple 'empieza con o termina con' utilizando el parametro: " + aux));
            } else {
                aux = param.replaceAll("%", "");
                aux = "%" + aux + "%";
                _logger.debug((Object)("Realizando la busqueda simple 'contiene con', utilizando el parametro: " + aux));
            }
            return this.getValue(axsfQ, field, aux, ctrlDef, locale);
        }
        return this.getValue(axsfQ, field, param, ctrlDef, locale);
    }

    private String getValue(Map params, String key) {
        Object object = params.get(key);
        if (object instanceof String[]) {
            return ((String[])object)[0];
        }
        return (String)object;
    }

    private Object getValue(AxSf axsfQ, AxSfQueryField field, String param, QCtrlDef ctrlDef, Locale locale) throws ParseException {
        if (field.getFldId().equals("fld6")) {
            if (StringUtils.isNotEmpty((String)param)) {
                param = param.toUpperCase();
            }
            return new Integer(RBUtil.getInstance(locale).getProperty("book.fld6." + param));
        }
        if (field.getFldId().equals("fld11") && axsfQ instanceof AxSfIn) {
            return new Integer(RBUtil.getInstance(locale).getProperty("book.fld11." + param));
        }
        if (axsfQ.getAttributeClass(field.getFldId()).equals(Date.class)) {
            SimpleDateFormat SDF = new SimpleDateFormat(RBUtil.getInstance(locale).getProperty("date.shortFormat"));
            SDF.setLenient(false);
            return SDF.parse(param);
        }
        if (axsfQ.getAttributeClass(field.getFldId()).equals(Integer.class)) {
            return new Integer(param);
        }
        if (axsfQ.getAttributeClass(field.getFldId()).equals(BigDecimal.class)) {
            return new Integer(param);
        }
        return param;
    }

    private List getAttributesForValidationForTable(AxSf axsf) {
        String data = axsf.getFormat().getData();
        TableFormat tableFormat = new TableFormat(data);
        ArrayList<Integer> result = new ArrayList<Integer>();
        List<String> flds = axsf.getAttributesNames();
        int aux = 0;
        for (String key2 : flds) {
            if (!key2.startsWith("fld")) continue;
            aux = Integer.parseInt(key2.substring("fld".length(), key2.length()));
            if (axsf instanceof AxSfIn) {
                if (aux <= 17 || !this.existAttributeInQueryFormat(tableFormat, aux)) continue;
                result.add(new Integer(aux));
                continue;
            }
            if (aux <= 13 || !this.existAttributeInQueryFormat(tableFormat, aux)) continue;
            result.add(new Integer(aux));
        }
        return result;
    }

    private List getAttributesForValidationForForm(UseCaseConf useCaseConf, Integer bookID, AxSf axsf, int page) throws ValidationException, AttributesException, BookException, SessionException {
        String data = axsf.getFormat().getData();
        FormFormat formFormat = new FormFormat(data);
        ArrayList<Integer> result = new ArrayList<Integer>();
        List flds = AttributesSession.getExtendedValidationFieldsForBook((String)useCaseConf.getSessionID(), (Integer)bookID, (String)useCaseConf.getEntidadId());
        int aux = 0;
        Iterator it = flds.iterator();
        while (it.hasNext()) {
            aux = Integer.parseInt(it.next().toString());
            if (axsf instanceof AxSfIn) {
                if (aux <= 17 || !this.existAttributeInQueryFormat(formFormat, aux, page)) continue;
                result.add(new Integer(aux));
                continue;
            }
            if (aux <= 13 || !this.existAttributeInQueryFormat(formFormat, aux, page)) continue;
            result.add(new Integer(aux));
        }
        return result;
    }

    private boolean existAttributeInQueryFormat(TableFormat tf, int fldid) {

	for (Iterator it03 = tf.getDlgDef().getColdefs().values().iterator(); it03.hasNext();) {
            TColDef colDef2 = (TColDef)  it03.next();
            if (colDef2.getFldId() != fldid) continue;
            return true;
        }
        return false;
    }

    private boolean existAttributeInQueryFormat(FormFormat ff, int fldid, int page) {
        TreeMap pages = ff.getDlgDef().getPagedefs();
        FPageDef pageDef = (FPageDef)pages.get(new Integer(page));
        TreeMap ctrls = pageDef.getCtrldefs();
        for (Iterator it03 = ctrls.values().iterator(); it03.hasNext();) {
            FCtrlDef ctrlDef2 = (FCtrlDef) it03.next();
            if (ctrlDef2.getFldId() != fldid) continue;
            return true;
        }
        return false;
    }

    private void assignValidationFields(AxSf axsf, AxSfQueryResults queryResults, UseCaseConf useCaseConf, Integer bookID) throws ValidationException, AttributesException, BookException, SessionException, SecurityException {
        List<Integer> list = this.getAttributesForValidationForTable(axsf);
        if (!list.isEmpty()) {
            Map fldids = new HashMap(list.size());
            String fldName = null;
            ArrayList<String> fldidsValues = null;
            for (Integer fldid22 : list) {
                fldName = "fld" + fldid22.toString();
                fldidsValues = new ArrayList<String>();
		for (Iterator it03 = queryResults.getResults().iterator(); it03.hasNext();) {
		    AxSf auxAxsf2  = (AxSf) it03.next();
                    fldidsValues.add(auxAxsf2.getAttributeValueAsString(fldName));
                }
                fldids.put((Integer)fldid22, fldidsValues);
            }
            Map result = AttributesSession.getExtendedValidationFieldValues((String)useCaseConf.getSessionID(), (Integer)bookID, fldids, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
            String oldValue2 = null;
            String newValue = null;
            for (Iterator it03 = result.keySet().iterator(); it03.hasNext();) {
		Integer fldid22=(Integer) it03.next();
                fldName = "fld" + fldid22.toString();
                fldids = (Map)result.get(fldid22);
                for (Iterator it04 = fldids.keySet().iterator(); it04.hasNext();) {
		    oldValue2 = (String) it04.next();
                    newValue = (String)fldids.get(oldValue2);
                    for (Iterator it05 = queryResults.getResults().iterator(); it05.hasNext();) {
			AxSf auxAxsf2 = (AxSf) it05.next();
                        if (auxAxsf2.getAttributeValueAsString(fldName) == null || auxAxsf2.getAttributeValueAsString(fldName).length() <= 0 || !auxAxsf2.getAttributeValueAsString(fldName).equals(oldValue2)) continue;
                        auxAxsf2.setAttributeValue(fldName, (Object)newValue);
                    }
                }
            }
        }
    }

    private Map getValidationFields(AxSf axsf, UseCaseConf useCaseConf, Integer bookID, int page) throws ValidationException, AttributesException, BookException, SessionException {
        List<Integer> list = this.getAttributesForValidationForForm(useCaseConf, bookID, axsf, page);
        Map result = new HashMap();
        if (!list.isEmpty()) {
            HashMap fldids = new HashMap(list.size());
            String fldName = null;
            ArrayList<String> values = null;
            for (Integer fldid2 : list) {
                fldName = "fld" + fldid2.toString();
                values = new ArrayList<String>();
                values.add(axsf.getAttributeValueAsString(fldName));
                fldids.put(fldid2, values);
            }
            result = AttributesSession.getExtendedValidationFieldValues((String)useCaseConf.getSessionID(), (Integer)bookID, fldids, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
        }
        return result;
    }

    private String getDest(UseCaseConf useCaseConf, Integer bookId, int fldid) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, Exception {
        StringBuffer buffer = new StringBuffer();
        InteresadosDecorator interesadosDecorator = ISicresBeansProvider.getInstance().getInteresadosDecorator();
        return interesadosDecorator.getInteresados(bookId.toString(), String.valueOf(fldid));
    }

    private void checkValue(FlushFdrField flushFdrField, SimpleDateFormat shortFormatter, SimpleDateFormat longFormatter, FieldFormat fieldFormat, List preResult) {
        FFldDef fldDef = fieldFormat.getFFldDef(flushFdrField.getFldid());
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("comprobando errores det en :" + flushFdrField.getFldid()));
            _logger.debug((Object)("\tvalue :" + flushFdrField.getValue()));
            _logger.debug((Object)("\tvalue :" + flushFdrField.getValue().length()));
            _logger.debug((Object)("\ttype :" + fldDef.getType()));
            _logger.debug((Object)("\tlen :" + fldDef.getLen()));
        }
        switch (fldDef.getType()) {
            case 1: 
            case 2: {
                break;
            }
            case 3: 
            case 4: {
                try {
                    Long.parseLong(flushFdrField.getValue());
                }
                catch (Exception e) {
                    preResult.add(new Integer(flushFdrField.getFldid()));
                }
                break;
            }
            case 5: 
            case 6: {
                try {
                    new BigDecimal(flushFdrField.getValue());
                }
                catch (Exception e) {
                    preResult.add(new Integer(flushFdrField.getFldid()));
                }
                break;
            }
            case 7: {
                try {
                    if (flushFdrField.getValue().length() > 10) {
                        new Exception("Fecha mal formada");
                    }
                    shortFormatter.parse(flushFdrField.getValue());
                    if (shortFormatter.getCalendar().get(1) >= 1970 && shortFormatter.getCalendar().get(1) <= 2040) break;
                    preResult.add(new Integer(flushFdrField.getFldid()));
                }
                catch (Exception e) {
                    try {
                        if (flushFdrField.getValue().length() > 10) {
                            new Exception("Fecha mal formada");
                        }
                        shortFormatter.parse(flushFdrField.getValue());
                        if (shortFormatter.getCalendar().get(1) >= 1970 && shortFormatter.getCalendar().get(1) <= 2040) break;
                        preResult.add(new Integer(flushFdrField.getFldid()));
                        break;
                    }
                    catch (Exception e1) {
                        preResult.add(new Integer(flushFdrField.getFldid()));
                    }
                }
                break;
            }
            case 8: 
            case 9: {
                try {
                    longFormatter.parse(flushFdrField.getValue());
                    if (longFormatter.getCalendar().get(1) >= 1970 && shortFormatter.getCalendar().get(1) <= 2040) break;
                    preResult.add(new Integer(flushFdrField.getFldid()));
                    break;
                }
                catch (Exception e) {
                    try {
                        shortFormatter.parse(flushFdrField.getValue());
                        if (shortFormatter.getCalendar().get(1) >= 1970 && shortFormatter.getCalendar().get(1) <= 2040) break;
                        preResult.add(new Integer(flushFdrField.getFldid()));
                        break;
                    }
                    catch (Exception e1) {
                        preResult.add(new Integer(flushFdrField.getFldid()));
                    }
                }
            }
        }
    }

    private Object convertValue(FlushFdrField flushFdrField, SimpleDateFormat shortFormatter, SimpleDateFormat longFormatter, FieldFormat fieldFormat) {
        FFldDef fldDef = fieldFormat.getFFldDef(flushFdrField.getFldid());
        switch (fldDef.getType()) {
            case 1: 
            case 2: {
                return flushFdrField.getValue();
            }
            case 3: 
            case 4: {
                if (flushFdrField.getValue() != null) {
                    return new Integer(flushFdrField.getValue());
                }
                return flushFdrField.getValue();
            }
            case 5: 
            case 6: {
                if (flushFdrField.getValue() != null) {
                    BigDecimal result = null;
                    try {
                        result = new BigDecimal(flushFdrField.getValue());
                    }
                    catch (NumberFormatException e) {
                        // empty catch block
                    }
                    return result;
                }
                return flushFdrField.getValue();
            }
            case 7: {
                try {
                    if (flushFdrField.getValue() != null) {
                        return shortFormatter.parse(flushFdrField.getValue());
                    }
                    return flushFdrField.getValue();
                }
                catch (ParseException e) {
                    // empty catch block
                }
            }
            case 8: 
            case 9: {
                try {
                    if (flushFdrField.getValue() != null) {
                        return longFormatter.parse(flushFdrField.getValue());
                    }
                    return flushFdrField.getValue();
                }
                catch (ParseException e) {
                    // empty catch block
                }
            }
        }
        return null;
    }

    public int closeRegisters(UseCaseConf useCaseConf, Integer bookID, Date beginDate, Date endDate, String unit, boolean isTarget) throws BookException, SessionException, SQLException, SecurityException, ParseException, AttributesException, ValidationException {
        int nofUpdatedRegs = 0;
        FlushFdrField field = new FlushFdrField();
        field.setFldid(6);
        field.setValue(new Integer(5).toString());
        ArrayList<FlushFdrField> fields = new ArrayList<FlushFdrField>(1);
        fields.add(field);
        String sessionID = useCaseConf.getSessionID();
        boolean canCloseRegs = SecuritySession.canOpenCloseReg((String)sessionID, (Integer)bookID);
        if (!canCloseRegs) {
            throw new SecurityException("securityexception.can_not_open_close_reg");
        }
        List listIdsRegister = FolderSession.getRegistersToClose((String)useCaseConf.getSessionID(), (Integer)bookID, (Date)beginDate, (Date)endDate, (String)unit, (boolean)isTarget, (String)useCaseConf.getEntidadId());
        if (listIdsRegister != null && listIdsRegister.size() > 0) {
            nofUpdatedRegs = listIdsRegister.size();
            this.updateRegisterToClose(useCaseConf, bookID, fields, listIdsRegister);
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)"closeRegisters");
        }
        return nofUpdatedRegs;
    }

    public void openRegisters(UseCaseConf useCaseConf, Integer bookID, List listIdsRegister) throws BookException, SessionException, ValidationException, ParseException, AttributesException, SecurityException {
        FlushFdrField field = new FlushFdrField();
        field.setFldid(6);
        field.setValue(new Integer(0).toString());
        ArrayList<FlushFdrField> fields = new ArrayList<FlushFdrField>(1);
        fields.add(field);
        String sessionID = useCaseConf.getSessionID();
        boolean canCloseRegs = SecuritySession.canOpenCloseReg((String)sessionID, (Integer)bookID);
        this.updateFields(useCaseConf, bookID, fields, listIdsRegister);
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)"openRegisters");
        }
    }

    public void updateRegisterToClose(UseCaseConf useCaseConf, Integer bookId, List fields, List<Integer> listIdsRegister) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, ParseException {
        BookSession.preUpdateRegisterToClose((String)useCaseConf.getSessionID(), (Integer)bookId, (List)listIdsRegister, (String)useCaseConf.getEntidadId());
        for (Integer folderID2 : listIdsRegister) {
            this.saveOrUpdateFolder(useCaseConf, bookId, folderID2, null, fields, null, null);
        }
        BookSession.postUpdateFields((String)useCaseConf.getSessionID(), (Integer)bookId, (List)listIdsRegister, (String)useCaseConf.getEntidadId());
    }

    public void updateFields(UseCaseConf useCaseConf, Integer bookId, List fields, List<Integer> listIdsRegister) throws ValidationException, SecurityException, AttributesException, BookException, SessionException, ParseException {
        BookSession.preUpdateFields((String)useCaseConf.getSessionID(), (Integer)bookId, (List)listIdsRegister, (String)useCaseConf.getEntidadId());
        for (Integer folderID2 : listIdsRegister) {
            this.saveOrUpdateFolder(useCaseConf, bookId, folderID2, null, fields, null, null);
        }
        BookSession.postUpdateFields((String)useCaseConf.getSessionID(), (Integer)bookId, (List)listIdsRegister, (String)useCaseConf.getEntidadId());
    }

    public Map getOperadoresCampos(UseCaseConf useCaseConf, Integer archiveId, long archivePId) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        AxSf axsf = BookSession.getQueryFormat((String)useCaseConf.getSessionID(), (Integer)archiveId, (String)useCaseConf.getEntidadId());
        Idocarchdet idocarchdet = BookSession.getIdocarchdetFld((String)useCaseConf.getSessionID(), (Integer)archiveId);
        FieldFormat fieldFormat = new FieldFormat(idocarchdet.getDetval());
        String dataBaseType = null;
        try {
            dataBaseType = BookSession.getDataBaseType((String)useCaseConf.getSessionID());
        }
        catch (Exception e) {
            // empty catch block
        }
        return this.getOperadores(axsf, fieldFormat, archiveId, archivePId, null, useCaseConf.getLocale(), dataBaseType);
    }

    public Map getOperadores(AxSf axsf, FieldFormat fieldFormat, Integer archiveId, long archivePId, Map fieldsNotEqual, Locale locale, String dataBaseType) {
        String data = axsf.getFormat().getData();
        QueryFormat queryFormat = new QueryFormat(data);
        Integer fldId = null;
        QCtrlDef ctrlDef = null;
        HashMap<String, Integer> ctrBoxDisabled = new HashMap<String, Integer>();
        HashMap<Integer, Integer> ctrBoxOfFldId = new HashMap<Integer, Integer>();
        HashMap<String, List> operadoresCampos = new HashMap<String, List>();
        if (!(fieldsNotEqual == null || fieldsNotEqual.isEmpty())) {
	    for (Iterator it03 = queryFormat.getDlgDef().getCtrldefs().keySet().iterator(); it03.hasNext();) {
 		Integer ctrlId2 = (Integer) it03.next();
                ctrlDef = (QCtrlDef)queryFormat.getDlgDef().getCtrldefs().get(ctrlId2);
                if (!fieldsNotEqual.containsKey("FLD" + ctrlDef.getFldId()) || !ctrlDef.getName().startsWith("IDOC_EDIT")) continue;
                ctrBoxDisabled.put("CTR" + (ctrlId2 - 1), new Integer(ctrlId2 - 1));
            }
        }
	for (Iterator it03 = queryFormat.getDlgDef().getCtrldefs().keySet().iterator(); it03.hasNext();) {
 	    Integer ctrlId2 = (Integer) it03.next();
            ctrlDef = (QCtrlDef)queryFormat.getDlgDef().getCtrldefs().get(ctrlId2);
            if (!ctrlDef.getName().startsWith("IDOC_EDIT")) continue;
            ctrBoxOfFldId.put(new Integer(ctrlDef.getRelCtrlId()), new Integer(ctrlDef.getFldId()));
        }
        Iterator it = queryFormat.getDlgDef().getCtrldefs().keySet().iterator();
        while (it.hasNext()) {
            Integer ctrlId2 = (Integer)it.next();
            ctrlDef = (QCtrlDef)queryFormat.getDlgDef().getCtrldefs().get(ctrlId2);
            List operators = null;
            fldId = new Integer(ctrlDef.getId());
            if (!ctrlDef.getName().startsWith("IDOC_CBOX")) continue;
            if (ctrBoxOfFldId.containsKey(new Integer(ctrlDef.getId()))) {
                fldId = (Integer)ctrBoxOfFldId.get(new Integer(ctrlDef.getId()));
            }
            operators = this.addOperators(ctrlDef.getOprs(), ctrlDef.getText(), locale, dataBaseType, fldId);
            if (!it.hasNext()) continue;
            ctrlId2 = (Integer)it.next();
            ctrlDef = (QCtrlDef)queryFormat.getDlgDef().getCtrldefs().get(ctrlId2);
            if (!ctrlDef.getName().startsWith("IDOC_EDIT")) continue;
            fldId = new Integer(ctrlDef.getFldId());
            operadoresCampos.put("FLD" + Integer.toString(fldId), operators);
        }
        return operadoresCampos;
    }

    private List addOperators(int oprs, String text, Locale locale, String dataBaseType, Integer fldId) {
        SearchOperator searchOperator = null;
        ArrayList<SearchOperator> arrayOperators = new ArrayList<SearchOperator>();
        searchOperator = this.addOperator(oprs, text, 1, "=", RBUtil.getInstance(locale).getProperty("query.equal.text.value"), fldId);
        if (searchOperator.getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 2, "!=", RBUtil.getInstance(locale).getProperty("query.not.equal.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 4, ">", RBUtil.getInstance(locale).getProperty("query.greater.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 8, ">=", RBUtil.getInstance(locale).getProperty("query.greater.equal.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 16, "<", RBUtil.getInstance(locale).getProperty("query.lesser.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 32, "<=", RBUtil.getInstance(locale).getProperty("query.lesser.equal.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 64, "..", RBUtil.getInstance(locale).getProperty("query.between.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 128, "%", RBUtil.getInstance(locale).getProperty("query.like.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 256, "|", RBUtil.getInstance(locale).getProperty("query.or.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 512, "Abc", RBUtil.getInstance(locale).getProperty("query.abc.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 1024, "C&", RBUtil.getInstance(locale).getProperty("query.in.and.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if ((searchOperator = this.addOperator(oprs, text, 2048, "c|", RBUtil.getInstance(locale).getProperty("query.in.or.text.value"), fldId)).getIdOperator() != null) {
            arrayOperators.add(searchOperator);
        }
        if (fldId != null && dataBaseType.equals("ORACLE") && (fldId == 7 || fldId == 8)) {
            searchOperator.setIdOperator(new Integer(100));
            searchOperator.setLiteral(RBUtil.getInstance(locale).getProperty("query.depend.of.value"));
            arrayOperators.add(searchOperator);
        }
        return arrayOperators;
    }

    private SearchOperator addOperator(int oprs, String text, int operator, String operatorSimbol, String operatorText, Integer fldId) {
        SearchOperator searchOperator = new SearchOperator();
        if ((oprs & operator) == operator) {
            searchOperator.setIdOperator(new Integer(operator));
            searchOperator.setLiteral(operatorText);
            searchOperator.setOperatorSimbol(operatorSimbol);
        }
        return searchOperator;
    }

    private AxSf getQueryFormat(String sessionID, Integer bookId, String idEntidad) throws ValidationException, BookException, SessionException {
        AxSf axsfQ = BookSession.getQueryFormat((String)sessionID, (Integer)bookId, (String)idEntidad);
        axsfQ.addAttributeClass("fld" + Integer.toString(5), String.class.getName());
        axsfQ.addAttributeClass("fld" + Integer.toString(7), String.class.getName());
        axsfQ.addAttributeClass("fld" + Integer.toString(8), String.class.getName());
        if (axsfQ instanceof AxSfIn) {
            axsfQ.addAttributeClass("fld" + Integer.toString(13), String.class.getName());
            axsfQ.addAttributeClass("fld" + Integer.toString(16), String.class.getName());
        } else {
            axsfQ.addAttributeClass("fld" + Integer.toString(12), String.class.getName());
        }
        return axsfQ;
    }

    public List validateAdvancedQueryParams(UseCaseConf useCaseConf, Integer bookId, Map fieldSearchAdvancedValues) throws ValidationException, BookException, AttributesException, SessionException, SecurityException {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Locale locale = useCaseConf.getLocale();
        if (locale == null) {
            locale = new Locale("es");
        }
        AxSf axsfQ = this.getQueryFormat(useCaseConf.getSessionID(), bookId, useCaseConf.getEntidadId());
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        Collection formatterFields = formatter.getDlgDef().getCtrldefs().values();
        int fldid = 0;
        String fld = null;
	for (Iterator it03 = formatterFields.iterator(); it03.hasNext();) {
	    QCtrlDef ctrlDef2=(QCtrlDef) it03.next();
            List fieldValuesList;
            fldid = ctrlDef2.getFldId();
            fld = "fld" + new Integer(fldid).toString();
            if (!ctrlDef2.getName().startsWith("IDOC_EDIT") || fieldSearchAdvancedValues.get(fld) == null || (fieldValuesList = (List)fieldSearchAdvancedValues.get(fld)) == null || fieldValuesList.size() <= 0) continue;
	    for (Iterator it04 = fieldValuesList.iterator(); it04.hasNext();) {
		RowQuerySearchAdvanced rowSearchAdv=(RowQuerySearchAdvanced) it04.next();
                Integer rowId = new Integer(rowSearchAdv.getFieldSearchAvanced().getRowId());
                try {
                    HashMap<Integer, String> idToTranslate;
                    Map idTranslated;
                    AxSfQueryField field = new AxSfQueryField();
                    field.setFldId("fld" + ctrlDef2.getFldId());
                    field.setValue(this.getValue(axsfQ, field, rowSearchAdv, ctrlDef2, null, useCaseConf.getLocale()));
                    field.setOperator(this.translateOperator(rowSearchAdv.getIdoperator(), useCaseConf.getLocale()));
                    field.setNexo(this.translateNexo(rowSearchAdv.getNexo()));
                    if (field.getValue().getClass().equals(ArrayList.class) && ((List)field.getValue()).size() != 2 && !field.getOperator().equals("|")) {
                        result.add(rowId);
                    }
                    if ((fldid == 2 || fldid == 4) && field.getValue().getClass().equals(Date.class)) {
                        String auxDate = rowSearchAdv.getValueWhere();
                        SimpleDateFormat shortFormatter = new SimpleDateFormat(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("date.shortFormat"));
                        shortFormatter.setLenient(false);
                        try {
                            if (auxDate.length() > 10) {
                                result.add(rowId);
                            }
                            shortFormatter.parse(auxDate);
                            if (shortFormatter.getCalendar().get(1) < 1970) {
                                result.add(rowId);
                            }
                        }
                        catch (Exception e) {
                            result.add(rowId);
                        }
                    }
                    if (!(fldid != 5 && fldid != 7 && fldid != 8 || field.getValue().getClass().equals(List.class))) {
                        idToTranslate = new HashMap<Integer, String>();
                        idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                        idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                        if (idTranslated == null || idTranslated.size() == 0) {
                            result.add(rowId);
                        }
                    }
                    if (axsfQ instanceof AxSfIn) {
                        if (!(fldid != 13 && fldid != 16 || field.getValue().getClass().equals(List.class))) {
                            idToTranslate = new HashMap();
                            idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                            idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                            if (idTranslated == null || idTranslated.size() == 0) {
                                result.add(rowId);
                            }
                        }
                        if (fldid <= 17 || AttributesSession.getExtendedValidationFieldValue((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)field.getValue().toString(), (String)locale.getLanguage(), (String)useCaseConf.getEntidadId()) != null) continue;
                        result.add(rowId);
                        continue;
                    }
                    if (fldid == 12) {
                        idToTranslate = new HashMap();
                        idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                        idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                        if (idTranslated == null || idTranslated.size() == 0) {
                            result.add(rowId);
                        }
                    }
                    if (fldid <= 13 || AttributesSession.getExtendedValidationFieldValue((String)useCaseConf.getSessionID(), (Integer)bookId, (int)fldid, (String)field.getValue().toString(), (String)locale.getLanguage(), (String)useCaseConf.getEntidadId()) != null) continue;
                    result.add(rowId);
                }
                catch (Exception e) {
                    result.add(rowId);
                }
            }
        }
        return result;
    }

    public List translateAdvancedQueryParams(UseCaseConf useCaseConf, Integer bookId, Map fieldSearchAdvancedValues) throws ValidationException, BookException, AttributesException, SessionException, ParseException, SecurityException {
        HashMap<Integer, AxSfQueryField> resultMap = new HashMap<Integer, AxSfQueryField>();
        AxSf axsfQ = this.getQueryFormat(useCaseConf.getSessionID(), bookId, useCaseConf.getEntidadId());
        QueryFormat formatter = new QueryFormat(axsfQ.getFormat().getData());
        Collection formatterFields = formatter.getDlgDef().getCtrldefs().values();
        int fldid = 0;
        String fld = null;
	for (Iterator it03 = formatterFields.iterator(); it03.hasNext();) {
	    QCtrlDef ctrlDef2=(QCtrlDef) it03.next();
            List fieldValuesList;
            fldid = ctrlDef2.getFldId();
            fld = "fld" + new Integer(fldid).toString();
            if (!ctrlDef2.getName().startsWith("IDOC_EDIT") || fieldSearchAdvancedValues.get(fld) == null || (fieldValuesList = (List)fieldSearchAdvancedValues.get(fld)) == null || fieldValuesList.size() <= 0) continue;
	    for (Iterator it04 = fieldValuesList.iterator(); it04.hasNext();) {
		RowQuerySearchAdvanced rowSearchAdv=(RowQuerySearchAdvanced) it04.next();
                Map idTranslated;
                HashMap<Integer, String> idToTranslate;
                AxSfQueryField field = new AxSfQueryField();
                field.setFldId("fld" + ctrlDef2.getFldId());
                field.setOperator(this.translateOperator(rowSearchAdv.getIdoperator(), useCaseConf.getLocale()));
                field.setNexo(this.translateNexo(rowSearchAdv.getNexo()));
                if (fldid == 5) {
                    idToTranslate = new HashMap<Integer, String>();
                    idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                    idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                    field.setValue(this.getValue(axsfQ, field, rowSearchAdv, ctrlDef2, idTranslated, useCaseConf.getLocale()));
                } else if (!(fldid != 7 && fldid != 8 || field.getOperator().equals("dde"))) {
                    idToTranslate = new HashMap();
                    idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                    idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                    field.setValue(this.getValue(axsfQ, field, rowSearchAdv, ctrlDef2, idTranslated, useCaseConf.getLocale()));
                } else if (axsfQ instanceof AxSfIn && (fldid == 13 || fldid == 16)) {
                    idToTranslate = new HashMap();
                    idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                    idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                    field.setValue(this.getValue(axsfQ, field, rowSearchAdv, ctrlDef2, idTranslated, useCaseConf.getLocale()));
                } else if (axsfQ instanceof AxSfOut && fldid == 12) {
                    idToTranslate = new HashMap();
                    idToTranslate.put(new Integer(fldid), rowSearchAdv.getValueWhere());
                    idTranslated = AttributesSession.translateFixedValues((String)useCaseConf.getSessionID(), (Integer)bookId, idToTranslate, (String)useCaseConf.getEntidadId());
                    field.setValue(this.getValue(axsfQ, field, rowSearchAdv, ctrlDef2, idTranslated, useCaseConf.getLocale()));
                } else {
                    field.setValue(this.getValue(axsfQ, field, rowSearchAdv, ctrlDef2, null, useCaseConf.getLocale()));
                }
                resultMap.put(rowSearchAdv.getRowId(), field);
            }
        }
        ArrayList<AxSfQueryField> result = new ArrayList<AxSfQueryField>();
        if (!resultMap.isEmpty()) {
            for (int i = 0; i < resultMap.size(); ++i) {
                AxSfQueryField queryField = (AxSfQueryField)resultMap.get(new Integer(i));
                result.add(queryField);
            }
        }
        return result;
    }

    private Object getValue(AxSf axsfQ, AxSfQueryField field, RowQuerySearchAdvanced rowQuerySearchAdvanced, QCtrlDef ctrlDef, Map translations, Locale locale) throws ParseException {
        String valueWhere = rowQuerySearchAdvanced.getValueWhere();
        int operator = rowQuerySearchAdvanced.getIdoperator();
        if (translations != null && translations.containsKey(new Integer(ctrlDef.getFldId()))) {
            return translations.get(new Integer(ctrlDef.getFldId()));
        }
        if (operator == 256 || operator == 64) {
            StringTokenizer tokenizer = new StringTokenizer(valueWhere, ";");
            ArrayList<Object> list = new ArrayList<Object>(tokenizer.countTokens());
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                list.add(this.getValue(axsfQ, field, tokenizer.nextToken(), ctrlDef, locale));
                ++i;
            }
            return list;
        }
        if (operator == 128) {
            String aux = valueWhere;
            if (StringUtils.startsWith((String)valueWhere, (String)"%") || StringUtils.endsWith((String)valueWhere, (String)"%")) {
                _logger.debug((Object)("Realizando la busqueda avanzada 'empieza con o termina con' utilizando el parametro: " + aux));
            } else {
                aux = valueWhere.replaceAll("%", "");
                aux = "%" + aux + "%";
                _logger.debug((Object)("Realizando la busqueda avanzada 'contiene con', utilizando el parametro: " + aux));
            }
            return this.getValue(axsfQ, field, aux, ctrlDef, locale);
        }
        return this.getValue(axsfQ, field, valueWhere, ctrlDef, locale);
    }

    public int openTableResults(UseCaseConf useCaseConf, Integer bookId, List<AxSfQueryField> fieldList, String listOrder) throws ValidationException, BookException, SessionException, SecurityException {
        AxSfQuery axsfQuery = new AxSfQuery();
        axsfQuery.setOrderBy(listOrder);
        axsfQuery.setBookId(bookId);
        axsfQuery.setPageSize(Integer.parseInt(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/DefaultPageTableResultsSize")));
        if (fieldList != null && fieldList.size() > 0) {
            for (AxSfQueryField field : fieldList) {
                axsfQuery.addField(field);
            }
        }
        if (axsfQuery.getPageSize() <= 0) {
            throw new BookException("bookexception.error_page_size");
        }
        int size = FolderSession.openRegistersQuery((String)useCaseConf.getSessionID(), (AxSfQuery)axsfQuery, (List)null, (Integer)new Integer(0), (String)useCaseConf.getEntidadId());
        return size;
    }
}
