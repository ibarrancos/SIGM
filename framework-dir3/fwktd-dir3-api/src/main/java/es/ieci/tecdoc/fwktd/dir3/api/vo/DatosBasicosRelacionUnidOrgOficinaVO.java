package es.ieci.tecdoc.fwktd.dir3.api.vo;

import es.ieci.tecdoc.fwktd.core.model.Entity;

public class DatosBasicosRelacionUnidOrgOficinaVO
extends Entity {
    private static final long serialVersionUID = -7443550436672001424L;
    private String codigoUnidadOrganica;
    private String denominacionUnidadOrganica;
    private String codigoOficina;
    private String denominacionOficina;
    private String estadoRelacion;

    public String getCodigoUnidadOrganica() {
        return this.codigoUnidadOrganica;
    }

    public void setCodigoUnidadOrganica(String codigoUnidadOrganica) {
        this.codigoUnidadOrganica = codigoUnidadOrganica;
    }

    public String getDenominacionUnidadOrganica() {
        return this.denominacionUnidadOrganica;
    }

    public void setDenominacionUnidadOrganica(String denominacionUnidadOrganica) {
        this.denominacionUnidadOrganica = denominacionUnidadOrganica;
    }

    public String getCodigoOficina() {
        return this.codigoOficina;
    }

    public void setCodigoOficina(String codigoOficina) {
        this.codigoOficina = codigoOficina;
    }

    public String getDenominacionOficina() {
        return this.denominacionOficina;
    }

    public void setDenominacionOficina(String denominacionOficina) {
        this.denominacionOficina = denominacionOficina;
    }

    public String getEstadoRelacion() {
        return this.estadoRelacion;
    }

    public void setEstadoRelacion(String estadoRelacion) {
        this.estadoRelacion = estadoRelacion;
    }
}
