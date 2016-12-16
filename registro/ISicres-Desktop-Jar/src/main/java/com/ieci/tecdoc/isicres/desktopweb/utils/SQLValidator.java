package com.ieci.tecdoc.isicres.desktopweb.utils;

import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.ValidationRBUtil;
import com.ieci.tecdoc.isicres.session.distribution.DistributionSession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.distribution.xml.DistributionFields;
import com.ieci.tecdoc.isicres.usecase.distribution.xml.DistributionSearchFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SQLValidator {
    private static Logger _logger = Logger.getLogger((Class)SQLValidator.class);
    private static final String FLD = "@FLD";
    private static final String SPACE_AND_SPACE = " AND ";
    private static final String COMILLA_SPACE_AND = "' AND";
    private static final String COMILLA = "'";
    protected Pattern validacion_estandar = Pattern.compile(ValidationRBUtil.getInstance(null).getProperty("sqlvalidator.validacion.sql.estandar"));
    public static final String SQLVALIDATOR_VALIDACION_SQL_ESTANDAR = "sqlvalidator.validacion.sql.estandar";
    private static SQLValidator _instance = null;

    public SQLValidator() {
        if (_logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("validacion_estandar: ").append(this.validacion_estandar);
            _logger.debug((Object)sb.toString());
        }
    }

    public static synchronized SQLValidator getInstance() {
        if (_instance == null) {
            _instance = new SQLValidator();
        }
        return _instance;
    }

    public void validateDistributionDistWhere(String distWhere) throws ValidationException {
        if (StringUtils.isNotBlank((String)distWhere)) {
            String[] campos = distWhere.split(" AND ");
            for (int i = 0; i < campos.length; ++i) {
                String cadenaConsulta = campos[i];
                if (!(cadenaConsulta.startsWith("STATE") || cadenaConsulta.startsWith("DIST_DATE") || cadenaConsulta.startsWith("STATE_DATE") || cadenaConsulta.startsWith("@ORIG") || cadenaConsulta.startsWith("@DEST"))) {
                    throw new ValidationException("validationexception.error_validation_data");
                }
                this.validarCadenaSQLInjection(cadenaConsulta, this.validacion_estandar);
            }
        }
    }

    public String validateDistributionRegWhere(UseCaseConf useCaseConf, Integer typeDist, String regWhere) throws Exception {
        if (_logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("RegWhere distribuci\u00f3n: ").append(regWhere);
            _logger.debug((Object)sb.toString());
        }
        StringBuffer cadenaConsultaTratada = new StringBuffer();
        if (StringUtils.isNotBlank((String)regWhere)) {
            if (regWhere.contains((CharSequence)"\\'")) {
                throw new ValidationException("validationexception.error_validation_data");
            }
            HashMap camposDeBusqueda = this.obtenerInfoCamposConsulta(useCaseConf, typeDist);
            String[] campos = regWhere.split("@FLD");
            for (int i = 0; i < campos.length; ++i) {
                String cadenaConsulta = campos[i];
                if (!StringUtils.isNotBlank((String)cadenaConsulta)) continue;
                if (StringUtils.isNotBlank((String)cadenaConsultaTratada.toString())) {
                    cadenaConsultaTratada.append(" AND ");
                }
                this.validarCamposRegWhereDistribution(cadenaConsultaTratada, camposDeBusqueda, cadenaConsulta);
            }
        }
        if (_logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Consulta distribuci\u00f3n: ").append(cadenaConsultaTratada.toString());
            _logger.debug((Object)sb.toString());
        }
        return cadenaConsultaTratada.toString();
    }

    public String validateQueryCamposValidados(String vldQuery, String vldQueryValue) throws ValidationException {
        if (StringUtils.isNotBlank((String)vldQueryValue)) {
            if (_logger.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("ValidateQueryCamposValidados: vldQuery ").append(vldQuery).append(" vldQueryValue: ").append(vldQueryValue);
                _logger.debug((Object)sb.toString());
            }
            if (vldQueryValue.contains((CharSequence)"\\'")) {
                throw new ValidationException("validationexception.error_validation_data");
            }
            String[] stringFieldName = Keys.STRING_FIELD_NAME;
            boolean campoCorrecto = false;
            for (int i = 0; i < stringFieldName.length; ++i) {
                String campoConsulta = stringFieldName[i];
                if (!vldQuery.startsWith(campoConsulta)) continue;
                campoCorrecto = true;
            }
            if (!campoCorrecto) {
                throw new ValidationException("validationexception.error_validation_data");
            }
            vldQueryValue = "'" + StringEscapeUtils.escapeSql((String)vldQueryValue) + "'";
            if (_logger.isDebugEnabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("ValidateQueryCamposValidados adaptados a SQL: vldQuery ").append(vldQuery).append(" vldQueryValue: ").append(vldQueryValue);
                _logger.debug((Object)sb.toString());
            }
        }
        return vldQueryValue;
    }

    public void validateQueryInteresados(String cadenaConsulta) {
    }

    private void validarCamposRegWhereDistribution(StringBuffer cadenaConsultaTratada, HashMap camposDeBusqueda, String cadenaConsulta) throws ValidationException {
        int idCampo = this.getIDCampoWhereDistribution(cadenaConsulta);
        if (idCampo == 1 || idCampo == 2 || idCampo == 7 || idCampo == 8 || idCampo == 9 || idCampo == 16 || idCampo == 17) {
            cadenaConsultaTratada.append(this.validarCampo(Integer.toString(idCampo), camposDeBusqueda, cadenaConsulta));
        } else {
            this.exceptionCampoNoValido(cadenaConsulta);
        }
    }

    public void validateOrderQueryRegister(String orderBy) throws ValidationException {
        if (StringUtils.isNotBlank((String)orderBy)) {
            StringTokenizer camposOrdenados = new StringTokenizer(orderBy, ",");
            while (camposOrdenados.hasMoreTokens()) {
                String campoOrdenado = camposOrdenados.nextToken();
                String[] partesCampoOrdenado = campoOrdenado.split(" ");
                for (int posicionCadena = 0; posicionCadena < partesCampoOrdenado.length; ++posicionCadena) {
                    String cadenaAux = partesCampoOrdenado[posicionCadena];
                    if (posicionCadena == 0) {
                        this.validarFLDName(cadenaAux.toUpperCase());
                    }
                    if (posicionCadena == 1) {
                        this.validateCadenaOrder(cadenaAux.toUpperCase());
                    }
                    if (posicionCadena <= 1) continue;
                    throw new ValidationException("validationexception.error_validation_data");
                }
            }
        }
    }

    private void validateCadenaOrder(String cadenaAux) throws ValidationException {
        if (!(!StringUtils.isNotBlank((String)cadenaAux) || "ASC".equalsIgnoreCase(cadenaAux) || "DESC".equalsIgnoreCase(cadenaAux))) {
            throw new ValidationException("validationexception.error_validation_data");
        }
    }

    private void validarFLDName(String nombreCampo) throws ValidationException {
        if (nombreCampo.startsWith("FLD")) {
            try {
                Integer.parseInt(nombreCampo.substring("FLD".length(), nombreCampo.length()));
            }
            catch (NumberFormatException nFE) {
                throw new ValidationException("validationexception.error_validation_data");
            }
        } else {
            throw new ValidationException("validationexception.error_validation_data");
        }
    }

    private int getIDCampoWhereDistribution(String cadenaConsulta) {
        int idCampo;
        try {
            idCampo = Integer.parseInt(cadenaConsulta.substring(0, 2));
        }
        catch (NumberFormatException nFE) {
            idCampo = Integer.parseInt(cadenaConsulta.substring(0, 1));
        }
        return idCampo;
    }

    private void exceptionCampoNoValido(String cadenaConsulta) throws ValidationException {
        if (_logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("El campo indicado no es v\u00e1lido: ").append(cadenaConsulta);
            _logger.debug((Object)sb.toString());
        }
        throw new ValidationException("validationexception.error_validation_data");
    }

    private HashMap obtenerInfoCamposConsulta(UseCaseConf useCaseConf, Integer typeDist) throws Exception {
        String dataBaseType = DistributionSession.getDataBaseType((String)useCaseConf.getSessionID());
        DistributionSearchFields distributionSearchFields = new DistributionSearchFields(new Integer(2), typeDist, useCaseConf.getLocale(), dataBaseType);
        List fieldSearch = distributionSearchFields.getResult();
        HashMap<String, DistributionFields> camposDeBusqueda = new HashMap<String, DistributionFields>();
        DistributionFields datosCampo2 = null;
        for (DistributionFields datosCampo2 : fieldSearch) {
            camposDeBusqueda.put(datosCampo2.getFieldName(), datosCampo2);
        }
        return camposDeBusqueda;
    }

    private String validarCampo(String campo, HashMap camposDeBusqueda, String cadenaConsulta) throws ValidationException {
        String result = "";
        DistributionFields datosCampo = (DistributionFields)camposDeBusqueda.get("@FLD" + campo);
        String cadenaConsultaAux = cadenaConsulta.substring(campo.length());
        if (cadenaConsultaAux.startsWith(" ")) {
            cadenaConsultaAux = cadenaConsultaAux.substring(1, cadenaConsultaAux.length());
        }
        Map operadores = datosCampo.getOperators();
        Object key = null;
        Object valueOperator = null;
        String whereCampo = null;
        if (datosCampo.getFieldType() == 2) {
            whereCampo = this.validateCampoTipoFecha(cadenaConsultaAux);
            result = "@FLD" + campo + whereCampo;
        } else if (datosCampo.getFieldType() == 0 || datosCampo.getFieldType() == 1) {
            result = this.validateCamposStringOrNumeric(campo, datosCampo, cadenaConsultaAux, operadores);
        }
        if (StringUtils.isBlank((String)result)) {
            throw new ValidationException("validationexception.error_validation_data");
        }
        return result;
    }

    private String validateCamposStringOrNumeric(String campo, DistributionFields datosCampo, String cadenaConsultaAux, Map operadores) throws ValidationException {
        String result = null;
        for (String key : operadores.keySet()) {
            String valueOperator = (String)operadores.get(key);
            if (!cadenaConsultaAux.startsWith(valueOperator)) continue;
            String whereCampo = cadenaConsultaAux.substring(valueOperator.length());
            if (datosCampo.getFieldType() == 0) {
                String criterioBusqueda = this.obtenerCriterioDeBusquedaParaString(whereCampo);
                whereCampo = "'" + StringEscapeUtils.escapeSql((String)criterioBusqueda) + "'";
            } else {
                this.validateCampoTipoNumerico(datosCampo, whereCampo);
            }
            result = "@FLD" + campo + " " + valueOperator + " " + whereCampo;
        }
        return result;
    }

    private String validateCampoTipoFecha(String cadenaConsultaAux) throws ValidationException {
        String whereCampo = cadenaConsultaAux;
        if (whereCampo.endsWith(" AND ")) {
            whereCampo = whereCampo.substring(0, whereCampo.length() - " AND ".length());
        }
        this.validarCadenaSQLInjection(whereCampo, this.validacion_estandar);
        return whereCampo;
    }

    private void validateCampoTipoNumerico(DistributionFields datosCampo, String whereCampo) throws ValidationException {
        if (datosCampo.getFieldType() == 1) {
            if (whereCampo.endsWith(" AND ")) {
                whereCampo = whereCampo.substring(0, whereCampo.length() - " AND ".length());
            }
            try {
                Integer.parseInt(whereCampo);
            }
            catch (NumberFormatException nFE) {
                throw new ValidationException("validationexception.error_validation_data");
            }
        }
    }

    private String obtenerCriterioDeBusquedaParaString(String whereCampo) {
        String criterioBusqueda = whereCampo.startsWith(" ") ? whereCampo.substring(2, whereCampo.length() - " ".length()) : whereCampo.substring(1, whereCampo.length() - " ".length());
        if (criterioBusqueda.endsWith("' AND")) {
            criterioBusqueda = criterioBusqueda.substring(0, criterioBusqueda.length() - "' AND".length());
        }
        return criterioBusqueda;
    }

    private void validarCadenaSQLInjection(String cadenaConsulta, Pattern patronValidacion) throws ValidationException {
        boolean valido;
        Matcher matcher = patronValidacion.matcher((CharSequence)cadenaConsulta.toLowerCase());
        boolean bl = valido = !matcher.find();
        if (!valido) {
            throw new ValidationException("validationexception.error_validation_data");
        }
    }
}
