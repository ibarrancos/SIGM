package es.ieci.tecdoc.fwktd.dir3.api.vo.relacion;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value="OficinaOrganismo")
public class RelacionUnidOrgOficinaVO {
    @XStreamAlias(value="Codigo_Unidad_Organica")
    private String codigoUnidadOrganica;
    @XStreamAlias(value="Denominacion_Unidad_Organica")
    private String denominacionUnidadOrganica;
    @XStreamAlias(value="Codigo_Oficina")
    private String codigoOficina;
    @XStreamAlias(value="Denominacion_Oficina")
    private String denominacionOficina;
    @XStreamAlias(value="Estado_Relacion")
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
