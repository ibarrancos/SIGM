package es.ieci.tecdoc.isicres.api.compulsa.business.manager.impl;

import com.ieci.tecdoc.common.conf.IsicresContextServletPath;
import com.ieci.tecdoc.common.entity.dao.DBEntityDAOFactory;
import com.ieci.tecdoc.common.utils.Configurator;
import es.ieci.tecdoc.fwktd.core.spring.configuration.jdbc.datasource.MultiEntityContextHolder;
import es.ieci.tecdoc.isicres.api.business.dao.RegistroDAO;
import es.ieci.tecdoc.isicres.api.business.manager.RegistroManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ContextoAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.DocumentoFisicoVO;
import es.ieci.tecdoc.isicres.api.business.vo.DocumentoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.IdentificadorRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.PaginaDocumentoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.compulsa.business.manager.CompulsaManager;
import es.ieci.tecdoc.isicres.api.compulsa.business.vo.ConfiguracionCompulsaVO;
import es.ieci.tecdoc.isicres.api.compulsa.business.vo.DatosEspecificosCompulsaDefaultVO;
import es.ieci.tecdoc.isicres.api.compulsa.business.vo.DatosEspecificosCompulsaVO;
import es.ieci.tecdoc.isicres.api.compulsa.business.vo.DocumentoCompulsarVO;
import es.ieci.tecdoc.isicres.api.compulsa.business.vo.DocumentoFisicoCompulsaVO;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.business.manager.impl.CompulsaJustificanteManager;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.business.manager.impl.helper.CompulsaJustificanteLocatorHelper;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.business.vo.ISicresCompulsaJustificanteVO;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.exception.ISicresCompulsaJustificanteException;
import es.ieci.tecdoc.isicres.api.contextholder.IsicresContextHolder;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.manager.DocumentoElectronicoAnexoManager;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.ConfiguracionCreateDocumentoElectronicoAnexoVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.DocumentoElectronicoAnexoContenidoVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.DocumentoElectronicoAnexoDatosFirmaVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.DocumentoElectronicoAnexoVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.IdentificadorDocumentoElectronicoAnexoVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.TipoDocumentoAnexoEnumVO;
import es.ieci.tecdoc.isicres.api.documento.electronico.business.vo.TipoValidezDocumentoAnexoEnumVO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class CompulsaManagerImpl
implements CompulsaManager {
    private static final Logger logger = Logger.getLogger((Class)CompulsaManagerImpl.class);
    protected DocumentoElectronicoAnexoManager documentoElectronicoAnexoManager;
    protected RegistroManager registroManager;
    protected CompulsaJustificanteManager compulsaJustificanteManager;
    protected RegistroDAO registroDAO;
    public static final String DEFAULT_CLASIFICADOR = "Documentos Compulsados";
    public static final String RESOURCEBUNDLE_NAME = "Isicres-Api/ISicres-Compulsa-resources";
    public static final String CLASIFICADOR_KEY = "compulsa.documento.clasificador";
    private static final String COMA = ",";
    private static final String IGUAL = "=";
    private static final String CN = "CN=";

    public void compulsar(DocumentoCompulsarVO documentoCompulsar, ConfiguracionCompulsaVO configuracion) {
        DocumentoElectronicoAnexoVO documentoElectronicoAnexo = null;
        UsuarioVO usuarioRegistro = IsicresContextHolder.getContextoAplicacion().getUsuarioActual();
        IdentificadorRegistroVO idRegistro = this.populateIdentificadorRegistroVO(documentoCompulsar);
        documentoElectronicoAnexo = new DocumentoElectronicoAnexoVO();
        this.populateDocumentoElectronicoAnexoVO(documentoCompulsar, configuracion, documentoElectronicoAnexo);
        ConfiguracionCreateDocumentoElectronicoAnexoVO cfg = new ConfiguracionCreateDocumentoElectronicoAnexoVO();
        String clasificador = this.generateClasificador(idRegistro, configuracion, usuarioRegistro);
        cfg.setClasificador(clasificador);
        this.documentoElectronicoAnexoManager.create(documentoElectronicoAnexo, cfg);
        ISicresCompulsaJustificanteVO compulsaJustificanteVO = this.generateJustificante(documentoCompulsar, configuracion);
        byte[] justificanteByteArray = ((ByteArrayOutputStream)compulsaJustificanteVO.getPdfCompulsaStream()).toByteArray();
        DocumentoRegistroVO documentoRegistroJustificante = new DocumentoRegistroVO();
        this.populateDocumentoRegistroVO(documentoElectronicoAnexo, cfg, documentoRegistroJustificante, justificanteByteArray);
        documentoRegistroJustificante = this.registroManager.attachDocument(idRegistro, documentoRegistroJustificante, usuarioRegistro);
        int idPagina = Integer.parseInt(((PaginaDocumentoRegistroVO)documentoRegistroJustificante.getPaginas().get(2)).getId());
        this.saveLocator(compulsaJustificanteVO, idRegistro, idPagina);
    }

    protected void saveLocator(ISicresCompulsaJustificanteVO compulsaJustificanteVO, IdentificadorRegistroVO idRegistro, int idPagina) {
        String entidad = MultiEntityContextHolder.getEntity();
        String localizador = compulsaJustificanteVO.getLocator();
        try {
            DBEntityDAOFactory.getCurrentDBEntityDAO().insertScrDocLocator(Integer.parseInt(idRegistro.getIdLibro()), Integer.parseInt(idRegistro.getIdRegistro()), idPagina, localizador, entidad);
        }
        catch (Exception e) {
            CompulsaManagerImpl.getLogger().error((Object)("Error en el proceso de almacenamiento del localizador del registro:" + idRegistro.toString()), (Throwable)e);
            throw new ISicresCompulsaJustificanteException(e);
        }
    }

    protected String generateClasificador(IdentificadorRegistroVO idRegistro, ConfiguracionCompulsaVO configuracion, UsuarioVO usuarioRegistro) {
        String result = null;
        Locale locale = usuarioRegistro.getConfiguracionUsuario().getLocale();
        result = ResourceBundle.getBundle("Isicres-Api/ISicres-Compulsa-resources", locale).getString("compulsa.documento.clasificador");
        if (result == null) {
            result = "Documentos Compulsados";
        }
        boolean existsName = this.registroManager.existDocumentByName(idRegistro, result, usuarioRegistro);
        int i = 1;
        String sufijo = "";
        while (existsName) {
            sufijo = "_" + String.format("%03d", i);
            existsName = this.registroManager.existDocumentByName(idRegistro, result + sufijo, usuarioRegistro);
            ++i;
        }
        result = result + sufijo;
        return result;
    }

    protected IdentificadorRegistroVO populateIdentificadorRegistroVO(DocumentoCompulsarVO documento) {
        IdentificadorRegistroVO result = new IdentificadorRegistroVO();
        result.setIdLibro(documento.getIdentificadorRegistro().getIdLibro().toString());
        result.setIdRegistro(documento.getIdentificadorRegistro().getIdRegistro().toString());
        return result;
    }

    protected IdentificadorRegistroVO populateIdentificadorRegistroVO(DocumentoElectronicoAnexoVO documento) {
        IdentificadorRegistroVO result = new IdentificadorRegistroVO();
        result.setIdLibro(documento.getId().getIdLibro().toString());
        result.setIdRegistro(documento.getId().getIdRegistro().toString());
        return result;
    }

    protected void populateDocumentoRegistroVO(DocumentoElectronicoAnexoVO documento, ConfiguracionCreateDocumentoElectronicoAnexoVO cfg, DocumentoRegistroVO documentoRegistro, byte[] justificante) {
        IdentificadorRegistroVO idRegistro = this.populateIdentificadorRegistroVO(documento);
        documentoRegistro.setIdRegistro(idRegistro);
        documentoRegistro.setName(cfg.getClasificador());
        ArrayList<PaginaDocumentoRegistroVO> paginas = new ArrayList<PaginaDocumentoRegistroVO>();
        PaginaDocumentoRegistroVO pagina = new PaginaDocumentoRegistroVO();
        String extension = FilenameUtils.getExtension((String)documento.getName());
        String name = FilenameUtils.getBaseName((String)documento.getName());
        String path = FilenameUtils.getFullPath((String)documento.getName());
        String namePagina = path + name + "_LOC." + extension;
        pagina.setName(namePagina);
        DocumentoFisicoVO documentoFisico = new DocumentoFisicoVO();
        documentoFisico.setContent(justificante);
        documentoFisico.setExtension(extension);
        documentoFisico.setExtension(extension);
        pagina.setDocumentoFisico(documentoFisico);
        paginas.add(pagina);
        documentoRegistro.setPaginas(paginas);
    }

    protected void populateDocumentoElectronicoAnexoVO(DocumentoCompulsarVO documentoCompulsar, ConfiguracionCompulsaVO configuracion, DocumentoElectronicoAnexoVO documentoElectronicoAnexo) {
        IdentificadorDocumentoElectronicoAnexoVO id = new IdentificadorDocumentoElectronicoAnexoVO();
        id.setIdLibro(Long.parseLong(documentoCompulsar.getIdentificadorRegistro().getIdLibro().toString()));
        id.setIdRegistro(Long.parseLong(documentoCompulsar.getIdentificadorRegistro().getIdRegistro()));
        documentoElectronicoAnexo.setId(id);
        String codeName = "codeName" + documentoCompulsar.getDocumentoOriginal().getName();
        codeName = StringUtils.abbreviate((String)codeName, (int)21);
        documentoElectronicoAnexo.setCodeName(codeName);
        String extension = documentoCompulsar.getDocumentoOriginal().getExtension();
        documentoElectronicoAnexo.setExtension(extension);
        DocumentoElectronicoAnexoContenidoVO contenido = new DocumentoElectronicoAnexoContenidoVO();
        contenido.setContent(documentoCompulsar.getDocumentoOriginal().getContent());
        contenido.setDocUID(documentoCompulsar.getDocumentoOriginal().getLocation());
        documentoElectronicoAnexo.setContenido(contenido);
        DatosEspecificosCompulsaDefaultVO datosEspecificosDocumento = (DatosEspecificosCompulsaDefaultVO)documentoCompulsar.getDatosEspecificos();
        DocumentoElectronicoAnexoDatosFirmaVO datosFirmaDocumento = new DocumentoElectronicoAnexoDatosFirmaVO();
        String hashDocumento = documentoCompulsar.getDocumentoOriginal().getHash();
        datosFirmaDocumento.setHash(hashDocumento);
        String hashAlgDocumento = documentoCompulsar.getDocumentoOriginal().getHashAlg();
        datosFirmaDocumento.setHashAlg(hashAlgDocumento);
        documentoElectronicoAnexo.setDatosFirma(datosFirmaDocumento);
        String name = documentoCompulsar.getDocumentoOriginal().getName();
        documentoElectronicoAnexo.setName(name);
        documentoElectronicoAnexo.setTipoDocumentoAnexo(TipoDocumentoAnexoEnumVO.FICHERO_TECNICO);
        documentoElectronicoAnexo.setTipoValidez(TipoValidezDocumentoAnexoEnumVO.COPIA_COMPULSADA);
        ArrayList<DocumentoElectronicoAnexoVO> firmas = new ArrayList<DocumentoElectronicoAnexoVO>();
        DocumentoElectronicoAnexoVO firmaDocumento = new DocumentoElectronicoAnexoVO();
        IdentificadorDocumentoElectronicoAnexoVO idFima = new IdentificadorDocumentoElectronicoAnexoVO();
        idFima.setIdLibro(Long.parseLong(documentoCompulsar.getIdentificadorRegistro().getIdLibro().toString()));
        idFima.setIdRegistro(Long.parseLong(documentoCompulsar.getIdentificadorRegistro().getIdLibro()));
        firmaDocumento.setId(id);
        String codeNameFirma = "codeName" + documentoCompulsar.getFirma().getName();
        codeNameFirma = StringUtils.abbreviate((String)codeNameFirma, (int)21);
        firmaDocumento.setCodeName(codeNameFirma);
        DocumentoElectronicoAnexoContenidoVO contenidoFirma = new DocumentoElectronicoAnexoContenidoVO();
        contenidoFirma.setContent(documentoCompulsar.getFirma().getContent());
        contenidoFirma.setDocUID(documentoCompulsar.getFirma().getLocation());
        firmaDocumento.setContenido(contenidoFirma);
        String extensionFirma = documentoCompulsar.getFirma().getExtension();
        firmaDocumento.setExtension(extensionFirma);
        DatosEspecificosCompulsaDefaultVO datosEspecificos = (DatosEspecificosCompulsaDefaultVO)documentoCompulsar.getDatosEspecificos();
        DocumentoElectronicoAnexoDatosFirmaVO datosFirmaDocumentoFirma = new DocumentoElectronicoAnexoDatosFirmaVO();
        String hashDocumentoFirma = documentoCompulsar.getFirma().getHash();
        datosFirmaDocumentoFirma.setHash(hashDocumentoFirma);
        String hashAlgDocumentoFirma = documentoCompulsar.getFirma().getHashAlg();
        datosFirmaDocumentoFirma.setHashAlg(hashAlgDocumentoFirma);
        firmaDocumento.setDatosFirma(datosFirmaDocumentoFirma);
        String nameFirma = documentoCompulsar.getFirma().getName();
        firmaDocumento.setName(nameFirma);
        firmaDocumento.setTipoDocumentoAnexo(TipoDocumentoAnexoEnumVO.FICHERO_TECNICO);
        firmaDocumento.setTipoValidez(TipoValidezDocumentoAnexoEnumVO.ORIGINAL);
        String algFirma = datosEspecificosDocumento.getAlgoritmoFirma();
        datosFirmaDocumentoFirma.setAlgFirma(algFirma);
        String certificado = datosEspecificosDocumento.getCertificado();
        datosFirmaDocumentoFirma.setCertificado(certificado);
        String firma = datosEspecificosDocumento.getFirma();
        datosFirmaDocumentoFirma.setFirma(firma);
        String formatoFirma = datosEspecificosDocumento.getFormatoFirma();
        datosFirmaDocumentoFirma.setFormatoFirma(formatoFirma);
        firmaDocumento.setDatosFirma(datosFirmaDocumentoFirma);
        firmas.add(firmaDocumento);
        documentoElectronicoAnexo.setFirmas(firmas);
    }

    protected void populateCompulsaJustificanteVO(DocumentoCompulsarVO documentoCompulsar, ConfiguracionCompulsaVO configuracion, ISicresCompulsaJustificanteVO compulsaJustificanteVO) {
        ContextoAplicacionVO contextoAplicacion = IsicresContextHolder.getContextoAplicacion();
        String datosPath = null;
        datosPath = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/Compulsar/DatosCompulsaPath/@isRelative") ? IsicresContextServletPath.getRealPathFromWebContextResource((String)Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Compulsar/DatosCompulsaPath")) : Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Compulsar/DatosCompulsaPath");
        compulsaJustificanteVO.setDatosPath(datosPath);
        String fondoPath = null;
        fondoPath = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/Compulsar/FondoPath/@isRelative") ? IsicresContextServletPath.getRealPathFromWebContextResource((String)Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Compulsar/FondoPath")) : Configurator.getInstance().getProperty("/ISicres-Configuration/ISicres-DesktopWeb/Compulsar/FondoPath");
        compulsaJustificanteVO.setFondoPath(fondoPath);
        DatosEspecificosCompulsaDefaultVO datosEspecificos = (DatosEspecificosCompulsaDefaultVO)documentoCompulsar.getDatosEspecificos();
        compulsaJustificanteVO.setCNcertificado(this.getNombreFirmante(datosEspecificos.getUsuario()));
        String entidad = contextoAplicacion.getUsuarioActual().getConfiguracionUsuario().getIdEntidad();
        compulsaJustificanteVO.setEntidad(entidad);
        compulsaJustificanteVO.setFechaCompulsa(new Date());
        BaseRegistroVO registro = contextoAplicacion.getRegistroActual();
        compulsaJustificanteVO.setRegistro(registro);
        String numRegistro = this.registroDAO.getNumRegistroById(registro.getId());
        compulsaJustificanteVO.setNumeroRegistro(numRegistro);
        CompulsaJustificanteLocatorHelper.getInstance().setLocator(compulsaJustificanteVO);
        Locale locale = contextoAplicacion.getUsuarioActual().getConfiguracionUsuario().getLocale();
        compulsaJustificanteVO.setLocale(locale);
        ByteArrayInputStream pdfOriginalStream = new ByteArrayInputStream(documentoCompulsar.getDocumentoOriginal().getContent());
        compulsaJustificanteVO.setPdfOriginalStream(pdfOriginalStream);
        ByteArrayOutputStream pdfCompulsaStream = new ByteArrayOutputStream();
        compulsaJustificanteVO.setPdfCompulsaStream(pdfCompulsaStream);
    }

    protected String getNumRegistro(IdentificadorRegistroVO id) {
        String result = this.registroDAO.getNumRegistroById(id);
        return result;
    }

    protected String getNombreFirmante(String cadenaDatosFirmante) {
        String result = cadenaDatosFirmante;
        if (result.substring(result.indexOf("CN=") + 3) != null) {
            if ((result = result.substring(result.indexOf("CN=") + 3)).indexOf("=") != -1) {
                result = result.substring(0, result.indexOf("="));
                result = result.substring(0, result.lastIndexOf(","));
            }
        } else {
            result = " ";
        }
        return result;
    }

    protected ISicresCompulsaJustificanteVO generateJustificante(DocumentoCompulsarVO documentoCompulsar, ConfiguracionCompulsaVO configuracion) {
        Object result = null;
        ISicresCompulsaJustificanteVO compulsaJustificanteVO = new ISicresCompulsaJustificanteVO();
        this.populateCompulsaJustificanteVO(documentoCompulsar, configuracion, compulsaJustificanteVO);
        this.compulsaJustificanteManager.generateJustificante(compulsaJustificanteVO);
        return compulsaJustificanteVO;
    }

    public RegistroManager getRegistroManager() {
        return this.registroManager;
    }

    public void setRegistroManager(RegistroManager registroManager) {
        this.registroManager = registroManager;
    }

    public DocumentoElectronicoAnexoManager getDocumentoElectronicoAnexoManager() {
        return this.documentoElectronicoAnexoManager;
    }

    public void setDocumentoElectronicoAnexoManager(DocumentoElectronicoAnexoManager documentoElectronicoAnexoManager) {
        this.documentoElectronicoAnexoManager = documentoElectronicoAnexoManager;
    }

    public RegistroDAO getRegistroDAO() {
        return this.registroDAO;
    }

    public void setRegistroDAO(RegistroDAO registroDAO) {
        this.registroDAO = registroDAO;
    }

    public CompulsaJustificanteManager getCompulsaJustificanteManager() {
        return this.compulsaJustificanteManager;
    }

    public void setCompulsaJustificanteManager(CompulsaJustificanteManager compulsaJustificanteManager) {
        this.compulsaJustificanteManager = compulsaJustificanteManager;
    }

    public static Logger getLogger() {
        return logger;
    }
}
