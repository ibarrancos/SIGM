package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

import es.ieci.tecdoc.fwktd.core.model.BaseValueObject;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIRSalidaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.OperadorCriterioBusquedaIREnum;

public class CriterioBusquedaIRSalidaVO
extends BaseValueObject {
    private static final long serialVersionUID = 1336948933110953963L;
    private CriterioBusquedaIRSalidaEnum nombre = null;
    private OperadorCriterioBusquedaIREnum operador = OperadorCriterioBusquedaIREnum.EQUAL;
    private Object valor = null;

    public CriterioBusquedaIRSalidaVO() {
    }

    public CriterioBusquedaIRSalidaVO(CriterioBusquedaIRSalidaEnum nombre, Object valor) {
        this(nombre, OperadorCriterioBusquedaIREnum.EQUAL, valor);
    }

    public CriterioBusquedaIRSalidaVO(CriterioBusquedaIRSalidaEnum nombre, OperadorCriterioBusquedaIREnum operador, Object valor) {
        this();
        this.setNombre(nombre);
        this.setOperador(operador);
        this.setValor(valor);
    }

    public CriterioBusquedaIRSalidaEnum getNombre() {
        return this.nombre;
    }

    public void setNombre(CriterioBusquedaIRSalidaEnum nombre) {
        this.nombre = nombre;
    }

    public OperadorCriterioBusquedaIREnum getOperador() {
        return this.operador;
    }

    public void setOperador(OperadorCriterioBusquedaIREnum operador) {
        this.operador = operador;
    }

    public Object getValor() {
        return this.valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }
}
