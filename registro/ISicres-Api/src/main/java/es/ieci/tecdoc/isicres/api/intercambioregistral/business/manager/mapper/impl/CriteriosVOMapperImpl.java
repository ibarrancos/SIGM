package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper.impl;

import es.ieci.tecdoc.fwktd.sir.core.types.CriterioEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.EstadoAsientoRegistralEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.OperadorCriterioEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.CriterioVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.CriteriosVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.ConfiguracionIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper.CriteriosVOMapper;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.OperadorCriterioBusquedaIREnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EntidadRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class CriteriosVOMapperImpl
implements CriteriosVOMapper {
    private static final Logger logger = Logger.getLogger((Class)CriteriosVOMapper.class);
    protected ConfiguracionIntercambioRegistralManager configuracionIntercambioRegistralManager;

    public CriteriosVO map(CriteriosBusquedaIREntradaVO criteriosBusquedaIntercambioRegistralVO) {
        CriteriosVO criteriosVO = null;
        if (criteriosBusquedaIntercambioRegistralVO != null) {
            criteriosVO = new CriteriosVO();
            if (CollectionUtils.isNotEmpty(criteriosBusquedaIntercambioRegistralVO.getCriterios())) {
                ArrayList<CriterioVO> lista = new ArrayList<CriterioVO>();
                for (CriterioBusquedaIREntradaVO criterioBusquedaIntercambioRegistralVO : criteriosBusquedaIntercambioRegistralVO.getCriterios()) {
                    CriterioVO criterio = this.map(criterioBusquedaIntercambioRegistralVO);
                    lista.add(criterio);
                }
                criteriosVO.setCriterios(lista);
            }
            if (CollectionUtils.isNotEmpty(criteriosBusquedaIntercambioRegistralVO.getOrderBy())) {
                ArrayList<CriterioEnum> orderByList = new ArrayList<CriterioEnum>();
                for (CriterioBusquedaIREntradaEnum criterioBusquedaIREnum : criteriosBusquedaIntercambioRegistralVO.getOrderBy()) {
                    CriterioEnum criterioEnum = this.map(criterioBusquedaIREnum);
                    orderByList.add(criterioEnum);
                }
                criteriosVO.setOrderBy(orderByList);
            }
        }
        return criteriosVO;
    }

    public CriterioVO map(CriterioBusquedaIREntradaVO criterioBusquedaIntercambioRegistralVO) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"map(CriterioBusquedaIREntradaVO) - start");
        }
        CriterioVO criterioVO = null;
        if (criterioBusquedaIntercambioRegistralVO != null) {
            criterioVO = new CriterioVO();
            if (criterioBusquedaIntercambioRegistralVO.getNombre().equals((Object)CriterioBusquedaIREntradaEnum.ID_OFICINA)) {
                this.mapearCriterioOficina(criterioBusquedaIntercambioRegistralVO, criterioVO);
            } else if (criterioBusquedaIntercambioRegistralVO.getNombre().equals((Object)CriterioBusquedaIREntradaEnum.STATE)) {
                this.mapearCriterioEstado(criterioBusquedaIntercambioRegistralVO, criterioVO);
            } else {
                this.mapearCriterioGenerico(criterioBusquedaIntercambioRegistralVO, criterioVO);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"map(CriterioBusquedaIREntradaVO) - end");
        }
        return criterioVO;
    }

    private void mapearCriterioEstado(CriterioBusquedaIREntradaVO criterioBusquedaIntercambioRegistralVO, CriterioVO criterioVO) {
        criterioVO.setNombre(this.map(criterioBusquedaIntercambioRegistralVO.getNombre()));
        criterioVO.setOperador(this.map(criterioBusquedaIntercambioRegistralVO.getOperador()));
        if (!(criterioBusquedaIntercambioRegistralVO.getValor() instanceof EstadoIntercambioRegistralEntradaEnumVO)) {
            throw new IntercambioRegistralException("Error al mapear el criterio de estado. El valor del operador debe de ser de la clase EstadoIntercambioRegistralEntradaEnumVO");
        }
        EstadoIntercambioRegistralEntradaEnumVO estadoIntercambioRegistralEntradaEnumVO = (EstadoIntercambioRegistralEntradaEnumVO)criterioBusquedaIntercambioRegistralVO.getValor();
        criterioVO.setValor((Object)this.mapearEstadoAsientoEnum(estadoIntercambioRegistralEntradaEnumVO));
    }

    private void mapearCriterioGenerico(CriterioBusquedaIREntradaVO criterioBusquedaIntercambioRegistralVO, CriterioVO criterioVO) {
        criterioVO.setNombre(this.map(criterioBusquedaIntercambioRegistralVO.getNombre()));
        criterioVO.setOperador(this.map(criterioBusquedaIntercambioRegistralVO.getOperador()));
        criterioVO.setValor(criterioBusquedaIntercambioRegistralVO.getValor());
    }

    private void mapearCriterioOficina(CriterioBusquedaIREntradaVO criterioBusquedaIntercambioRegistralVO, CriterioVO criterioVO) {
        String idOficina = String.valueOf(criterioBusquedaIntercambioRegistralVO.getValor());
        EntidadRegistralVO entidadRegistralDestino = this.getConfiguracionIntercambioRegistralManager().getEntidadRegistralVOByIdScrOfic(idOficina);
        criterioVO.setValor((Object)entidadRegistralDestino.getCode());
        criterioVO.setOperador(this.map(criterioBusquedaIntercambioRegistralVO.getOperador()));
        criterioVO.setNombre(this.map(criterioBusquedaIntercambioRegistralVO.getNombre()));
    }

    private EstadoAsientoRegistralEnum mapearEstadoAsientoEnum(EstadoIntercambioRegistralEntradaEnumVO estadoIntercambioRegistralEntradaEnumVO) {
        switch (estadoIntercambioRegistralEntradaEnumVO.getValue()) {
            case 1: {
                return EstadoAsientoRegistralEnum.ACEPTADO;
            }
            case 0: {
                return EstadoAsientoRegistralEnum.RECIBIDO;
            }
            case 2: {
                return EstadoAsientoRegistralEnum.RECHAZADO;
            }
            case 3: {
                return EstadoAsientoRegistralEnum.REENVIADO;
            }
        }
        throw new IntercambioRegistralException("No se ha encontrado un estado equivalente en el SIR");
    }

    public CriterioEnum map(CriterioBusquedaIREntradaEnum criterioBusquedaIREnum) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"map(CriterioBusquedaIREntradaEnum) - start");
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.CODE_ENTITY)) {
            return CriterioEnum.ASIENTO_CODIGO_ENTIDAD_REGISTRAL;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.CONTACTO_ORIGINAL)) {
            return CriterioEnum.ASIENTO_CONTACTO_USUARIO;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.EXCHANGE_DATE)) {
            return CriterioEnum.ASIENTO_FECHA_ENVIO;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.ID_EXCHANGE_SIR)) {
            return CriterioEnum.ASIENTO_IDENTIFICADOR_INTERCAMBIO;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.STATE_DATE)) {
            return CriterioEnum.ASIENTO_FECHA_ESTADO;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.CODE_TRAMUNIT)) {
            return CriterioEnum.ASIENTO_CODIGO_UNIDAD_TRAMITACION_ORIGEN;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.COMMENTS)) {
            return CriterioEnum.ASIENTO_OBSERVACIONES_APUNTE;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.NAME_ENTITY)) {
            return CriterioEnum.ASIENTO_DESCRIPCION_ENTIDAD_REGISTRAL_ORIGEN;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.NAME_TRAMUNIT)) {
            return CriterioEnum.ASIENTO_DESCRIPCION_UNIDAD_TRAMITACION_ORIGEN;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.NUM_REG_ORIGINAL)) {
            return CriterioEnum.ASIENTO_NUMERO_REGISTRO_INICIAL;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.USERNAME)) {
            return CriterioEnum.ASIENTO_CONTACTO_USUARIO;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.ID_OFICINA)) {
            return CriterioEnum.ASIENTO_CODIGO_ENTIDAD_REGISTRAL_DESTINO;
        }
        if (criterioBusquedaIREnum.equals((Object)CriterioBusquedaIREntradaEnum.STATE)) {
            return CriterioEnum.ASIENTO_ESTADO;
        }
        logger.error((Object)"No se ha encontrado el criterio de busqueda equivalente para el sir");
        throw new IntercambioRegistralException("No se ha encontrado el criterio de busqueda equivalente para el sir");
    }

    private OperadorCriterioEnum map(OperadorCriterioBusquedaIREnum operadorCriterioBusquedaIREnum) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"map(OperadorCriterioBusquedaIREnum) - start");
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.EQUAL)) {
            return OperadorCriterioEnum.EQUAL;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.EQUAL_OR_GREATER_THAN)) {
            return OperadorCriterioEnum.EQUAL_OR_GREATER_THAN;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.EQUAL_OR_LESS_THAN)) {
            return OperadorCriterioEnum.EQUAL_OR_LESS_THAN;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.GREATER_THAN)) {
            return OperadorCriterioEnum.GREATER_THAN;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.IN)) {
            return OperadorCriterioEnum.IN;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.LESS_THAN)) {
            return OperadorCriterioEnum.LESS_THAN;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.LIKE)) {
            return OperadorCriterioEnum.LIKE;
        }
        if (operadorCriterioBusquedaIREnum.equals((Object)OperadorCriterioBusquedaIREnum.NOT_EQUAL)) {
            return OperadorCriterioEnum.NOT_EQUAL;
        }
        logger.error((Object)"No se ha encontrado un operador equipavelente para el SIR");
        throw new IntercambioRegistralException("No se ha encontrado un operador equipavelente para el SIR");
    }

    public ConfiguracionIntercambioRegistralManager getConfiguracionIntercambioRegistralManager() {
        return this.configuracionIntercambioRegistralManager;
    }

    public void setConfiguracionIntercambioRegistralManager(ConfiguracionIntercambioRegistralManager configuracionIntercambioRegistralManager) {
        this.configuracionIntercambioRegistralManager = configuracionIntercambioRegistralManager;
    }
}
