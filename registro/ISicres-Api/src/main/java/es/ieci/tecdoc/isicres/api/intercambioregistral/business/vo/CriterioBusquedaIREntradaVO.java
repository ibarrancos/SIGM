package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

import es.ieci.tecdoc.fwktd.core.model.BaseValueObject;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.OperadorCriterioBusquedaIREnum;

public class CriterioBusquedaIREntradaVO
extends BaseValueObject {
    private static final long serialVersionUID = 1336948933110953963L;
    private CriterioBusquedaIREntradaEnum nombre = null;
    private OperadorCriterioBusquedaIREnum operador = OperadorCriterioBusquedaIREnum.EQUAL;
    private Object valor = null;

    public CriterioBusquedaIREntradaVO() {
    }

    public CriterioBusquedaIREntradaVO(CriterioBusquedaIREntradaEnum nombre, Object valor) {
        this(nombre, OperadorCriterioBusquedaIREnum.EQUAL, valor);
    }

    public CriterioBusquedaIREntradaVO(CriterioBusquedaIREntradaEnum nombre, OperadorCriterioBusquedaIREnum operador, Object valor) {
        this();
        this.setNombre(nombre);
        this.setOperador(operador);
        this.setValor(valor);
    }

    public CriterioBusquedaIREntradaEnum getNombre() {
        return this.nombre;
    }

    public void setNombre(CriterioBusquedaIREntradaEnum nombre) {
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
