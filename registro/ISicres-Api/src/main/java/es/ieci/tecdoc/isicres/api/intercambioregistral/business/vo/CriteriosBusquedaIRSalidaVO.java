/*
 * Decompiled with CFR 0_97.
 * 
 * Could not load the following classes:
 *  es.ieci.tecdoc.fwktd.core.model.BaseValueObject
 */
package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

import es.ieci.tecdoc.fwktd.core.model.BaseValueObject;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIRSalidaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIRSalidaVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 * Failed to analyse overrides
 */
public class CriteriosBusquedaIRSalidaVO
extends BaseValueObject {
    private static final long serialVersionUID = -7831555035143536214L;
    private List<CriterioBusquedaIRSalidaVO> criterios = new ArrayList<CriterioBusquedaIRSalidaVO>();
    private List<CriterioBusquedaIRSalidaEnum> orderBy = new ArrayList<CriterioBusquedaIRSalidaEnum>();

    public CriteriosBusquedaIRSalidaVO() {
    }

    public CriteriosBusquedaIRSalidaVO(List<CriterioBusquedaIRSalidaVO> criterios) {
        this.setCriterios(criterios);
    }

    public List<CriterioBusquedaIRSalidaVO> getCriterios() {
        return this.criterios;
    }

    public void setCriterios(List<CriterioBusquedaIRSalidaVO> criterios) {
        this.criterios.clear();
        if (criterios != null) {
            this.criterios.addAll(criterios);
        }
    }

    public CriteriosBusquedaIRSalidaVO addCriterioVO(CriterioBusquedaIRSalidaVO criterio) {
        if (criterio != null) {
            this.criterios.add(criterio);
        }
        return this;
    }

    public List<CriterioBusquedaIRSalidaEnum> getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(List<CriterioBusquedaIRSalidaEnum> orderBy) {
        this.orderBy.clear();
        if (orderBy != null) {
            this.orderBy.addAll(orderBy);
        }
    }

    public CriteriosBusquedaIRSalidaVO addOrderBy(CriterioBusquedaIRSalidaEnum criterio) {
        if (criterio != null) {
            this.orderBy.add(criterio);
        }
        return this;
    }
}
