package com.ieci.tecdoc.isicres.usecase.book.xml;

import com.ieci.tecdoc.common.conf.FieldConf;
import com.ieci.tecdoc.common.conf.UserConf;
import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.exception.SessionException;
import com.ieci.tecdoc.common.exception.TecDocException;
import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.common.invesdoc.Idocarchdet;
import com.ieci.tecdoc.common.invesdoc.Idocfmt;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.invesicres.ScrOrg;
import com.ieci.tecdoc.common.invesicres.ScrRegstate;
import com.ieci.tecdoc.common.isicres.AxDoch;
import com.ieci.tecdoc.common.isicres.AxPageh;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.common.isicres.AxSfIn;
import com.ieci.tecdoc.common.isicres.SessionInformation;
import com.ieci.tecdoc.common.utils.ISicresGenPerms;
import com.ieci.tecdoc.idoc.decoder.form.FDlgDef;
import com.ieci.tecdoc.idoc.decoder.form.FPageDef;
import com.ieci.tecdoc.idoc.decoder.form.FormFormat;
import com.ieci.tecdoc.idoc.decoder.validation.idocarchdet.MiscFormat;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.security.SecurityUseCase;
import com.ieci.tecdoc.utils.cache.CacheBag;
import com.ieci.tecdoc.utils.cache.CacheFactory;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLBookTree
implements Keys {
    private static Logger _logger = Logger.getLogger((Class)XMLBookTree.class);
    private static final String FOLDER_NAME = "{0}";
    private static final String FLD_LOOKFOR = "@FLD(";
    private static final String LLAVE_IZQ = "{";
    private static final String LLAVE_DER = "}";
    private static final String PAR_IZQ = ")";
    protected static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static ISicresGenPerms permisos = null;

    public static Document createXMLBookTree(UseCaseConf useCaseConf, Integer bookID, ScrRegstate scrregstate, AxSf axsf, Idocarchdet idocarchdet, boolean readOnly, long folderPId, int folderId, int row, List docs, String url, int bookType, boolean isBookAdmin, Locale locale, int vldSave, SessionInformation sessionInformation, UserConf usrConf, String archiveName) throws ValidationException, SecurityException {
        FormFormat formFormat = new FormFormat(axsf.getFormat().getData());
        String folderName = XMLBookTree.getFolderName(scrregstate, idocarchdet, folderId, axsf, archiveName);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("FrmTree");
        Element sessionInfo = root.addElement("Session");
        String readOnlyS = "1";
        if (!readOnly) {
            readOnlyS = "0";
        }
        XMLBookTree.getPermisosUsuarioCache(useCaseConf, scrregstate);
        XMLBookTree.addSessionInfo(sessionInformation, sessionInfo);
        XMLBookTree.addParams(folderName, url, folderPId, readOnlyS, folderId, vldSave, row, bookType, isBookAdmin, root);
        XMLBookTree.addProperties(useCaseConf, bookType, scrregstate.getImageAuth(), axsf, root);
        XMLBookTree.addUserConfig(root, usrConf, locale);
        if (!(formFormat.getDlgDef().getPagedefs() == null || formFormat.getDlgDef().getPagedefs().isEmpty())) {
            FPageDef page = null;
	    for (Iterator it03 = formFormat.getDlgDef().getPagedefs().keySet().iterator(); it03.hasNext();) {
		Integer key2=(Integer) it03.next();
                page = (FPageDef)formFormat.getDlgDef().getPagedefs().get(key2);
                if (page.getCtrldefs().isEmpty()) continue;
                String title = "";
                try {
                    title = axsf.getLocaleAttributePage(locale, page.getTitle());
                }
                catch (Exception e) {
                    // empty catch block
                }
                XMLBookTree.addNodeDat(key2, title, root);
            }
        }
        XMLBookTree.addNodeDocs(docs, root);
        return document;
    }

    private static void getPermisosUsuarioCache(UseCaseConf useCaseConf, ScrRegstate scrregstate) {
        CacheBag cacheBag = null;
        try {
            if (scrregstate.getState() == 0) {
                cacheBag = CacheFactory.getCacheInterface().getCacheEntry(useCaseConf.getSessionID());
                permisos = (ISicresGenPerms)cacheBag.get((Object)"GenPermsUser");
            } else {
                permisos = null;
            }
        }
        catch (SessionException sE) {
            _logger.warn((Object)"No se ha podido obtener la informaci\u00f3n de los permisos del usuario", (Throwable)sE);
            permisos = null;
        }
        catch (TecDocException tE) {
            _logger.warn((Object)"No se ha podido obtener la informaci\u00f3n de los permisos del usuario", (Throwable)tE);
            permisos = null;
        }
    }

    public static Document createEmptyXMLBookTree(Integer bookID, ScrRegstate scrregstate, AxSf axsf, Idocarchdet idocarchdet, boolean readOnly, long folderPId, int folderId, int row, List docs, String url, int bookType, Locale locale, String archiveName) {
        FormFormat formFormat = new FormFormat(axsf.getFormat().getData());
        String folderName = XMLBookTree.getFolderName(scrregstate, idocarchdet, folderId, axsf, archiveName);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("FrmTree");
        String readOnlyS = "1";
        if (!readOnly) {
            readOnlyS = "0";
        }
        XMLBookTree.addParams(folderName, url, folderPId, readOnlyS, folderId, 1, row, bookType, true, root);
        if (!(formFormat.getDlgDef().getPagedefs() == null || formFormat.getDlgDef().getPagedefs().isEmpty())) {
            FPageDef page = null;
	    for (Iterator it03 = formFormat.getDlgDef().getPagedefs().keySet().iterator(); it03.hasNext();) {
		Integer key2=(Integer) it03.next();
                page = (FPageDef)formFormat.getDlgDef().getPagedefs().get(key2);
                if (page.getCtrldefs().isEmpty()) continue;
                String title = "";
                try {
                    title = axsf.getLocaleAttributePage(locale, page.getTitle());
                }
                catch (Exception e) {
                    // empty catch block
                }
                XMLBookTree.addNodeDat(key2, title, root);
            }
        }
        XMLBookTree.addNodeDocs(docs, root);
        return document;
    }

    public static Document createXMLUserConfig(UseCaseConf useCaseConf, Integer bookID, UserConf usrConf, Locale locale) throws Exception {
        Document document = null;
        if (usrConf != null) {
            document = DocumentHelper.createDocument();
            Element root = document.addElement("UserConfig");
            List checkFields = usrConf.getFieldConf();
            Element fields = root.addElement("Fields");
            Element field = null;
            if (!(checkFields == null || checkFields.isEmpty())) {
	        for (Iterator it03 = checkFields.iterator(); it03.hasNext();) {
		    FieldConf fieldConf2=(FieldConf) it03.next();
                    field = fields.addElement("Field");
                    field.addAttribute("Id", new Integer(fieldConf2.getFieldId()).toString());
                    field.addAttribute("Checked", new Integer(fieldConf2.getFieldCheck()).toString());
                    field.addAttribute("Ind", "0");
                    field.addElement("Label").add(DocumentHelper.createCDATA((String)fieldConf2.getFieldLabel()));
                }
            }
            Element parameters = root.addElement("Parameters");
            XMLBookTree.addParameters(parameters, usrConf, locale, 1);
        }
        return document;
    }

    private static String getFolderName(ScrRegstate scrregstate, Idocarchdet idocarchdet, int folderId, AxSf axsf, String archiveName) {
        String folderName = "";
        if (idocarchdet == null) {
            folderName = folderId == -1 ? archiveName : MessageFormat.format("{0}", archiveName);
        } else {
            MiscFormat misc = new MiscFormat(idocarchdet.getDetval());
            String f = misc.getFdrname();
            String x = null;
            String sub = null;
            ArrayList<Integer> campos = new ArrayList<Integer>();
            StringBuffer transformed = new StringBuffer();
            if (StringUtils.countMatches((String)f, (String)"@FLD(") > 0) {
                int transformedIndex = 0;
                int index = 0;
                do {
                    if ((index = f.indexOf("@FLD(")) == -1) continue;
                    transformed.append(f.substring(0, index));
                    transformed.append("{" + Integer.toString(transformedIndex++) + "}");
                    sub = f.substring(index + "@FLD(".length(), f.length());
                    x = sub.substring(0, sub.indexOf(")"));
                    campos.add(new Integer(x));
                    f = f.substring(index + "@FLD(".length() + x.length() + 1, f.length());
                } while (index != -1);
            } else {
                transformed.append(f);
            }
            if (!campos.isEmpty()) {
                ArrayList<String> values = new ArrayList<String>(campos.size());
                Iterator it = campos.iterator();
                while (it.hasNext()) {
                    values.add(axsf.getAttributeValueAsString("fld" + it.next().toString()));
                }
                folderName = MessageFormat.format(transformed.toString(), values.toArray());
            } else {
                folderName = transformed.length() > 0 ? transformed.toString() : (folderId == -1 ? archiveName : MessageFormat.format("{0}", archiveName));
            }
        }
        return folderName;
    }

    private static void addSessionInfo(SessionInformation sessionInformation, Element parent) {
        parent.addElement("User").addText(sessionInformation.getUser());
        parent.addElement("UserName").add(DocumentHelper.createCDATA((String)sessionInformation.getUserName()));
        parent.addElement("OfficeCode").addText(sessionInformation.getOfficeCode());
        parent.addElement("OfficeName").add(DocumentHelper.createCDATA((String)sessionInformation.getOfficeName()));
        parent.addElement("OfficeEnabled").addText(sessionInformation.getOfficeEnabled());
    }

    private static void addParams(String folderName, String fileForm, long folderPid, String fdrReadOnly, int folderId, int vldSave, int row, int bookType, boolean isBookAdmin, Element parent) {
        Element node = parent.addElement("PARAMS");
        node.addElement("FolderName").add(DocumentHelper.createCDATA((String)folderName));
        if (fileForm != null) {
            node.addElement("FileForm").addText(fileForm);
        } else {
            node.addElement("FileForm").addText("");
        }
        node.addElement("FolderPId").addText(Long.toString(folderPid));
        node.addElement("FdrReadOnly").addText(fdrReadOnly);
        node.addElement("FolderId").addText(Integer.toString(folderId));
        node.addElement("VldSave").addText(Integer.toString(vldSave));
        node.addElement("Row").addText(Integer.toString(row));
        node.addElement("BookType").addText(Integer.toString(bookType));
        if (isBookAdmin) {
            node.addElement("IsBookAdm").addText(Integer.toString(1));
        } else {
            node.addElement("IsBookAdm").addText(Integer.toString(0));
        }
        if (permisos != null) {
            node.addElement("CanDist").addText(Integer.toString(permisos.isCanDistRegisters() ? 1 : 0));
        } else {
            node.addElement("CanDist").addText(Integer.toString(0));
        }
    }

    private static void addProperties(UseCaseConf useCaseConf, int bookType, int authentication, AxSf axsf, Element parent) throws ValidationException, SecurityException {
        Element node = parent.addElement("Properties");
        SecurityUseCase securityUseCase = new SecurityUseCase();
        Date regDate = (Date)axsf.getAttributeValue("fld2");
        String regDateString = "";
        String regNumberString = "";
        String strUnitCode = "";
        String strStampOficCode = "";
        String strStampOficDesc = "";
        ScrOfic ofic = securityUseCase.getCurrentUserOfic(useCaseConf);
        if (regDate != null) {
            regDateString = FORMATTER.format(regDate);
        }
        if (axsf.getAttributeValue("fld1") != null) {
            regNumberString = (String)axsf.getAttributeValue("fld1");
        }
        if (axsf.getAttributeValue("fld5") != null) {
            strStampOficCode = axsf.getFld5().getCode();
            strStampOficDesc = !StringUtils.isBlank((String)axsf.getFld5().getStamp()) ? axsf.getFld5().getStamp() : axsf.getFld5Name();
            strStampOficDesc = strStampOficDesc.replaceAll("\r\n", "\r");
        }
        strUnitCode = axsf instanceof AxSfIn ? (axsf.getFld8() != null ? axsf.getFld8().getCode() : "") : (axsf.getFld7() != null ? axsf.getFld7().getCode() : "");
        node.addElement("BookType").addText(Integer.toString(bookType));
        node.addElement("Authentication").addText(Integer.toString(authentication));
        node.addElement("RegNumber").addText(regNumberString);
        node.addElement("RegDate").addText(regDateString);
        node.addElement("StampOfficeCode").addText(strStampOficCode);
        node.addElement("StampOfficeDesc").addText(strStampOficDesc);
        node.addElement("UnitCode").addText(strUnitCode);
    }

    private static void addNodeDat(int id, String title, Element parent) {
        Element node = parent.addElement("Node");
        node.addAttribute("Type", "Dat");
        node.addElement("Id").addText(Integer.toString(id));
        node.addElement("Title").add(DocumentHelper.createCDATA((String)title));
    }

    private static void addNodePage(String title, int id, int order, String ext, Element parent) {
        Element node = parent.addElement("Node");
        node.addAttribute("Type", "Pag");
        node.addElement("Title").add(DocumentHelper.createCDATA((String)title));
        node.addElement("Id").addText(Integer.toString(id));
        node.addElement("Order").addText(Integer.toString(order));
        node.addElement("Ext").add(DocumentHelper.createCDATA((String)ext));
    }

    private static void addNodeDocs(List<AxDoch> docs, Element parent) {
        Element infDoc = parent.addElement("Node");
        infDoc.addAttribute("Type", "InfDoc");
        if (!(docs == null || docs.isEmpty())) {
            for (AxDoch axdoch2 : docs) {
                Element doc = infDoc.addElement("Node");
                doc.addAttribute("Type", "Doc");
                doc.addElement("Title").add(DocumentHelper.createCDATA((String)axdoch2.getName()));
                doc.addElement("Id").addText(Integer.toString(axdoch2.getId()));
		for (Iterator it03 = axdoch2.getPages().iterator(); it03.hasNext();) {
		    AxPageh axpageh2=(AxPageh) it03.next();
                    XMLBookTree.addNodePage(axpageh2.getName(), axpageh2.getId(), axpageh2.getSortOrder(), axpageh2.getLoc(), doc);
                }
            }
        }
    }

    private static void addUserConfig(Element parent, UserConf usrConf, Locale locale) {
        Element usrCf = null;
        if (usrConf != null) {
            usrCf = parent.addElement("UserConfig");
            List checkFields = usrConf.getFieldConf();
            Element fields = usrCf.addElement("Fields");
            Element field = null;
            if (!(checkFields == null || checkFields.isEmpty())) {
		for (Iterator it03 = checkFields.iterator(); it03.hasNext();) {
		    FieldConf fieldConf2=(FieldConf) it03.next();
                    if (fieldConf2.getFieldCheck() != 1) continue;
                    field = fields.addElement("Field");
                    field.addAttribute("Id", new Integer(fieldConf2.getFieldId()).toString());
                }
            }
            Element parameters = usrCf.addElement("Parameters");
            XMLBookTree.addParameters(parameters, usrConf, locale, 0);
        }
    }

    private static void addParameters(Element parent, UserConf usrConf, Locale locale, int type) {
        Element parameter = null;
        for (int i = 1; i < 5; ++i) {
            parameter = parent.addElement("Parameter");
            parameter.addAttribute("Id", new Integer(i).toString());
            if (i == 1) {
                parameter.addAttribute("Checked", new Integer(usrConf.getPersonValidation()).toString());
            }
            if (i == 2) {
                parameter.addAttribute("Checked", new Integer(usrConf.getShowScanDlg()).toString());
            }
            if (i == 3) {
                parameter.addAttribute("Checked", new Integer(usrConf.getRememberLastSelectedUnit()).toString());
            }
            if (i == 4 || type != 1) continue;
            parameter.addAttribute("Ind", "1");
            parameter.addElement("Label").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("userconf.parameter." + i)));
        }
    }
}
