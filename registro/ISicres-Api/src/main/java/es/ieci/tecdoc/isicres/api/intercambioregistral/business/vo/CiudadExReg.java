package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

public class CiudadExReg {
    private Integer id;
    private String codigo;
    private String nombre;
    private Integer idCiudadSicres;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getIdCiudadSicres() {
        return this.idCiudadSicres;
    }

    public void setIdCiudadSicres(Integer idCiudadSicres) {
        this.idCiudadSicres = idCiudadSicres;
    }
}
