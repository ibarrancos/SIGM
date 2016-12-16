package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.impl;

import com.ieci.tecdoc.common.adapter.IRepositoryManager;
import com.ieci.tecdoc.common.repository.vo.ISRepositoryRetrieveDocumentVO;
import es.ieci.tecdoc.fwktd.core.spring.configuration.jdbc.datasource.MultiEntityContextHolder;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoRegistroEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoTransporteEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralFormVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.InteresadoVO;
import es.ieci.tecdoc.isicres.api.business.manager.TipoAsuntoManager;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.TipoAsuntoVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.manager.DocumentoElectronicoAnexoManager;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.DocumentoElectronicoAnexoDatosFirmaVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.DocumentoElectronicoAnexoVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.IdentificadorDocumentoElectronicoAnexoVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.TipoDocumentoAnexoEnumVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.TipoValidezDocumentoAnexoEnumVO;
import es.ieci.tecdoc.isicres.api.intercambio.registral.business.util.IntercambioRegistralConfiguration;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.ConfiguracionIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.InfoRegistroDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralException;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.exception.IntercambioRegistralExceptionCodes;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.DireccionesIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.IntercambioRegistralGeneradorObjetosManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.TipoTransporteIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.mapper.AsientoRegistralMapper;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.repository.RepositoryFactory;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CiudadExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EntidadRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoAsientoRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroCamposExtendidosVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroDireccionTelematicaInteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroDireccionVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroDomicilioInteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroInteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroPageRepositoryVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroPersonaFisicaOJuridicaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroRepresentanteVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoRegistroVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InteresadoExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.PaisExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.ProvinciaExReg;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoRegistroEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoTransporteIntercambioRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionIntercambioRegistralVO;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

public class IntercambioRegistralGeneradorObjetosManagerImpl
implements IntercambioRegistralGeneradorObjetosManager {
    private static Logger logger = Logger.getLogger((Class)IntercambioRegistralGeneradorObjetosManagerImpl.class);
    protected InfoRegistroDAO infoRegistroDAO;
    protected ConfiguracionIntercambioRegistralDAO configuracionIntercambioRegistralDAO;
    protected DocumentoElectronicoAnexoManager documentoElectronicoAnexoManager;
    protected TipoAsuntoManager tipoAsuntoManager;
    protected TipoTransporteIntercambioRegistralManager tipoTransporteIntercambioRegistralManager;
    protected DireccionesIntercambioRegistralManager direccionesIntercambioRegistralRegManager;

    @Override
    public AsientoRegistralFormVO getAsientoRegistralIntercambioRegistralVO(IntercambioRegistralSalidaVO intercambioSalidaVO, String entidad) {
        AsientoRegistralMapper registroMapper = new AsientoRegistralMapper();
        EntidadRegistralVO entidadRegistralOrigen = this.getConfiguracionIntercambioRegistralDAO().getEntidadRegistralVOByIdScrOfic(String.valueOf(intercambioSalidaVO.getIdOfic()));
        if (entidadRegistralOrigen == null) {
            throw new IntercambioRegistralException("No hay configuraci\u00f3n de intercambio registral para la entidad de origen", IntercambioRegistralExceptionCodes.ERROR_CODE_OFICINA_NO_MAPEADA);
        }
        InfoRegistroVO infoRegistro = this.getInfoRegistroAxsf(intercambioSalidaVO);
        List<InfoRegistroPageRepositoryVO> documentos = this.getInfoRegistroDocumentos(intercambioSalidaVO);
        infoRegistro.setListadoDocumentos(documentos);
        if (StringUtils.isNotEmpty((String)infoRegistro.getUnidadOrigen())) {
            UnidadTramitacionIntercambioRegistralVO unidadTramitacionOrigen = this.getConfiguracionIntercambioRegistralDAO().getUnidadTramitacionIntercambioRegistralVOByIdScrOrgs(infoRegistro.getUnidadOrigen());
            infoRegistro.setUnidadTramitacionOrigen(unidadTramitacionOrigen);
        }
        infoRegistro.setEntidadRegistralOrigen(entidadRegistralOrigen);
        infoRegistro.setTipoRegistro(TipoRegistroEnum.getTipoRegistro((String)intercambioSalidaVO.getTipoOrigen().toString()));
        AsientoRegistralFormVO registro = registroMapper.toAsientoRegistralFormVO(infoRegistro);
        return registro;
    }

    @Override
    public AsientoRegistralFormVO getAsientoRegistralIntercambioRegistralVO(IntercambioRegistralSalidaVO intercambioSalidaVO, UnidadTramitacionIntercambioRegistralVO unidadTramitacionDestino) {
        AsientoRegistralMapper registroMapper = new AsientoRegistralMapper();
        InfoRegistroVO infoRegistro = this.getInfoRegistroAxsf(intercambioSalidaVO);
        List<InfoRegistroPageRepositoryVO> documentos = this.getInfoRegistroDocumentos(intercambioSalidaVO);
        infoRegistro.setListadoDocumentos(documentos);
        EntidadRegistralVO entidadRegistralOrigen = this.getConfiguracionIntercambioRegistralDAO().getEntidadRegistralVOByIdScrOfic(String.valueOf(intercambioSalidaVO.getIdOfic()));
        UnidadTramitacionIntercambioRegistralVO unidadTramitacionOrigen = this.getConfiguracionIntercambioRegistralDAO().getUnidadTramitacionIntercambioRegistralVOByIdScrOrgs(infoRegistro.getUnidadOrigen());
        if (entidadRegistralOrigen == null) {
            throw new IntercambioRegistralException("No hay configuraci\u00f3n de intercambio registral para la oficina de origen.", IntercambioRegistralExceptionCodes.ERROR_CODE_OFICINA_NO_MAPEADA);
        }
        infoRegistro.setEntidadRegistralOrigen(entidadRegistralOrigen);
        infoRegistro.setUnidadTramitacionDestino(unidadTramitacionDestino);
        infoRegistro.setUnidadTramitacionOrigen(unidadTramitacionOrigen);
        infoRegistro.setTipoRegistro(TipoRegistroEnumVO.getTipoRegistroSIR(intercambioSalidaVO.getTipoOrigen()));
        AsientoRegistralFormVO registro = registroMapper.toAsientoRegistralFormVO(infoRegistro);
        return registro;
    }

    @Override
    public InteresadoExReg getInteresadoExReg(InteresadoVO interesado) {
        InteresadoExReg interesadoExReg = null;
        if (interesado != null) {
            CiudadExReg municipio;
            PaisExReg pais;
            ProvinciaExReg provincia;
            interesadoExReg = new InteresadoExReg();
            BeanUtils.copyProperties((Object)interesado, (Object)interesadoExReg);
            if (StringUtils.isNotEmpty((String)interesado.getCodigoProvinciaInteresado())) {
                provincia = this.direccionesIntercambioRegistralRegManager.getProvinciaExRegByCodigo(interesado.getCodigoProvinciaInteresado());
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
            if (StringUtils.isNotEmpty((String)interesado.getCodigoProvinciaRepresentante())) {
                provincia = this.direccionesIntercambioRegistralRegManager.getProvinciaExRegByCodigo(interesado.getCodigoProvinciaRepresentante());
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
        return interesadoExReg;
    }

    @Override
    public InfoAsientoRegistralVO getInfoAsientoRegistralVO(AsientoRegistralVO asientoRegistralVO) {
        InfoAsientoRegistralVO infoAsientoRegistralVO = null;
        if (asientoRegistralVO != null) {
            infoAsientoRegistralVO = new InfoAsientoRegistralVO();
            BeanUtils.copyProperties((Object)asientoRegistralVO, (Object)infoAsientoRegistralVO);
            List listInteresados = asientoRegistralVO.getInteresados();
            LinkedList<InteresadoExReg> listInteresadoExReg = new LinkedList<InteresadoExReg>();
            if (listInteresados != null) {
                for (InteresadoVO interesadoVO : listInteresados) {
                    InteresadoExReg interesadoExReg = this.getInteresadoExReg(interesadoVO);
                    listInteresadoExReg.add(interesadoExReg);
                }
            }
            infoAsientoRegistralVO.setInteresadosExReg(listInteresadoExReg);
        }
        return infoAsientoRegistralVO;
    }

    private List<InfoRegistroPageRepositoryVO> getInfoRegistroDocumentos(IntercambioRegistralSalidaVO intercambioSalidaVO) {
        List<InfoRegistroPageRepositoryVO> listaDocumentos = this.getInfoRegistroDAO().getInfoRegistroPageRepositories(intercambioSalidaVO.getIdLibro(), intercambioSalidaVO.getIdRegistro());
        List<DocumentoElectronicoAnexoVO> listaDocumentosElectronicos = this.getDocumentoElectronicoAnexoManager().getDocumentosElectronicoAnexoByRegistro(intercambioSalidaVO.getIdLibro(), intercambioSalidaVO.getIdRegistro());
        try {
            IRepositoryManager repositoryManager = RepositoryFactory.getCurrentPolicy();
            for (InfoRegistroPageRepositoryVO infoRegistroPageRepository : listaDocumentos) {
                DocumentoElectronicoAnexoVO docElectronico = this.getDocumentoElectronicoAnexoVO(Long.parseLong(infoRegistroPageRepository.getIdPageh()), listaDocumentosElectronicos);
                if (docElectronico != null) {
                    infoRegistroPageRepository.setDatosFirma(docElectronico.getDatosFirma());
                    infoRegistroPageRepository.setTipoDocumentoAnexo(docElectronico.getTipoDocumentoAnexo());
                    infoRegistroPageRepository.setTipoValidez(docElectronico.getTipoValidez());
                }
                ISRepositoryRetrieveDocumentVO doc = new ISRepositoryRetrieveDocumentVO();
                doc.setDocumentUID(infoRegistroPageRepository.getDocUID());
                doc.setBookID(Integer.valueOf(infoRegistroPageRepository.getIdLibro()));
                doc.setFdrid(Integer.valueOf(infoRegistroPageRepository.getIdRegistro()));
                doc.setPageID(Integer.valueOf(infoRegistroPageRepository.getIdPageh()));
                doc.setEntidad(MultiEntityContextHolder.getEntity());
                doc = repositoryManager.retrieveDocument(doc);
                infoRegistroPageRepository.setContent(doc.getFileContent());
            }
        }
        catch (Exception e) {
            logger.error((Object)"Error al obtener los documentos del repositorio para anexarlos al intercambio registral", (Throwable)e);
        }
        return listaDocumentos;
    }

    private DocumentoElectronicoAnexoVO getDocumentoElectronicoAnexoVO(Long id, List<DocumentoElectronicoAnexoVO> listaDocumentosElectronicos) {
        DocumentoElectronicoAnexoVO result = null;
        DocumentoElectronicoAnexoVO documento = null;
        for (int i = 0; i < listaDocumentosElectronicos.size(); ++i) {
            documento = listaDocumentosElectronicos.get(i);
            if (!id.equals(documento.getId().getIdPagina())) continue;
            result = documento;
        }
        return result;
    }

    private InfoRegistroVO getInfoRegistroAxsf(IntercambioRegistralSalidaVO intercambioSalidaVO) {
        InfoRegistroVO infoRegistro = null;
        infoRegistro = intercambioSalidaVO.getTipoOrigen() == 1 ? this.getInfoRegistroDAO().getInfoRegistroTipoEntrada(intercambioSalidaVO) : this.getInfoRegistroDAO().getInfoRegistroTipoSalida(intercambioSalidaVO);
        infoRegistro.setCamposExtendidos(this.getInfoRegistroDAO().getInfoRegistroCamposExtendidos(infoRegistro));
        if (StringUtils.isEmpty((String)infoRegistro.getCodigoAsunto()) & StringUtils.isEmpty((String)infoRegistro.getResumen())) {
            throw new IntercambioRegistralException("El registro ha de tener Asunto o Resumen.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_ASUNTO_RESUMEN);
        }
        if (StringUtils.isNotEmpty((String)infoRegistro.getCodigoAsunto())) {
            UsuarioVO usuario = new UsuarioVO();
            usuario.getConfiguracionUsuario().setLocale(new Locale("es", "ES"));
            TipoAsuntoVO asunto = this.getTipoAsuntoManager().getTipoAsuntoById(usuario, infoRegistro.getCodigoAsunto());
            if (asunto != null) {
                infoRegistro.setCodigoAsunto(asunto.getCodigo());
                infoRegistro.setDescripcionAsunto(asunto.getDescripcion());
            }
        }
        List<InfoRegistroInteresadoVO> interesados = this.getInteresados(infoRegistro);
        infoRegistro.setInteresados(interesados);
        TipoTransporteIntercambioRegistralVO t = this.tipoTransporteIntercambioRegistralManager.getTipoTransporteByDesc(infoRegistro.getTipoTransporte());
        if (t != null) {
            infoRegistro.setCodigoTransporteSIR(t.getCodigoSIR());
        }
        this.validateInfoRegistroForIR(infoRegistro);
        return infoRegistro;
    }

    private void validateInfoRegistroForIR(InfoRegistroVO infoRegistro) {
        TipoTransporteEnum tipoTransporteEnum;
        String numeroTransporte = infoRegistro.getNumeroTransporte();
        if (numeroTransporte != null && numeroTransporte.length() > 20) {
            throw new IntercambioRegistralException("La longitud del campo n\u00famero de transporte debe de ser menor de 20 caracteres.", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_LENGTH_NUMERO_TRANSPORTE);
        }
        String tipoTransporte = infoRegistro.getCodigoTransporteSIR();
        if (tipoTransporte != null && (tipoTransporteEnum = TipoTransporteEnum.getTipoTransporte((String)tipoTransporte)) == null) {
            throw new IntercambioRegistralException("El valor del campo Tipo de transporte debe de estar comprendido entre los valores definidos en la clase TipoTransporteEnum ", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_TIPO_TRANSPORTE);
        }
    }

    private List<InfoRegistroInteresadoVO> getInteresados(InfoRegistroVO infoRegistroAxsf) {
        List<InfoRegistroInteresadoVO> listaInteresados = this.getInfoRegistroDAO().getInfoRegistroInteresados(infoRegistroAxsf);
        for (InfoRegistroInteresadoVO interesado : listaInteresados) {
            boolean direccionInteresadoValida = false;
            boolean direccionRepresentanteValida = false;
            InfoRegistroPersonaFisicaOJuridicaVO persona = null;
            persona = this.getInfoPersonaORepresentante(interesado);
            if (persona == null) continue;
            interesado.setInfoPersona(persona);
            try {
                InfoRegistroDireccionVO direccion = this.getInfoRegistroDAO().getInfoRegistroDireccion(interesado);
                InfoRegistroRepresentanteVO representante = this.getInfoRegistroDAO().getInfoRegistroRepresentante(interesado);
                if (direccion == null && representante == null) {
                    throw new IntercambioRegistralException("El interesado o el representante debe de tener al menos una direcci\u00f3n registrada", IntercambioRegistralExceptionCodes.ERROR_CODE_VALIDACION_INTERESADOS_DIRECCION);
                }
                direccionInteresadoValida = this.validarDireccion(direccion);
                interesado.setDireccion(direccion);
                if (representante != null) {
                    interesado.setInfoRepresentante(this.getRepresentanteByInteresado(interesado));
                    InfoRegistroInteresadoVO interesadoRepresentante = new InfoRegistroInteresadoVO();
                    interesadoRepresentante.setIdDireccion(representante.getIdAddress());
                    InfoRegistroDireccionVO direccionRepresentante = this.getInfoRegistroDAO().getInfoRegistroDireccion(interesadoRepresentante);
                    if (direccionRepresentante != null) {
                        direccionRepresentanteValida = this.validarDireccion(direccionRepresentante);
                    }
                }
                if (direccionRepresentanteValida || direccionInteresadoValida) continue;
                if (!direccionInteresadoValida) {
                    throw new IntercambioRegistralException("Es obligatorio introducir una direccion telematica o que la localidad o provincia de alguno de los interesados tenga un equivalente en el Directorio Comun.", IntercambioRegistralExceptionCodes.ERROR_CODE_PROVINCIA_LOCALIDAD_NO_MAPEADAS);
                }
                throw new IntercambioRegistralException("La localidad o provincia de alguno del representante no tiene equivalente en el Directorio Comun.", IntercambioRegistralExceptionCodes.ERROR_CODE_PROVINCIA_LOCALIDAD_REPRESENTANTES_NO_MAPEADAS);
            }
            catch (IntercambioRegistralException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IntercambioRegistralException("Excepcion. La localidad o provincia de alguno de los interesados no tiene equivalente en el Directorio Comun.", IntercambioRegistralExceptionCodes.ERROR_CODE_PROVINCIA_LOCALIDAD_NO_MAPEADAS);
            }
        }
        return listaInteresados;
    }

    private boolean validarDireccion(InfoRegistroDireccionVO direccion) {
        if (direccion == null) {
            return false;
        }
        InfoRegistroDireccionTelematicaInteresadoVO direccionTelematica = this.getInfoRegistroDAO().getInfoRegistroDireccionTelematicaInteresado(direccion);
        direccion.setDireccionTelematicaInteresado(direccionTelematica);
        InfoRegistroDomicilioInteresadoVO domicilio = this.getInfoRegistroDAO().getInfoRegistroDomicilioInteresado(direccion);
        direccion.setDomicilioInteresado(domicilio);
        if (this.isDireccionTelematicaValida(direccionTelematica) || this.isDomicilioValido(domicilio)) {
            return true;
        }
        return false;
    }

    private boolean isDomicilioValido(InfoRegistroDomicilioInteresadoVO domicilio) {
        if (domicilio == null) {
            return false;
        }
        if (domicilio != null && StringUtils.isNotBlank((String)domicilio.getCiudad()) && StringUtils.isNotBlank((String)domicilio.getDireccion()) && StringUtils.isNotBlank((String)domicilio.getPais()) && StringUtils.isNotBlank((String)domicilio.getProvincia())) {
            return true;
        }
        if (StringUtils.isNotBlank((String)domicilio.getDireccion()) && StringUtils.isNotBlank((String)domicilio.getPais()) && !this.isCodigoPaisInstalacion(domicilio.getPais())) {
            return true;
        }
        return false;
    }

    private boolean isCodigoPaisInstalacion(String codigoPais) {
        boolean isCodigoPaisInstalacion = true;
        IntercambioRegistralConfiguration intercambioRegistralConfiguration = IntercambioRegistralConfiguration.getInstance();
        String codigoPaisInstalacion = intercambioRegistralConfiguration.getCountryCode();
        try {
            Integer codigoPaisInstalacionNumber = Integer.valueOf(codigoPaisInstalacion);
            Integer codigoPaisNumber = Integer.valueOf(codigoPais);
            if (codigoPaisNumber.intValue() == codigoPaisInstalacionNumber.intValue()) {
                return true;
            }
        }
        catch (Exception e) {
            logger.error((Object)"Ha ocurrido un error al intentar obtener el valor numerico de un codigo de pais.", (Throwable)e);
        }
        isCodigoPaisInstalacion = codigoPaisInstalacion.equalsIgnoreCase(codigoPais);
        return isCodigoPaisInstalacion;
    }

    private boolean isDireccionTelematicaValida(InfoRegistroDireccionTelematicaInteresadoVO direccionTelematica) {
        if (direccionTelematica == null || StringUtils.isBlank((String)direccionTelematica.getDireccion())) {
            return false;
        }
        return true;
    }

    private InfoRegistroPersonaFisicaOJuridicaVO getRepresentanteByInteresado(InfoRegistroInteresadoVO interesado) {
        InfoRegistroRepresentanteVO infoRepresentante = this.getInfoRegistroDAO().getInfoRegistroRepresentante(interesado);
        InfoRegistroPersonaFisicaOJuridicaVO result = null;
        if (infoRepresentante != null) {
            InfoRegistroInteresadoVO auxInfoRegistroInteresado = new InfoRegistroInteresadoVO();
            auxInfoRegistroInteresado.setIdPersona(infoRepresentante.getIdRepresentante());
            result = this.getInfoPersonaORepresentante(auxInfoRegistroInteresado);
            auxInfoRegistroInteresado.setIdDireccion(infoRepresentante.getIdAddress());
            InfoRegistroDireccionVO direccionRepresentante = this.getInfoRegistroDAO().getInfoRegistroDireccion(auxInfoRegistroInteresado);
            if (direccionRepresentante != null) {
                InfoRegistroDireccionTelematicaInteresadoVO direccionTelematica = this.getInfoRegistroDAO().getInfoRegistroDireccionTelematicaInteresado(direccionRepresentante);
                direccionRepresentante.setDireccionTelematicaInteresado(direccionTelematica);
                try {
                    InfoRegistroDomicilioInteresadoVO domicilio = this.getInfoRegistroDAO().getInfoRegistroDomicilioInteresado(direccionRepresentante);
                    direccionRepresentante.setDomicilioInteresado(domicilio);
                }
                catch (Exception e) {
                    throw new IntercambioRegistralException("La localidad o provincia de alguno del representante no tiene equivalente en el Directorio Comun.", IntercambioRegistralExceptionCodes.ERROR_CODE_PROVINCIA_LOCALIDAD_REPRESENTANTES_NO_MAPEADAS);
                }
                interesado.setDireccionRepresentante(direccionRepresentante);
            }
        }
        return result;
    }

    private InfoRegistroPersonaFisicaOJuridicaVO getInfoPersonaORepresentante(InfoRegistroInteresadoVO infoRegistroInteresado) {
        InfoRegistroPersonaFisicaOJuridicaVO result = null;
        result = this.getInfoRegistroDAO().getInfoRegistroPersonaFisica(infoRegistroInteresado);
        if (result == null) {
            result = this.getInfoRegistroDAO().getInfoRegistroPersonaJuridica(infoRegistroInteresado);
        }
        return result;
    }

    public InfoRegistroDAO getInfoRegistroDAO() {
        return this.infoRegistroDAO;
    }

    public void setInfoRegistroDAO(InfoRegistroDAO infoRegistroDAO) {
        this.infoRegistroDAO = infoRegistroDAO;
    }

    public ConfiguracionIntercambioRegistralDAO getConfiguracionIntercambioRegistralDAO() {
        return this.configuracionIntercambioRegistralDAO;
    }

    public void setConfiguracionIntercambioRegistralDAO(ConfiguracionIntercambioRegistralDAO configuracionIntercambioRegistralDAO) {
        this.configuracionIntercambioRegistralDAO = configuracionIntercambioRegistralDAO;
    }

    public DocumentoElectronicoAnexoManager getDocumentoElectronicoAnexoManager() {
        return this.documentoElectronicoAnexoManager;
    }

    public void setDocumentoElectronicoAnexoManager(DocumentoElectronicoAnexoManager documentoElectronicoAnexoManager) {
        this.documentoElectronicoAnexoManager = documentoElectronicoAnexoManager;
    }

    public TipoAsuntoManager getTipoAsuntoManager() {
        return this.tipoAsuntoManager;
    }

    public void setTipoAsuntoManager(TipoAsuntoManager tipoAsuntoManager) {
        this.tipoAsuntoManager = tipoAsuntoManager;
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
}
