package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager;

import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CiudadExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.PaisExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.ProvinciaExReg;

public interface DireccionesIntercambioRegistralManager {
    public ProvinciaExReg getProvinciaExRegByCodigo(String var1);

    public CiudadExReg getCiudadExRegByCodigo(String var1, String var2);

    public PaisExReg getPaisExRegByCodigo(String var1);
}
