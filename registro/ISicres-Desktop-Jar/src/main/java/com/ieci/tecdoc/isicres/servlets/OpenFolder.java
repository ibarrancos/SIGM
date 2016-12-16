package com.ieci.tecdoc.isicres.servlets;

import com.ieci.tecdoc.common.exception.BookException;
import com.ieci.tecdoc.common.exception.DistributionException;
import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.exception.SessionException;
import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.common.invesicres.ScrRegstate;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.desktopweb.utils.RequestUtils;
import com.ieci.tecdoc.isicres.desktopweb.utils.ResponseUtils;
import com.ieci.tecdoc.isicres.session.book.BookSession;
import com.ieci.tecdoc.isicres.session.folder.FolderSession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.book.BookUseCase;
import es.ieci.tecdoc.fwktd.core.config.web.ContextUtil;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;
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
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.DocumentSource;

public class OpenFolder
extends HttpServlet
implements Keys {
    private static Logger _logger = Logger.getLogger((Class)OpenFolder.class);
    private static final String INTERROGACION = "?";
    private static final String IGUAL = "=";
    private static final String AMPERSAN = "&";
    private static final String PUNTO_COMA = ";";
    private static final String BARRA = "|";
    public static long folderPId = 1;
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
        Integer archiveId = RequestUtils.parseRequestParameterAsInteger(request, "ArchiveId");
        Integer archivePId = RequestUtils.parseRequestParameterAsInteger(request, "ArchivePId");
        Integer folderId = RequestUtils.parseRequestParameterAsInteger(request, "FolderId");
        Integer fdrQryPId = RequestUtils.parseRequestParameterAsInteger(request, "FdrQryPId");
        Integer copyFdr = RequestUtils.parseRequestParameterAsInteger(request, "CopyFdr");
        Integer row = RequestUtils.parseRequestParameterAsInteger(request, "Row");
        Boolean form = RequestUtils.parseRequestParameterAsBoolean(request, "Form");
        Boolean openFolderDtr = RequestUtils.parseRequestParameterAsBoolean(request, "OpenFolderDtr", new Boolean(false));
        Boolean openEditDistr = RequestUtils.parseRequestParameterAsBoolean(request, "OpenEditDistr", new Boolean(false));
        HttpSession session = request.getSession();
        String idioma = (String)session.getAttribute("JIdioma");
        Long numIdioma = (Long)session.getAttribute("JNumIdioma");
        UseCaseConf useCaseConf = (UseCaseConf)session.getAttribute("JUseCaseConf");
        session.setAttribute("JOpenFolderForm", (Object)form);
        PrintWriter writer = response.getWriter();
        try {
            openFolderDtr = this.valiteOpenEditDistri(archiveId, folderId, openFolderDtr, openEditDistr, useCaseConf);
            if (form.booleanValue() || openFolderDtr.booleanValue()) {
                Document xmlDocument = this.bookUseCase.getBookTree(useCaseConf, archiveId, true, folderPId++, folderId, row, this.getBeforeEncondedResponseURL(request, response), openFolderDtr, useCaseConf.getLocale());
                String xslPath = ContextUtil.getRealPath((ServletContext)session.getServletContext(), (String)"/xsl/frmt.xsl");
                StreamSource s = new StreamSource(new InputStreamReader(new BufferedInputStream(new FileInputStream(xslPath))));
                Templates cachedXSLT = this.factory.newTemplates(s);
                Transformer transformer = cachedXSLT.newTransformer();
                DocumentSource source = new DocumentSource(xmlDocument);
                StreamResult result = new StreamResult(writer);
                transformer.transform((Source)source, result);
            } else if (!(form.booleanValue() || openFolderDtr.booleanValue())) {
                boolean bIsOpened = false;
                boolean readOnly = false;
                String archFolder = (String)session.getAttribute("JStrCarpeta");
                if (!(form.booleanValue() || openFolderDtr.booleanValue() || (bIsOpened = this.getIsOpened(session, archiveId, folderId, archFolder)))) {
                    bIsOpened = !this.bookUseCase.lockFolder(useCaseConf, archiveId, folderId);
                }
                readOnly = form != false || bIsOpened || openFolderDtr != false;
                AxSf axsf = FolderSession.getBookFolder((String)useCaseConf.getSessionID(), (Integer)archiveId, (int)folderId, (Locale)useCaseConf.getLocale(), (String)useCaseConf.getEntidadId());
                String estado = axsf.getAttributeValueAsString("fld6");
                boolean folderCloseOrCanceled = this.bookUseCase.validateStateFolderIfClosedOrCancel(estado);
                if (!(readOnly || folderCloseOrCanceled)) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(archiveId.toString());
                    buffer.append("|");
                    buffer.append(folderId.toString());
                    buffer.append(";");
                    if (archFolder != null) {
                        buffer.insert(0, archFolder);
                    }
                    session.setAttribute("JStrCarpeta", (Object)buffer.toString());
                }
                Document xmlDocument = this.bookUseCase.getBookTree(useCaseConf, archiveId, bIsOpened, folderPId, folderId, row, this.getBeforeEncondedResponseURL(request, response), openFolderDtr, useCaseConf.getLocale());
                String xslPath = ContextUtil.getRealPath((ServletContext)session.getServletContext(), (String)"/xsl/frmt.xsl");
                StreamSource s = new StreamSource(new InputStreamReader(new BufferedInputStream(new FileInputStream(xslPath))));
                Templates cachedXSLT = this.factory.newTemplates(s);
                Transformer transformer = cachedXSLT.newTransformer();
                DocumentSource source = new DocumentSource(xmlDocument);
                StreamResult result = new StreamResult(writer);
                transformer.transform((Source)source, result);
                session.setAttribute("JRegister", (Object)folderId);
                session.setAttribute("JBook", (Object)archiveId);
                session.setAttribute("JRow", (Object)row);
            }
        }
        catch (ValidationException e) {
            _logger.fatal((Object)"Error de validacion", (Throwable)e);
            writer.write("<script language=javascript>try{top.Main.Folder.FolderBar.bLoadForm = true;window.open(top.g_URL + \"/fldbarUpdate.htm\", \"FolderBar\",\"location=no\",true);top.g_Page = 0;top.g_OpcAval=true;if(top.g_FolderView){window.open(top.g_URL + \"/tb_folder.htm\", \"ToolBarFrm\",\"location=no\",true);top.Main.Folder.ToolBarFrm.ToolBarEnabled();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}else{window.open(top.g_URL + \"/tb_form.htm\", \"ToolBarFrm\",\"location=no\",true);}top.ShowForm();top.Main.Folder.ToolBarFrm.habilitar();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}catch(excep){}</script>");
            ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.validationException"));
        }
        catch (BookException e) {
            _logger.fatal((Object)"Error en el libro", (Throwable)e);
            writer.write("<script language=javascript>try{top.Main.Folder.FolderBar.bLoadForm = true;window.open(top.g_URL + \"/fldbarUpdate.htm\", \"FolderBar\",\"location=no\",true);top.g_Page = 0;top.g_OpcAval=true;if(top.g_FolderView){window.open(top.g_URL + \"/tb_folder.htm\", \"ToolBarFrm\",\"location=no\",true);top.Main.Folder.ToolBarFrm.ToolBarEnabled();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}else{window.open(top.g_URL + \"/tb_form.htm\", \"ToolBarFrm\",\"location=no\",true);}top.ShowForm();top.Main.Folder.ToolBarFrm.habilitar();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}catch(excep){}</script>");
            ResponseUtils.generateJavaScriptError((Writer)writer, e);
        }
        catch (SessionException e) {
            _logger.fatal((Object)"Error en la sesion", (Throwable)e);
            ResponseUtils.generateJavaScriptErrorSessionExpiredOpenFolder(writer, e, idioma, numIdioma, form);
        }
        catch (TransformerConfigurationException e) {
            _logger.fatal((Object)"Error al abrir el registro", (Throwable)e);
            writer.write("<script language=javascript>try{top.Main.Folder.FolderBar.bLoadForm = true;window.open(top.g_URL + \"/fldbarUpdate.htm\", \"FolderBar\",\"location=no\",true);top.g_Page = 0;top.g_OpcAval=true;if(top.g_FolderView){window.open(top.g_URL + \"/tb_folder.htm\", \"ToolBarFrm\",\"location=no\",true);top.Main.Folder.ToolBarFrm.ToolBarEnabled();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}else{window.open(top.g_URL + \"/tb_form.htm\", \"ToolBarFrm\",\"location=no\",true);}top.ShowForm();top.Main.Folder.ToolBarFrm.habilitar();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}catch(excep){}</script>");
            ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_QRY_ABORT_CAUSE_INVALID_TEXT"));
        }
        catch (TransformerFactoryConfigurationError e) {
            _logger.fatal((Object)"Error al abrir el registro", (Throwable)e);
            writer.write("<script language=javascript>try{top.Main.Folder.FolderBar.bLoadForm = true;window.open(top.g_URL + \"/fldbarUpdate.htm\", \"FolderBar\",\"location=no\",true);top.g_Page = 0;top.g_OpcAval=true;if(top.g_FolderView){window.open(top.g_URL + \"/tb_folder.htm\", \"ToolBarFrm\",\"location=no\",true);top.Main.Folder.ToolBarFrm.ToolBarEnabled();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}else{window.open(top.g_URL + \"/tb_form.htm\", \"ToolBarFrm\",\"location=no\",true);}top.ShowForm();top.Main.Folder.ToolBarFrm.habilitar();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}catch(excep){}</script>");
            ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_QRY_ABORT_CAUSE_INVALID_TEXT"));
        }
        catch (TransformerException e) {
            _logger.fatal((Object)"Error al abrir el registro", (Throwable)e);
            writer.write("<script language=javascript>try{top.Main.Folder.FolderBar.bLoadForm = true;window.open(top.g_URL + \"/fldbarUpdate.htm\", \"FolderBar\",\"location=no\",true);top.g_Page = 0;top.g_OpcAval=true;if(top.g_FolderView){window.open(top.g_URL + \"/tb_folder.htm\", \"ToolBarFrm\",\"location=no\",true);top.Main.Folder.ToolBarFrm.ToolBarEnabled();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}else{window.open(top.g_URL + \"/tb_form.htm\", \"ToolBarFrm\",\"location=no\",true);}top.ShowForm();top.Main.Folder.ToolBarFrm.habilitar();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}catch(excep){}</script>");
            ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_QRY_ABORT_CAUSE_INVALID_TEXT"));
        }
        catch (Exception e) {
            _logger.fatal((Object)"Error al abrir el registro", (Throwable)e);
            writer.write("<script language=javascript>try{top.Main.Folder.FolderBar.bLoadForm = true;window.open(top.g_URL + \"/fldbarUpdate.htm\", \"FolderBar\",\"location=no\",true);top.g_Page = 0;top.g_OpcAval=true;if(top.g_FolderView){window.open(top.g_URL + \"/tb_folder.htm\", \"ToolBarFrm\",\"location=no\",true);top.Main.Folder.ToolBarFrm.ToolBarEnabled();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}else{window.open(top.g_URL + \"/tb_form.htm\", \"ToolBarFrm\",\"location=no\",true);}top.ShowForm();top.Main.Folder.ToolBarFrm.habilitar();top.g_ActivateTree=true;top.g_bIsNewFolder=false;}catch(excep){}</script>");
            ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_ERR_CREATING_FDRQRY_OBJ"));
        }
    }

    private Boolean valiteOpenEditDistri(Integer archiveId, Integer folderId, Boolean openFolderDtr, Boolean openEditDistr, UseCaseConf useCaseConf) throws ValidationException, SecurityException, BookException, SessionException, DistributionException {
        if (openEditDistr.booleanValue()) {
            int perms = new BookUseCase().getPermisosLibro(useCaseConf, archiveId);
            ScrRegstate scrregstate = BookSession.getBook((String)useCaseConf.getSessionID(), (Integer)archiveId);
            int size = FolderSession.getCountRegisterByIdReg((String)useCaseConf.getSessionID(), (String)useCaseConf.getEntidadId(), (Integer)archiveId, (Integer)folderId);
            if (perms >> 0 != 0 && scrregstate.getState() != 1 && size != 0) {
                openFolderDtr = false;
            }
        }
        return openFolderDtr;
    }

    private boolean getIsOpened(HttpSession session, Integer archiveId, Integer folderId, String archFolder) {
        boolean isOpened = false;
        if (archFolder != null && archFolder.length() > 0) {
            StringTokenizer tokens = new StringTokenizer(archFolder, ";");
            String aux = null;
            String auxArch = null;
            String auxFolder = null;
            while (tokens.hasMoreTokens() && !isOpened) {
                aux = tokens.nextToken();
                auxArch = aux.substring(0, aux.indexOf("|"));
                auxFolder = aux.substring(aux.indexOf("|") + 1, aux.length());
                isOpened = auxArch.equals(archiveId.toString()) && auxFolder.equals(folderId.toString());
            }
        }
        return isOpened;
    }

    private String getBeforeEncondedResponseURL(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getQueryString();
        query = query.replaceFirst("JOpenFolderType=" + J_OPENFOLDER_TYPE_XMLBOOKTREE_FORM_BUTTON, "JOpenFolderType=" + J_OPENFOLDER_TYPE_XMLBOOK);
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
        return response.encodeURL(buffer.toString());
    }
}
