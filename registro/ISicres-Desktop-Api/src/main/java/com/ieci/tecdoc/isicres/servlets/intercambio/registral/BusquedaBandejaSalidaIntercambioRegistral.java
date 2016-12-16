package com.ieci.tecdoc.isicres.servlets.intercambio.registral;

import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.web.util.ContextoAplicacionUtil;
import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.business.manager.LibroManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseLibroVO;
import es.ieci.tecdoc.isicres.api.business.vo.BaseOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.ContextoAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIRSalidaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.OperadorCriterioBusquedaIREnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIRSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIRSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaEnumVO;
import es.ieci.tecdoc.isicres.intercambio.registral.util.IntercambioRegistralManejarExcepciones;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

public class BusquedaBandejaSalidaIntercambioRegistral
extends HttpServlet {
    private static final String BANDEJA_SALIDA_BUSQUEDA_INTERCAMBIO_REGISTRAL_JSP_PATH = "/bandejaSalidaBusquedaIntercambioRegistral.jsp";
    private static Logger logger = Logger.getLogger((Class)BusquedaBandejaSalidaIntercambioRegistral.class);
    private static final long serialVersionUID = 1;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doWork(req, resp);
    }

    protected void doWork(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setDateHeader("Expires", 0);
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        resp.setContentType("text/html; charset=UTF-8");
        Integer idOficina = null;
        Integer estado = null;
        Integer libroSeleccionado = null;
        List<BaseLibroVO> librosIntercambio = null;
        ContextoAplicacionVO contextoAplicacion = null;
        UsuarioVO usuario = null;
        List bandejaSalida = null;
        try {
            LibroManager libroManager = IsicresManagerProvider.getInstance().getLibroManager();
            IntercambioRegistralManager intercambioManager = IsicresManagerProvider.getInstance().getIntercambioRegistralManager();
            contextoAplicacion = ContextoAplicacionUtil.getContextoAplicacion(req);
            usuario = contextoAplicacion.getUsuarioActual();
            idOficina = Integer.parseInt(contextoAplicacion.getOficinaActual().getId());
            libroSeleccionado = this.getIdLibroSeleccionado(req);
            estado = this.getEstadoBusqueda(req);
            String identificadorIntercambio = req.getParameter("identificadorIntercambio");
            librosIntercambio = this.getLibrosIntercambio(usuario, libroManager);
            CriteriosBusquedaIRSalidaVO criterios = new CriteriosBusquedaIRSalidaVO();
            this.addCriterioOficina(idOficina, criterios);
            this.addCriterioIdIntercambio(identificadorIntercambio, criterios);
            EstadoIntercambioRegistralSalidaEnumVO estadoIntercambioRegistralSalidaEnumVO = EstadoIntercambioRegistralSalidaEnumVO.getEnum((int)estado);
            this.addCriterioEstado(criterios, estadoIntercambioRegistralSalidaEnumVO);
            bandejaSalida = intercambioManager.findBandejaSalidaByCriterios(estadoIntercambioRegistralSalidaEnumVO, criterios, libroSeleccionado);
            req.getSession().setAttribute("bandejaSalida", (Object)bandejaSalida);
            req.setAttribute("identificadorIntercambio", (Object)identificadorIntercambio);
            req.getSession().setAttribute("libroSeleccionado", (Object)libroSeleccionado);
            req.getSession().setAttribute("librosIntercambio", librosIntercambio);
            req.getSession().setAttribute("estado", (Object)estado);
            req.getSession().setAttribute("tipoBandeja", (Object)"0");
            RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/bandejaSalidaBusquedaIntercambioRegistral.jsp");
            dispatcher.forward((ServletRequest)req, (ServletResponse)resp);
        }
        catch (IntercambioRegistralException irEx) {
            logger.error((Object)"Ha ocurrido un error al obtener la bandeja de salida de Intercambio Regisral.", (Throwable)irEx);
            req.setAttribute("error", (Object)IntercambioRegistralManejarExcepciones.getInstance().manejarExcepcion(irEx, req.getLocale()));
            RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/intercambioRegistralResult.jsp");
            rd.forward((ServletRequest)req, (ServletResponse)resp);
        }
        catch (Exception e) {
            logger.error((Object)"Ha ocurrido un error al obtener la bandeja de salida de intercambio registral.", (Throwable)e);
            String error = RBUtil.getInstance((Locale)ContextoAplicacionUtil.getCurrentLocale(usuario)).getProperty("intercambioRegistral.bandejaSalidaError");
            req.setAttribute("error", (Object)error);
            RequestDispatcher rd = this.getServletConfig().getServletContext().getRequestDispatcher("/bandejaSalidaBusquedaIntercambioRegistral.jsp");
            rd.forward((ServletRequest)req, (ServletResponse)resp);
        }
    }

    private void addCriterioLibro(Integer libroSeleccionado, CriteriosBusquedaIRSalidaVO criterios) {
        if (libroSeleccionado != null) {
            CriterioBusquedaIRSalidaVO criterioLibro = new CriterioBusquedaIRSalidaVO();
            criterioLibro.setNombre(CriterioBusquedaIRSalidaEnum.ID_ARCHIVADOR);
            criterioLibro.setOperador(OperadorCriterioBusquedaIREnum.EQUAL);
            criterioLibro.setValor((Object)libroSeleccionado);
            criterios.addCriterioVO(criterioLibro);
        }
    }

    private void addCriterioEstado(CriteriosBusquedaIRSalidaVO criterios, EstadoIntercambioRegistralSalidaEnumVO estadoIntercambioRegistralSalidaEnumVO) {
        CriterioBusquedaIRSalidaVO criterioEstado = new CriterioBusquedaIRSalidaVO();
        criterioEstado.setNombre(CriterioBusquedaIRSalidaEnum.STATE);
        criterioEstado.setOperador(OperadorCriterioBusquedaIREnum.EQUAL);
        criterioEstado.setValor((Object)estadoIntercambioRegistralSalidaEnumVO);
        criterios.addCriterioVO(criterioEstado);
    }

    private void addCriterioOficina(Integer idOficina, CriteriosBusquedaIRSalidaVO criterios) {
        CriterioBusquedaIRSalidaVO criterioOficina = new CriterioBusquedaIRSalidaVO();
        criterioOficina.setNombre(CriterioBusquedaIRSalidaEnum.ID_OFICINA);
        criterioOficina.setOperador(OperadorCriterioBusquedaIREnum.EQUAL);
        criterioOficina.setValor((Object)idOficina);
        criterios.addCriterioVO(criterioOficina);
    }

    private void addCriterioIdIntercambio(String identificadorIntercambio, CriteriosBusquedaIRSalidaVO criterios) {
        if (StringUtils.isNotEmpty((String)identificadorIntercambio)) {
            CriterioBusquedaIRSalidaVO criterioIdentificadorIntercambio = new CriterioBusquedaIRSalidaVO();
            criterioIdentificadorIntercambio.setNombre(CriterioBusquedaIRSalidaEnum.ID_EXCHANGE_SIR);
            criterioIdentificadorIntercambio.setOperador(OperadorCriterioBusquedaIREnum.LIKE);
            criterioIdentificadorIntercambio.setValor((Object)identificadorIntercambio);
            criterios.addCriterioVO(criterioIdentificadorIntercambio);
        }
    }

    protected List<BaseLibroVO> getLibrosIntercambio(UsuarioVO usuario, LibroManager libroManager) {
        ArrayList<BaseLibroVO> result = new ArrayList<BaseLibroVO>();
        List librosEntradaIntercambio = libroManager.findLibrosEntradaIntercambioByUser(usuario);
        List librosSalidaIntercambio = libroManager.findLibrosSalidaIntercambioByUser(usuario);
        if (librosEntradaIntercambio != null) {
            result.addAll(librosEntradaIntercambio);
        }
        if (librosSalidaIntercambio != null) {
            result.addAll(librosSalidaIntercambio);
        }
        return result;
    }

    protected Integer getIdLibroSeleccionado(HttpServletRequest req) {
        Integer result = null;
        String libroSeleccionadoString = req.getParameter("libroSeleccionado");
        if (!StringUtils.isEmpty((String)libroSeleccionadoString)) {
            result = Integer.parseInt(libroSeleccionadoString);
        }
        return result;
    }

    protected Integer getEstadoBusqueda(HttpServletRequest req) {
        Integer result = EstadoIntercambioRegistralSalidaEnumVO.ENVIADO_VALUE;
        String estadoString = req.getParameter("estadosSalida");
        if (!StringUtils.isEmpty((String)estadoString)) {
            result = Integer.parseInt(estadoString);
        }
        return result;
    }
}
