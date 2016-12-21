package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.impl;

import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.fwktd.sir.core.types.CriterioEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.DocumentacionFisicaEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.EstadoAsientoRegistralEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.OperadorCriterioEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoDocumentoIdentificacionEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoRechazoEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoRegistroEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoTransporteEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.AnexoVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.CriterioVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.CriteriosVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.InteresadoVO;
import es.ieci.tecdoc.isicres.api.business.keys.ConstantKeys;
import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.business.manager.RegistroManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.CampoAdicionalRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.DocumentoFisicoVO;
import es.ieci.tecdoc.isicres.api.business.vo.DocumentoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.IdentificadorRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.PaginaDocumentoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroEntradaVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroOriginalVO;
import es.ieci.tecdoc.isicres.api.business.vo.TipoTransporteVO;
import es.ieci.tecdoc.isicres.api.business.vo.TransporteVO;
import es.ieci.tecdoc.isicres.api.business.vo.UnidadAdministrativaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.contextholder.IsicresContextHolder;
import es.ieci.tecdoc.isicres.api.intercambio.registral.business.util.IntercambioRegistralConfiguration;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.BandejaEntradaIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.BandejaSalidaIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralExceptionCodes;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.ConfiguracionIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.DireccionesIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralEntradaManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralSIRManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.TipoTransporteIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper.AsientoRegistralMapper;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper.CriteriosVOMapper;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.util.InteresadoUtils;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaEntradaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CiudadExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EntidadRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralEntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InteresadoExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.PaisExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.ProvinciaExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoTransporteIntercambioRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadAdministrativaIntercambioRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionIntercambioRegistralVO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public class IntercambioRegistralEntradaManagerImpl
implements IntercambioRegistralEntradaManager {
    private static final String ESPACIO = " ";
    private static final String SALTO_LINEA = "\n";
    private static final String BLANK = " ";
    private static final int maxLengthCampoInteresado = 95;
    private static Logger logger = Logger.getLogger((Class)IntercambioRegistralEntradaManagerImpl.class);
    protected IntercambioRegistralSIRManager intercambioRegistralSIRManager;
    protected ConfiguracionIntercambioRegistralManager configuracionIntercambioRegistralManager;
    protected BandejaEntradaIntercambioRegistralDAO bandejaEntradaIntercambioRegistralDAO;
    protected BandejaSalidaIntercambioRegistralDAO bandejaSalidaIntercambioRegistralDAO;
    protected DataFieldMaxValueIncrementer intercambioRegistralEntradaIncrementer;
    protected TipoTransporteIntercambioRegistralManager tipoTransporteIntercambioRegistralManager;
    protected DireccionesIntercambioRegistralManager direccionesIntercambioRegistralRegManager;
    protected CriteriosVOMapper criteriosVOMapper;

    @Override
    public List<BandejaEntradaItemVO> getBandejaEntradaIntercambioRegistral(Integer estado, Integer idOficina) {
        List<BandejaEntradaItemVO> result = null;
        if (EstadoIntercambioRegistralEntradaEnumVO.ACEPTADO.getValue() == estado.intValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"Obtenemos la bandeja de entrada para el estado ACEPTADOS");
            }
            result = this.getBandejaEntradaIntercambioRegistralAceptados(idOficina);
        }
        if (EstadoIntercambioRegistralEntradaEnumVO.RECHAZADO.getValue() == estado.intValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"Obtenemos la bandeja de entrada para el estado rechazados");
            }
            result = this.getBandejaEntradaIntercambioRegistralRechazados(idOficina);
        }
        if (EstadoIntercambioRegistralEntradaEnumVO.PENDIENTE.getValue() == estado.intValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"Obtenemos la bandeja de entrada para el estado pendiente");
            }
            result = this.getBandejaEntradaIntercambioRegistralPendientes(idOficina, estado);
        }
        if (EstadoIntercambioRegistralEntradaEnumVO.REENVIADO.getValue() == estado.intValue()) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"Obtenemos la bandeja de entrada para el estado reenviado");
            }
            result = this.getBandejaEntradaIntercambioRegistralReenviados(idOficina, estado);
        }
        return result;
    }

    private List<BandejaEntradaItemVO> getBandejaEntradaIntercambioRegistralPendientes(Integer idOficina, Integer estado) {
        estado = this.convertEstadoToSIREnum(estado);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Obtenemos la bandeja de entrada para el estado pendientes : y la oficina: " + idOficina));
        }
        EntidadRegistralVO entidadRegistralDestino = this.getConfiguracionIntercambioRegistralManager().getEntidadRegistralVOByIdScrOfic(String.valueOf(idOficina));
        ArrayList<BandejaEntradaItemVO> bandejaEntradaItems = new ArrayList<BandejaEntradaItemVO>();
        if (entidadRegistralDestino != null) {
            CriteriosVO criterios = new CriteriosVO();
            CriterioVO criterioEntidadDestino = new CriterioVO(CriterioEnum.ASIENTO_CODIGO_ENTIDAD_REGISTRAL_DESTINO, (Object)entidadRegistralDestino.getCode());
            criterios.addCriterioVO(criterioEntidadDestino);
            CriterioVO criterioEstado = new CriterioVO(CriterioEnum.ASIENTO_ESTADO, OperadorCriterioEnum.IN, (Object)new Integer[]{EstadoAsientoRegistralEnum.RECIBIDO.getValue(), EstadoAsientoRegistralEnum.DEVUELTO.getValue()});
            criterios.addCriterioVO(criterioEstado);
            List<AsientoRegistralVO> bandejaEntrada = this.getIntercambioRegistralSIRManager().findAsientosRegistrales(criterios);
            AsientoRegistralMapper mapper = new AsientoRegistralMapper();
            for (AsientoRegistralVO asientoRegistralVO : bandejaEntrada) {
                BandejaEntradaItemVO bandejaItem = mapper.toBandejaEntradaItemVO(asientoRegistralVO);
                bandejaEntradaItems.add(bandejaItem);
            }
        } else {
            logger.info((Object)("No hay configuracion de intercambio registral para esta oficina: " + idOficina));
            throw new IntercambioRegistralException("No se puede enviar porque esta oficina no tiene configurada su Entidad Registral del Directorio Com\u00fan de Organismos.", IntercambioRegistralExceptionCodes.ERROR_CODE_OFICINA_NO_MAPEADA);
        }
        return bandejaEntradaItems;
    }

    private List<BandejaEntradaItemVO> getBandejaEntradaIntercambioRegistralReenviados(Integer idOficina, Integer estado) {
        return this.getBandejaEntradaIntercambioRegistralDAO().getBandejaEntradaByEstado(3, idOficina);
    }

    private Integer convertEstadoToSIREnum(Integer estado) {
        if (estado == 1) {
            estado = EstadoAsientoRegistralEnum.ACEPTADO.getValue();
        }
        if (estado == 0) {
            estado = EstadoAsientoRegistralEnum.RECIBIDO.getValue();
        }
        if (estado == 2) {
            estado = EstadoAsientoRegistralEnum.RECHAZADO.getValue();
        }
        if (estado == 3) {
            estado = EstadoAsientoRegistralEnum.REENVIADO.getValue();
        }
        return estado;
    }

    private List<BandejaEntradaItemVO> getBandejaEntradaIntercambioRegistralAceptados(Integer idOficina) {
        return this.getBandejaEntradaIntercambioRegistralDAO().getBandejaEntradaByEstado(1, idOficina);
    }

    private List<BandejaEntradaItemVO> getBandejaEntradaIntercambioRegistralRechazados(Integer idOficina) {
        return this.getBandejaEntradaIntercambioRegistralDAO().getBandejaEntradaByEstado(2, idOficina);
    }

    @Override
    public void guardarIntercambioRegistralEntrada(IntercambioRegistralEntradaVO intercambioRegistralEntrada) {
        Long id = this.getIntercambioRegistralEntradaIncrementer().nextLongValue();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Se va a guardar el intercambio registral de entrada con id de intercambio = " + intercambioRegistralEntrada.getIdIntercambioRegistral()));
        }
        intercambioRegistralEntrada.setId(id);
        this.getBandejaEntradaIntercambioRegistralDAO().save(intercambioRegistralEntrada);
    }

    @Override
    public BandejaEntradaItemVO completarBandejaEntradaItem(BandejaEntradaItemVO bandejaEntradaItemVO) {
        return this.getBandejaEntradaIntercambioRegistralDAO().completarBandejaEntradaItem(bandejaEntradaItemVO);
    }

    @Override
    public void rechazarIntercambioRegistralEntradaById(String idIntercambioInterno, String tipoRechazo, String observaciones) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Se va a rechazar el intercambio registral de entrada con id de intercambio = " + idIntercambioInterno));
        }
        UsuarioVO usuario = IsicresContextHolder.getContextoAplicacion().getUsuarioActual();
        Integer idOficina = Integer.parseInt(usuario.getConfiguracionUsuario().getOficina().getId());
        String idEntidad = usuario.getConfiguracionUsuario().getIdEntidad();
        AsientoRegistralVO asientoRegistral = this.getIntercambioRegistralSIRManager().getAsientoRegistral(idIntercambioInterno);
        if (asientoRegistral.getCodigoEntidadRegistralInicio().equalsIgnoreCase(asientoRegistral.getCodigoEntidadRegistralDestino())) {
            throw new IntercambioRegistralException("No se puede rechazar intercambio que tu mismo eres origen", IntercambioRegistralExceptionCodes.ERROR_CODE_RECHAZO_ACEPTACION_ORIGEN);
        }
        IntercambioRegistralEntradaVO intercambioRegistralEntradaVO = this.populateIntercambioRegistralEntrada(usuario, asientoRegistral, idOficina, idIntercambioInterno, EstadoIntercambioRegistralEntradaEnumVO.RECHAZADO, observaciones);
        this.getIntercambioRegistralSIRManager().rechazarAsientoRegistral(idIntercambioInterno, TipoRechazoEnum.getTipoRechazo((int)Integer.valueOf(tipoRechazo)), observaciones);
        String idIntercambioRegistralSir = asientoRegistral.getIdentificadorIntercambio();
        List<IntercambioRegistralEntradaVO> intercambiosPrevios = this.getBandejaEntradaIntercambioRegistralDAO().getIntercambioRegistralEntradaByIdIntercambioRegistralSir(idOficina, idIntercambioRegistralSir);
        for (IntercambioRegistralEntradaVO intercambioRegistralEntrada : intercambiosPrevios) {
            this.getBandejaEntradaIntercambioRegistralDAO().delete(intercambioRegistralEntrada);
        }
        this.guardarIntercambioRegistralEntrada(intercambioRegistralEntradaVO);
    }

    @Override
    public void reenviarIntercambioRegistralEntradaById(String idIntercambioRegistralEntrada, UnidadTramitacionIntercambioRegistralVO nuevoDestino, String observaciones) {
        this.validateRelacionEntidadRegistralUnidadTramitacion(nuevoDestino);
        UsuarioVO usuario = IsicresContextHolder.getContextoAplicacion().getUsuarioActual();
        Integer idOficina = Integer.parseInt(usuario.getConfiguracionUsuario().getOficina().getId());
        String idEntidad = usuario.getConfiguracionUsuario().getIdEntidad();
        AsientoRegistralVO asientoRegistral = this.getIntercambioRegistralSIRManager().getAsientoRegistral(idIntercambioRegistralEntrada);
        IntercambioRegistralEntradaVO intercambioRegistralEntradaVO = this.populateIntercambioRegistralEntrada(usuario, asientoRegistral, idOficina, idIntercambioRegistralEntrada, EstadoIntercambioRegistralEntradaEnumVO.REENVIADO, observaciones);
        this.guardarIntercambioRegistralEntrada(intercambioRegistralEntradaVO);
        this.getIntercambioRegistralSIRManager().reenviarAsientoRegistral(idIntercambioRegistralEntrada, null, null, observaciones, nuevoDestino);
        String idIntercambioRegistralSir = asientoRegistral.getIdentificadorIntercambio();
        List<IntercambioRegistralSalidaVO> intercambioRegistralSalidas = this.getBandejaSalidaIntercambioRegistralDAO().findByIdIntercambioRegistralSirYOficina(idIntercambioRegistralSir, idOficina);
        if (intercambioRegistralSalidas != null) {
            for (IntercambioRegistralSalidaVO intercambioRegistralSalidaItem : intercambioRegistralSalidas) {
                EstadoIntercambioRegistralSalidaVO estadoEnviado = new EstadoIntercambioRegistralSalidaVO();
                estadoEnviado.setEstado(EstadoIntercambioRegistralSalidaEnumVO.ENVIADO);
                estadoEnviado.setFechaEstado(new Date());
                estadoEnviado.setIdExReg(intercambioRegistralSalidaItem.getId());
                estadoEnviado.setUserName(usuario.getLoginName());
                this.getBandejaSalidaIntercambioRegistralDAO().updateEstado(intercambioRegistralSalidaItem, estadoEnviado);
                intercambioRegistralSalidaItem.setCodeEntity(nuevoDestino.getCodeEntity());
                intercambioRegistralSalidaItem.setNameEntity(nuevoDestino.getNameEntity());
                intercambioRegistralSalidaItem.setCodeTramunit(nuevoDestino.getCodeTramunit());
                intercambioRegistralSalidaItem.setNameTramunit(nuevoDestino.getNameTramunit());
                this.getBandejaSalidaIntercambioRegistralDAO().updateIntercambioRegistralSalidaVO(intercambioRegistralSalidaItem);
            }
        }
    }

    private void validateRelacionEntidadRegistralUnidadTramitacion(UnidadTramitacionIntercambioRegistralVO nuevoDestino) {
        if (IntercambioRegistralConfiguration.getInstance().getActiveValidationRelationEntidadUnidad() && StringUtils.isNotBlank((String)nuevoDestino.getCodeTramunit()) && !this.getConfiguracionIntercambioRegistralManager().existRelacionUnidOrgaOficina(nuevoDestino.getCodeEntity(), nuevoDestino.getCodeTramunit())) {
            throw new IntercambioRegistralException("La unid. de tramitaci\u00f3n indicada no se corresponde con la entidad registral", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_UNID_TRAMITA_ENTIDAD_REG);
        }
    }

    @Override
    public List<IntercambioRegistralEntradaVO> getHistorialIntercambioRegistralEntrada(String idLibro, String idRegistro, String idOficina) {
        IntercambioRegistralEntradaVO intercambioRegistralEntrada = new IntercambioRegistralEntradaVO();
        intercambioRegistralEntrada.setIdLibro(Long.parseLong(idLibro));
        intercambioRegistralEntrada.setIdRegistro(Long.parseLong(idRegistro));
        if (idOficina != null) {
            intercambioRegistralEntrada.setIdOfic(Integer.parseInt(idOficina));
        }
        List<IntercambioRegistralEntradaVO> infoEstados = this.getBandejaEntradaIntercambioRegistralDAO().getInfoEstado(intercambioRegistralEntrada);
        return infoEstados;
    }

    @Override
    public void aceptarIntercambioRegistralEntradaById(String idIntercambioInterno, String idLibro, String user, Integer idOficina, String codOficina, boolean llegoDocFisica) {
        RegistroManager registroManager = IsicresManagerProvider.getInstance().getRegistroManager();
        UsuarioVO usuario = IsicresContextHolder.getContextoAplicacion().getUsuarioActual();
        AsientoRegistralVO asientoRegistral = this.getIntercambioRegistralSIRManager().getAsientoRegistral(idIntercambioInterno);
        String idIntercambioRegistralSir = asientoRegistral.getIdentificadorIntercambio();
        if (asientoRegistral.getCodigoEntidadRegistralInicio().equalsIgnoreCase(asientoRegistral.getCodigoEntidadRegistralDestino())) {
            List<IntercambioRegistralSalidaVO> intercambioRegistralSalidas = this.getBandejaSalidaIntercambioRegistralDAO().findByIdIntercambioRegistralSirYOficina(idIntercambioRegistralSir, idOficina);
            if (intercambioRegistralSalidas != null) {
                for (IntercambioRegistralSalidaVO intercambioRegistralSalidaItem : intercambioRegistralSalidas) {
                    EstadoIntercambioRegistralSalidaVO estadoEnviado = new EstadoIntercambioRegistralSalidaVO();
                    estadoEnviado.setEstado(EstadoIntercambioRegistralSalidaEnumVO.ACEPTADO);
                    estadoEnviado.setFechaEstado(new Date());
                    estadoEnviado.setIdExReg(intercambioRegistralSalidaItem.getId());
                    estadoEnviado.setUserName(usuario.getLoginName());
                    this.getBandejaSalidaIntercambioRegistralDAO().updateEstado(intercambioRegistralSalidaItem, estadoEnviado);
                    intercambioRegistralSalidaItem.setCodeEntity(asientoRegistral.getCodigoEntidadRegistralDestino());
                    intercambioRegistralSalidaItem.setNameEntity(asientoRegistral.getDescripcionEntidadRegistralDestino());
                    intercambioRegistralSalidaItem.setCodeTramunit(asientoRegistral.getCodigoUnidadTramitacionDestino());
                    intercambioRegistralSalidaItem.setNameTramunit(asientoRegistral.getDescripcionUnidadTramitacionDestino());
                    this.getBandejaSalidaIntercambioRegistralDAO().updateIntercambioRegistralSalidaVO(intercambioRegistralSalidaItem);
                }
            }
            this.getIntercambioRegistralSIRManager().validarAsientoRegistral(asientoRegistral.getId(), asientoRegistral.getNumeroRegistro(), asientoRegistral.getFechaRegistro());
        } else {
            BaseRegistroVO registro = this.completarRegistroVO(asientoRegistral, usuario, idLibro);
            registro = registroManager.createRegistroEntrada(usuario, (RegistroEntradaVO)registro);
            registroManager.attachDocuments(registro.getId(), this.getAnexos(asientoRegistral, registro), usuario);
            IntercambioRegistralEntradaVO intercambioRegistralEntradaVO = this.populateIntercambioRegistralEntrada(usuario, asientoRegistral, idOficina, Long.parseLong(idLibro), Long.parseLong(registro.getIdRegistro()), idIntercambioInterno, EstadoIntercambioRegistralEntradaEnumVO.ACEPTADO, null);
            this.getIntercambioRegistralSIRManager().validarAsientoRegistral(asientoRegistral.getId(), registro.getNumeroRegistro(), registro.getFechaRegistro());
            List<IntercambioRegistralEntradaVO> intercambiosPrevios = this.getBandejaEntradaIntercambioRegistralDAO().getIntercambioRegistralEntradaByIdIntercambioRegistralSir(idOficina, idIntercambioRegistralSir);
            for (IntercambioRegistralEntradaVO intercambioRegistralEntrada : intercambiosPrevios) {
                this.getBandejaEntradaIntercambioRegistralDAO().delete(intercambioRegistralEntrada);
            }
            this.guardarIntercambioRegistralEntrada(intercambioRegistralEntradaVO);
        }
    }

    @Override
    public IntercambioRegistralEntradaVO getIntercambioRegistralEntradaByRegistro(Integer idLibro, Integer idRegistro, Integer estado) {
        return this.getBandejaEntradaIntercambioRegistralDAO().getIntercambioRegistralEntradaByRegistro(idLibro, idRegistro, estado);
    }

    protected IntercambioRegistralEntradaVO populateIntercambioRegistralEntrada(UsuarioVO usuario, AsientoRegistralVO asientoRegistral, Integer idOficina, String idIntercambioInterno, EstadoIntercambioRegistralEntradaEnumVO estado, String observaciones) {
        IntercambioRegistralEntradaVO result = null;
        result = this.populateIntercambioRegistralEntrada(usuario, asientoRegistral, idOficina, null, null, idIntercambioInterno, estado, observaciones);
        return result;
    }

    protected IntercambioRegistralEntradaVO populateIntercambioRegistralEntrada(UsuarioVO usuario, AsientoRegistralVO asientoRegistral, Integer idOficina, Long idLibro, Long idRegistro, String idIntercambioInterno, EstadoIntercambioRegistralEntradaEnumVO estado, String observaciones) {
        IntercambioRegistralEntradaVO result = new IntercambioRegistralEntradaVO();
        result.setIdRegistro(idRegistro);
        result.setIdLibro(idLibro);
        result.setIdOfic(idOficina);
        result.setEstado(estado);
        result.setFechaEstado(Calendar.getInstance().getTime());
        result.setIdIntercambioRegistral(asientoRegistral.getIdentificadorIntercambio());
        result.setIdIntercambioInterno(idIntercambioInterno);
        result.setTipoOrigen(Integer.parseInt(asientoRegistral.getTipoRegistro().getValue()));
        result.setFechaIntercambio(asientoRegistral.getFechaEstado());
        result.setUsername(usuario.getLoginName());
        result.setCodeEntity(asientoRegistral.getCodigoEntidadRegistralInicio());
        if (StringUtils.isEmpty((String)asientoRegistral.getDescripcionEntidadRegistralInicio()) && StringUtils.isNotEmpty((String)asientoRegistral.getCodigoEntidadRegistralInicio())) {
            result.setNameEntity(asientoRegistral.getCodigoEntidadRegistralInicio());
            UnidadAdministrativaIntercambioRegistralVO unidadOrigen = null;
            List<UnidadAdministrativaIntercambioRegistralVO> listaUnidadOrigen = this.getConfiguracionIntercambioRegistralManager().getUnidadAdmimistrativaByCodigoEntidadRegistral(asientoRegistral.getCodigoEntidadRegistralInicio());
            if (CollectionUtils.isNotEmpty(listaUnidadOrigen) && (unidadOrigen = listaUnidadOrigen.get(0)) != null) {
                result.setNameEntity(unidadOrigen.getName());
            }
        } else {
            result.setNameEntity(asientoRegistral.getDescripcionEntidadRegistralInicio());
        }
        result.setCodeTramunit(asientoRegistral.getCodigoUnidadTramitacionOrigen());
        if (StringUtils.isEmpty((String)asientoRegistral.getDescripcionUnidadTramitacionOrigen()) && StringUtils.isNotEmpty((String)asientoRegistral.getCodigoUnidadTramitacionOrigen())) {
            UnidadAdministrativaIntercambioRegistralVO unidadAdministrativaOrigen = this.configuracionIntercambioRegistralManager.getUnidadAdmimistrativaByCodigoEntidadRegistralYUnidadTramitacion(asientoRegistral.getCodigoUnidadTramitacionOrigen(), asientoRegistral.getCodigoEntidadRegistralInicio());
            if (unidadAdministrativaOrigen != null && StringUtils.isNotEmpty((String)unidadAdministrativaOrigen.getName())) {
                result.setNameTramunit(unidadAdministrativaOrigen.getName());
            } else {
                result.setNameTramunit(asientoRegistral.getCodigoUnidadTramitacionOrigen());
            }
        } else {
            result.setNameTramunit(asientoRegistral.getDescripcionUnidadTramitacionOrigen());
        }
        result.setNumeroRegistroOrigen(asientoRegistral.getNumeroRegistroInicial());
        result.setContactoOrigen(asientoRegistral.getContactoUsuario());
        result.setComentarios(observaciones);
        return result;
    }

    private AsientoRegistralVO getAsientoRegistral(String idIntercambioRegistral, Integer idOficina) {
        AsientoRegistralVO asiento = null;
        EntidadRegistralVO entidadRegistralDestino = this.getConfiguracionIntercambioRegistralManager().getEntidadRegistralVOByIdScrOfic(String.valueOf(idOficina));
        CriteriosVO criterios = new CriteriosVO();
        CriterioVO criterioIdentificador = new CriterioVO(CriterioEnum.ASIENTO_IDENTIFICADOR_INTERCAMBIO, (Object)idIntercambioRegistral);
        CriterioVO criterioEntidadDestino = new CriterioVO(CriterioEnum.ASIENTO_CODIGO_ENTIDAD_REGISTRAL, (Object)entidadRegistralDestino.getCode());
        criterios.addCriterioVO(criterioIdentificador);
        criterios.addCriterioVO(criterioEntidadDestino);
        List<AsientoRegistralVO> asientoRegistral = this.getIntercambioRegistralSIRManager().findAsientosRegistrales(criterios);
        if (asientoRegistral != null && asientoRegistral.size() == 1) {
            asiento = asientoRegistral.get(0);
        }
        return asiento;
    }

    private BaseRegistroVO completarRegistroVO(AsientoRegistralVO asientoRegistral, UsuarioVO usuario, String idLibro) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"Se va a completar la informaci\u00f3n del intercambio para aceptarla");
        }
        RegistroEntradaVO registro = new RegistroEntradaVO();
        this.mapearCamposAdicionales(registro, asientoRegistral);
        this.mapearTerceros(registro, asientoRegistral);
        registro.setComentario(asientoRegistral.getObservacionesApunte());
        this.mapearCamposEnComentarios(registro, asientoRegistral);
        registro.setUsuarioRegistro(usuario);
        registro.setOficinaRegistro(usuario.getConfiguracionUsuario().getOficina());
        registro.setIdLibro(idLibro);
        registro.setResumen(asientoRegistral.getResumen());
        registro.setReferenciaExpediente(asientoRegistral.getNumeroExpediente());
        RegistroOriginalVO registroOriginal = new RegistroOriginalVO();
        registroOriginal.setNumeroRegistroOriginal(asientoRegistral.getNumeroRegistro());
        registroOriginal.setFechaRegistroOriginal(asientoRegistral.getFechaRegistro());
        registroOriginal.setEntidadRegistral(registro.getUnidadAdministrativaOrigen());
        if (asientoRegistral.getTipoRegistro() == TipoRegistroEnum.ENTRADA) {
            registroOriginal.setTipoRegistroOriginal(Integer.toString(1));
        } else {
            registroOriginal.setTipoRegistroOriginal(Integer.toString(2));
        }
        this.mapearTransporte(asientoRegistral, registro);
        this.mapearUnidadesAdministrativas(registro, asientoRegistral);
        this.mapearComentariosAnexos(asientoRegistral, registro);
        registro.setRegistroOriginal(registroOriginal);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Se completo la recuperacion de informacion del intercambio con identificador = " + asientoRegistral.getIdentificadorIntercambio()));
        }
        return registro;
    }

    private void mapearComentariosAnexos(AsientoRegistralVO asientoRegistral, RegistroEntradaVO registro) {
        List<AnexoVO> listaAnexos = asientoRegistral.getAnexos();
        if (CollectionUtils.isNotEmpty((Collection)listaAnexos)) {
            StringBuffer comentariosAnexos = new StringBuffer();
            comentariosAnexos.append("\n");
            comentariosAnexos.append("Ficheros involucrados en el intercambio: \n");
            for (AnexoVO anexoVO : listaAnexos) {
                comentariosAnexos.append(anexoVO.getNombreFichero());
                if (StringUtils.isNotBlank((String)anexoVO.getObservaciones())) {
                    comentariosAnexos.append(" ");
                    comentariosAnexos.append("(");
                    comentariosAnexos.append(anexoVO.getObservaciones());
                    comentariosAnexos.append(")");
                }
                comentariosAnexos.append("\n");
            }
            registro.setComentario(registro.getComentario() + comentariosAnexos.toString());
        }
    }

    private void mapearTransporte(AsientoRegistralVO asientoRegistral, RegistroEntradaVO registro) {
        if (asientoRegistral.getTipoTransporte() != null || asientoRegistral.getNumeroTransporte() != null) {
            TipoTransporteIntercambioRegistralVO tipoTransporteIR;
            TransporteVO transporte = new TransporteVO();
            transporte.setNumeroTransporte(asientoRegistral.getNumeroTransporte());
            if (asientoRegistral.getTipoTransporte() != null && (tipoTransporteIR = this.getTipoTransporteIntercambioRegistral(asientoRegistral)) != null) {
                transporte.setId(String.valueOf(tipoTransporteIR.getIdTipoTransporte()));
                TipoTransporteVO tipoTransporte = new TipoTransporteVO();
                tipoTransporte.setId(String.valueOf(tipoTransporteIR.getIdTipoTransporte()));
                tipoTransporte.setDescripcion(tipoTransporteIR.getDescripcion());
                transporte.setTipoTransporte(tipoTransporte);
            }
            registro.setTransporte(transporte);
        }
    }

    private TipoTransporteIntercambioRegistralVO getTipoTransporteIntercambioRegistral(AsientoRegistralVO asientoRegistral) {
        String codigoSir = asientoRegistral.getTipoTransporte().getValue();
        String idTipoTransporteByDefault = IntercambioRegistralConfiguration.getInstance().getProperty("tipo.transporte.por.defecto." + codigoSir);
        int countTipoTransporte = this.tipoTransporteIntercambioRegistralManager.getCountTipoTransporteByCodigo(codigoSir);
        TipoTransporteIntercambioRegistralVO tipoTransporteIR = null;
        tipoTransporteIR = countTipoTransporte > 1 && StringUtils.isNotBlank((String)idTipoTransporteByDefault) ? this.tipoTransporteIntercambioRegistralManager.getTipoTransporteByCodigoAndIDScrTT(codigoSir, new Integer(idTipoTransporteByDefault)) : this.tipoTransporteIntercambioRegistralManager.getTipoTransporteByCodigo(codigoSir);
        return tipoTransporteIR;
    }

    private void mapearCamposAdicionales(RegistroEntradaVO registro, AsientoRegistralVO asientoRegistral) {
        CampoAdicionalRegistroVO campoExpone = new CampoAdicionalRegistroVO();
        campoExpone.setName(String.valueOf(501));
        campoExpone.setValue(asientoRegistral.getExpone());
        registro.getCamposAdicionales().add(campoExpone);
        CampoAdicionalRegistroVO campoSolicita = new CampoAdicionalRegistroVO();
        campoSolicita.setName(String.valueOf(502));
        campoSolicita.setValue(asientoRegistral.getSolicita());
        registro.getCamposAdicionales().add(campoSolicita);
        CampoAdicionalRegistroVO campoAdicionalIsIntercambioRegistral = new CampoAdicionalRegistroVO();
        campoAdicionalIsIntercambioRegistral.setName(String.valueOf(503));
        campoAdicionalIsIntercambioRegistral.setValue(ConstantKeys.REG_IS_INTERCAMBIO_REGISTRAL);
        registro.getCamposAdicionales().add(campoAdicionalIsIntercambioRegistral);
        if (DocumentacionFisicaEnum.DOCUMENTACION_FISICA_REQUERIDA == asientoRegistral.getDocumentacionFisica()) {
            CampoAdicionalRegistroVO campoDocFisicaRequerida = new CampoAdicionalRegistroVO();
            campoDocFisicaRequerida.setName(String.valueOf(504));
            campoDocFisicaRequerida.setValue("1");
            registro.getCamposAdicionales().add(campoDocFisicaRequerida);
        } else if (DocumentacionFisicaEnum.DOCUMENTACION_FISICA_COMPLEMENTARIA == asientoRegistral.getDocumentacionFisica()) {
            CampoAdicionalRegistroVO campoDocFisicaCompl = new CampoAdicionalRegistroVO();
            campoDocFisicaCompl.setName(String.valueOf(505));
            campoDocFisicaCompl.setValue("1");
            registro.getCamposAdicionales().add(campoDocFisicaCompl);
        } else {
            CampoAdicionalRegistroVO noDocFisica = new CampoAdicionalRegistroVO();
            noDocFisica.setName(String.valueOf(506));
            noDocFisica.setValue("1");
            registro.getCamposAdicionales().add(noDocFisica);
        }
    }

    private RegistroEntradaVO mapearCamposEnComentarios(RegistroEntradaVO registro, AsientoRegistralVO asientoRegistral) {
        StringBuffer comentario = new StringBuffer("");
        if (StringUtils.isNotEmpty((String)registro.getComentario())) {
            comentario.append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getIdentificadorIntercambio())) {
            comentario.append("Ident. Intercambio: ").append(asientoRegistral.getIdentificadorIntercambio()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getAplicacion())) {
            comentario.append("Aplicaci\u00f3n: ").append(asientoRegistral.getAplicacion()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getNumeroRegistroInicial())) {
            comentario.append("Num. Registro Inicial: ").append(asientoRegistral.getNumeroRegistroInicial()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getReferenciaExterna())) {
            comentario.append("Referencia Externa: ").append(asientoRegistral.getReferenciaExterna()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getCodigoAsunto())) {
            comentario.append("Cod. Asunto: ").append(asientoRegistral.getCodigoAsunto()).append("\n");
        }
        if (asientoRegistral.getTipoTransporte() != null) {
            comentario.append("Tipo de Transporte: ").append(asientoRegistral.getTipoTransporte().getName()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getNumeroTransporte())) {
            comentario.append("Num. Transporte:").append(asientoRegistral.getNumeroTransporte()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getCodigoEntidadRegistralOrigen())) {
            String descripcionEntidad = asientoRegistral.getDescripcionEntidadRegistralOrigen();
            if (StringUtils.isEmpty((String)descripcionEntidad)) {
                descripcionEntidad = asientoRegistral.getCodigoEntidadRegistralOrigen();
            }
            comentario.append("Entidad Origen del Intercambio: ").append(asientoRegistral.getCodigoEntidadRegistralOrigen()).append(" - ").append(descripcionEntidad).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getCodigoUnidadTramitacionOrigen())) {
            String descripcionUnidad = asientoRegistral.getDescripcionUnidadTramitacionOrigen();
            if (StringUtils.isEmpty((String)descripcionUnidad)) {
                descripcionUnidad = asientoRegistral.getCodigoUnidadTramitacionOrigen();
            }
            comentario.append("Unidad de Tramitaic\u00f3n Origen del Intercambio: ").append(asientoRegistral.getCodigoUnidadTramitacionOrigen()).append(" - ").append(descripcionUnidad).append("\n");
        }
        if (StringUtils.isNotEmpty((String)asientoRegistral.getCodigoEntidadRegistralInicio())) {
            String descripcionEntidadInicial = asientoRegistral.getDescripcionEntidadRegistralInicio();
            if (StringUtils.isEmpty((String)descripcionEntidadInicial)) {
                descripcionEntidadInicial = asientoRegistral.getCodigoEntidadRegistralInicio();
            }
            comentario.append("Entidad Inicial del Intercambio: ").append(asientoRegistral.getCodigoEntidadRegistralInicio()).append(" - ").append(descripcionEntidadInicial).append("\n");
        }
        comentario = this.mapearRepresentantes(comentario, asientoRegistral);
        String comentarioResult = registro.getComentario();
        if (comentarioResult == null) {
            comentarioResult = "";
        }
        comentarioResult = comentarioResult + comentario.toString();
        registro.setComentario(comentarioResult);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Mapeados campos en el siguiente comentario: " + comentario));
        }
        return registro;
    }

    private StringBuffer mapearRepresentantes(StringBuffer comentario, AsientoRegistralVO asientoRegistral) {
        for (InteresadoVO interesado : asientoRegistral.getInteresados()) {
            if (!StringUtils.isNotEmpty((String)interesado.getNombreRepresentante()) && !StringUtils.isNotEmpty((String)interesado.getRazonSocialRepresentante())) continue;
            comentario.append("El representante de ").append(this.getNombreInteresadoString(interesado)).append(" es ").append(this.getNombreRepresentanteString(interesado)).append("\n");
        }
        return comentario;
    }

    private RegistroEntradaVO mapearTerceros(RegistroEntradaVO registro, AsientoRegistralVO asientoRegistral) {
        ArrayList<es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO> listaTerceros = new ArrayList<es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO>();
        List listaInteresados = asientoRegistral.getInteresados();
        String terceroString = null;
        String representanteString = null;
        boolean interesadoPrincipal = true;
        for (int i = 0; i < listaInteresados.size(); ++i) {
            InteresadoVO interesado = (InteresadoVO)listaInteresados.get(i);
            InteresadoExReg interesadoExReg = new InteresadoExReg();
            BeanUtils.copyProperties((Object)interesado, (Object)interesadoExReg);
            this.completarDatosInteresado(interesado, interesadoExReg);
            this.completarDatosRepresentante(interesado, interesadoExReg);
            terceroString = this.getInteresadoString(interesadoExReg);
            representanteString = this.getRepresentanteString(interesadoExReg);
            interesadoPrincipal = this.setDatosInteresadoORepresentante(registro, listaTerceros, terceroString, interesadoPrincipal, interesadoExReg, false);
            this.setDatosInteresadoORepresentante(registro, listaTerceros, representanteString, false, interesadoExReg, true);
        }
        registro.setInteresados(listaTerceros);
        return registro;
    }

    private boolean setDatosInteresadoORepresentante(RegistroEntradaVO registro, List<es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO> listaTerceros, String cadenaDatosPersona, boolean interesadoPrincipal, InteresadoExReg interesadoExReg, boolean isRepresentante) {
        if (cadenaDatosPersona.length() > 95) {
            List<String> datosPersona = this.getDatosInteresadoOrRepresentante(interesadoExReg, isRepresentante);
            for (String cadenaInteresado : datosPersona) {
                interesadoPrincipal = this.addInteresado(registro, listaTerceros, cadenaInteresado, interesadoPrincipal);
            }
        } else {
            interesadoPrincipal = this.addInteresado(registro, listaTerceros, cadenaDatosPersona, interesadoPrincipal);
        }
        return interesadoPrincipal;
    }

    private List<String> getDatosInteresadoOrRepresentante(InteresadoExReg interesadoExReg, boolean isRepresentante) {
        List<String> datosPersona = null;
        datosPersona = isRepresentante ? InteresadoUtils.getDatosRepresentanteArray(interesadoExReg) : InteresadoUtils.getDatosInteresadoArray(interesadoExReg);
        return datosPersona;
    }

    private void completarDatosRepresentante(InteresadoVO interesado, InteresadoExReg interesadoExReg) {
        PaisExReg pais;
        if (StringUtils.isNotEmpty((String)interesado.getCodigoProvinciaRepresentante())) {
            CiudadExReg municipio;
            ProvinciaExReg provincia = this.direccionesIntercambioRegistralRegManager.getProvinciaExRegByCodigo(interesado.getCodigoProvinciaRepresentante());
            if (provincia != null) {
                interesadoExReg.setNombreProvinciaRepresentante(provincia.getNombre());
            }
            if (StringUtils.isNotEmpty((String)interesado.getCodigoMunicipioRepresentante()) && (municipio = this.direccionesIntercambioRegistralRegManager.getCiudadExRegByCodigo(interesado.getCodigoProvinciaRepresentante(), interesado.getCodigoMunicipioRepresentante())) != null) {
                interesadoExReg.setNombreMunicipioRepresentante(municipio.getNombre());
            }
        }
        if (StringUtils.isNotEmpty((String)interesado.getCodigoPaisRepresentante()) && (pais = this.direccionesIntercambioRegistralRegManager.getPaisExRegByCodigo(interesado.getCodigoPaisRepresentante())) != null) {
            interesadoExReg.setNombrePaisRepresentante(pais.getNombre());
        }
    }

    private void completarDatosInteresado(InteresadoVO interesado, InteresadoExReg interesadoExReg) {
        PaisExReg pais;
        if (StringUtils.isNotEmpty((String)interesado.getCodigoProvinciaInteresado())) {
            CiudadExReg municipio;
            ProvinciaExReg provincia = this.direccionesIntercambioRegistralRegManager.getProvinciaExRegByCodigo(interesado.getCodigoProvinciaInteresado());
            if (provincia != null) {
                interesadoExReg.setNombreProvinciaInteresado(provincia.getNombre());
            }
            if (StringUtils.isNotEmpty((String)interesado.getCodigoMunicipioInteresado()) && (municipio = this.direccionesIntercambioRegistralRegManager.getCiudadExRegByCodigo(interesado.getCodigoProvinciaInteresado(), interesado.getCodigoMunicipioInteresado())) != null) {
                interesadoExReg.setNombreMunicipioInteresado(municipio.getNombre());
            }
        }
        if (StringUtils.isNotEmpty((String)interesado.getCodigoPaisInteresado()) && (pais = this.direccionesIntercambioRegistralRegManager.getPaisExRegByCodigo(interesado.getCodigoPaisInteresado())) != null) {
            interesadoExReg.setNombrePaisInteresado(pais.getNombre());
        }
    }

    private boolean addInteresado(RegistroEntradaVO registro, List<es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO> listaTerceros, String terceroString, boolean interesadoPrincipal) {
        es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO tercero = new es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO();
        if (!StringUtils.isEmpty((String)terceroString)) {
            tercero.setPrincipal(interesadoPrincipal);
            tercero.setNombre(terceroString);
            if (interesadoPrincipal) {
                registro.setInteresadoPrincipal(tercero);
                interesadoPrincipal = false;
            } else {
                listaTerceros.add(tercero);
            }
        }
        return interesadoPrincipal;
    }

    private void mapearUnidadesAdministrativas(RegistroEntradaVO registro, AsientoRegistralVO asientoRegistral) {
        String comentarioResult;
        String codigoEntidadRegistralInicio = asientoRegistral.getCodigoEntidadRegistralInicio();
        String codigoEntidadRegistralDestino = asientoRegistral.getCodigoEntidadRegistralDestino();
        String codigoUnidadTramitacionDestino = asientoRegistral.getCodigoUnidadTramitacionDestino();
        if (StringUtils.isNotEmpty((String)codigoEntidadRegistralInicio)) {
            logger.debug((Object)("Buscando el codigo de la entidad registral de inicio'" + codigoEntidadRegistralInicio + "'"));
            UnidadAdministrativaIntercambioRegistralVO unidadOrigen = null;
            List<UnidadAdministrativaIntercambioRegistralVO> listaUnidadOrigen = this.getConfiguracionIntercambioRegistralManager().getUnidadAdmimistrativaByCodigoEntidadRegistral(codigoEntidadRegistralInicio);
            if (CollectionUtils.isNotEmpty(listaUnidadOrigen) && (unidadOrigen = listaUnidadOrigen.get(0)) != null) {
                UnidadAdministrativaVO unidadAdministrativaOrigen = new UnidadAdministrativaVO();
                unidadAdministrativaOrigen.setCodigoUnidad(unidadOrigen.getCode());
                registro.setUnidadAdministrativaOrigen(unidadAdministrativaOrigen);
            }
        }
        if (StringUtils.isNotEmpty((String)codigoEntidadRegistralDestino)) {
            List<UnidadAdministrativaIntercambioRegistralVO> listaUnidadDestino;
            logger.debug((Object)("Buscando el codigo de la entidad registral de destino'" + codigoEntidadRegistralDestino + "'"));
            logger.debug((Object)("Buscando el codigo de la unidad de tramitacion de destino'" + codigoUnidadTramitacionDestino + "'"));
            UnidadAdministrativaIntercambioRegistralVO unidadDestino = this.getConfiguracionIntercambioRegistralManager().getUnidadAdmimistrativaByCodigoEntidadRegistralYUnidadTramitacion(codigoUnidadTramitacionDestino, codigoEntidadRegistralDestino);
            if (unidadDestino == null && CollectionUtils.isNotEmpty(listaUnidadDestino = this.getConfiguracionIntercambioRegistralManager().getUnidadAdmimistrativaByCodigoEntidadRegistral(codigoEntidadRegistralDestino))) {
                unidadDestino = listaUnidadDestino.get(0);
            }
            if (unidadDestino != null) {
                UnidadAdministrativaVO unidadAdministrativaDestino = new UnidadAdministrativaVO();
                unidadAdministrativaDestino.setCodigoUnidad(unidadDestino.getCode());
                registro.setUnidadAdministrativaDestino(unidadAdministrativaDestino);
            }
        }
        String codigoOrigenEntidadRegistral = asientoRegistral.getCodigoEntidadRegistralOrigen();
        String codigoOrigenUnidadTram = asientoRegistral.getCodigoUnidadTramitacionOrigen();
        StringBuffer comentario = new StringBuffer("");
        comentario.append("Este registro ha sido creado a partir de un intercambio registral con el siguiente origen y destino:");
        comentario.append("\n");
        if (StringUtils.isNotEmpty((String)codigoOrigenEntidadRegistral)) {
            comentario.append("Codigo DC Entidad Registral origen:" + codigoOrigenEntidadRegistral);
            comentario.append("\n");
        }
        if (StringUtils.isNotEmpty((String)codigoOrigenUnidadTram)) {
            comentario.append("Codigo DC Unidad Tramitaci\u00f3n origen:" + codigoOrigenUnidadTram);
            comentario.append("\n");
        }
        if (StringUtils.isNotEmpty((String)codigoEntidadRegistralDestino)) {
            comentario.append("Codigo DC Entidad registral destino:" + codigoEntidadRegistralDestino);
            comentario.append("\n");
        }
        if (StringUtils.isNotEmpty((String)codigoUnidadTramitacionDestino)) {
            comentario.append("Codigo DC Unidad Tramitaci\u00f3n destino:" + codigoUnidadTramitacionDestino);
            comentario.append("\n");
        }
        if ((comentarioResult = registro.getComentario()) == null) {
            comentarioResult = "";
        }
        comentarioResult = comentarioResult + comentario.toString();
        registro.setComentario(comentarioResult);
    }

    private List<DocumentoRegistroVO> getAnexos(AsientoRegistralVO asientoRegistral, BaseRegistroVO registro) {
        List anexos = asientoRegistral.getAnexos();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Se van a recupear los anexos del intercambio registral con identificador = " + asientoRegistral.getIdentificadorIntercambio()));
        }
        if (anexos == null || anexos.size() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"El intercambio no tiene anexos");
            }
            return null;
        }
        List<DocumentoRegistroVO> documentoRegistro = this.mapearListAnexoVOaListDocumentoRegistroVO(anexos);
        return documentoRegistro;
    }

    private List<DocumentoRegistroVO> mapearListAnexoVOaListDocumentoRegistroVO(List<AnexoVO> anexos) {
        ArrayList<DocumentoRegistroVO> result = new ArrayList<DocumentoRegistroVO>();
        DocumentoRegistroVO documento = new DocumentoRegistroVO();
        List<PaginaDocumentoRegistroVO> paginas = this.generatePaginasDocumentoRegistro(anexos);
        documento.setName("Doc. Inter.");
        documento.setPaginas(paginas);
        result.add(documento);
        return result;
    }

    private List<PaginaDocumentoRegistroVO> generatePaginasDocumentoRegistro(List<AnexoVO> anexos) {
        int numPagina = 1;
        ArrayList<PaginaDocumentoRegistroVO> paginas = new ArrayList<PaginaDocumentoRegistroVO>();
        for (AnexoVO anexo2 : anexos) {
            PaginaDocumentoRegistroVO pagina = this.createPaginaDocumentoRegistro(anexo2, numPagina);
            paginas.add(pagina);
            ++numPagina;
        }
        return paginas;
    }

    private List<AnexoVO> verificarAnexos(List<AnexoVO> anexosVO) {
        ArrayList<AnexoVO> result = new ArrayList<AnexoVO>();
        HashMap<String, AnexoVO> nombresDeAnexosVO = new HashMap<String, AnexoVO>();
        int count_nombreAnexo_duplicado = 0;
        for (AnexoVO anexoVO : anexosVO) {
            String nombreFichero = this.obtenerNombrePagina(count_nombreAnexo_duplicado, anexoVO, nombresDeAnexosVO);
            nombresDeAnexosVO.put(nombreFichero, anexoVO);
            anexoVO.setNombreFichero(nombreFichero);
            result.add(anexoVO);
        }
        return result;
    }

    private PaginaDocumentoRegistroVO createPaginaDocumentoRegistro(AnexoVO anexoVO, int numPagina) {
        PaginaDocumentoRegistroVO pagina = new PaginaDocumentoRegistroVO();
        byte[] contenido = this.getIntercambioRegistralSIRManager().getContenidoAnexo(anexoVO.getId());
        DocumentoFisicoVO documentoFisico = this.createDocumentoFisicoVO(contenido, anexoVO.getNombreFichero());
        pagina.setDocumentoFisico(documentoFisico);
        pagina.setName(anexoVO.getNombreFichero());
        pagina.setNumeroPagina(numPagina);
        return pagina;
    }

    private DocumentoFisicoVO createDocumentoFisicoVO(byte[] contenido, String nombreFichero) {
        DocumentoFisicoVO documentoFisico = new DocumentoFisicoVO();
        documentoFisico.setName(nombreFichero);
        documentoFisico.setContent(contenido);
        documentoFisico.setExtension(nombreFichero.substring(nombreFichero.lastIndexOf(".") + 1, nombreFichero.length()));
        return documentoFisico;
    }

    private String obtenerNombrePagina(int count_nombreAnexo_duplicado, AnexoVO anexoVO, Map<String, AnexoVO> nombresDeAnexosVO) {
        String result = anexoVO.getNombreFichero();
        String nombreFichero = result.substring(0, result.lastIndexOf("."));
        String extension = result.substring(result.lastIndexOf("."), result.length());
        if (nombresDeAnexosVO.containsKey(result)) {
            String sufijo = "_" + ++count_nombreAnexo_duplicado;
            result = (result + sufijo).length() > 64 ? nombreFichero.substring(0, 64 - (sufijo.length() + extension.length())) + sufijo + extension : nombreFichero + sufijo + extension;
        } else if (result.length() > 64) {
            result = nombreFichero.substring(0, 64 - extension.length()) + extension;
        }
        return result;
    }

    private String getInteresadoString(InteresadoExReg interesado) {
        StringBuffer nombreTercero = new StringBuffer("");
        String nombre = this.getNombreInteresadoString((InteresadoVO)interesado);
        String direccion = InteresadoUtils.getDireccionInteresado(interesado);
        nombreTercero.append(nombre);
        if (StringUtils.isNotBlank((String)direccion)) {
            nombreTercero.append(" - ");
            nombreTercero.append(direccion);
        }
        return nombreTercero.toString();
    }

    private String getNombreInteresadoString(InteresadoVO interesado) {
        StringBuffer nombreTercero = new StringBuffer("");
        if (!StringUtils.isEmpty((String)interesado.getNombreInteresado())) {
            nombreTercero.append(interesado.getNombreInteresado()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getPrimerApellidoInteresado())) {
            nombreTercero.append(interesado.getPrimerApellidoInteresado()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getSegundoApellidoInteresado())) {
            nombreTercero.append(interesado.getSegundoApellidoInteresado()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getRazonSocialInteresado())) {
            nombreTercero.append(interesado.getRazonSocialInteresado());
        }
        if (interesado.getTipoDocumentoIdentificacionInteresado() != null) {
            nombreTercero.append(" - ").append(interesado.getTipoDocumentoIdentificacionInteresado().getValue());
        }
        if (!StringUtils.isEmpty((String)interesado.getDocumentoIdentificacionInteresado())) {
            nombreTercero.append(": ").append(interesado.getDocumentoIdentificacionInteresado());
        }
        return nombreTercero.toString();
    }

    private String getNombreRepresentanteString(InteresadoVO interesado) {
        StringBuffer nombreTercero = new StringBuffer("");
        if (!StringUtils.isEmpty((String)interesado.getNombreRepresentante())) {
            nombreTercero.append(interesado.getNombreRepresentante()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getPrimerApellidoRepresentante())) {
            nombreTercero.append(interesado.getPrimerApellidoRepresentante()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getSegundoApellidoRepresentante())) {
            nombreTercero.append(interesado.getSegundoApellidoRepresentante()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getRazonSocialRepresentante())) {
            nombreTercero.append(interesado.getRazonSocialRepresentante());
        }
        if (interesado.getTipoDocumentoIdentificacionRepresentante() != null) {
            nombreTercero.append(" - ").append(interesado.getTipoDocumentoIdentificacionRepresentante().getValue());
        }
        if (!StringUtils.isEmpty((String)interesado.getDocumentoIdentificacionRepresentante())) {
            nombreTercero.append(": ").append(interesado.getDocumentoIdentificacionRepresentante());
        }
        return nombreTercero.toString();
    }

    private String getRepresentanteString(InteresadoExReg interesado) {
        StringBuffer nombreTercero = new StringBuffer("");
        String nombre = this.getNombreRepresentanteString((InteresadoVO)interesado);
        nombreTercero.append(nombre);
        String direccion = InteresadoUtils.getDireccionRepresentante(interesado);
        if (StringUtils.isNotBlank((String)direccion)) {
            nombreTercero.append(" - ");
            nombreTercero.append(direccion);
        }
        return nombreTercero.toString();
    }

    public ConfiguracionIntercambioRegistralManager getConfiguracionIntercambioRegistralManager() {
        return this.configuracionIntercambioRegistralManager;
    }

    public void setConfiguracionIntercambioRegistralManager(ConfiguracionIntercambioRegistralManager configuracionIntercambioRegistralManager) {
        this.configuracionIntercambioRegistralManager = configuracionIntercambioRegistralManager;
    }

    public BandejaEntradaIntercambioRegistralDAO getBandejaEntradaIntercambioRegistralDAO() {
        return this.bandejaEntradaIntercambioRegistralDAO;
    }

    public void setBandejaEntradaIntercambioRegistralDAO(BandejaEntradaIntercambioRegistralDAO bandejaEntradaIntercambioRegistralDAO) {
        this.bandejaEntradaIntercambioRegistralDAO = bandejaEntradaIntercambioRegistralDAO;
    }

    public BandejaSalidaIntercambioRegistralDAO getBandejaSalidaIntercambioRegistralDAO() {
        return this.bandejaSalidaIntercambioRegistralDAO;
    }

    public void setBandejaSalidaIntercambioRegistralDAO(BandejaSalidaIntercambioRegistralDAO bandejaSalidaIntercambioRegistralDAO) {
        this.bandejaSalidaIntercambioRegistralDAO = bandejaSalidaIntercambioRegistralDAO;
    }

    public DataFieldMaxValueIncrementer getIntercambioRegistralEntradaIncrementer() {
        return this.intercambioRegistralEntradaIncrementer;
    }

    public void setIntercambioRegistralEntradaIncrementer(DataFieldMaxValueIncrementer intercambioRegistralEntradaIncrementer) {
        this.intercambioRegistralEntradaIncrementer = intercambioRegistralEntradaIncrementer;
    }

    public IntercambioRegistralSIRManager getIntercambioRegistralSIRManager() {
        return this.intercambioRegistralSIRManager;
    }

    public void setIntercambioRegistralSIRManager(IntercambioRegistralSIRManager intercambioRegistralSIRManager) {
        this.intercambioRegistralSIRManager = intercambioRegistralSIRManager;
    }

    public TipoTransporteIntercambioRegistralManager getTipoTransporteIntercambioRegistralManager() {
        return this.tipoTransporteIntercambioRegistralManager;
    }

    public void setTipoTransporteIntercambioRegistralManager(TipoTransporteIntercambioRegistralManager tipoTransporteIntercambioRegistralManager) {
        this.tipoTransporteIntercambioRegistralManager = tipoTransporteIntercambioRegistralManager;
    }

    public DireccionesIntercambioRegistralManager getDireccionesIntercambioRegistralRegManager() {
        return this.direccionesIntercambioRegistralRegManager;
    }

    public void setDireccionesIntercambioRegistralRegManager(DireccionesIntercambioRegistralManager direccionesIntercambioRegistralRegManager) {
        this.direccionesIntercambioRegistralRegManager = direccionesIntercambioRegistralRegManager;
    }

    @Override
    public List<BandejaEntradaItemVO> findBandejaEntradaByCriterios(EstadoIntercambioRegistralEntradaEnumVO estado, CriteriosBusquedaIREntradaVO criterios) {
        if (estado != EstadoIntercambioRegistralEntradaEnumVO.PENDIENTE) {
            return this.getBandejaEntradaIntercambioRegistralDAO().findByCriterios(estado, criterios);
        }
        CriteriosVO criteriosSIR = this.criteriosVOMapper.map(criterios);
        if (criteriosSIR == null) {
            criteriosSIR = new CriteriosVO();
        }
        CriterioVO criterioEstado = new CriterioVO();
        criterioEstado.setNombre(CriterioEnum.ASIENTO_ESTADO);
        criterioEstado.setOperador(OperadorCriterioEnum.IN);
        EstadoAsientoRegistralEnum[] estadosPendientesEntrada = new EstadoAsientoRegistralEnum[]{EstadoAsientoRegistralEnum.RECIBIDO, EstadoAsientoRegistralEnum.ENVIADO};
        criterioEstado.setValor((Object)estadosPendientesEntrada);
        criteriosSIR.addCriterioVO(criterioEstado);
        List<AsientoRegistralVO> bandejaEntrada = this.intercambioRegistralSIRManager.findAsientosRegistrales(criteriosSIR);
        ArrayList<BandejaEntradaItemVO> bandejaEntradaItems = new ArrayList<BandejaEntradaItemVO>();
        AsientoRegistralMapper mapper = new AsientoRegistralMapper();
        for (AsientoRegistralVO asientoRegistralVO : bandejaEntrada) {
            BandejaEntradaItemVO bandejaItem = mapper.toBandejaEntradaItemVO(asientoRegistralVO);
            bandejaEntradaItems.add(bandejaItem);
        }
        return bandejaEntradaItems;
    }

    @Override
    public PaginatedArrayList<BandejaEntradaItemVO> findBandejaEntradaByCriterios(EstadoIntercambioRegistralEntradaEnumVO estado, CriteriosBusquedaIREntradaVO criterios, PageInfo pageInfo) {
        if (estado != EstadoIntercambioRegistralEntradaEnumVO.PENDIENTE) {
            return this.getBandejaEntradaIntercambioRegistralDAO().findByCriterios(estado, criterios, pageInfo);
        }
        CriteriosVO criteriosSIR = this.getCriteriosBusquedaAsientosPendientes(criterios);
        criteriosSIR.setPageInfo(pageInfo);
        PaginatedArrayList bandejaEntrada = (PaginatedArrayList)this.intercambioRegistralSIRManager.findAsientosRegistrales(criteriosSIR);
        PaginatedArrayList bandejaEntradaItems = new PaginatedArrayList(bandejaEntrada.getPageInfo());
        AsientoRegistralMapper mapper = new AsientoRegistralMapper();
	for (Iterator it03 = bandejaEntrada.getList().iterator(); it03.hasNext();) {
	    AsientoRegistralVO asientoRegistralVO=(AsientoRegistralVO) it03.next();
            BandejaEntradaItemVO bandejaItem = mapper.toBandejaEntradaItemVO(asientoRegistralVO);
            bandejaEntradaItems.add((Object)bandejaItem);
        }
        return bandejaEntradaItems;
    }

    private CriteriosVO getCriteriosBusquedaAsientosPendientes(CriteriosBusquedaIREntradaVO criterios) {
        CriteriosVO criteriosSIR = this.criteriosVOMapper.map(criterios);
        if (criteriosSIR == null) {
            criteriosSIR = new CriteriosVO();
        }
        CriterioVO criterioEstado = new CriterioVO();
        criterioEstado.setNombre(CriterioEnum.ASIENTO_ESTADO);
        criterioEstado.setOperador(OperadorCriterioEnum.IN);
        EstadoAsientoRegistralEnum[] estadosPendientesEntrada = new EstadoAsientoRegistralEnum[]{EstadoAsientoRegistralEnum.RECIBIDO, EstadoAsientoRegistralEnum.ENVIADO};
        criterioEstado.setValor((Object)estadosPendientesEntrada);
        criteriosSIR.addCriterioVO(criterioEstado);
        return criteriosSIR;
    }

    public CriteriosVOMapper getCriteriosVOMapper() {
        return this.criteriosVOMapper;
    }

    public void setCriteriosVOMapper(CriteriosVOMapper criteriosVOMapper) {
        this.criteriosVOMapper = criteriosVOMapper;
    }
}
