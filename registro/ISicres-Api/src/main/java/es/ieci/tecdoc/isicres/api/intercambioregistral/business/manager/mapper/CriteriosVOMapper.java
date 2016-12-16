package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper;

import es.ieci.tecdoc.fwktd.sir.core.types.CriterioEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.CriterioVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.CriteriosVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIREntradaVO;

public interface CriteriosVOMapper {
    public CriteriosVO map(CriteriosBusquedaIREntradaVO var1);

    public CriterioVO map(CriterioBusquedaIREntradaVO var1);

    public CriterioEnum map(CriterioBusquedaIREntradaEnum var1);
}
