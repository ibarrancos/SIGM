package com.ieci.tecdoc.isicres.servlets.intercambio.registral;

import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.web.util.ContextoAplicacionUtil;
import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.business.manager.LibroManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseLibroVO;
import es.ieci.tecdoc.isicres.api.business.vo.BaseOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ContextoAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.LibroEntradaVO;
import es.ieci.tecdoc.isicres.api.business.vo.LibroSalidaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.OperadorCriterioBusquedaIREnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;
import es.ieci.tecdoc.isicres.intercambio.registral.util.IntercambioRegistralManejarExcepciones;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

public class BusquedaBandejaEntradaIntercambioRegistral
extends HttpServlet {
    private static final String BANDEJA_ENTRADA_INTERCAMBIO_REGISTRAL_JSP_PATH = "/bandejaEntradaBusquedaIntercambioRegistral.jsp";
    private static Logger logger = Logger.getLogger((Class)BusquedaBandejaEntradaIntercambioRegistral.class);
    private static final long serialVersionUID = 1;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doWork(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.setHeaders(resp);
        ContextoAplicacionVO contexto = null;
        UsuarioVO usuario = null;
        Integer estado = null;
        Integer idOficina = null;
        List<LibroEntradaVO> librosEntradaIntercambio = null;
        List<LibroSalidaVO> librosSalidaIntercambio = null;
        List<BaseLibroVO> librosIntercambio = null;
        try {
            LibroManager libroManager = IsicresManagerProvider.getInstance().getLibroManager();
            IntercambioRegistralManager intercambioManager = IsicresManagerProvider.getInstance().getIntercambioRegistralManager();
            estado = this.getEstadoBusqueda(req);
            String identificadorIntercambio = req.getParameter("identificadorIntercambio");
            contexto = ContextoAplicacionUtil.getContextoAplicacion(req);
            usuario = contexto.getUsuarioActual();
            idOficina = Integer.parseInt(contexto.getOficinaActual().getId());
            librosEntradaIntercambio = this.getLibrosEntradaIntercambio(usuario, libroManager);
            librosSalidaIntercambio = this.getLibrosSalidaIntercambio(usuario, libroManager);
            librosIntercambio = this.getAllLibrosIntercambio(librosEntradaIntercambio, librosSalidaIntercambio);
            if (CollectionUtils.isEmpty(librosEntradaIntercambio)) {
                logger.error((Object)"No hay ning\u00fan libro de intercambio para este usuario.");
                String error = RBUtil.getInstance((Locale)usuario.getConfiguracionUsuario().getLocale()).getProperty("intercambioRegistral.bandejaEntradaError");
                req.setAttribute("error", (Object)error);
                RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/bandejaEntradaBusquedaIntercambioRegistral.jsp");
                rd.forward((ServletRequest)req, (ServletResponse)resp);
            } else {
                CriteriosBusquedaIREntradaVO criterios = new CriteriosBusquedaIREntradaVO();
                this.addCriterioOficina(idOficina, criterios);
                this.addCriterioIdIntercambio(identificadorIntercambio, criterios);
                EstadoIntercambioRegistralEntradaEnumVO estadoIntercambioRegistralEntradaEnumVO = EstadoIntercambioRegistralEntradaEnumVO.getEnum((int)estado);
                this.addCriterioEstado(criterios, estadoIntercambioRegistralEntradaEnumVO);
                List bandejaEntrada = new LinkedList();
                bandejaEntrada = intercambioManager.findBandejaEntradaByCriterios(estadoIntercambioRegistralEntradaEnumVO, criterios);
                req.setAttribute("bandejaEntrada", bandejaEntrada);
                req.setAttribute("identificadorIntercambio", (Object)identificadorIntercambio);
                req.getSession().setAttribute("estado", (Object)estado);
                req.getSession().setAttribute("tipoBandeja", (Object)"1");
                req.getSession().setAttribute("librosIntercambio", librosIntercambio);
                req.getSession().setAttribute("librosEntrada", librosEntradaIntercambio);
                RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/bandejaEntradaBusquedaIntercambioRegistral.jsp");
                dispatcher.forward((ServletRequest)req, (ServletResponse)resp);
            }
        }
        catch (IntercambioRegistralException irEx) {
            logger.error((Object)"Ha ocurrido un error al obtener la bandeja de entrada de Intercambio Regisral.", (Throwable)irEx);
            req.setAttribute("error", (Object)IntercambioRegistralManejarExcepciones.getInstance().manejarExcepcion(irEx, req.getLocale()));
            RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/intercambioRegistralResult.jsp");
            rd.forward((ServletRequest)req, (ServletResponse)resp);
        }
        catch (Exception e) {
            logger.error((Object)"Ha ocurrido un error al obtener la bandeja de entrada de intercambio registral.", (Throwable)e);
            String error = RBUtil.getInstance((Locale)ContextoAplicacionUtil.getCurrentLocale(usuario)).getProperty("intercambioRegistral.bandejaEntradaError");
            req.setAttribute("error", (Object)error);
            RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/bandejaEntradaBusquedaIntercambioRegistral.jsp");
            rd.forward((ServletRequest)req, (ServletResponse)resp);
        }
    }

    private void setHeaders(HttpServletResponse resp) {
        resp.setDateHeader("Expires", 0);
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        resp.setContentType("text/html; charset=UTF-8");
    }

    private void addCriterioEstado(CriteriosBusquedaIREntradaVO criterios, EstadoIntercambioRegistralEntradaEnumVO estadoIntercambioRegistralEntradaEnumVO) {
        CriterioBusquedaIREntradaVO criterioEstado = new CriterioBusquedaIREntradaVO();
        criterioEstado.setNombre(CriterioBusquedaIREntradaEnum.STATE);
        criterioEstado.setOperador(OperadorCriterioBusquedaIREnum.EQUAL);
        criterioEstado.setValor((Object)estadoIntercambioRegistralEntradaEnumVO);
        criterios.addCriterioVO(criterioEstado);
    }

    private void addCriterioOficina(Integer idOficina, CriteriosBusquedaIREntradaVO criterios) {
        CriterioBusquedaIREntradaVO criterioOficina = new CriterioBusquedaIREntradaVO();
        criterioOficina.setNombre(CriterioBusquedaIREntradaEnum.ID_OFICINA);
        criterioOficina.setOperador(OperadorCriterioBusquedaIREnum.EQUAL);
        criterioOficina.setValor((Object)idOficina);
        criterios.addCriterioVO(criterioOficina);
    }

    private void addCriterioIdIntercambio(String identificadorIntercambio, CriteriosBusquedaIREntradaVO criterios) {
        if (StringUtils.isNotEmpty((String)identificadorIntercambio)) {
            CriterioBusquedaIREntradaVO criterioIdentificadorIntercambio = new CriterioBusquedaIREntradaVO();
            criterioIdentificadorIntercambio.setNombre(CriterioBusquedaIREntradaEnum.ID_EXCHANGE_SIR);
            criterioIdentificadorIntercambio.setOperador(OperadorCriterioBusquedaIREnum.LIKE);
            criterioIdentificadorIntercambio.setValor((Object)identificadorIntercambio);
            criterios.addCriterioVO(criterioIdentificadorIntercambio);
        }
    }

    protected List<LibroEntradaVO> getLibrosEntradaIntercambio(UsuarioVO usuario, LibroManager libroManager) {
        List result = libroManager.findLibrosEntradaIntercambioByUser(usuario);
        return result;
    }

    protected List<LibroSalidaVO> getLibrosSalidaIntercambio(UsuarioVO usuario, LibroManager libroManager) {
        List result = libroManager.findLibrosSalidaIntercambioByUser(usuario);
        return result;
    }

    protected List<BaseLibroVO> getAllLibrosIntercambio(List<LibroEntradaVO> librosEntradaIntercambio, List<LibroSalidaVO> librosSalidaIntercambio) {
        ArrayList<BaseLibroVO> result = new ArrayList<BaseLibroVO>();
        if (librosEntradaIntercambio != null) {
            result.addAll(librosEntradaIntercambio);
        }
        if (librosSalidaIntercambio != null) {
            result.addAll(librosSalidaIntercambio);
        }
        return result;
    }

    protected Integer getEstadoBusqueda(HttpServletRequest req) {
        Integer result = 0;
        String estadoString = req.getParameter("estadosEntrada");
        if (!StringUtils.isEmpty((String)estadoString)) {
            result = Integer.parseInt(estadoString);
        }
        return result;
    }
}
