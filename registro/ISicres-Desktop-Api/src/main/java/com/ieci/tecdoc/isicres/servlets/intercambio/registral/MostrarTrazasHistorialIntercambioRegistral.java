package com.ieci.tecdoc.isicres.servlets.intercambio.registral;

import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.desktopweb.utils.RequestUtils;
import com.ieci.tecdoc.isicres.web.util.ContextoAplicacionUtil;
import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralManager;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class MostrarTrazasHistorialIntercambioRegistral
extends HttpServlet {
    private static Logger _logger = Logger.getLogger((Class)MostrarTrazasHistorialIntercambioRegistral.class);
    private static final long serialVersionUID = 1;

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
        IntercambioRegistralManager intercambioManager = IsicresManagerProvider.getInstance().getIntercambioRegistralManager();
        UsuarioVO usuarioActual = null;
        String idIntercambio = RequestUtils.parseRequestParameterAsString((HttpServletRequest)request, (String)"idIntercambio");
        try {
            List trazasIntercambio = intercambioManager.getTrazasIntercambioRegistral(idIntercambio);
            request.setAttribute("trazasIntercambio", (Object)trazasIntercambio);
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/jsp/intercambio/include/trazasHistorialIntercambio.jsp");
            dispatcher.forward((ServletRequest)request, (ServletResponse)response);
        }
        catch (Exception e) {
            _logger.error((Object)"Ha ocurrido un error al obtener las trazas del historial de intercambio registral.", (Throwable)e);
            String error = RBUtil.getInstance((Locale)ContextoAplicacionUtil.getCurrentLocale(usuarioActual)).getProperty("intercambioRegistral.errorObtenerHistorialIntercambio");
            request.setAttribute("error", (Object)error);
            RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/WEB-INF/jsp/intercambio/include/trazasHistorialIntercambio.jsp");
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
    }
}
