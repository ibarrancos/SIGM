package com.ieci.tecdoc.isicres.servlets;

import com.ieci.tecdoc.common.utils.Configurator;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.helper.ConfigExtensionFileHelper;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.desktopweb.utils.RequestUtils;
import com.ieci.tecdoc.isicres.desktopweb.utils.ResponseUtils;
import com.ieci.tecdoc.isicres.session.folder.FolderFileSession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class GetPage
extends HttpServlet
implements Keys {
    private static Logger _logger = Logger.getLogger((Class)GetPage.class);
    private static final String INTERROGACION = "?";
    private static final String BARRAINC = "/";
    private static final String DOSPUNTOS = ":";
    private static final String SERVLETCALL = "FileDownload";

    public void init() throws ServletException {
        super.init();
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
        String topURL = RequestUtils.parseRequestParameterAsString(request, "topURL");
        String fileName = RequestUtils.parseRequestParameterAsString(request, "FileName");
        Integer bookId = RequestUtils.parseRequestParameterAsInteger(request, "BookId");
        Integer regId = RequestUtils.parseRequestParameterAsInteger(request, "RegId");
        Integer docId = RequestUtils.parseRequestParameterAsInteger(request, "DocId");
        Integer pageId = RequestUtils.parseRequestParameterAsInteger(request, "PageId");
        HttpSession session = request.getSession();
        UseCaseConf useCaseConf = (UseCaseConf)session.getAttribute("JUseCaseConf");
        PrintWriter writer = response.getWriter();
        try {
            String URL = this.getUrl(request, topURL);
            String fileExt = FolderFileSession.getExtensionFile((String)fileName, (String)bookId.toString(), (int)regId, (int)pageId, (String)useCaseConf.getEntidadId());
            if (fileExt != null) {
                fileExt = fileExt.toUpperCase();
            }
            _logger.info((Object)URL);
            _logger.info((Object)fileExt);
            writer.write("<HTML>");
            writer.write("<head>");
            writer.write("<link REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"./css/global.css\">");
            writer.write("<link REL=\"stylesheet\" TYPE=\"text/css\" HREF=\"./css/font.css\">");
            writer.write("<script type=\"text/javascript\" language=\"javascript\" src=\"./scripts/global.js\"></script>");
            writer.write("<script type=\"text/javascript\" language=\"javascript\" src=\"./scripts/genmsg.js\"></script>");
            writer.write("<script type=\"text/javascript\" language=\"javascript\" src=\"./scripts/frmdata.js\"></script>");
            writer.write("<script type=\"text/javascript\" language=\"javascript\" src=\"./scripts/frmint.js\"></script>");
            writer.write("<script type=\"text/javascript\" language=\"javascript\" src=\"./scripts/frmt.js\"></script>");
            writer.write("</head>");
            this.getViewFile(writer, URL, fileExt);
            writer.write("</BODY>");
            writer.write("</HTML>");
        }
        catch (Exception e) {
            _logger.fatal((Object)"Error al obtener el fichero", (Throwable)e);
            writer.write("<script language=javascript>top.g_ActivateTree=true;if (!top.g_FolderView){top.g_OpcAval=true;top.Main.Folder.ToolBarFrm.habilitar();}else{top.Main.Folder.ToolBarFrm.ToolBarEnabled();}</script>");
            ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_ERR_CREATING_FDRQRY_OBJ"));
        }
    }

    private void getViewFile(PrintWriter writer, String URL, String fileExt) {
        boolean showDialogSaveOpen = ConfigExtensionFileHelper.getShowDialogSaveOpenFile(fileExt);
        if (!showDialogSaveOpen && (fileExt.equals("JPG") || fileExt.equals("JPEG") || fileExt.equals("TIF") || fileExt.equals("TIFF") || fileExt.equals("BMP"))) {
            writer.write("<BODY tabIndex=\"-1\" onload=\"ActivateTree();\" onunload=\"\" scroll=\"no\">");
            writer.write("<object classid=\"CLSID:24C6D59E-6D0D-11D4-8128-00C0F049167F\" width=\"100%\" height=\"100%\"");
            writer.write("codebase=\"plugins/ides.cab#version=2,2,0,0\" id=Control1>");
            writer.write("<PARAM name=\"FileName\" value=\"" + URL + "\">");
            writer.write("<PARAM name=\"FitMode\" value=0>");
            writer.write("<PARAM name=\"Enhancement\" value=2>");
            if (Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/IdocImgEnableSaveAs")) {
                writer.write("<PARAM name=\"EnableSaveAs\" value=1>");
            }
            writer.write("</object>");
        } else {
            StringBuffer htmlBody = new StringBuffer();
            htmlBody.append("<BODY tabIndex=\"-1\" onload=\"ActivateTree();window.open('" + URL + "','frmPage','location=no',true); ");
            if (showDialogSaveOpen) {
                htmlBody.append("mostrarFrameFolderFormData()");
            }
            htmlBody.append(";\" scroll=\"no\">");
            writer.write(htmlBody.toString());
            writer.write("<iframe id=\"frmPage\" name=\"frmPage\" style=\"position:absolute;top:0px;left:0px;width:100%;height:100%\">");
            writer.write("</iframe>");
        }
    }

    private String getUrl(HttpServletRequest request, String topURL) {
        String query = request.getQueryString();
        String sProtocol = topURL.substring(0, topURL.indexOf(":"));
        StringBuffer buffer = new StringBuffer();
        buffer.append(topURL);
        buffer.append("/");
        buffer.append("FileDownload");
        buffer.append("?");
        buffer.append(query);
        return this.escapeSpecialChars(buffer.toString());
    }

    private String escapeSpecialChars(String url) {
        String[] specialChars = new String[]{"'"};
        return url.replaceAll("'", "\\\\'");
    }
}
