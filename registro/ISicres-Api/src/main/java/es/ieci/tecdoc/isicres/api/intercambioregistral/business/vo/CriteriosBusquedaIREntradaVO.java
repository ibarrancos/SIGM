package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

import es.ieci.tecdoc.fwktd.core.model.BaseValueObject;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIREntradaVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CriteriosBusquedaIREntradaVO
extends BaseValueObject {
    private static final long serialVersionUID = -7831555035143536214L;
    private List<CriterioBusquedaIREntradaVO> criterios = new ArrayList<CriterioBusquedaIREntradaVO>();
    private List<CriterioBusquedaIREntradaEnum> orderBy = new ArrayList<CriterioBusquedaIREntradaEnum>();

    public CriteriosBusquedaIREntradaVO() {
    }

    public CriteriosBusquedaIREntradaVO(List<CriterioBusquedaIREntradaVO> criterios) {
        this.setCriterios(criterios);
    }

    public List<CriterioBusquedaIREntradaVO> getCriterios() {
        return this.criterios;
    }

    public void setCriterios(List<CriterioBusquedaIREntradaVO> criterios) {
        this.criterios.clear();
        if (criterios != null) {
            this.criterios.addAll(criterios);
        }
    }

    public CriteriosBusquedaIREntradaVO addCriterioVO(CriterioBusquedaIREntradaVO criterio) {
        if (criterio != null) {
            this.criterios.add(criterio);
        }
        return this;
    }

    public List<CriterioBusquedaIREntradaEnum> getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(List<CriterioBusquedaIREntradaEnum> orderBy) {
        this.orderBy.clear();
        if (orderBy != null) {
            this.orderBy.addAll(orderBy);
        }
    }

    public CriteriosBusquedaIREntradaVO addOrderBy(CriterioBusquedaIREntradaEnum criterio) {
        if (criterio != null) {
            this.orderBy.add(criterio);
        }
        return this;
    }
}
