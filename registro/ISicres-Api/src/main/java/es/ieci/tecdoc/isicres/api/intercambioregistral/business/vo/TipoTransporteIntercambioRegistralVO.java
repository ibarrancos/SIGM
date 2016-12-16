package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

public class TipoTransporteIntercambioRegistralVO {
    private Long id;
    private Integer idTipoTransporte;
    private String codigoSIR;
    private String descripcion;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoSIR() {
        return this.codigoSIR;
    }

    public void setCodigoSIR(String codigoSIR) {
        this.codigoSIR = codigoSIR;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdTipoTransporte() {
        return this.idTipoTransporte;
    }

    public void setIdTipoTransporte(Integer idTipoTransporte) {
        this.idTipoTransporte = idTipoTransporte;
    }
}
