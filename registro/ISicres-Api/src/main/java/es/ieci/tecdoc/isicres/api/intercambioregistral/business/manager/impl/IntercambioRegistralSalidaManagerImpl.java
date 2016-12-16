package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.impl;

import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.fwktd.sir.core.types.IndicadorPruebaEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralFormVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.isicres.api.business.manager.ContextoAplicacionManager;
import es.ieci.tecdoc.isicres.api.business.manager.IsicresManagerProvider;
import es.ieci.tecdoc.isicres.api.business.manager.RegistroManager;
import es.ieci.tecdoc.isicres.api.business.vo.CampoGenericoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.IdentificadorRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.enums.TipoLibroEnum;
import es.ieci.tecdoc.isicres.api.intercambio.registral.business.util.IntercambioRegistralConfiguration;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.BandejaSalidaIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralExceptionCodes;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.ConfiguracionIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralGeneradorObjetosManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralSIRManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralSalidaManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaSalidaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIRSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionIntercambioRegistralVO;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public class IntercambioRegistralSalidaManagerImpl
implements IntercambioRegistralSalidaManager {
    private static Logger logger = Logger.getLogger((Class)IntercambioRegistralSalidaManagerImpl.class);
    protected ConfiguracionIntercambioRegistralManager configuracionIntercambioRegistralManager;
    protected IntercambioRegistralSIRManager intercambioRegistralSIRManager;
    protected RegistroManager registroManager;
    protected BandejaSalidaIntercambioRegistralDAO bandejaSalidaIntercambioRegistralDAO;
    protected IntercambioRegistralGeneradorObjetosManager intercambioRegistralGeneradorObjetosManager;
    protected DataFieldMaxValueIncrementer intercambioRegistralSalidaIncrementer;
    protected DataFieldMaxValueIncrementer intercambioRegistralSalidaEstadoIncrementer;

    @Override
    public boolean isIntercambioRegistral(String idUnidadAdministrativa) {
        UnidadTramitacionIntercambioRegistralVO unidadDestino;
        boolean result = false;
        if (!StringUtils.isEmpty((String)idUnidadAdministrativa) && (unidadDestino = this.getConfiguracionIntercambioRegistralManager().getUnidadTramitacionIntercambioRegistralVOByIdScrOrgs(idUnidadAdministrativa)) != null && StringUtils.isNotEmpty((String)unidadDestino.getCodeEntity())) {
            result = true;
        }
        if (logger.isDebugEnabled()) {
            String resultString = result ? "SI" : "NO";
            logger.debug((Object)("La unidad admnistrativa " + idUnidadAdministrativa + " " + resultString + " tiene configuracion de intercambio registral"));
        }
        return result;
    }

    @Override
    public void toIntercambioRegistral(String idLibro, String idRegistro, String idOfic, String tipoOrigen, String idUnidadTramitacionDestino, String user) {
        if (this.isIntercambioRegistral(idUnidadTramitacionDestino)) {
            IntercambioRegistralSalidaVO intercambioRegistral = new IntercambioRegistralSalidaVO();
            intercambioRegistral.setId(this.getIntercambioRegistralSalidaIncrementer().nextLongValue());
            intercambioRegistral.setIdLibro(Long.valueOf(idLibro));
            intercambioRegistral.setIdOfic(Integer.valueOf(idOfic));
            intercambioRegistral.setIdRegistro(Long.valueOf(idRegistro));
            intercambioRegistral.setEstado(EstadoIntercambioRegistralSalidaEnumVO.PENDIENTE);
            intercambioRegistral.setFechaEstado(Calendar.getInstance().getTime());
            intercambioRegistral.setTipoOrigen(Integer.valueOf(tipoOrigen));
            intercambioRegistral.setUsername(user);
            this.getBandejaSalidaIntercambioRegistralDAO().save(intercambioRegistral);
            this.saveHistorialCambiosEstado(intercambioRegistral);
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Guardado el intercambio registral de salida con id = " + intercambioRegistral.getId()));
            }
        } else {
            logger.info((Object)"No se puede guardar el intercambio registral de salida porque la unidad de tramitacion destino no tiene configuracion de intercambio registral mapeada.");
            throw new IntercambioRegistralException("No se puede guardar el intercambio registral de salida porque la unidad de tramitacion destino no tiene configuracion de intercambio registral mapeada.", IntercambioRegistralExceptionCodes.ERROR_CODE_UNIDAD_TRAMITACION_NO_MAPEADA);
        }
    }

    @Override
    public void toIntercambioRegistralManual(List<String> idRegistros, String idLibro, String idOfic, String tipoOrigen, String user) {
        for (String idRegistro : idRegistros) {
            if (!this.isInBandejasalidaIntercambioRegistral(idRegistro, idLibro)) {
                IntercambioRegistralSalidaVO intercambioRegistral = new IntercambioRegistralSalidaVO();
                intercambioRegistral.setId(this.getIntercambioRegistralSalidaIncrementer().nextLongValue());
                intercambioRegistral.setIdLibro(Long.valueOf(idLibro));
                intercambioRegistral.setIdOfic(Integer.valueOf(idOfic));
                intercambioRegistral.setIdRegistro(Long.valueOf(idRegistro));
                intercambioRegistral.setEstado(EstadoIntercambioRegistralSalidaEnumVO.PENDIENTE);
                intercambioRegistral.setFechaEstado(Calendar.getInstance().getTime());
                intercambioRegistral.setTipoOrigen(Integer.valueOf(tipoOrigen));
                intercambioRegistral.setUsername(user);
                this.getBandejaSalidaIntercambioRegistralDAO().save(intercambioRegistral);
                if (!logger.isDebugEnabled()) continue;
                logger.debug((Object)("Guardado el intercambio registral de salida con id = " + intercambioRegistral.getId()));
                continue;
            }
            logger.info((Object)("El registro del libro = " + idLibro + " con id = " + idRegistro + " YA EST\u00c1 en la bandeja de salida de intercambio registral."));
        }
    }

    private void saveBandejaSalida(IntercambioRegistralSalidaVO intercambioRegistralSalida, IdentificadorRegistroVO identificadorRegistro, TipoLibroEnum tipoLibro) {
        this.getBandejaSalidaIntercambioRegistralDAO().save(intercambioRegistralSalida);
        this.saveHistorialCambiosEstado(intercambioRegistralSalida);
        UsuarioVO usuario = IsicresManagerProvider.getInstance().getContextoAplicacionManager().getUsuarioActual();
        ArrayList<CampoGenericoRegistroVO> camposGenericos = new ArrayList<CampoGenericoRegistroVO>();
        CampoGenericoRegistroVO campoInvolucradoIntercambioRegistral = new CampoGenericoRegistroVO(Integer.toString(503), "1");
        camposGenericos.add(campoInvolucradoIntercambioRegistral);
        if (TipoLibroEnum.ENTRADA == tipoLibro) {
            this.registroManager.updateRegistroEntradaIR(usuario, identificadorRegistro, camposGenericos);
        } else {
            this.registroManager.updateRegistroSalidaIR(usuario, identificadorRegistro, camposGenericos);
        }
    }

    @Override
    public void anularIntercambioRegistralSalidaById(String id) {
        BandejaSalidaIntercambioRegistralDAO dao = this.getBandejaSalidaIntercambioRegistralDAO();
        IntercambioRegistralSalidaVO intecambioRegistralSalida = dao.get(Long.parseLong(id));
        EstadoIntercambioRegistralSalidaVO estadoAnulado = new EstadoIntercambioRegistralSalidaVO();
        estadoAnulado.setEstado(EstadoIntercambioRegistralSalidaEnumVO.ANULADO);
        estadoAnulado.setFechaEstado(new Date());
        estadoAnulado.setIdExReg(Long.parseLong(id));
        String userName = "usuario anulando";
        estadoAnulado.setUserName(userName);
        this.updateEstado(intecambioRegistralSalida, estadoAnulado);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Anulado el intercambio registral de salida con id = " + id));
        }
    }

    @Override
    public String enviarIntercambioRegistralSalida(String idLibro, String idRegistro, String idOfic, String username, String tipoOrigen, UnidadTramitacionIntercambioRegistralVO unidadDestino) {
        String result = null;
        IdentificadorRegistroVO identificadorRegistro = new IdentificadorRegistroVO(idRegistro, idLibro);
        TipoLibroEnum tipoLibro = TipoLibroEnum.getEnum(Integer.parseInt(tipoOrigen));
        List<IntercambioRegistralSalidaVO> listaIntercambiosSalida = this.getBandejaSalidaIntercambioRegistralDAO().getIntercambiosRegistralesSalida(Integer.valueOf(idRegistro), Integer.valueOf(idLibro), Integer.valueOf(idOfic));
        ListIterator<IntercambioRegistralSalidaVO> itr = listaIntercambiosSalida.listIterator();
        Object temp = null;
        IntercambioRegistralSalidaVO intercambioSalida = new IntercambioRegistralSalidaVO();
        intercambioSalida.setIdLibro(Long.parseLong(idLibro));
        intercambioSalida.setIdRegistro(Long.parseLong(idRegistro));
        intercambioSalida.setIdOfic(Integer.parseInt(idOfic));
        intercambioSalida.setTipoOrigen(Integer.parseInt(tipoOrigen));
        intercambioSalida.setUsername(username);
        result = this.enviarIntercambioRegistralSalida(intercambioSalida, unidadDestino);
        this.saveBandejaSalida(intercambioSalida, identificadorRegistro, tipoLibro);
        return result;
    }

    @Override
    public void undoAnularIntercambioRegistral(String id) {
        BandejaSalidaIntercambioRegistralDAO dao = this.getBandejaSalidaIntercambioRegistralDAO();
        IntercambioRegistralSalidaVO intecambioRegistralSalida = dao.get(Long.parseLong(id));
        EstadoIntercambioRegistralSalidaVO estadoPendiente = new EstadoIntercambioRegistralSalidaVO();
        estadoPendiente.setEstado(EstadoIntercambioRegistralSalidaEnumVO.PENDIENTE);
        estadoPendiente.setFechaEstado(new Date());
        estadoPendiente.setIdExReg(Long.parseLong(id));
        String userName = "usuario desanulando";
        estadoPendiente.setUserName(userName);
        this.updateEstado(intecambioRegistralSalida, estadoPendiente);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Desanulado el intercambio registral de salida con id = " + id));
        }
    }

    @Override
    public void updateEstado(IntercambioRegistralSalidaVO intecambioRegistralSalida, EstadoIntercambioRegistralSalidaVO estado) {
        this.getBandejaSalidaIntercambioRegistralDAO().updateEstado(intecambioRegistralSalida, estado);
        this.saveHistorialCambiosEstado(intecambioRegistralSalida);
    }

    @Override
    public List<IntercambioRegistralSalidaVO> getIntercambiosRegistralesSalida(Integer estado) {
        return this.getBandejaSalidaIntercambioRegistralDAO().getIntercambiosRegistralesSalida(estado);
    }

    @Override
    public BandejaSalidaItemVO completarBandejaSalidaItem(BandejaSalidaItemVO bandejaSalidaItemVO) {
        bandejaSalidaItemVO = this.getBandejaSalidaIntercambioRegistralDAO().completarBandejaSalidaItem(bandejaSalidaItemVO);
        return bandejaSalidaItemVO;
    }

    @Override
    public List<BandejaSalidaItemVO> getBandejaSalidaIntercambioRegistral(Integer estado, Integer idOficina) {
        return this.getBandejaSalidaIntercambioRegistralDAO().getBandejaSalidaByEstadoYOficina(estado, idOficina);
    }

    @Override
    public List<BandejaSalidaItemVO> getBandejaSalidaIntercambioRegistral(Integer estado, Integer idOficina, Integer idLibro) {
        return this.getBandejaSalidaIntercambioRegistralDAO().getBandejaSalidaByEstadoOficinaYLibro(estado, idOficina, idLibro);
    }

    @Override
    public IntercambioRegistralSalidaVO getIntercambioRegistralSalidaById(String id) {
        IntercambioRegistralSalidaVO intercambio = null;
        try {
            intercambio = this.getBandejaSalidaIntercambioRegistralDAO().get(Long.parseLong(id));
        }
        catch (NumberFormatException e) {
            logger.error((Object)"Error al parsear el ID de intercambio registral", (Throwable)e);
        }
        return intercambio;
    }

    @Override
    public void deleteIntercambioRegistralSalida(Integer idLibro, Integer idRegistro, Integer idOficina) {
        this.getBandejaSalidaIntercambioRegistralDAO().deleteByIdArchIdFdr(idLibro, idRegistro, idOficina);
        if (logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Eliminado el intercambio registral de salida del registro ").append(idRegistro).append(" del libro ").append(idLibro);
            logger.debug((Object)sb.toString());
        }
    }

    @Override
    public void reenviarIntercambioRegistralSalidaById(String id, String usuario, String contacto, String descripcionReenvio, UnidadTramitacionIntercambioRegistralVO nuevoDestino) {
        this.getIntercambioRegistralSIRManager().reenviarAsientoRegistral(id, usuario, contacto, descripcionReenvio, nuevoDestino);
        IntercambioRegistralSalidaVO intecambioRegistralSalida = null;
        intecambioRegistralSalida = this.getBandejaSalidaIntercambioRegistralDAO().get(Long.parseLong(id));
        EstadoIntercambioRegistralSalidaVO estadoEnviado = new EstadoIntercambioRegistralSalidaVO();
        estadoEnviado.setEstado(EstadoIntercambioRegistralSalidaEnumVO.ENVIADO);
        estadoEnviado.setFechaEstado(new Date());
        estadoEnviado.setIdExReg(Long.parseLong(id));
        estadoEnviado.setUserName(usuario);
        this.updateEstado(intecambioRegistralSalida, estadoEnviado);
    }

    @Override
    public List<IntercambioRegistralSalidaVO> getHistorialIntercambioRegistralSalida(String idLibro, String idRegistro, String idOficina) {
        List<IntercambioRegistralSalidaVO> intercambiosRegistralSalidaVO = null;
        intercambiosRegistralSalidaVO = idOficina == null ? this.getBandejaSalidaIntercambioRegistralDAO().getIntercambiosRegistralesSalida(Integer.parseInt(idRegistro), Integer.parseInt(idLibro), null) : this.getBandejaSalidaIntercambioRegistralDAO().getIntercambiosRegistralesSalida(Integer.parseInt(idRegistro), Integer.parseInt(idLibro), Integer.parseInt(idOficina));
        for (IntercambioRegistralSalidaVO intercambioReg : intercambiosRegistralSalidaVO) {
            List<EstadoIntercambioRegistralSalidaVO> cambiosEstadoIntercambioRegistral = this.getBandejaSalidaIntercambioRegistralDAO().getDetalleEstadosIntercambioRegistralSalida(intercambioReg.getId());
            intercambioReg.setEstadosIntercambioRegistralSalida(cambiosEstadoIntercambioRegistral);
        }
        return intercambiosRegistralSalidaVO;
    }

    private void saveHistorialCambiosEstado(IntercambioRegistralSalidaVO intercambioRegistral) {
        EstadoIntercambioRegistralSalidaVO estado = new EstadoIntercambioRegistralSalidaVO();
        estado.setEstado(intercambioRegistral.getEstado());
        estado.setFechaEstado(intercambioRegistral.getFechaEstado());
        estado.setId(this.getIntercambioRegistralSalidaEstadoIncrementer().nextLongValue());
        estado.setIdExReg(intercambioRegistral.getId());
        estado.setUserName(intercambioRegistral.getUsername());
        this.getBandejaSalidaIntercambioRegistralDAO().saveDetalleEstado(estado);
    }

    private String enviarIntercambioRegistralSalida(IntercambioRegistralSalidaVO intercambioRegistralSalida, UnidadTramitacionIntercambioRegistralVO unidadDestino) {
        AsientoRegistralFormVO asientoRegistralIntercambio = this.getIntercambioRegistralGeneradorObjetosManager().getAsientoRegistralIntercambioRegistralVO(intercambioRegistralSalida, unidadDestino);
        this.validateDatosIR(asientoRegistralIntercambio);
        asientoRegistralIntercambio.setIndicadorPrueba(IndicadorPruebaEnum.NORMAL);
        AsientoRegistralVO asiento = this.getIntercambioRegistralSIRManager().enviarAsientoRegistral(asientoRegistralIntercambio);
        if (StringUtils.isNotEmpty((String)asiento.getCodigoError())) {
            logger.error((Object)("Ha ocurrido un error en el envio del asiento de intercambio registral con id = " + intercambioRegistralSalida.getId()));
            logger.error((Object)("Codigo de error = " + asiento.getCodigoError()));
            logger.error((Object)("Descripcion de error = " + asiento.getDescripcionError()));
            throw new IntercambioRegistralException("Ha ocurrido un error en el envio del asiento de intercambio registral", IntercambioRegistralExceptionCodes.ERROR_NOT_SEND_INTERCAMBIO_REGISTRAL);
        }
        intercambioRegistralSalida.setId(this.getIntercambioRegistralSalidaIncrementer().nextLongValue());
        intercambioRegistralSalida.setIdIntercambioRegistral(asiento.getIdentificadorIntercambio());
        intercambioRegistralSalida.setIdIntercambioInterno(asiento.getId());
        intercambioRegistralSalida.setCodeEntity(asiento.getCodigoEntidadRegistralDestino());
        intercambioRegistralSalida.setNameEntity(asiento.getDescripcionEntidadRegistralDestino());
        intercambioRegistralSalida.setCodeTramunit(asiento.getCodigoUnidadTramitacionDestino());
        intercambioRegistralSalida.setNameTramunit(asiento.getDescripcionUnidadTramitacionDestino());
        intercambioRegistralSalida.setEstado(EstadoIntercambioRegistralSalidaEnumVO.ENVIADO);
        intercambioRegistralSalida.setFechaIntercambio(Calendar.getInstance().getTime());
        intercambioRegistralSalida.setFechaEstado(Calendar.getInstance().getTime());
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Se ha enviado correctamente el asiento de intercambio registral con id = " + intercambioRegistralSalida.getId() + " e identificador de intercambio = " + asiento.getIdentificadorIntercambio()));
        }
        return intercambioRegistralSalida.getIdIntercambioRegistral();
    }

    private void validateDatosIR(AsientoRegistralFormVO asientoRegistralIntercambio) {
        this.validateRelacionEntidadRegistralUnidadTramitacion(asientoRegistralIntercambio.getCodigoEntidadRegistralDestino(), asientoRegistralIntercambio.getCodigoUnidadTramitacionDestino());
    }

    private void validateRelacionEntidadRegistralUnidadTramitacion(String codigoEntidad, String codigoUnidTram) {
        if (IntercambioRegistralConfiguration.getInstance().getActiveValidationRelationEntidadUnidad()) {
            this.activeValidateRelacionEntidadRegistralUnidadTramitacion(codigoEntidad, codigoUnidTram);
        }
    }

    private void activeValidateRelacionEntidadRegistralUnidadTramitacion(String codigoEntidad, String codigoUnidTram) {
        if (logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Validando relaci\u00f3n de la entidad [").append(codigoEntidad).append("] con la unid. tramitaci\u00f3n [").append(codigoUnidTram).append("]");
            logger.debug((Object)sb.toString());
        }
        if (StringUtils.isNotBlank((String)codigoUnidTram) && !this.getConfiguracionIntercambioRegistralManager().existRelacionUnidOrgaOficina(codigoEntidad, codigoUnidTram)) {
            throw new IntercambioRegistralException("La unid. de tramitaci\u00f3n indicada no se corresponde con la entidad registral", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_UNID_TRAMITA_ENTIDAD_REG);
        }
    }

    public BandejaSalidaIntercambioRegistralDAO getBandejaSalidaIntercambioRegistralDAO() {
        return this.bandejaSalidaIntercambioRegistralDAO;
    }

    public void setBandejaSalidaIntercambioRegistralDAO(BandejaSalidaIntercambioRegistralDAO bandejaSalidaIntercambioRegistralDAO) {
        this.bandejaSalidaIntercambioRegistralDAO = bandejaSalidaIntercambioRegistralDAO;
    }

    public ConfiguracionIntercambioRegistralManager getConfiguracionIntercambioRegistralManager() {
        return this.configuracionIntercambioRegistralManager;
    }

    public void setConfiguracionIntercambioRegistralManager(ConfiguracionIntercambioRegistralManager configuracionIntercambioRegistralManager) {
        this.configuracionIntercambioRegistralManager = configuracionIntercambioRegistralManager;
    }

    public IntercambioRegistralGeneradorObjetosManager getIntercambioRegistralGeneradorObjetosManager() {
        return this.intercambioRegistralGeneradorObjetosManager;
    }

    public void setIntercambioRegistralGeneradorObjetosManager(IntercambioRegistralGeneradorObjetosManager intercambioRegistralGeneradorObjetosManager) {
        this.intercambioRegistralGeneradorObjetosManager = intercambioRegistralGeneradorObjetosManager;
    }

    public DataFieldMaxValueIncrementer getIntercambioRegistralSalidaIncrementer() {
        return this.intercambioRegistralSalidaIncrementer;
    }

    public DataFieldMaxValueIncrementer getIntercambioRegistralSalidaEstadoIncrementer() {
        return this.intercambioRegistralSalidaEstadoIncrementer;
    }

    public void setIntercambioRegistralSalidaEstadoIncrementer(DataFieldMaxValueIncrementer intercambioRegistralSalidaEstadoIncrementer) {
        this.intercambioRegistralSalidaEstadoIncrementer = intercambioRegistralSalidaEstadoIncrementer;
    }

    public void setIntercambioRegistralSalidaIncrementer(DataFieldMaxValueIncrementer intercambioRegistralSalidaIncrementer) {
        this.intercambioRegistralSalidaIncrementer = intercambioRegistralSalidaIncrementer;
    }

    public IntercambioRegistralSIRManager getIntercambioRegistralSIRManager() {
        return this.intercambioRegistralSIRManager;
    }

    public void setIntercambioRegistralSIRManager(IntercambioRegistralSIRManager intercambioRegistralSIRManager) {
        this.intercambioRegistralSIRManager = intercambioRegistralSIRManager;
    }

    @Override
    public boolean isInBandejasalidaIntercambioRegistral(String idRegistro, String idLibro) {
        return false;
    }

    public RegistroManager getRegistroManager() {
        return this.registroManager;
    }

    public void setRegistroManager(RegistroManager registroManager) {
        this.registroManager = registroManager;
    }

    @Override
    public List<BandejaSalidaItemVO> findBandejaSalidaByCriterios(EstadoIntercambioRegistralSalidaEnumVO estado, CriteriosBusquedaIRSalidaVO criterios, Integer idLibro) {
        return this.getBandejaSalidaIntercambioRegistralDAO().findByCriterios(estado, criterios, idLibro);
    }

    @Override
    public PaginatedArrayList<BandejaSalidaItemVO> findBandejaSalidaByCriterios(EstadoIntercambioRegistralSalidaEnumVO estado, CriteriosBusquedaIRSalidaVO criterios, Integer idLibro, PageInfo pageInfo) {
        return this.getBandejaSalidaIntercambioRegistralDAO().findByCriterios(estado, criterios, idLibro, pageInfo);
    }
}
