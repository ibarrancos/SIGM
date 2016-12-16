package com.ieci.tecdoc.isicres.servlets.intercambio.registral;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;

import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionDCO;

public class BuscarUnidadesTramitacionDCO extends HttpServlet{

	private static Logger _logger = Logger.getLogger(BuscarEntidadesRegistralesDCO.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doWork(req,resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doWork(req,resp);
	}

	protected void doWork(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		resp.setDateHeader("Expires", 0);
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Pragma", "no-cache");
		resp.setContentType("text/html; charset=UTF-8");

		HttpSession mySession = req.getSession();
		UseCaseConf useCaseConf = (UseCaseConf) mySession.getAttribute(com.ieci.tecdoc.isicres.desktopweb.Keys.J_USECASECONF);
		List listaUnidadesTramitacionDCO;

		try{
			String codeEntityToFind = req.getParameter("codeEntityToFind");
			IntercambioRegistralManager intercambioManager =  IsicresManagerProvider.getInstance().getIntercambioRegistralManager();

			String tramunitCodeToFind = req.getParameter("tramunitCodeToFind");
			String tramunitNameToFind = req.getParameter("tramunitNameToFind");
			if (StringUtils.isNotEmpty((String)codeEntityToFind)) {
			    listaUnidadesTramitacionDCO = intercambioManager.buscarUnidadesTramitacionDCOByEntidad(codeEntityToFind, tramunitCodeToFind, tramunitNameToFind);
			    req.setAttribute("listaUnidadesTramitacionDCO", (Object)listaUnidadesTramitacionDCO);
			    req.setAttribute("tramunitCodeToFind", (Object)tramunitCodeToFind);
			    req.setAttribute("tramunitNameToFind", (Object)tramunitNameToFind);
			} else if (StringUtils.isNotEmpty((String)tramunitCodeToFind) || StringUtils.isNotEmpty((String)tramunitNameToFind)) {
			    listaUnidadesTramitacionDCO = intercambioManager.buscarUnidadesTramitacionDCO(tramunitCodeToFind, tramunitNameToFind);
				req.setAttribute("listaUnidadesTramitacionDCO", listaUnidadesTramitacionDCO);
				req.setAttribute("tramunitCodeToFind", tramunitCodeToFind);
				req.setAttribute("tramunitNameToFind", tramunitNameToFind);

			}


			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/listadoUnidadesTramitacionDCO.jsp");
			dispatcher.forward(req, resp);
		}catch (Exception e) {
			_logger.error("Ha ocurrido un error en la búsqueda de unidades de tramitación.", e);

			String error = RBUtil.getInstance(useCaseConf.getLocale()).getProperty(Keys.I18N_ISICRESIR_INPUT_DESKTOP_ERROR);
			req.setAttribute(com.ieci.tecdoc.isicres.desktopweb.Keys.REQUEST_ERROR, error);

			RequestDispatcher rd = getServletConfig().getServletContext().getRequestDispatcher("/intercambioRegistralResult.jsp");
			rd.forward(req, resp);
		}

	}

}
