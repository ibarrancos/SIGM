package com.ieci.tecdoc.isicres.servlets;

import com.ieci.tecdoc.common.exception.AttributesException;
import com.ieci.tecdoc.common.exception.BookException;
import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.exception.SessionException;
import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.common.invesicres.ScrRegstate;
import com.ieci.tecdoc.common.utils.Configurator;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrDocument;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrField;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrFile;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrInter;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrPage;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.desktopweb.utils.RequestUtils;
import com.ieci.tecdoc.isicres.desktopweb.utils.ResponseUtils;
import com.ieci.tecdoc.isicres.events.exception.EventException;
import com.ieci.tecdoc.isicres.session.book.BookSession;
import com.ieci.tecdoc.isicres.session.folder.FolderSession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.book.BookUseCase;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLSaveFdrErrors;
import es.ieci.tecdoc.fwktd.core.config.web.ContextUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.DocumentSource;

public class FlushFdr
extends HttpServlet
implements Keys {
    private static Logger _logger = Logger.getLogger((Class)FlushFdr.class);
    private static final String FIELD_TYPE_STRING_TREEUPDATE = "TreeUpdate";
    private static final String FIELD_TYPE_STRING_FLDDATA = "FldData";
    private static final String FIELD_TYPE_STRING_SESSIONPID = "SessionPId";
    private static final String FIELD_TYPE_STRING_FOLDERPID = "FolderPId";
    private static final String FIELD_TYPE_STRING_ARCHIVEPID = "ArchivePId";
    private static final String FIELD_TYPE_STRING_FOLDERID = "FolderId";
    private static final String FIELD_TYPE_STRING_FOLDERIDAUX = "FolderIdAux";
    private static final String FIELD_TYPE_STRING_ARCHIVEID = "ArchiveId";
    private static final String FIELD_TYPE_STRING_FILESCAN = "FileScan";
    private static final String FILEDATA = "FileData";
    private static final int FIELD_TYPE_TREEUPDATE = 1;
    private static final int FIELD_TYPE_FLDDATA = 2;
    private static final int FIELD_TYPE_SESSIONPID = 3;
    private static final int FIELD_TYPE_FOLDERPID = 4;
    private static final int FIELD_TYPE_ARCHIVEPID = 5;
    private static final int FIELD_TYPE_FOLDERID = 6;
    private static final int FIELD_TYPE_ARCHIVEID = 7;
    private static final int FIELD_TYPE_FILESCAN = 8;
    private static final String PUNTO_COMA = ";";
    private static final String BARRA = "|";
    private static final String DOSBARRA = "||";
    private static final String DOSPATH = "\\";
    private static final String PUNTO = ".";
    private static final String INTERROGACION = "?";
    private static final String ALMOHADILLA = "#";
    private static final String IGUAL = "=";
    private static final String AMPERSAN = "&";
    private static final String AMPERSANDOBLE = "&&";
    private static final String GUIONBAJO = "_";
    private BookUseCase bookUseCase = null;
    private TransformerFactory factory = null;

    public void init() throws ServletException {
        super.init();
        this.bookUseCase = new BookUseCase();
        this.factory = TransformerFactory.newInstance();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doWork(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doWork(request, response);
    }

    private void doWork(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/html; charset=UTF-8");
        String strFilesScan = RequestUtils.parseRequestParameterAsStringWithEmpty(request, "FileScan");
        String treeUpdate = RequestUtils.parseRequestParameterAsStringWithEmpty(request, "TreeUpdate");
        String fldData = RequestUtils.parseRequestParameterAsStringWithEmpty(request, "FldData");
        String fileDataReq = RequestUtils.parseRequestParameterAsStringWithEmpty(request, "FileData");
        Integer folderIdAux = RequestUtils.parseRequestParameterAsInteger(request, "FolderIdAux");
        Integer bookID = RequestUtils.parseRequestParameterAsInteger(request, "ArchiveId");
        Integer newFolderID = new Integer(-1);
        Integer folderIDFromRequest = RequestUtils.parseRequestParameterAsInteger(request, "FolderId");
        String sessionPId = RequestUtils.parseRequestParameterAsStringWithEmpty(request, "SessionPId");
        Integer folderPId = RequestUtils.parseRequestParameterAsInteger(request, "FolderPId");
        Integer folderID = RequestUtils.parseRequestParameterAsInteger(request, "FolderId");
        boolean errorPageName = false;
        HttpSession session = request.getSession();
        String idioma = (String)session.getAttribute("JIdioma");
        Long numIdioma = (Long)session.getAttribute("JNumIdioma");
        UseCaseConf useCaseConf = (UseCaseConf)session.getAttribute("JUseCaseConf");
        Integer row = RequestUtils.parseRequestParameterAsInteger(request, "Row", new Integer(-1));
        Long maxUploadFileSize = new Long(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/MaxUploadFileSize"));
        File fNew = null;
        PrintWriter writer = response.getWriter();
        Document xmlDocument = null;
        try {
            this.validationSecurityUser(session, bookID, folderIDFromRequest, useCaseConf);
            String allFields = "";
            boolean isMultipart = FileUpload.isMultipartContent((HttpServletRequest)request);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("Content Type =" + request.getContentType()));
            }
            DiskFileUpload fu = new DiskFileUpload();
            fu.setSizeMax(maxUploadFileSize.longValue());
            List fileItems = fu.parseRequest(request);
            Iterator itr = fileItems.iterator();
            StringBuffer fileData = new StringBuffer();
            List fieldItems = null;
            ArrayList<FlushFdrDocument> docItems = new ArrayList<FlushFdrDocument>();
            ArrayList<FlushFdrPage> pageItems = new ArrayList<FlushFdrPage>();
            ArrayList<String> keys = new ArrayList<String>();
            List inter = new ArrayList();
            ArrayList<FlushFdrFile> filesInfo = new ArrayList<FlushFdrFile>();
            ArrayList filesScanInfo = new ArrayList();
            while (itr.hasNext()) {
                FlushFdrFile flushFdrFile;
                FileItem fi = (FileItem)itr.next();
                if (!fi.isFormField()) {
                    Object[] obj = new Object[2];
                    String aux = fi.getName();
                    if (aux.equals("")) continue;
                    String fileNameFis = null;
                    String extension = this.getExtension(fi.getName());
                    int order = this.getOrderFileScan(fi.getFieldName());
                    if (folderIDFromRequest == null) {
                        Integer folderIdCurrent = (Integer)session.getAttribute("JRegister");
                        fileNameFis = this.getFileNameFis(useCaseConf.getSessionID(), folderIdCurrent.toString(), order, fi.getName());
                    } else {
                        fileNameFis = this.getFileNameFis(useCaseConf.getSessionID(), folderIDFromRequest.toString(), order, fi.getName());
                    }
                    File newDir = null;
                    newDir = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName/@isRelative") ? new File(ContextUtil.getRealPath((ServletContext)session.getServletContext(), (String)Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName"))) : new File(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName"));
                    if (!newDir.exists()) {
                        newDir.mkdir();
                    }
                    fNew = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName/@isRelative") ? new File(ContextUtil.getRealPath((ServletContext)session.getServletContext(), (String)Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName")), fileNameFis) : new File(Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName"), fileNameFis);
                    fNew.deleteOnExit();
                    if (_logger.isDebugEnabled()) {
                        _logger.debug((Object)("fNew.getName(): " + fNew.getName()));
                        _logger.debug((Object)("fNew.getPath(): " + fNew.getPath()));
                    }
                    flushFdrFile = new FlushFdrFile();
                    flushFdrFile.setOrder(new Integer(order).intValue());
                    flushFdrFile.setExtension(extension);
                    flushFdrFile.setFileNameFis(fNew.getAbsolutePath());
                    flushFdrFile.setFileNameLog(aux.substring(aux.lastIndexOf("\\") + 1, aux.length()));
                    filesInfo.add(flushFdrFile);
                    fi.write(fNew);
                    fileData.append(fNew.getAbsolutePath());
                    fileData.append("|");
                    fileData.append(fNew.getName());
                    fileData.append("||");
                    continue;
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug((Object)("Field =" + fi.getFieldName()));
                    _logger.debug((Object)("Value =" + fi.getString("UTF-8")));
                }
                int intField = 0;
                StringBuffer params = new StringBuffer();
                intField = this.mappingStrFieldToInt(fi.getFieldName());
                switch (intField) {
                    case 1: {
                        String type;
                        String treeUpdateForParse = "";
                        treeUpdateForParse = fi.getString("UTF-8");
                        if (_logger.isDebugEnabled()) {
                            _logger.debug((Object)("treeUpdateForParse: " + treeUpdateForParse));
                        }
                        if ((type = this.parseTree(treeUpdateForParse)).equals("CL2")) {
                            FlushFdrDocument flushFdrDocument = this.parseDoc(treeUpdateForParse);
                            String claveDoc = flushFdrDocument.getTreeId();
                            keys.add(claveDoc);
                            docItems.add(flushFdrDocument);
                        }
                        if (!type.equals("CL3")) break;
                        FlushFdrPage flushFdrPage = this.parsePage(treeUpdateForParse);
                        Object ext = null;
                        String pageName = flushFdrPage.getPageName();
                        String clave = flushFdrPage.getFatherId();
                        if (!keys.contains(clave)) {
                            FlushFdrDocument flushFdrDocument = new FlushFdrDocument();
                            flushFdrDocument.setTreeId(clave);
                            flushFdrDocument.setDocumentName(this.bookUseCase.getDocName(useCaseConf, bookID, folderID, flushFdrPage.getFatherId()));
                            docItems.add(flushFdrDocument);
                            keys.add(clave);
                        }
                        pageItems.add(flushFdrPage);
                        break;
                    }
                    case 2: {
                        String fldDataForParse = "";
                        fldDataForParse = fi.getString("UTF-8");
                        fldDataForParse = fldDataForParse.replaceAll("\r\n", "\n");
                        fieldItems = this.parseData(fldDataForParse);
                        params.append("&");
                        params.append("FldData");
                        params.append("=");
                        params.append(fi.getString("UTF-8"));
                        break;
                    }
                    case 8: {
                        StringTokenizer tokens = new StringTokenizer(fi.getString("UTF-8"), "|");
                        String idFile = null;
                        String fileScanName = null;
                        while (tokens.hasMoreTokens()) {
                            idFile = tokens.nextToken();
                            fileScanName = tokens.nextToken();
                            fileScanName = fileScanName.substring(1, fileScanName.length());
                            if (!_logger.isDebugEnabled()) continue;
                            _logger.debug((Object)("idFile FILESCAN: " + idFile));
                            _logger.debug((Object)("fileScanName FILESCAN: " + fileScanName));
                        }
                        int orderFScan = this.getOrderFileScan(idFile);
                        flushFdrFile = new FlushFdrFile();
                        flushFdrFile.setOrder(orderFScan);
                        flushFdrFile.setExtension(this.getExtension(fileScanName));
                        String fileName = null;
                        if (folderIDFromRequest == null) {
                            Integer folderIdCurrent = (Integer)session.getAttribute("JRegister");
                            fileName = this.getFileNameFis(useCaseConf.getSessionID(), folderIdCurrent.toString(), orderFScan, fileScanName);
                        } else {
                            fileName = this.getFileNameFis(useCaseConf.getSessionID(), folderIDFromRequest.toString(), orderFScan, fileScanName);
                        }
                        String beginPath = null;
                        beginPath = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName/@isRelative") ? ContextUtil.getRealPath((ServletContext)session.getServletContext(), (String)Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName")) : Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/TemporalDirectoryName");
                        flushFdrFile.setFileNameFis(beginPath + File.separator + fileName);
                        flushFdrFile.setFileNameLog(fileScanName);
                        filesInfo.add(flushFdrFile);
                        if (!_logger.isDebugEnabled()) break;
                        _logger.debug((Object)("############filesScanInfo: " + flushFdrFile.toString()));
                        break;
                    }
                }
                allFields = allFields + params.toString();
            }
            if (!sessionPId.equals(useCaseConf.getSessionID())) {
                throw new SessionException("sessionexception.error_session_expired");
            }
            if (!strFilesScan.equals("")) {
                fileData.insert(0, strFilesScan);
            }
	    for (Iterator it03 = fieldItems.iterator(); it03.hasNext();) {
		FlushFdrField flushFdrField = (FlushFdrField) it03.next();
                if (_logger.isDebugEnabled()) {
                    _logger.debug((Object)flushFdrField);
                }
                if (flushFdrField.getFldid() != 9) continue;
                inter = this.parseInter(flushFdrField.getValue());
                flushFdrField.setValue(((FlushFdrInter)inter.get(0)).getInterName());
            }
            if (_logger.isDebugEnabled()) {
                for (FlushFdrFile flI : filesInfo) {
                    _logger.debug((Object)("===================> filesInfo " + flI.toString()));
                }
                for (String clv : keys) {
                    _logger.debug((Object)("===================> keys " + clv));
                }
            }
            Map documents = this.generateTreeUpdateMap(docItems, pageItems, keys);
            FlushFdrDocument flushFdrDocument = null;
            FlushFdrFile flushFdrFile = null;
	    for (Iterator it03 = documents.keySet().iterator(); it03.hasNext();) {
		String key=(String) it03.next();
                if (_logger.isDebugEnabled()) {
                    _logger.debug((Object)("===================> Map keysdocument " + key));
                }
                flushFdrDocument = (FlushFdrDocument)documents.get(key);
                if (_logger.isDebugEnabled()) {
                    _logger.debug((Object)("===================> Map flushFdrDocument " + flushFdrDocument.toString()));
                }
		for (Iterator it04 = flushFdrDocument.getPages().iterator(); it04.hasNext();) {
		    FlushFdrPage flushFdrPage2=(FlushFdrPage) it04.next();
                    if (_logger.isDebugEnabled()) {
                        _logger.debug((Object)("===================> Map flushFdrPage " + flushFdrPage2.toString()));
                    }
                    Iterator it2 = filesInfo.iterator();
                    while (it2.hasNext() && flushFdrPage2.getFile() == null) {
                        flushFdrFile = (FlushFdrFile)it2.next();
                        if (_logger.isDebugEnabled()) {
                            _logger.debug((Object)("===================> Map flushFdrFile " + flushFdrFile.toString()));
                        }
                        String fileNameLog = flushFdrFile.getFileNameLog();
                        String pageName = flushFdrPage2.getPageName();
                        String fileOrder = String.valueOf(flushFdrFile.getOrder());
                        String pageCode = flushFdrPage2.getTreeId().substring(flushFdrPage2.getTreeId().length() - 1, flushFdrPage2.getTreeId().length());
                        if (!fileNameLog.equals(pageName) && !fileOrder.equals(pageCode)) continue;
                        if (_logger.isDebugEnabled()) {
                            _logger.debug((Object)("===================> entra en flushFdrFile.getFileNameLog().equals(flushFdrPage.getPageName()) " + flushFdrFile.toString()));
                        }
                        String pageExtension = this.getExtension(pageName);
                        String fileExtension = flushFdrFile.getExtension();
                        if (StringUtils.isEmpty((String)pageExtension) || "-".equalsIgnoreCase(pageExtension) || !fileExtension.equalsIgnoreCase(pageExtension)) {
                            pageName = this.changeExtension(pageName, fileExtension, pageExtension);
                        }
                        if (pageName.length() > 64) {
                            errorPageName = true;
                            _logger.error((Object)("El nombre de la pagina [" + pageName + "] excede los 64 caracteres"));
                            throw new ValidationException("validationexception.error_page_name_length");
                        }
                        flushFdrFile.setFileNameLog(pageName);
                        flushFdrPage2.setFile(flushFdrFile);
                    }
                    flushFdrPage2.getFile().loadFile();
                }
            }
            if (bookID == null) {
                throw new SessionException("sessionexception.error_session_expired");
            }
            List badCtrls = this.bookUseCase.validateFolder(useCaseConf, bookID, folderID, filesInfo, fieldItems, documents);
            if (badCtrls.isEmpty()) {
                folderIdAux = new Integer(this.bookUseCase.saveOrUpdateFolder(useCaseConf, bookID, folderID, filesInfo, fieldItems, inter, documents));
		for (Iterator it03 = filesInfo.iterator(); it03.hasNext();) {
		    FlushFdrFile file2 = (FlushFdrFile) it03.next();
                    try {
                        new File(file2.getFileNameFis()).delete();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (folderID == -1) {
                    String archFolder = (String)session.getAttribute("JStrCarpeta");
                    StringBuffer strCarpeta = new StringBuffer();
                    strCarpeta.append(bookID.toString());
                    strCarpeta.append("|");
                    strCarpeta.append(folderIdAux.toString());
                    strCarpeta.append(";");
                    if (archFolder != null) {
                        strCarpeta.insert(0, archFolder);
                    }
                    session.setAttribute("JStrCarpeta", (Object)strCarpeta.toString());
                }
                session.setAttribute("JRegister", (Object)folderIdAux);
                xmlDocument = this.bookUseCase.getBookTree(useCaseConf, bookID, false, folderPId.intValue(), folderIdAux, row, this.getBeforeEncondedResponseURL(request, response), false, useCaseConf.getLocale());
            } else {
                xmlDocument = XMLSaveFdrErrors.createXMLErrors(useCaseConf, badCtrls);
            }
            String xslPath = ContextUtil.getRealPath((ServletContext)session.getServletContext(), (String)"/xsl/frmt.xsl");
            StreamSource s = new StreamSource(new InputStreamReader(new BufferedInputStream(new FileInputStream(xslPath))));
            Templates cachedXSLT = this.factory.newTemplates(s);
            Transformer transformer = cachedXSLT.newTransformer();
            DocumentSource source = new DocumentSource(xmlDocument);
            StreamResult result = new StreamResult(writer);
            transformer.transform((Source)source, result);
        }
        catch (FileUploadException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("FileUploadException...............1 " + System.currentTimeMillis()));
            }
            _logger.fatal((Object)"Error cargando ficheros", (Throwable)e);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("FileUploadException...............2 " + System.currentTimeMillis()));
            }
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("FileUploadException...............3 " + System.currentTimeMillis()));
            }
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("FileUploadException...............4 " + System.currentTimeMillis()));
            }
            String msg = MessageFormat.format(RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.maxuploadfilesize"), maxUploadFileSize.toString());
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("FileUploadException...............5 " + System.currentTimeMillis()));
            }
            ResponseUtils.generateJavaScriptLog(writer, msg);
            if (_logger.isDebugEnabled()) {
                _logger.debug((Object)("FileUploadException...............6 " + System.currentTimeMillis()));
            }
        }
        catch (RemoteException e) {
            _logger.fatal((Object)"Error de comunicaciones", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptLogForSave(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.remoteException"));
        }
        catch (AttributesException e) {
            _logger.fatal((Object)"Error en los atributos", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptError((Writer)writer, e, useCaseConf.getLocale());
        }
        catch (SecurityException e) {
            _logger.fatal((Object)"Error de seguridad", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptError((Writer)writer, e, useCaseConf.getLocale());
        }
        catch (ValidationException e) {
            _logger.fatal((Object)"Error de validacion", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            if (errorPageName) {
                ResponseUtils.generateJavaScriptLogForSave(writer, e.getMessage());
            } else {
                ResponseUtils.generateJavaScriptLogForSave(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.validationException"));
            }
        }
        catch (BookException e) {
            _logger.fatal((Object)"Error en el libro", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptError((Writer)writer, e);
        }
        catch (EventException e) {
            _logger.fatal((Object)"Error en el evento", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptError((Writer)writer, (Exception)e);
        }
        catch (SessionException e) {
            _logger.fatal((Object)"Error en la sesion", (Throwable)e);
            ResponseUtils.generateJavaScriptLogSessionExpiredDtrfdr(writer, e.getMessage(), idioma, numIdioma);
        }
        catch (TransformerConfigurationException e) {
            _logger.fatal((Object)"Error al guardar", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptLogForSave(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_QRY_ABORT_CAUSE_INVALID_TEXT"));
        }
        catch (TransformerFactoryConfigurationError e) {
            _logger.fatal((Object)"Error al guardar", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptLogForSave(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_QRY_ABORT_CAUSE_INVALID_TEXT"));
        }
        catch (TransformerException e) {
            _logger.fatal((Object)"Error al guardar", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptLogForSave(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_QRY_ABORT_CAUSE_INVALID_TEXT"));
        }
        catch (Exception e) {
            _logger.fatal((Object)"Error al guardar", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorForUpdateFolderNoBadCtrls(writer);
            ResponseUtils.generateJavaScriptErrorForSave(writer);
            ResponseUtils.generateJavaScriptLogForSave(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_ERR_CREATING_FDRSAVE_OBJ"));
        }
    }

    private int getOrderFileScan(String idFile) {
        int result;
        try {
            String codigoFichero = idFile.substring("LI".length(), idFile.length());
            result = Integer.parseInt(codigoFichero);
        }
        catch (Exception e) {
            _logger.warn((Object)("No se ha podido obtener el orden del documento: " + idFile));
            result = Integer.parseInt(idFile.substring(idFile.length() - 1, idFile.length()));
        }
        return result;
    }

    private void validationSecurityUser(HttpSession session, Integer bookID, Integer folderID, UseCaseConf useCaseConf) throws BookException, SessionException, ValidationException {
        int size;
        ScrRegstate scrregstate = BookSession.getBook((String)useCaseConf.getSessionID(), (Integer)bookID);
        if (scrregstate.getState() == 1) {
            _logger.warn((Object)("El libro [" + bookID + "] esta cerrado no se puede modificar el registro [" + folderID + "]"));
            throw new BookException("bookexception.error_book_close");
        }
        Integer idFolderAux = null;
        idFolderAux = folderID == null ? (Integer)session.getAttribute("JRegister") : folderID;
        if (idFolderAux != null && idFolderAux != -1 && (size = FolderSession.getCountRegisterByIdReg((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId(), (Integer)bookID, (Integer)idFolderAux)) == 0) {
            _logger.warn((Object)("El usuario [" + useCaseConf.getUserName() + "] no tiene acceso al registro [" + idFolderAux + "] del Libro [" + bookID + "] con lo que no puede modificarlo"));
            throw new BookException("bookexception.update_folder");
        }
    }

    private int mappingStrFieldToInt(String field) {
        int result = 0;
        HashMap<String, Integer> optionId = new HashMap<String, Integer>();
        optionId.put("TreeUpdate", new Integer(1));
        optionId.put("FldData", new Integer(2));
        optionId.put("SessionPId", new Integer(3));
        optionId.put("FolderPId", new Integer(4));
        optionId.put("ArchivePId", new Integer(5));
        optionId.put("FolderId", new Integer(6));
        optionId.put("ArchiveId", new Integer(7));
        optionId.put("FileScan", new Integer(8));
        if ((Integer)optionId.get(field) != null) {
            result = (Integer)optionId.get(field);
        }
        return result;
    }

    private String getBeforeEncondedResponseURL(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getQueryString();
        query = query.replaceFirst("JOpenFolderType=" + J_OPENFOLDER_TYPE_XMLBOOKTREE_UDPATE_BUTTON, "JOpenFolderType=" + J_OPENFOLDER_TYPE_XMLBOOK);
        String path = request.getServletPath();
        path = path.substring(5, path.length());
        StringBuffer buffer = new StringBuffer();
        buffer.append(path);
        buffer.append("?");
        buffer.append(query);
        buffer.append("&");
        buffer.append("JOpenFolderType");
        buffer.append("=");
        buffer.append(J_OPENFOLDER_TYPE_XMLBOOK);
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("URL2: " + buffer.toString()));
        }
        return response.encodeURL(buffer.toString());
    }

    private List parseData(String data) {
        ArrayList<FlushFdrField> result = new ArrayList<FlushFdrField>();
        FlushFdrField dataField = null;
        String token = null;
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("********************* data : " + data));
        }
        StringTokenizer doblesBarras = new StringTokenizer(data, "|", true);
        while (doblesBarras.hasMoreTokens()) {
            dataField = new FlushFdrField();
            token = doblesBarras.nextToken();
            dataField.setFldid(new Integer(token).intValue());
            token = doblesBarras.nextToken();
            token = doblesBarras.nextToken();
            if (!token.equals("|")) {
                dataField.setValue(this.decodeValue(token));
                token = doblesBarras.nextToken();
            } else {
                dataField.setValue(null);
            }
            token = doblesBarras.nextToken();
            dataField.setCtrlid(new Integer(token).intValue());
            token = doblesBarras.nextToken();
            token = doblesBarras.nextToken();
            result.add(dataField);
        }
        return result;
    }

    private String decodeValue(String value) {
        String result = null;
        try {
            result = URLDecoder.decode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            _logger.error((Object)("Excepci\u00f3n decodificando valor:" + value + "en decodeValue"));
            throw new RuntimeException(e);
        }
        return result;
    }

    private List parseInter(String inter) {
        ArrayList<FlushFdrInter> result = new ArrayList<FlushFdrInter>();
        FlushFdrInter dataInter = null;
        if (inter != null) {
            String[] interesados;
            for (String interesado : interesados = StringUtils.split((String)inter, (String)"&&")) {
                this.parseDataInter(interesado, result);
            }
        } else {
            dataInter = new FlushFdrInter();
            dataInter.setInterId(-1);
            result.add(dataInter);
        }
        return result;
    }

    private void parseDataInter(String interesado, List result) {
        FlushFdrInter dataInter = null;
        FlushFdrInter representante = null;
        String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens((String)interesado, (String)"#");
        block16 : for (int i = 0; i < tokens.length; ++i) {
            String valorIt = tokens[i];
            switch (i) {
                case 0: {
                    dataInter = new FlushFdrInter();
                    Integer dataInterId = new Integer(tokens[i].trim());
                    dataInter.setInterId(dataInterId.intValue());
                    continue block16;
                }
                case 1: {
                    dataInter.setInterName(this.decodeValue(valorIt));
                    continue block16;
                }
                case 2: {
                    if (StringUtils.isEmpty((String)tokens[i])) continue block16;
                    try {
                        Integer dataDomId = new Integer(tokens[i]);
                        dataInter.setDomId(dataDomId.intValue());
                    }
                    catch (NumberFormatException e) {}
                    continue block16;
                }
                case 3: {
                    dataInter.setDirection(tokens[i]);
                    continue block16;
                }
                case 5: {
                    if (StringUtils.isBlank((String)tokens[i])) continue block16;
                    representante = new FlushFdrInter();
                    try {
                        representante.setInterId(Integer.valueOf(tokens[i]).intValue());
                    }
                    catch (NumberFormatException nfe) {}
                    continue block16;
                }
                case 6: {
                    if (null == representante) continue block16;
                    representante.setInterName(tokens[i]);
                    continue block16;
                }
                case 7: {
                    if (null == representante || StringUtils.isBlank((String)tokens[i])) continue block16;
                    try {
                        representante.setDomId(Integer.valueOf(tokens[i]).intValue());
                    }
                    catch (NumberFormatException nfe) {}
                    continue block16;
                }
                case 8: {
                    if (null == representante) continue block16;
                    representante.setDirection(tokens[i]);
                    dataInter.setRepresentante(representante);
                }
            }
        }
        result.add(dataInter);
    }

    private FlushFdrDocument parseDoc(String treeData) {
        FlushFdrDocument result = new FlushFdrDocument();
        StringTokenizer tokens = new StringTokenizer(treeData, "|");
        int i = 0;
        String mostrar = "";
        while (tokens.hasMoreTokens()) {
            mostrar = tokens.nextToken();
            switch (i) {
                case 2: {
                    result.setTreeId(mostrar);
                    break;
                }
                case 3: {
                    result.setDocumentName(mostrar);
                    break;
                }
                case 4: {
                    result.setFatherId(mostrar);
                    break;
                }
                case 5: {
                    result.setFatherClassName(mostrar);
                }
            }
            ++i;
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("result FlushFdrDocument : " + (Object)result));
        }
        return result;
    }

    private FlushFdrPage parsePage(String treeData) {
        FlushFdrPage result = new FlushFdrPage();
        StringTokenizer tokens = new StringTokenizer(treeData, "|");
        int i = 0;
        String mostrar = "";
        while (tokens.hasMoreTokens()) {
            mostrar = tokens.nextToken();
            if (i == 2) {
                result.setTreeId(mostrar);
            }
            if (i == 3) {
                result.setPageName(mostrar);
            }
            if (i == 4) {
                result.setFatherId(mostrar);
            }
            if (i == 5) {
                result.setFatherClassName(mostrar);
            }
            ++i;
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("result FlushFdrPage : " + (Object)result));
        }
        return result;
    }

    private String parseTree(String treeData) {
        StringTokenizer tokens = new StringTokenizer(treeData, "|");
        String mostrar = "";
        while (!tokens.hasMoreTokens() || (mostrar = tokens.nextToken()).equals("CL2") || mostrar.equals("CL3")) {
        }
        return mostrar;
    }

    private Map generateTreeUpdateMap(List<FlushFdrDocument> docItems, List pageItems, List claves) {
        HashMap<String, FlushFdrDocument> documents = new HashMap<String, FlushFdrDocument>();
        ArrayList<FlushFdrPage> resultPages = null;
        Object docExist = null;
        String clavedocument = null;
        for (FlushFdrDocument flushFdrDocument : docItems) {
            clavedocument = flushFdrDocument.getTreeId();
            if (claves.contains(clavedocument)) {
                resultPages = new ArrayList<FlushFdrPage>();
		for (Iterator it03 = pageItems.iterator(); it03.hasNext();) {
		    FlushFdrPage flushFdrPage = (FlushFdrPage) it03.next();
                    if (!flushFdrPage.getFatherId().equals(clavedocument)) continue;
                    resultPages.add(flushFdrPage);
                }
                flushFdrDocument.setPages(resultPages);
            }
            documents.put(clavedocument, flushFdrDocument);
        }
        return documents;
    }

    private String getFileNameFis(String sessionId, String folderId, int order, String name) {
        String extension = this.getExtension(name);
        StringBuffer buffer = new StringBuffer();
        buffer.append(sessionId);
        buffer.append("_");
        buffer.append(folderId);
        buffer.append("_");
        buffer.append(order);
        if (!extension.equals("-")) {
            buffer.append(".");
            buffer.append(extension);
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug((Object)("getFileName: " + buffer.toString()));
        }
        return buffer.toString();
    }

    private String getExtension(String name) {
        String extension = name.substring(name.lastIndexOf("\\") + 1, name.length());
        extension = extension.indexOf(".") == -1 ? "-" : extension.substring(extension.lastIndexOf(".") + 1, extension.length());
        return extension;
    }

    private String changeExtension(String fileName, String extensionNew, String extensionOld) {
        if ("-".equalsIgnoreCase(extensionOld)) {
            if (!"-".equalsIgnoreCase(extensionNew)) {
                fileName = fileName + "." + extensionNew;
            }
        } else {
            fileName = !"-".equalsIgnoreCase(extensionNew) ? (StringUtils.isBlank((String)extensionOld) ? fileName + extensionNew : fileName.replaceFirst("." + extensionOld, "." + extensionNew)) : fileName.replaceFirst("." + extensionOld, "");
        }
        return fileName;
    }
}
