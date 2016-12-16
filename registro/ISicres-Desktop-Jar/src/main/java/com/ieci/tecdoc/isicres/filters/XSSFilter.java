package com.ieci.tecdoc.isicres.filters;

import com.ieci.tecdoc.isicres.desktopweb.utils.IdiomaUtils;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.desktopweb.utils.ResponseUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

public class XSSFilter
implements Filter {
    public Map validador = new HashMap();
    public static final String VALIDACION_ESTANDAR = "pattern.defecto";
    public Pattern validacionEstandar = null;
    public Properties config = new Properties();
    private static Logger _logger = Logger.getLogger((Class)XSSFilter.class);

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (req != null) {
            HttpServletRequest request = (HttpServletRequest)req;
            HttpServletResponse response = (HttpServletResponse)res;
            Enumeration paramNames = req.getParameterNames();
            String paramName = null;
            String paramValue = null;
            boolean valido = true;
            Pattern pattern = null;
            Matcher matcher = null;
            while (paramNames.hasMoreElements()) {
                paramName = (String)paramNames.nextElement();
                paramValue = req.getParameter(paramName);
                if (this.validador.containsKey(paramName)) {
                    pattern = (Pattern)this.validador.get(paramName);
                    matcher = pattern.matcher((CharSequence)paramValue);
                    valido = matcher.matches();
                }
                if (valido) {
                    matcher = this.validacionEstandar.matcher((CharSequence)paramValue.toLowerCase());
                    boolean bl = valido = !matcher.find();
                }
                if (valido) continue;
                this.stopErrorValidation(request, response, paramName, paramValue);
                return;
            }
            chain.doFilter(req, res);
        }
    }

    private void stopErrorValidation(HttpServletRequest request, HttpServletResponse response, String paramName, String paramValue) throws IOException {
        Long numIdioma;
        _logger.warn((Object)("Se ha detectado un parametro invalido: " + paramName + "(" + paramValue + ")"));
        Locale locale = request.getLocale();
        String idioma = (String)request.getSession().getAttribute("JIdioma");
        if (idioma == null) {
            idioma = IdiomaUtils.getInstance().getIdioma(request);
        }
        if ((numIdioma = (Long)request.getSession().getAttribute("JNumIdioma")) == null) {
            numIdioma = IdiomaUtils.getInstance().getNumIdioma(request);
        }
        response.setContentType("text/html; charset=UTF-8");
        RBUtil rbutil = RBUtil.getInstance(locale);
        PrintWriter writer = response.getWriter();
        ResponseUtils.generateJavaScriptLogSessionExpiredDtrfdr(writer, rbutil.getProperty("exception.validationException"), idioma, numIdioma);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            HashMap<String, Pattern> patterns = new HashMap<String, Pattern>();
            _logger.info((Object)"Inicializando configuracion");
            _logger.info((Object)"Cargando fichero propiedades");
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("ParamValidation.properties");
            this.config.load(is);
            _logger.info((Object)"Fichero cargado");
            _logger.info((Object)"Configurando validacion estandar");
            this.validacionEstandar = Pattern.compile(this.config.getProperty("pattern.defecto"));
            Enumeration props = this.config.propertyNames();
            String prop = null;
            String propPattern = null;
            String sPattern = null;
            while (props.hasMoreElements()) {
                prop = (String)props.nextElement();
                _logger.info((Object)("Cargando " + prop));
                if (prop.startsWith("pattern.")) continue;
                propPattern = this.config.getProperty(prop);
                if (!patterns.containsKey(propPattern)) {
                    sPattern = this.config.getProperty(propPattern);
                    patterns.put(propPattern, Pattern.compile(sPattern));
                }
                this.validador.put(prop, patterns.get(propPattern));
            }
        }
        catch (Exception e) {
            _logger.fatal((Object)"No se ha podido configurar el filtro anti-xss.", (Throwable)e);
            throw new ServletException("No se ha podido configurar el filtro anti-xss.", (Throwable)e);
        }
    }

    public void destroy() {
    }
}
