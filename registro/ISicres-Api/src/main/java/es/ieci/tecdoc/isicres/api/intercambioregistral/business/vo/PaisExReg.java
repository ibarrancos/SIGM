package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

public class PaisExReg {
    private Integer id;
    private String codigo;
    private String nombre;
    private Integer idPaisSicres;

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdPaisSicres() {
        return this.idPaisSicres;
    }

    public void setIdPaisSicres(Integer idPaisSicres) {
        this.idPaisSicres = idPaisSicres;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
