package com.ieci.tecdoc.isicres.servlets.core;

import com.ieci.tecdoc.common.exception.SecurityException;
import com.ieci.tecdoc.common.utils.ISicresGenPerms;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.desktopweb.utils.RequestUtils;
import com.ieci.tecdoc.isicres.desktopweb.utils.ResponseUtils;
import com.ieci.tecdoc.isicres.session.folder.FolderFileSession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.utils.cache.CacheBag;
import com.ieci.tecdoc.utils.cache.CacheFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class FileDelete
extends HttpServlet {
    private static Logger _logger = Logger.getLogger((Class)FileDelete.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    private void doWork(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        block3 : {
            Integer bookId = RequestUtils.parseRequestParameterAsInteger(req, "BookId");
            Integer regId = RequestUtils.parseRequestParameterAsInteger(req, "RegId");
            Integer docId = RequestUtils.parseRequestParameterAsInteger(req, "DocId");
            Integer pageId = RequestUtils.parseRequestParameterAsInteger(req, "PageId");
            HttpSession session = req.getSession();
            String idioma = (String)session.getAttribute("JIdioma");
            Long numIdioma = (Long)session.getAttribute("JNumIdioma");
            UseCaseConf useCaseConf = (UseCaseConf)session.getAttribute("JUseCaseConf");
            PrintWriter writer = resp.getWriter();
            try {
                CacheBag cacheBag = CacheFactory.getCacheInterface().getCacheEntry(useCaseConf.getSessionID());
                ISicresGenPerms permisos = (ISicresGenPerms)cacheBag.get((Object)"GenPermsUser");
                if (permisos.isCanDeleteDocuments()) {
                    FolderFileSession.deleteFileOfRegister((String)useCaseConf.getEntidadId(), (Integer)regId, (Integer)docId, (Integer)pageId, (Integer)bookId);
                    break block3;
                }
                StringBuffer sb = new StringBuffer();
                sb.append("El usuario [").append(useCaseConf.getUserName()).append("] no posee permisos para borrar ficheros CanDeleteDocuments[").append(permisos.isCanDeleteDocuments()).append("]");
                _logger.warn((Object)sb.toString());
                throw new SecurityException("securityexception.user_not_perms_necessary");
            }
            catch (Exception e) {
                StringBuffer sb = new StringBuffer();
                sb.append("No se ha podido concluir el borrado del fichero con los datos: FILEID[").append(pageId).append("] DOCID [").append(docId).append("] REGID [").append(regId).append("]");
                _logger.fatal((Object)sb.toString(), (Throwable)e);
                ResponseUtils.generateJavaScriptLog(writer, RBUtil.getInstance(useCaseConf.getLocale()).getProperty("exception.ISICRESSRV_ERR_DELETE_OBJ"));
            }
        }
    }
}
