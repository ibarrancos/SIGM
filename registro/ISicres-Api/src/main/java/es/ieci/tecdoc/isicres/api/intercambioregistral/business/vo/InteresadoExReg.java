package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

import es.ieci.tecdoc.fwktd.sir.core.vo.InteresadoVO;

public class InteresadoExReg
extends InteresadoVO {
    private static final long serialVersionUID = -579647416089509621L;
    private String nombrePaisInteresado;
    private String nombreProvinciaInteresado;
    private String nombreMunicipioInteresado;
    private String nombrePaisRepresentante;
    private String nombreProvinciaRepresentante;
    private String nombreMunicipioRepresentante;

    public String getNombrePaisInteresado() {
        return this.nombrePaisInteresado;
    }

    public void setNombrePaisInteresado(String nombrePaisInteresado) {
        this.nombrePaisInteresado = nombrePaisInteresado;
    }

    public String getNombreProvinciaInteresado() {
        return this.nombreProvinciaInteresado;
    }

    public void setNombreProvinciaInteresado(String nombreProvinciaInteresado) {
        this.nombreProvinciaInteresado = nombreProvinciaInteresado;
    }

    public String getNombreMunicipioInteresado() {
        return this.nombreMunicipioInteresado;
    }

    public void setNombreMunicipioInteresado(String nombreMunicipioInteresado) {
        this.nombreMunicipioInteresado = nombreMunicipioInteresado;
    }

    public String getNombrePaisRepresentante() {
        return this.nombrePaisRepresentante;
    }

    public void setNombrePaisRepresentante(String nombrePaisRepresentante) {
        this.nombrePaisRepresentante = nombrePaisRepresentante;
    }

    public String getNombreProvinciaRepresentante() {
        return this.nombreProvinciaRepresentante;
    }

    public void setNombreProvinciaRepresentante(String nombreProvinciaRepresentante) {
        this.nombreProvinciaRepresentante = nombreProvinciaRepresentante;
    }

    public String getNombreMunicipioRepresentante() {
        return this.nombreMunicipioRepresentante;
    }

    public void setNombreMunicipioRepresentante(String nombreMunicipioRepresentante) {
        this.nombreMunicipioRepresentante = nombreMunicipioRepresentante;
    }

    public static long getSerialversionuid() {
        return -579647416089509621L;
    }
}
