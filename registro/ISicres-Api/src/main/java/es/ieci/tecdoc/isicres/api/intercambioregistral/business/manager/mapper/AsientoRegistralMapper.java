package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper;

import es.ieci.tecdoc.fwktd.sir.core.types.CanalNotificacionEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.DocumentacionFisicaEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.EstadoAsientoRegistralEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoDocumentoEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoDocumentoIdentificacionEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoRegistroEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoTransporteEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.AnexoFormVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralFormVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.InteresadoFormVO;
import es.ieci.tecdoc.fwktd.util.mime.MimeUtil;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.DocumentoElectronicoAnexoDatosFirmaVO;
import es.ieci.tecdoc.isicres.api.intercambio.registral.business.util.IntercambioRegistralConfiguration;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralExceptionCodes;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaEntradaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.DocumentacionFisicaIntercambioRegistralEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EntidadRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroCamposExtendidosVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroDireccionTelematicaInteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroDireccionVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroDomicilioInteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroInfoDocumentoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroInteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroPageRepositoryVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroPersonaFisicaOJuridicaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoRegistroEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionIntercambioRegistralVO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

public class AsientoRegistralMapper {
    private static final int DIRECCION_INTERESADOS_IR_MAX_VALUE = 160;
    private static final Logger logger = Logger.getLogger((Class)AsientoRegistralMapper.class);
    private static final String CODE_TIPO_DOCUMENTO_OTROS = "X";
    private static final int TIPO_DIR_TELEMATICA_TLF_FIJO = 1;
    private static final int TIPO_DIR_TELEMATICA_TLF_MOVIL = 5;
    private static final int TIPO_DIR_TELEMATICA_EMAIL = 2;
    private static final int TIPO_DIR_TELEMATICA_FAX = 3;
    private static final int TIPO_DIR_TELEMATICA_DEU = 4;
    private static final int TIPO_DIR_TELEMATICA_COMPARECENCIA_ELECTRONICA = 6;
    private static final String EXTENSION_XSIG = "XSIG";
    private static final String EXTENSION_XADES = "XADES";

    public BandejaEntradaItemVO toBandejaEntradaItemVO(AsientoRegistralVO asientoRegistral) {
        BandejaEntradaItemVO bandejaEntradaItem = new BandejaEntradaItemVO();
        bandejaEntradaItem.setOrigen(asientoRegistral.getCodigoEntidadRegistralOrigen());
        bandejaEntradaItem.setOrigenName(asientoRegistral.getDescripcionEntidadRegistralOrigen());
        bandejaEntradaItem.setFechaEstado(asientoRegistral.getFechaEstado());
        bandejaEntradaItem.setFechaIntercambioRegistral(asientoRegistral.getFechaEnvio());
        bandejaEntradaItem.setFechaRegistro(asientoRegistral.getFechaRegistro());
        bandejaEntradaItem.setIdIntercambioRegistral(asientoRegistral.getIdentificadorIntercambio());
        bandejaEntradaItem.setIdIntercambioInterno(asientoRegistral.getIdAsLong());
        bandejaEntradaItem.setTipoLibro(TipoRegistroEnumVO.getTipoRegistroFromSIR(asientoRegistral.getTipoRegistro()).getValue());
        bandejaEntradaItem.setNumeroRegistroOriginal(asientoRegistral.getNumeroRegistro());
        bandejaEntradaItem.setDocumentacionFisicaIntercambioRegistral(DocumentacionFisicaIntercambioRegistralEnum.getDocumentacionFisica(asientoRegistral.getDocumentacionFisica().getValue()));
        if (EstadoAsientoRegistralEnum.RECIBIDO == asientoRegistral.getEstado()) {
            bandejaEntradaItem.setEstado(EstadoIntercambioRegistralEntradaEnumVO.PENDIENTE);
        } else if (EstadoAsientoRegistralEnum.REENVIADO == asientoRegistral.getEstado()) {
            bandejaEntradaItem.setEstado(EstadoIntercambioRegistralEntradaEnumVO.REENVIADO);
        } else {
            bandejaEntradaItem.setEstado(EstadoIntercambioRegistralEntradaEnumVO.RECHAZADO);
        }
        return bandejaEntradaItem;
    }

    public AsientoRegistralFormVO toAsientoRegistralFormVO(InfoRegistroVO infoRegistro) {
        AsientoRegistralFormVO asientoRegistral = null;
        asientoRegistral = new AsientoRegistralFormVO();
        asientoRegistral.setContactoUsuario(null);
        asientoRegistral.setNombreUsuario(null);
        asientoRegistral.setFechaRegistro(infoRegistro.getFechaRegistro());
        asientoRegistral.setNumeroExpediente(infoRegistro.getNumeroExpediente());
        asientoRegistral.setNumeroRegistro(infoRegistro.getNumeroRegistro());
        asientoRegistral.setObservacionesApunte(infoRegistro.getObservacionesApunte());
        asientoRegistral.setResumen(infoRegistro.getResumen());
        if (StringUtils.isEmpty((String)infoRegistro.getResumen())) {
            asientoRegistral.setResumen(infoRegistro.getDescripcionAsunto());
        }
        this.setCamposExtendidos(asientoRegistral, infoRegistro);
        if (StringUtils.isNotEmpty((String)infoRegistro.getCodigoTransporteSIR())) {
            TipoTransporteEnum tipoTransporte = TipoTransporteEnum.getTipoTransporte((String)infoRegistro.getCodigoTransporteSIR());
            asientoRegistral.setTipoTransporte(tipoTransporte);
        } else if (StringUtils.isNotEmpty((String)infoRegistro.getTipoTransporte())) {
            StringBuffer comentarioRegistro = new StringBuffer();
            if (StringUtils.isNotEmpty((String)asientoRegistral.getObservacionesApunte())) {
                comentarioRegistro.append(asientoRegistral.getObservacionesApunte()).append("\n");
            }
            comentarioRegistro.append("Tipo de transporte: " + infoRegistro.getTipoTransporte());
            asientoRegistral.setObservacionesApunte(comentarioRegistro.toString());
        }
        asientoRegistral.setNumeroTransporte(infoRegistro.getNumeroTransporte());
        UnidadTramitacionIntercambioRegistralVO unidadTramitacionDestino = infoRegistro.getUnidadTramitacionDestino();
        asientoRegistral.setCodigoEntidadRegistralDestino(unidadTramitacionDestino.getCodeEntity());
        asientoRegistral.setDescripcionEntidadRegistralDestino(unidadTramitacionDestino.getNameEntity());
        if (unidadTramitacionDestino.getCodeTramunit() != null) {
            asientoRegistral.setCodigoUnidadTramitacionDestino(unidadTramitacionDestino.getCodeTramunit());
            asientoRegistral.setDescripcionUnidadTramitacionDestino(unidadTramitacionDestino.getNameTramunit());
        }
        EntidadRegistralVO entidadRegistralOrigen = infoRegistro.getEntidadRegistralOrigen();
        asientoRegistral.setCodigoEntidadRegistralOrigen(entidadRegistralOrigen.getCode());
        asientoRegistral.setDescripcionEntidadRegistralOrigen(entidadRegistralOrigen.getName());
        asientoRegistral.setCodigoEntidadRegistralInicio(entidadRegistralOrigen.getCode());
        asientoRegistral.setDescripcionEntidadRegistralInicio(entidadRegistralOrigen.getName());
        if (infoRegistro.getUnidadTramitacionOrigen() != null) {
            asientoRegistral.setCodigoUnidadTramitacionOrigen(infoRegistro.getUnidadTramitacionOrigen().getCodeTramunit());
            asientoRegistral.setDescripcionUnidadTramitacionOrigen(infoRegistro.getUnidadTramitacionOrigen().getNameTramunit());
        }
        asientoRegistral.setCodigoEntidadRegistral(entidadRegistralOrigen.getCode());
        asientoRegistral.setTipoRegistro(infoRegistro.getTipoRegistro());
        if (!this.setInteresados(asientoRegistral, infoRegistro)) {
            logger.info((Object)"El registro a enviar NO tiene terceros, o no son terceros validados");
            this.validateInteresadosYUnidTramitacionOrigen(asientoRegistral);
        } else {
            this.validateInteresadosRegistroSalida(asientoRegistral);
        }
        this.setAnexos(asientoRegistral, infoRegistro);
        asientoRegistral.setDocumentacionFisica(this.getDocumentacionFisica(infoRegistro));
        this.validarAsientoRegistral(asientoRegistral);
        return asientoRegistral;
    }

    private void validateInteresadosRegistroSalida(AsientoRegistralFormVO asientoRegistral) {
        if (asientoRegistral.getTipoRegistro() == TipoRegistroEnum.SALIDA && StringUtils.isEmpty((String)asientoRegistral.getCodigoUnidadTramitacionOrigen()) && !this.validateInteresadoTipoCodigoOrigen(asientoRegistral.getInteresados())) {
            throw new IntercambioRegistralException("El registro no tiene ni interesados v\u00e1lidos ni una Unidad Administrativa de Origen mapeada en el DCO.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_INTERESADOS_U_ORIGEN);
        }
    }

    private boolean validateInteresadoTipoCodigoOrigen(List<InteresadoFormVO> listaInteresados) {
        boolean result = false;
        for (InteresadoFormVO interesado : listaInteresados) {
            if (interesado.getTipoDocumentoIdentificacionInteresado() != TipoDocumentoIdentificacionEnum.CODIGO_ORIGEN) continue;
            return true;
        }
        return result;
    }

    private void validateInteresadosYUnidTramitacionOrigen(AsientoRegistralFormVO asientoRegistral) {
        if (asientoRegistral.getTipoRegistro() == TipoRegistroEnum.ENTRADA) {
            throw new IntercambioRegistralException("El registro debe tener al menos un interesados validados.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_MINIMOS_INTERESADOS);
        }
        if (StringUtils.isEmpty((String)asientoRegistral.getCodigoUnidadTramitacionOrigen())) {
            throw new IntercambioRegistralException("El registro no tiene ni interesados validados ni una Unidad Administrativa de Origen mapeada en el DCO.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_INTERESADOS_U_ORIGEN);
        }
        logger.info((Object)("El registro se env\u00eda igualmente porque tiene mapeada la UT=" + asientoRegistral.getCodigoUnidadTramitacionOrigen()));
    }

    private void validarAsientoRegistral(AsientoRegistralFormVO asientoRegistral) {
        this.validarObservaciones(asientoRegistral);
        this.validarInteresados(asientoRegistral);
        this.validarCampoExpone(asientoRegistral);
        this.validarCampoSolicita(asientoRegistral);
    }

    private void validarObservaciones(AsientoRegistralFormVO asientoRegistral) {
        String observaciones = asientoRegistral.getObservacionesApunte();
        if (StringUtils.isNotEmpty((String)observaciones) && observaciones.length() > 50) {
            throw new IntercambioRegistralException("La longitud del campo Comentarios debe de ser menor de 50 caracteres.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_LENGTH_COMENTARIOS);
        }
    }

    private void validarInteresados(AsientoRegistralFormVO asientoRegistral) {
        List<InteresadoFormVO> interesados = asientoRegistral.getInteresados();
        for (InteresadoFormVO interesadoFormVO : interesados) {
            if (interesadoFormVO.getDireccionInteresado() != null && interesadoFormVO.getDireccionInteresado().length() > 160) {
                throw new IntercambioRegistralException("La longitud de la direccion del interesado debe de ser menor de 160 caracteres.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_INTERESADOS_DIRECCION_LENGTH);
            }
            if (interesadoFormVO.getDireccionRepresentante() == null || interesadoFormVO.getDireccionRepresentante().length() <= 160) continue;
            throw new IntercambioRegistralException("La longitud de la direccion del interesado debe de ser menor de 160 caracteres.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_INTERESADOS_DIRECCION_LENGTH);
        }
    }

    private void validarCampoSolicita(AsientoRegistralFormVO asientoRegistral) {
        if (StringUtils.isNotEmpty((String)asientoRegistral.getSolicita()) && asientoRegistral.getSolicita().length() > 4000) {
            throw new IntercambioRegistralException("La longitud del campo Solicita debe de ser menor de 4000 caracteres.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_SOLICITA_LENGTH);
        }
    }

    private void validarCampoExpone(AsientoRegistralFormVO asientoRegistral) {
        if (StringUtils.isNotEmpty((String)asientoRegistral.getExpone()) && asientoRegistral.getExpone().length() > 4000) {
            throw new IntercambioRegistralException("La longitud del campo Expone debe de ser menor de 4000 caracteres.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_EXPONE_LENGTH);
        }
    }

    private void setAsunto(InfoRegistroVO infoRegistro, AsientoRegistralFormVO asientoRegistral) {
        StringBuffer comentarioRegistro = new StringBuffer();
        if (StringUtils.isNotEmpty((String)asientoRegistral.getObservacionesApunte())) {
            comentarioRegistro.append(asientoRegistral.getObservacionesApunte()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)infoRegistro.getCodigoAsunto())) {
            comentarioRegistro.append("Cod. Asunto: ").append(infoRegistro.getCodigoAsunto());
        }
        asientoRegistral.setObservacionesApunte(comentarioRegistro.toString());
    }

    private void setDatosTransporte(InfoRegistroVO infoRegistro, AsientoRegistralFormVO asientoRegistral) {
        StringBuffer comentarioRegistro = new StringBuffer();
        if (StringUtils.isNotEmpty((String)asientoRegistral.getObservacionesApunte())) {
            comentarioRegistro.append(asientoRegistral.getObservacionesApunte()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)infoRegistro.getTipoTransporte())) {
            comentarioRegistro.append("Tipo de Transporte: ").append(infoRegistro.getTipoTransporte()).append("\n");
        }
        if (StringUtils.isNotEmpty((String)infoRegistro.getNumeroTransporte())) {
            comentarioRegistro.append("Num. Transporte: ").append(infoRegistro.getNumeroTransporte());
        }
        asientoRegistral.setObservacionesApunte(comentarioRegistro.toString());
    }

    private void setAnexos(AsientoRegistralFormVO asientoRegistral, InfoRegistroVO infoRegistro) {
        ArrayList<AnexoFormVO> anexos = new ArrayList<AnexoFormVO>();
        List<InfoRegistroPageRepositoryVO> listaDocumetnos = infoRegistro.getListadoDocumentos();
        for (InfoRegistroPageRepositoryVO documento : listaDocumetnos) {
            AnexoFormVO anexo = new AnexoFormVO();
            anexo.setContenido(documento.getContent());
            anexo.setNombreFichero(documento.getInfoDocumento().getNombre());
            this.validateExtensionFileByIntercambioReg(documento.getInfoDocumento().getExtension());
            anexo.setTipoMIME(MimeUtil.getExtensionMimeType((String)documento.getInfoDocumento().getExtension()));
            if ("XADES".equalsIgnoreCase(documento.getInfoDocumento().getExtension()) || "XSIG".equalsIgnoreCase(documento.getInfoDocumento().getExtension())) {
                anexo.setTipoDocumento(TipoDocumentoEnum.FICHERO_TECNICO_INTERNO);
            } else {
                anexo.setTipoDocumento(TipoDocumentoEnum.DOCUMENTO_ADJUNTO);
            }
            if (documento.getDatosFirma() != null) {
                anexo.setCodigoFichero(documento.getDatosFirma().getIdAttachment().toString());
            }
            if (documento.getDatosFirma() != null && documento.getDatosFirma().getFirma() != null) {
                try {
                    anexo.setCertificado(documento.getDatosFirma().getCertificado().getBytes());
                    anexo.setCodigoFicheroFirmado(documento.getDatosFirma().getIdAttachmentFirmado().toString());
                    if (documento.getDatosFirma().getSelloTiempo() != null) {
                        anexo.setTimestamp(documento.getDatosFirma().getSelloTiempo().getBytes());
                    }
                    if (documento.getDatosFirma().getOcspValidation() != null) {
                        anexo.setValidacionOCSPCertificado(documento.getDatosFirma().getOcspValidation().getBytes());
                    }
                }
                catch (Exception e) {
                    throw new IntercambioRegistralException("Error al leer los ficheros electr\u00f3nicos del registro.", IntercambioRegistralExceptionCodes.ERROR_CODE_LEER_ADJUNTOS);
                }
            }
            anexos.add(anexo);
        }
        asientoRegistral.setAnexos(anexos);
        this.validateAnexos(anexos);
    }

    private void validateExtensionFileByIntercambioReg(String extension) {
        boolean isValidExtension = this.isValidExtension(extension);
        if (!isValidExtension) {
            StringBuffer sb = new StringBuffer();
            sb.append("La extensi\u00f3n del fichero no es v\u00e1lida [").append(extension).append("] para realizar el intercambio registral");
            throw new IntercambioRegistralException(sb.toString(), IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_EXTENSION_FILES);
        }
    }

    private boolean isValidExtension(String extension) {
        List<String> extensionesValidas = IntercambioRegistralConfiguration.getInstance().getExtensiones();
        for (String extensionValida : extensionesValidas) {
            if (!extensionValida.equalsIgnoreCase(extension)) continue;
            return true;
        }
        return false;
    }

    private void validateAnexos(List<AnexoFormVO> anexos) {
        ListIterator<AnexoFormVO> itr = anexos.listIterator();
        Long maxSize = IntercambioRegistralConfiguration.getInstance().getFileMaxSize();
        Long maxSizeSetFiles = IntercambioRegistralConfiguration.getInstance().getFilesSetMaxSize();
        Integer maxFiles = IntercambioRegistralConfiguration.getInstance().getFileMaxNum();
        int numAnexos = this.countAnexosIR(anexos);
        if (numAnexos > maxFiles) {
            throw new IntercambioRegistralException("No se permiten enviar m\u00e1s de " + maxFiles + " ficheros", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_MAX_NUM_FICHEROS);
        }
        long sizeTotalFiles = this.getTotalSizeFilesAndValidateSizeFile(itr, maxSize);
        if (sizeTotalFiles > maxSizeSetFiles) {
            throw new IntercambioRegistralException("El conjunto de ficheros no puede superar los " + maxSizeSetFiles + " Bytes", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_MAX_SIZE_SET_FILES);
        }
    }

    private long getTotalSizeFilesAndValidateSizeFile(ListIterator<AnexoFormVO> itr, Long maxSize) {
        long sizeTotalFiles = 0;
        while (itr.hasNext()) {
            AnexoFormVO anexo = itr.next();
            long size = anexo.getContenido().length / 1024;
            if (anexo.getTipoDocumento() != TipoDocumentoEnum.FICHERO_TECNICO_INTERNO) {
                sizeTotalFiles+=size;
            }
            if (size <= maxSize) continue;
            throw new IntercambioRegistralException("Los ficheros no pueden superar los " + maxSize + " Bytes", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_MAX_SIZE);
        }
        return sizeTotalFiles;
    }

    public int countAnexosIR(List<AnexoFormVO> anexos) {
        int result = 0;
        for (AnexoFormVO anexo2 : anexos) {
            if (anexo2.getTipoDocumento() == TipoDocumentoEnum.FICHERO_TECNICO_INTERNO) continue;
            ++result;
        }
        return result;
    }

    private boolean setInteresados(AsientoRegistralFormVO asientoRegistral, InfoRegistroVO infoRegistro) {
        ArrayList<InteresadoFormVO> listaInteresados = new ArrayList<InteresadoFormVO>();
        boolean tieneTercerosNoValidados = false;
        boolean tieneInteresados = false;
        for (InfoRegistroInteresadoVO interesado : infoRegistro.getInteresados()) {
            InteresadoFormVO interesadoIntercambioRegistral = new InteresadoFormVO();
            InfoRegistroPersonaFisicaOJuridicaVO persona = interesado.getInfoPersona();
            if (persona == null) {
                tieneTercerosNoValidados = true;
            } else if (StringUtils.isEmpty((String)persona.getPrimerApellido())) {
                interesadoIntercambioRegistral.setRazonSocialInteresado(persona.getNombre());
                if (!"X".equalsIgnoreCase(persona.getTipoDocumento())) {
                    interesadoIntercambioRegistral.setDocumentoIdentificacionInteresado(persona.getDocumentoIdentificacion());
                    interesadoIntercambioRegistral.setTipoDocumentoIdentificacionInteresado(TipoDocumentoIdentificacionEnum.getTipoDocumentoIdentificacion((String)persona.getTipoDocumento()));
                }
            } else {
                interesadoIntercambioRegistral.setNombreInteresado(persona.getNombre());
                interesadoIntercambioRegistral.setPrimerApellidoInteresado(persona.getPrimerApellido());
                interesadoIntercambioRegistral.setSegundoApellidoInteresado(persona.getSegundoApellido());
                interesadoIntercambioRegistral.setDocumentoIdentificacionInteresado(persona.getDocumentoIdentificacion());
                interesadoIntercambioRegistral.setTipoDocumentoIdentificacionInteresado(TipoDocumentoIdentificacionEnum.getTipoDocumentoIdentificacion((String)persona.getTipoDocumento()));
            }
            if (interesado.getDireccion() != null) {
                InfoRegistroDireccionVO direccionInteresado = interesado.getDireccion();
                if (direccionInteresado.getDireccionTelematicaInteresado() != null) {
                    String canalNotificacion = null;
                    if (direccionInteresado.getDireccionTelematicaInteresado().getTipo() == 1) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.1");
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        interesadoIntercambioRegistral.setTelefonoInteresado(direccionInteresado.getDireccionTelematicaInteresado().getDireccion());
                    } else if (direccionInteresado.getDireccionTelematicaInteresado().getTipo() == 5) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.5");
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        interesadoIntercambioRegistral.setTelefonoInteresado(direccionInteresado.getDireccionTelematicaInteresado().getDireccion());
                    } else if (direccionInteresado.getDireccionTelematicaInteresado().getTipo() == 2) {
                        interesadoIntercambioRegistral.setCorreoElectronicoInteresado(direccionInteresado.getDireccionTelematicaInteresado().getDireccion());
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.2");
                    } else if (direccionInteresado.getDireccionTelematicaInteresado().getTipo() == 4) {
                        interesadoIntercambioRegistral.setDireccionElectronicaHabilitadaInteresado(direccionInteresado.getDireccionTelematicaInteresado().getDireccion());
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.4");
                        this.validateCanalNotificacionDireccionElectronicaHabilitada(canalNotificacion);
                    } else if (direccionInteresado.getDireccionTelematicaInteresado().getTipo() == 6) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.6");
                    }
                    if (StringUtils.isNotBlank((String)canalNotificacion)) {
                        interesadoIntercambioRegistral.setCanalPreferenteComunicacionInteresado(CanalNotificacionEnum.getCanalNotificacion((String)canalNotificacion));
                    }
                } else if (direccionInteresado.getDomicilioInteresado() != null) {
                    interesadoIntercambioRegistral.setCodigoMunicipioInteresado(direccionInteresado.getDomicilioInteresado().getCiudad());
                    interesadoIntercambioRegistral.setDireccionInteresado(direccionInteresado.getDomicilioInteresado().getDireccion());
                    interesadoIntercambioRegistral.setCodigoPostalInteresado(direccionInteresado.getDomicilioInteresado().getCodigoPostal());
                    interesadoIntercambioRegistral.setCodigoProvinciaInteresado(direccionInteresado.getDomicilioInteresado().getProvincia());
                    interesadoIntercambioRegistral.setCodigoPaisInteresado(direccionInteresado.getDomicilioInteresado().getPais());
                    if (StringUtils.isEmpty((String)interesadoIntercambioRegistral.getCodigoProvinciaInteresado()) && StringUtils.isNotBlank((String)direccionInteresado.getDomicilioInteresado().getNombreProvincia())) {
                        interesadoIntercambioRegistral.setDireccionInteresado(interesadoIntercambioRegistral.getDireccionInteresado() + ", " + direccionInteresado.getDomicilioInteresado().getNombreProvincia());
                    }
                    if (StringUtils.isEmpty((String)interesadoIntercambioRegistral.getCodigoMunicipioInteresado()) && StringUtils.isNotBlank((String)direccionInteresado.getDomicilioInteresado().getNombreCiudad())) {
                        interesadoIntercambioRegistral.setDireccionInteresado(interesadoIntercambioRegistral.getDireccionInteresado() + ", " + direccionInteresado.getDomicilioInteresado().getNombreCiudad());
                    }
                    interesadoIntercambioRegistral.setCanalPreferenteComunicacionInteresado(CanalNotificacionEnum.DIRECCION_POSTAL);
                }
            }
            this.setInfoRepresentante(interesado, interesadoIntercambioRegistral);
            tieneInteresados = true;
            listaInteresados.add(interesadoIntercambioRegistral);
        }
        asientoRegistral.setInteresados(listaInteresados);
        return !tieneTercerosNoValidados && tieneInteresados;
    }

    private void validateCanalNotificacionDireccionElectronicaHabilitada(String canalNotificacion) {
        if (StringUtils.isNotBlank((String)canalNotificacion) && CanalNotificacionEnum.getCanalNotificacion((String)canalNotificacion) == CanalNotificacionEnum.DIRECCION_POSTAL) {
            throw new IntercambioRegistralException("Error el canal de notificaci\u00f3n configurado no es correcto para el tipo de direcci\u00f3n elegida.", IntercambioRegistralExceptionCodes.ERROR_VALIDACION_CHANNEL_NOTIFICATION_DEFAULT_BY_ADDRESS);
        }
    }

    private void validateCanalNotificacionComparecenciaElectronica(String canalNotificacion) {
        if (StringUtils.isNotBlank((String)canalNotificacion) && CanalNotificacionEnum.getCanalNotificacion((String)canalNotificacion) != CanalNotificacionEnum.COMPARECENCIA_ELECTRONICA) {
            throw new IntercambioRegistralException("Error el canal de notificaci\u00f3n configurado no es correcto para el tipo de direcci\u00f3n elegida.", IntercambioRegistralExceptionCodes.ERROR_VALIDACION_CHANNEL_NOTIFICATION_DEFAULT_BY_ADDRESS);
        }
    }

    private void setInfoRepresentante(InfoRegistroInteresadoVO interesado, InteresadoFormVO interesadoIntercambioRegistral) {
        InfoRegistroPersonaFisicaOJuridicaVO representante = interesado.getInfoRepresentante();
        if (representante != null) {
            if (StringUtils.isEmpty((String)representante.getPrimerApellido())) {
                interesadoIntercambioRegistral.setRazonSocialRepresentante(representante.getNombre());
                if (!"X".equalsIgnoreCase(representante.getTipoDocumento())) {
                    interesadoIntercambioRegistral.setDocumentoIdentificacionRepresentante(representante.getDocumentoIdentificacion());
                    interesadoIntercambioRegistral.setTipoDocumentoIdentificacionRepresentante(TipoDocumentoIdentificacionEnum.getTipoDocumentoIdentificacion((String)representante.getTipoDocumento()));
                }
            } else {
                interesadoIntercambioRegistral.setNombreRepresentante(representante.getNombre());
                interesadoIntercambioRegistral.setPrimerApellidoRepresentante(representante.getPrimerApellido());
                interesadoIntercambioRegistral.setSegundoApellidoRepresentante(representante.getSegundoApellido());
                interesadoIntercambioRegistral.setDocumentoIdentificacionRepresentante(representante.getDocumentoIdentificacion());
                interesadoIntercambioRegistral.setTipoDocumentoIdentificacionRepresentante(TipoDocumentoIdentificacionEnum.getTipoDocumentoIdentificacion((String)representante.getTipoDocumento()));
            }
            if (interesado.getDireccionRepresentante() != null) {
                InfoRegistroDireccionVO direccionRepresentante = interesado.getDireccionRepresentante();
                String canalNotificacion = null;
                if (direccionRepresentante.getDireccionTelematicaInteresado() != null) {
                    if (direccionRepresentante.getDireccionTelematicaInteresado().getTipo() == 1) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.1");
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        interesadoIntercambioRegistral.setTelefonoRepresentante(direccionRepresentante.getDireccionTelematicaInteresado().getDireccion());
                    } else if (direccionRepresentante.getDireccionTelematicaInteresado().getTipo() == 5) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.5");
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        interesadoIntercambioRegistral.setTelefonoRepresentante(direccionRepresentante.getDireccionTelematicaInteresado().getDireccion());
                    } else if (direccionRepresentante.getDireccionTelematicaInteresado().getTipo() == 2) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.2");
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        interesadoIntercambioRegistral.setCorreoElectronicoRepresentante(direccionRepresentante.getDireccionTelematicaInteresado().getDireccion());
                    } else if (direccionRepresentante.getDireccionTelematicaInteresado().getTipo() == 4) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.4");
                        this.validateCanalNotificacionComparecenciaElectronica(canalNotificacion);
                        interesadoIntercambioRegistral.setDireccionElectronicaHabilitadaRepresentante(direccionRepresentante.getDireccionTelematicaInteresado().getDireccion());
                    } else if (direccionRepresentante.getDireccionTelematicaInteresado().getTipo() == 6) {
                        canalNotificacion = IntercambioRegistralConfiguration.getInstance().getProperty("canal.notificacion.direccion.6");
                    }
                    if (StringUtils.isNotBlank((String)canalNotificacion)) {
                        interesadoIntercambioRegistral.setCanalPreferenteComunicacionRepresentante(CanalNotificacionEnum.getCanalNotificacion((String)canalNotificacion));
                    }
                } else if (direccionRepresentante.getDomicilioInteresado() != null) {
                    interesadoIntercambioRegistral.setCodigoMunicipioRepresentante(direccionRepresentante.getDomicilioInteresado().getCiudad());
                    interesadoIntercambioRegistral.setDireccionRepresentante(direccionRepresentante.getDomicilioInteresado().getDireccion());
                    interesadoIntercambioRegistral.setCodigoPostalRepresentante(direccionRepresentante.getDomicilioInteresado().getCodigoPostal());
                    interesadoIntercambioRegistral.setCodigoProvinciaRepresentante(direccionRepresentante.getDomicilioInteresado().getProvincia());
                    interesadoIntercambioRegistral.setCodigoPaisRepresentante(direccionRepresentante.getDomicilioInteresado().getPais());
                    if (StringUtils.isEmpty((String)interesadoIntercambioRegistral.getCodigoProvinciaRepresentante()) && StringUtils.isNotBlank((String)direccionRepresentante.getDomicilioInteresado().getNombreProvincia())) {
                        interesadoIntercambioRegistral.setDireccionRepresentante(interesadoIntercambioRegistral.getDireccionRepresentante() + ", " + direccionRepresentante.getDomicilioInteresado().getNombreProvincia());
                    }
                    if (StringUtils.isEmpty((String)interesadoIntercambioRegistral.getCodigoMunicipioInteresado()) && StringUtils.isNotBlank((String)direccionRepresentante.getDomicilioInteresado().getNombreCiudad())) {
                        interesadoIntercambioRegistral.setDireccionRepresentante(interesadoIntercambioRegistral.getDireccionRepresentante() + ", " + direccionRepresentante.getDomicilioInteresado().getNombreCiudad());
                    }
                    interesadoIntercambioRegistral.setCanalPreferenteComunicacionRepresentante(CanalNotificacionEnum.DIRECCION_POSTAL);
                }
            }
        }
    }

    private void setCamposExtendidos(AsientoRegistralFormVO asientoRegistral, InfoRegistroVO infoRegistro) {
        if (!CollectionUtils.isEmpty(infoRegistro.getCamposExtendidos())) {
            ListIterator<InfoRegistroCamposExtendidosVO> camposExtendisoItr = infoRegistro.getCamposExtendidos().listIterator();
            while (camposExtendisoItr.hasNext()) {
                InfoRegistroCamposExtendidosVO campoExtendido = camposExtendisoItr.next();
                Integer fldid = campoExtendido.getFldid();
                if (fldid != null && 18 == fldid) {
                    asientoRegistral.setObservacionesApunte(campoExtendido.getValue());
                    continue;
                }
                if (fldid != null && 502 == fldid) {
                    asientoRegistral.setSolicita(campoExtendido.getValue());
                    continue;
                }
                if (fldid == null || 501 != fldid) continue;
                asientoRegistral.setExpone(campoExtendido.getValue());
            }
        }
    }

    private DocumentacionFisicaEnum getDocumentacionFisica(InfoRegistroVO infoRegistro) {
        Integer trueValue = new Integer("1");
        Integer falseValue = new Integer("0");
        if (infoRegistro.getDocFisicaComplementaria() != null && BooleanUtils.toBoolean((Integer)infoRegistro.getDocFisicaComplementaria(), (Integer)trueValue, (Integer)falseValue)) {
            return DocumentacionFisicaEnum.DOCUMENTACION_FISICA_COMPLEMENTARIA;
        }
        if (infoRegistro.getDocFisicaRequerida() != null && BooleanUtils.toBoolean((Integer)infoRegistro.getDocFisicaRequerida(), (Integer)trueValue, (Integer)falseValue)) {
            return DocumentacionFisicaEnum.DOCUMENTACION_FISICA_REQUERIDA;
        }
        return DocumentacionFisicaEnum.SIN_DOCUMENTACION_FISICA;
    }
}
