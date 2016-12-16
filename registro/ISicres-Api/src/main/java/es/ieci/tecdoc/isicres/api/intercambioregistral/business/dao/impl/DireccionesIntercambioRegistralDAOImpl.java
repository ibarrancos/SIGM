package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.DireccionesIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CiudadExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.PaisExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.ProvinciaExReg;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class DireccionesIntercambioRegistralDAOImpl
implements DireccionesIntercambioRegistralDAO {
    private static final String GET_CIUDAD_EXREG_BY_CODIGO_SQLMAP = "DireccionesExReg.getCiudadExRegByCodigo";
    private static final String GET_PROVINCIA_EXREG_BY_CODIGO_SQLMAP = "DireccionesExReg.getProvinciaExRegByCodigo";
    private static final String GET_PAIS_EXREG_BY_CODIGO_SQLMAP = "DireccionesExReg.getPaisExRegByCodigo";
    private static final Logger logger = Logger.getLogger((Class)DireccionesIntercambioRegistralDAOImpl.class);
    protected SqlMapClientTemplate sqlMapClientTemplate = new SqlMapClientTemplate();

    public ProvinciaExReg getProvinciaExRegByCodigo(String codigo) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("getProvinciaDCOByCodigo(String) - start - Codigo: [" + codigo + "]"));
        }
        ProvinciaExReg provincia = (ProvinciaExReg)this.getSqlMapClientTemplate().queryForObject("DireccionesExReg.getProvinciaExRegByCodigo", (Object)codigo);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getProvinciaDCOByCodigo(String) - end");
        }
        return provincia;
    }

    public CiudadExReg getCiudadExRegByCodigo(String codigoProvincia, String codigoMunicipio) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("getCiudadDCOByCodigo(String) - start - Codigo Provincia: [" + codigoProvincia + "] C\u00f3digo Municipio: [" + codigoMunicipio + "]"));
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("codigoProvincia", codigoProvincia);
        map.put("codigoMunicipio", codigoMunicipio);
        CiudadExReg ciudadDCO = (CiudadExReg)this.getSqlMapClientTemplate().queryForObject("DireccionesExReg.getCiudadExRegByCodigo", map);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getCiudadDCOByCodigo(String) - end");
        }
        return ciudadDCO;
    }

    public PaisExReg getPaisExRegByCodigo(String codigo) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("getPaisExRegByCodigo(String) - start - Codigo: [" + codigo + "]"));
        }
        PaisExReg pais = (PaisExReg)this.getSqlMapClientTemplate().queryForObject("DireccionesExReg.getPaisExRegByCodigo", (Object)codigo);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getPaisExRegByCodigo(String) - end");
        }
        return pais;
    }

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return this.sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    public final void setSqlMapClient(SqlMapClient aSqlMapClient) {
        this.sqlMapClientTemplate.setSqlMapClient(aSqlMapClient);
    }
}
