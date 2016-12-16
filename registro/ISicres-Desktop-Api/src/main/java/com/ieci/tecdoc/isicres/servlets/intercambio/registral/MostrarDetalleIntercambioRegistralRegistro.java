package com.ieci.tecdoc.isicres.servlets.intercambio.registral;

import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.web.util.ContextoAplicacionUtil;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.business.vo.BaseRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.ContextoAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralEntradaVO;
import java.io.IOException;
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

public class MostrarDetalleIntercambioRegistralRegistro
extends HttpServlet {
    private static Logger logger = Logger.getLogger((Class)MostrarDetalleIntercambioRegistralRegistro.class);
    private static final long serialVersionUID = 1;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doWork(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"doWork(HttpServletRequest, HttpServletResponse) - start");
        }
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/html; charset=UTF-8");
        IntercambioRegistralManager intercambioManager = IsicresManagerProvider.getInstance().getIntercambioRegistralManager();
        String idLibro = null;
        String idRegistro = null;
        UsuarioVO usuarioActual = null;
        AsientoRegistralVO asientoRegistralVO = null;
        try {
            ContextoAplicacionVO contextoAplicacion = ContextoAplicacionUtil.getContextoAplicacion(request);
            idLibro = contextoAplicacion.getRegistroActual().getIdLibro();
            idRegistro = contextoAplicacion.getRegistroActual().getIdRegistro();
            usuarioActual = contextoAplicacion.getUsuarioActual();
            Integer estado = EstadoIntercambioRegistralEntradaEnumVO.ACEPTADO.getValue();
            Integer idLibroInt = Integer.valueOf(idLibro);
            Integer idRegistroInt = Integer.valueOf(idRegistro);
            IntercambioRegistralEntradaVO intercambio = intercambioManager.getIntercambioRegistralEntradaByRegistro(idLibroInt, idRegistroInt, estado);
            if (intercambio != null) {
                String idIntercambio = String.valueOf(intercambio.getIdIntercambioInterno());
                asientoRegistralVO = intercambioManager.getIntercambioRegistralByIdIntercambio(idIntercambio);
            }
            if (asientoRegistralVO != null) {
                request.setAttribute("asientoRegistral", (Object)asientoRegistralVO);
                request.getRequestDispatcher("/mostraInformacionIntercambio.jsp").include((ServletRequest)request, (ServletResponse)response);
            } else {
                logger.warn((Object)"ERROR obtener el asiento registral");
                String error = RBUtil.getInstance((Locale)ContextoAplicacionUtil.getCurrentLocale(usuarioActual)).getProperty("intercambioRegistral.errorNoDataDetalleIntercambioRegistral");
                request.setAttribute("error", (Object)error);
                RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/mostraInformacionIntercambio.jsp");
                rd.forward((ServletRequest)request, (ServletResponse)response);
            }
        }
        catch (Exception e) {
            logger.error((Object)"ERROR obtener el asiento registral", (Throwable)e);
            String error = RBUtil.getInstance((Locale)ContextoAplicacionUtil.getCurrentLocale(usuarioActual)).getProperty("intercambioRegistral.errorObtenerDetalleIntercambioRegistral");
            request.setAttribute("error", (Object)error);
            RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/mostraInformacionIntercambio.jsp");
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"doWork(HttpServletRequest, HttpServletResponse) - end");
        }
    }
}
