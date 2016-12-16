package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.impl;

import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.DireccionesIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.DireccionesIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CiudadExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.PaisExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.ProvinciaExReg;

public class DireccionesIntercambioRegistralManagerImpl
implements DireccionesIntercambioRegistralManager {
    protected DireccionesIntercambioRegistralDAO direccionesIntercambioRegistralDAO;
    protected static final String CODIGO_PAIS_SPAIN = "724";
    private static final String NOMBRE_PAIS_SPAIN = "Espa\u00f1a";

    public ProvinciaExReg getProvinciaExRegByCodigo(String codigo) {
        return this.getDireccionesIntercambioRegistralDAO().getProvinciaExRegByCodigo(codigo);
    }

    public CiudadExReg getCiudadExRegByCodigo(String codigoProvincia, String codigoMunicipio) {
        return this.getDireccionesIntercambioRegistralDAO().getCiudadExRegByCodigo(codigoProvincia, codigoMunicipio);
    }

    public PaisExReg getPaisExRegByCodigo(String codigo) {
        PaisExReg pais = null;
        pais = this.getDireccionesIntercambioRegistralDAO().getPaisExRegByCodigo(codigo);
        return pais;
    }

    public DireccionesIntercambioRegistralDAO getDireccionesIntercambioRegistralDAO() {
        return this.direccionesIntercambioRegistralDAO;
    }

    public void setDireccionesIntercambioRegistralDAO(DireccionesIntercambioRegistralDAO direccionesExRegDAO) {
        this.direccionesIntercambioRegistralDAO = direccionesExRegDAO;
    }
}
