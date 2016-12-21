package es.ieci.tecdoc.isicres.api.business.manager.impl;

import com.ieci.tecdoc.common.AuthenticationUser;
import com.ieci.tecdoc.common.conf.BookConf;
import com.ieci.tecdoc.common.conf.BookTypeConf;
import com.ieci.tecdoc.common.entity.AxDochEntity;
import com.ieci.tecdoc.common.exception.BookException;
import com.ieci.tecdoc.common.exception.SessionException;
import com.ieci.tecdoc.common.exception.ValidationException;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.isicres.AxDoch;
import com.ieci.tecdoc.common.isicres.AxPageh;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.common.isicres.AxSfIn;
import com.ieci.tecdoc.common.isicres.AxSfOut;
import com.ieci.tecdoc.common.isicres.AxSfQuery;
import com.ieci.tecdoc.common.isicres.AxSfQueryResults;
import com.ieci.tecdoc.common.utils.Repository;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrField;
import com.ieci.tecdoc.idoc.flushfdr.FlushFdrInter;
import com.ieci.tecdoc.isicres.session.folder.FolderFileSession;
import com.ieci.tecdoc.isicres.session.folder.FolderSession;
import com.ieci.tecdoc.isicres.session.security.SecuritySession;
import com.ieci.tecdoc.isicres.session.utils.UtilsSession;
import com.ieci.tecdoc.isicres.usecase.UseCaseConf;
import com.ieci.tecdoc.isicres.usecase.book.BookUseCase;
import com.ieci.tecdoc.utils.HibernateUtil;
import com.ieci.tecdoc.utils.cache.CacheBag;
import com.ieci.tecdoc.utils.cache.CacheFactory;
import es.ieci.tecdoc.isicres.api.business.exception.RegistroException;
import es.ieci.tecdoc.isicres.api.business.helper.DocumentosHelper;
import es.ieci.tecdoc.isicres.api.business.helper.DocumentosValidacionHelper;
import es.ieci.tecdoc.isicres.api.business.helper.LibroHelper;
import es.ieci.tecdoc.isicres.api.business.helper.RegistroHelper;
import es.ieci.tecdoc.isicres.api.business.helper.RegistroValidacionHelper;
import es.ieci.tecdoc.isicres.api.business.helper.TercerosHelper;
import es.ieci.tecdoc.isicres.api.business.manager.DistribucionManager;
import es.ieci.tecdoc.isicres.api.business.manager.LibroManager;
import es.ieci.tecdoc.isicres.api.business.manager.PermisosManager;
import es.ieci.tecdoc.isicres.api.business.manager.RegistroManager;
import es.ieci.tecdoc.isicres.api.business.manager.UnidadAdministrativaManager;
import es.ieci.tecdoc.isicres.api.business.manager.impl.builder.BaseRegistroVOBuilder;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.ListOfAxDochToListOfDocumentoRegistroVOMapper;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.ListOfCampoGenericoRegistroVOToListOfFlushFdrFieldMapper;
import es.ieci.tecdoc.isicres.api.business.vo.BaseLibroVO;
import es.ieci.tecdoc.isicres.api.business.vo.BaseRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.CampoGenericoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.CriterioBusquedaDistribucionVO;
import es.ieci.tecdoc.isicres.api.business.vo.CriterioBusquedaRegistroSqlVO;
import es.ieci.tecdoc.isicres.api.business.vo.DistribucionVO;
import es.ieci.tecdoc.isicres.api.business.vo.DocumentoRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.IdentificadorRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.LibroEntradaVO;
import es.ieci.tecdoc.isicres.api.business.vo.LibroSalidaVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosLibroVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroEntradaExternoVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroEntradaVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroOriginalVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroSalidaExternoVO;
import es.ieci.tecdoc.isicres.api.business.vo.RegistroSalidaVO;
import es.ieci.tecdoc.isicres.api.business.vo.ResultadoBusquedaDistribucionVO;
import es.ieci.tecdoc.isicres.api.business.vo.ResultadoBusquedaRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.UnidadAdministrativaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.enums.EstadoDistribucionEnum;
import es.ieci.tecdoc.isicres.api.business.vo.enums.EstadoRegistroEnum;
import es.ieci.tecdoc.isicres.api.business.vo.enums.TipoLibroEnum;
import gnu.trove.THashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

public class RegistroManagerImpl
extends RegistroManager {
    private static final Logger logger = Logger.getLogger((Class)RegistroManagerImpl.class);
    protected LibroManager libroManager;
    protected PermisosManager permisosManager;
    protected UnidadAdministrativaManager unidadAdministrativaManager;
    protected DistribucionManager distribucionManager;
    protected BaseRegistroVOBuilder baseRegistroVOBuilder;

    @Override
    public final RegistroEntradaVO createRegistroEntrada(UsuarioVO usuario, RegistroEntradaVO registro) {
        LibroEntradaVO libroEntrada = (LibroEntradaVO)LibroHelper.obtenerDatosBasicosLibro(registro, new LibroEntradaVO());
        libroEntrada = (LibroEntradaVO)this.libroManager.abrirLibro(usuario, libroEntrada, TipoLibroEnum.ENTRADA);
        Boolean canCreateRegistro = this.canCreateRegistro(usuario, registro);
        if (canCreateRegistro.booleanValue()) {
            registro.getOficinaRegistro().setCodigoOficina(usuario.getConfiguracionUsuario().getOficina().getCodigoOficina());
            registro.getOficinaRegistro().setId(usuario.getConfiguracionUsuario().getOficina().getId());
            List listaErrores = RegistroValidacionHelper.validateRegistroEntradaCreate(usuario, registro);
            if (!(listaErrores == null || listaErrores.isEmpty())) {
                String mensaje = RegistroValidacionHelper.getMensajeErrorListaCampos(listaErrores);
                logger.warn((Object)("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje));
                throw new RegistroException("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje);
            }
            DocumentosValidacionHelper.validateDocuments(registro.getDocumentos());
            Map translatedIds = RegistroHelper.obtenerCamposValidadosRegistroEntrada(usuario, registro);
            AxSf axSfNew = RegistroHelper.completarRegistroAxSf((AxSf)new AxSfIn(), usuario, registro, translatedIds);
            try {
                this.preCreateRegistroEntrada(usuario, registro);
                registro = (RegistroEntradaVO)RegistroHelper.createRegistro(usuario, registro, axSfNew);
            }
            catch (Exception e) {
                this.createRegistroEntradaCatchException(usuario, registro, e);
            }
            finally {
                this.postCreateRegistroEntrada(usuario, registro);
            }
        }
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no tiene permisos de creaci\u00f3n para el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        //throw new RegistroException(sb.toString());
        return registro;
    }

    public void postCreateRegistroEntrada(UsuarioVO usuario, RegistroEntradaVO registro) {
    }

    public void createRegistroEntradaCatchException(UsuarioVO usuario, RegistroEntradaVO registro, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido crear el registro en el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preCreateRegistroEntrada(UsuarioVO usuario, RegistroEntradaVO registro) {
    }

    @Override
    public final RegistroSalidaVO createRegistroSalida(UsuarioVO usuario, RegistroSalidaVO registro) {
        LibroSalidaVO libroSalida = (LibroSalidaVO)LibroHelper.obtenerDatosBasicosLibro(registro, new LibroSalidaVO());
        libroSalida = (LibroSalidaVO)this.libroManager.abrirLibro(usuario, libroSalida, TipoLibroEnum.SALIDA);
        Boolean canCreateRegistro = this.canCreateRegistro(usuario, registro);
        if (canCreateRegistro.booleanValue()) {
            registro.getOficinaRegistro().setCodigoOficina(usuario.getConfiguracionUsuario().getOficina().getCodigoOficina());
            registro.getOficinaRegistro().setId(usuario.getConfiguracionUsuario().getOficina().getId());
            List listaErrores = RegistroValidacionHelper.validateRegistroSalidaCreate(usuario, registro);
            if (!(listaErrores == null || listaErrores.isEmpty())) {
                String mensaje = RegistroValidacionHelper.getMensajeErrorListaCampos(listaErrores);
                logger.warn((Object)("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje));
                throw new RegistroException("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje);
            }
            DocumentosValidacionHelper.validateDocuments(registro.getDocumentos());
            Map translatedIds = RegistroHelper.obtenerCamposValidadosRegistroSalida(usuario, registro);
            AxSf axSfNew = RegistroHelper.completarRegistroAxSf((AxSf)new AxSfOut(), usuario, registro, translatedIds);
            try {
                this.preCreateRegistroSalida(usuario, registro);
                registro = (RegistroSalidaVO)RegistroHelper.createRegistro(usuario, registro, axSfNew);
            }
            catch (Exception e) {
                this.createRegistroSalidaCatchException(usuario, registro, e);
            }
            finally {
                this.postCreateRegistroSalida(usuario, registro);
            }
        }
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no tiene permisos de creaci\u00f3n para el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        //throw new RegistroException(sb.toString());
        return registro;
    }

    public void postCreateRegistroSalida(UsuarioVO usuario, RegistroSalidaVO registro) {
    }

    public void createRegistroSalidaCatchException(UsuarioVO usuario, RegistroSalidaVO registro, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido crear el registro en el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preCreateRegistroSalida(UsuarioVO usuario, RegistroSalidaVO registro) {
    }

    @Override
    public final RegistroEntradaVO importRegistroEntrada(UsuarioVO usuario, RegistroEntradaExternoVO registro) {
        LibroEntradaVO libroEntrada = (LibroEntradaVO)LibroHelper.obtenerDatosBasicosLibro(registro, new LibroEntradaVO());
        libroEntrada = (LibroEntradaVO)this.libroManager.abrirLibro(usuario, libroEntrada, TipoLibroEnum.ENTRADA);
        Boolean canCreateRegistro = this.canCreateRegistro(usuario, registro);
        if (canCreateRegistro.booleanValue()) {
            List listaErrores = RegistroValidacionHelper.validateRegistroEntradaImport(usuario, registro);
            if (!CollectionUtils.isEmpty((Collection)listaErrores)) {
                String mensaje = RegistroValidacionHelper.getMensajeErrorListaCampos(listaErrores);
                logger.warn((Object)("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje));
                throw new RegistroException("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje);
            }
            DocumentosValidacionHelper.validateDocuments(registro.getDocumentos());
            Map translatedIds = RegistroHelper.obtenerCamposValidadosRegistroEntrada(usuario, registro);
            AxSf axSfNew = RegistroHelper.completarRegistroAxSf((AxSf)new AxSfIn(), usuario, registro, translatedIds);
            try {
                this.preImportRegistroEntrada(usuario, registro);
                registro = (RegistroEntradaExternoVO)RegistroHelper.createRegistro(usuario, registro, axSfNew);
            }
            catch (Exception e) {
                this.importRegistroEntradaCatchException(usuario, registro, e);
            }
            finally {
                this.postImportRegistroEntrada(usuario, registro);
            }
        }
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no tiene permisos de creaci\u00f3n para el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        //throw new RegistroException(sb.toString());
        return registro;
    }

    public void postImportRegistroEntrada(UsuarioVO usuario, RegistroEntradaExternoVO registro) {
    }

    public void importRegistroEntradaCatchException(UsuarioVO usuario, RegistroEntradaExternoVO registro, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido importar el registro en el libro [").append(registro.getIdLibro()).append("]");
        logger.error((Object)sb.toString());
        throw new RegistroException(sb.toString());
    }

    public void preImportRegistroEntrada(UsuarioVO usuario, RegistroEntradaExternoVO registro) {
    }

    @Override
    public final RegistroSalidaVO importRegistroSalida(UsuarioVO usuario, RegistroSalidaExternoVO registro) {
        LibroEntradaVO libroEntrada = (LibroEntradaVO)LibroHelper.obtenerDatosBasicosLibro(registro, new LibroEntradaVO());
        libroEntrada = (LibroEntradaVO)this.libroManager.abrirLibro(usuario, libroEntrada, TipoLibroEnum.SALIDA);
        Boolean canCreateRegistro = this.canCreateRegistro(usuario, registro);
        if (canCreateRegistro.booleanValue()) {
            List listaErrores = RegistroValidacionHelper.validateRegistroSalidaImport(usuario, registro);
            if (!CollectionUtils.isEmpty((Collection)listaErrores)) {
                String mensaje = RegistroValidacionHelper.getMensajeErrorListaCampos(listaErrores);
                logger.error((Object)("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje));
                throw new RegistroException("Error de Validacion. Los siguientes campos no son correctos:\n" + mensaje);
            }
            DocumentosValidacionHelper.validateDocuments(registro.getDocumentos());
            Map translatedIds = RegistroHelper.obtenerCamposValidadosRegistroSalida(usuario, registro);
            AxSf axSfNew = RegistroHelper.completarRegistroAxSf((AxSf)new AxSfOut(), usuario, registro, translatedIds);
            try {
                this.preImportRegistroSalida(usuario, registro);
                registro = (RegistroSalidaExternoVO)RegistroHelper.createRegistro(usuario, registro, axSfNew);
            }
            catch (Exception e) {
                this.importRegistroSalidaCatchException(usuario, registro, e);
            }
            finally {
                this.postImportRegistroSalida(usuario, registro);
            }
        }
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no tiene permisos de creaci\u00f3n para el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        //throw new RegistroException(sb.toString());
        return registro;
    }

    public void postImportRegistroSalida(UsuarioVO usuario, RegistroSalidaExternoVO registro) {
    }

    public void importRegistroSalidaCatchException(UsuarioVO usuario, RegistroSalidaExternoVO registro, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido importar el registro en el libro [").append(registro.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preImportRegistroSalida(UsuarioVO usuario, RegistroSalidaExternoVO registro) {
    }

    @Override
    public final void cancelRegistroEntradaById(UsuarioVO usuario, IdentificadorRegistroVO id) {
        Assert.notNull((Object)this.findRegistroEntradaById(usuario, id), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de entrada."));
        try {
            this.preCancelRegistroEntrada(usuario, id);
            this.cancelRegistroById(usuario, id);
        }
        catch (Exception e) {
            this.cancelRegistroEntradaCatchException(usuario, id, e);
        }
        finally {
            this.postCancelRegistroEntrada(usuario, id);
        }
    }

    public void postCancelRegistroEntrada(UsuarioVO usuario, IdentificadorRegistroVO id) {
    }

    public void cancelRegistroEntradaCatchException(UsuarioVO usuario, IdentificadorRegistroVO id, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido cancelar el registro de entrada con id [").append(id.getIdRegistro()).append("] en el libro [").append(id.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preCancelRegistroEntrada(UsuarioVO usuario, IdentificadorRegistroVO id) {
    }

    @Override
    public final void cancelRegistroSalidaById(UsuarioVO usuario, IdentificadorRegistroVO id) {
        Assert.notNull((Object)this.findRegistroSalidaById(usuario, id), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de salida."));
        try {
            this.preCancelRegistroSalida(usuario, id);
            this.cancelRegistroById(usuario, id);
        }
        catch (Exception e) {
            this.cancelRegistroSalidaCatchException(usuario, id, e);
        }
        finally {
            this.postCancelRegistroSalida(usuario, id);
        }
    }

    public void postCancelRegistroSalida(UsuarioVO usuario, IdentificadorRegistroVO id) {
    }

    public void cancelRegistroSalidaCatchException(UsuarioVO usuario, IdentificadorRegistroVO id, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido cancelar el registro de salida con id [").append(id.getIdRegistro()).append("] en el libro [").append(id.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preCancelRegistroSalida(UsuarioVO usuario, IdentificadorRegistroVO id) {
    }

    @Override
    public ResultadoBusquedaRegistroVO findAllRegistroEntradaByCriterioWhereSql(UsuarioVO usuario, LibroEntradaVO libro, CriterioBusquedaRegistroSqlVO criterioBusqueda) {
        List<AxSfQueryResults> axSfQueryResults = this.queryForAxSfQueryResults(usuario, criterioBusqueda.getOffset().intValue(), criterioBusqueda.getLimit().intValue(), Integer.valueOf(libro.getId()), TipoLibroEnum.ENTRADA, criterioBusqueda.getSql());
        int total = this.queryForAxSfQueryResultsTotalCount(axSfQueryResults);
        ArrayList<RegistroEntradaVO> registers = new ArrayList<RegistroEntradaVO>();
        for (AxSfQueryResults results : axSfQueryResults) {
	    for (Iterator it03 = results.getResults().iterator(); it03.hasNext();) {
                AxSf axSf = (AxSf) it03.next();
                RegistroEntradaVO result = this.mapToRegistroEntradaVO(usuario, results.getBookId(), axSf);
                registers.add(result);
            }
        }
        return new ResultadoBusquedaRegistroVO(registers, total);
    }

    @Override
    public ResultadoBusquedaRegistroVO findAllRegistroEntradaForUserByCriterioWhereSql(UsuarioVO usuario, CriterioBusquedaRegistroSqlVO criterioBusqueda) {
        List<AxSfQueryResults> axSfQueryResults = this.queryForAxSfQueryResults(usuario, criterioBusqueda.getOffset().intValue(), criterioBusqueda.getLimit().intValue(), null, TipoLibroEnum.ENTRADA, criterioBusqueda.getSql(), true);
        int total = this.queryForAxSfQueryResultsTotalCount(axSfQueryResults);
        ArrayList<RegistroEntradaVO> registers = new ArrayList<RegistroEntradaVO>();
        for (AxSfQueryResults results : axSfQueryResults) {
	    for (Iterator it03 = results.getResults().iterator(); it03.hasNext();) {
                AxSf axSf = (AxSf) it03.next();
                RegistroEntradaVO result = this.mapToRegistroEntradaVO(usuario, results.getBookId(), axSf);
                registers.add(result);
            }
        }
        return new ResultadoBusquedaRegistroVO(registers, total);
    }

    private RegistroEntradaVO mapToRegistroEntradaVO(UsuarioVO usuario, int bookId, AxSf axSf) {
        RegistroEntradaVO result = new RegistroEntradaVO();
        BaseRegistroVO baseRegistroVO = this.getBaseRegistroVOBuilder().build(usuario, axSf, String.valueOf(bookId), new RegistroEntradaVO());
        BeanUtils.copyProperties((Object)baseRegistroVO, (Object)result);
        result.setReferenciaExpediente(axSf.getAttributeValueAsString("fld19"));
        RegistroOriginalVO registroOriginal = new RegistroOriginalVO();
        UnidadAdministrativaVO entidadRegistral = null;
        String idEntidadRegistral = axSf.getAttributeValueAsString("fld13");
        if (StringUtils.isNotBlank((String)idEntidadRegistral)) {
            entidadRegistral = this.getUnidadAdministrativaManager().findUnidadById(usuario.getConfiguracionUsuario().getLocale(), new Integer(idEntidadRegistral));
        }
        registroOriginal.setEntidadRegistral(entidadRegistral);
        registroOriginal.setFechaRegistroOriginal((Date)axSf.getAttributeValue("fld12"));
        registroOriginal.setNumeroRegistroOriginal(axSf.getAttributeValueAsString("fld10"));
        registroOriginal.setTipoRegistroOriginal(axSf.getAttributeValueAsString("fld11"));
        result.setRegistroOriginal(registroOriginal);
        return result;
    }

    @Override
    public RegistroEntradaVO findRegistroEntradaById(UsuarioVO usuario, IdentificadorRegistroVO id) {
        RegistroEntradaVO result = new RegistroEntradaVO();
        result.setIdLibro(id.getIdLibro());
        try {
            LibroEntradaVO libroEntrada = (LibroEntradaVO)LibroHelper.obtenerDatosBasicosLibro(result, new LibroEntradaVO());
            libroEntrada = (LibroEntradaVO)this.getLibroManager().abrirLibro(usuario, libroEntrada, TipoLibroEnum.ENTRADA);
            AxSf axSf = FolderSession.getBookFolder((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (Locale)usuario.getConfiguracionUsuario().getLocale(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            if (axSf != null) {
                result = this.mapToRegistroEntradaVO(usuario, Integer.valueOf(id.getIdLibro()), axSf);
            }
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Error al obtener el registro con id [").append(id.getIdRegistro()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException("Error al Obtener el registro de Entrada por Id: [" + id.getIdRegistro() + "] ", e);
        }
        return result;
    }

    @Override
    public ResultadoBusquedaRegistroVO findAllRegistroSalidaByCriterioWhereSql(UsuarioVO usuario, LibroSalidaVO libro, CriterioBusquedaRegistroSqlVO criterioBusqueda) {
        List<AxSfQueryResults> axSfQueryResults = this.queryForAxSfQueryResults(usuario, criterioBusqueda.getOffset().intValue(), criterioBusqueda.getLimit().intValue(), Integer.valueOf(libro.getId()), TipoLibroEnum.SALIDA, criterioBusqueda.getSql());
        int total = this.queryForAxSfQueryResultsTotalCount(axSfQueryResults);
        ArrayList<BaseRegistroVO> registers = new ArrayList<BaseRegistroVO>();
        for (AxSfQueryResults results : axSfQueryResults) {
	    for (Iterator it03 = results.getResults().iterator(); it03.hasNext();) {
                AxSf axSf = (AxSf) it03.next();
                registers.add(this.getBaseRegistroVOBuilder().build(usuario, axSf, String.valueOf(results.getBookId()), new RegistroSalidaVO()));
            }
        }
        return new ResultadoBusquedaRegistroVO(registers, total);
    }

    @Override
    public ResultadoBusquedaRegistroVO findAllRegistroSalidaForUserByCriterioWhereSql(UsuarioVO usuario, CriterioBusquedaRegistroSqlVO criterioBusqueda) {
        List<AxSfQueryResults> axSfQueryResults = this.queryForAxSfQueryResults(usuario, criterioBusqueda.getOffset().intValue(), criterioBusqueda.getLimit().intValue(), null, TipoLibroEnum.SALIDA, criterioBusqueda.getSql(), true);
        int total = this.queryForAxSfQueryResultsTotalCount(axSfQueryResults);
        ArrayList<BaseRegistroVO> registers = new ArrayList<BaseRegistroVO>();
        for (AxSfQueryResults results : axSfQueryResults) {
	    for (Iterator it03 = results.getResults().iterator(); it03.hasNext();) {
                AxSf axSf = (AxSf) it03.next();
                registers.add(this.getBaseRegistroVOBuilder().build(usuario, axSf, String.valueOf(results.getBookId()), new RegistroSalidaVO()));
            }
        }
        return new ResultadoBusquedaRegistroVO(registers, total);
    }

    @Override
    public RegistroSalidaVO findRegistroSalidaById(UsuarioVO usuario, IdentificadorRegistroVO id) {
        RegistroSalidaVO result = new RegistroSalidaVO();
        result.setIdLibro(id.getIdLibro());
        try {
            LibroSalidaVO libroSalida = (LibroSalidaVO)LibroHelper.obtenerDatosBasicosLibro(result, new LibroSalidaVO());
            libroSalida = (LibroSalidaVO)this.getLibroManager().abrirLibro(usuario, libroSalida, TipoLibroEnum.SALIDA);
            AxSf axSf = FolderSession.getBookFolder((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (Locale)usuario.getConfiguracionUsuario().getLocale(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            Assert.isTrue((boolean)(axSf instanceof AxSfOut), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de salida"));
            if (axSf != null) {
                BaseRegistroVO baseRegistroVO = this.getBaseRegistroVOBuilder().build(usuario, axSf, id.getIdLibro(), new RegistroSalidaVO());
                BeanUtils.copyProperties((Object)baseRegistroVO, (Object)result);
            }
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Error al obtener el registro con id [").append(id.getIdRegistro()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException("Error al Obtener el registro de Salida por Id: [" + id.getIdRegistro() + "] ", e);
        }
        return result;
    }

    @Override
    public void lockRegistro(UsuarioVO usuario, IdentificadorRegistroVO id) {
        Session session = null;
        Transaction tran = null;
        try {
            session = HibernateUtil.currentSession((String)usuario.getConfiguracionUsuario().getIdEntidad());
            tran = session.beginTransaction();
            CacheBag cacheBag = CacheFactory.getCacheInterface().getCacheEntry(usuario.getConfiguracionUsuario().getSessionID());
            ScrOfic scrOfic = (ScrOfic)cacheBag.get((Object)"com.ieci.tecdoc.common.invesicres.ScrOfic");
            AuthenticationUser user = (AuthenticationUser)cacheBag.get((Object)"com.ieci.tecdoc.common.invesdoc.Iuseruserhdr");
            if (!FolderSession.lock((Session)session, (Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (AuthenticationUser)user, (ScrOfic)scrOfic, (String)usuario.getConfiguracionUsuario().getIdEntidad())) {
                throw new RegistroException("No se ha podido bloquear el registro [" + id.getIdRegistro() + "]");
            }
            HibernateUtil.commitTransaction((Transaction)tran);
        }
        catch (Exception e) {
            HibernateUtil.rollbackTransaction((Transaction)tran);
            StringBuffer sb = new StringBuffer("No se ha podido bloquear el registro con id [").append(id.getIdRegistro()).append("] para el libro [").append(id.getIdLibro()).append("] para el usuario [").append(usuario.getLoginName()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException(sb.toString(), e);
        }
        finally {
            HibernateUtil.closeSession((String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
    }

    @Override
    public void unlockRegistro(UsuarioVO usuario, IdentificadorRegistroVO id) {
        AuthenticationUser au = new AuthenticationUser();
        au.setId(Integer.valueOf(usuario.getId()));
        Transaction tran = null;
        try {
            Session session = HibernateUtil.currentSession((String)usuario.getConfiguracionUsuario().getIdEntidad());
            tran = session.beginTransaction();
            FolderSession.unlock((Session)session, (Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (AuthenticationUser)au);
            HibernateUtil.commitTransaction((Transaction)tran);
        }
        catch (HibernateException e) {
            HibernateUtil.rollbackTransaction((Transaction)tran);
            StringBuffer sb = new StringBuffer("No se ha podido desbloquear el registro con id [").append(id.getIdRegistro()).append("] para el libro [").append(id.getIdLibro()).append("] para el usuario [").append(usuario.getLoginName()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException(sb.toString(), (Throwable)e);
        }
        finally {
            HibernateUtil.closeSession((String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
    }

    @Override
    public final void updateRegistroEntrada(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos) {
        Assert.notNull((Object)this.findRegistroEntradaById(usuario, id), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de entrada."));
        try {
            this.preUpdateRegistroEntrada(usuario, id, camposGenericos);
            this.updateRegistro(usuario, id, camposGenericos, false);
        }
        catch (Exception e) {
            this.updateRegistroEntradaCatchException(usuario, id, camposGenericos, e);
        }
        finally {
            this.postUpdateRegistroEntrada(usuario, id, camposGenericos);
        }
    }

    @Override
    public void updateRegistroEntradaIR(UsuarioVO usuario, IdentificadorRegistroVO id, List<CampoGenericoRegistroVO> camposGenericos) {
        Assert.notNull((Object)this.findRegistroEntradaById(usuario, id), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de entrada."));
        try {
            this.preUpdateRegistroEntrada(usuario, id, camposGenericos);
            this.updateRegistro(usuario, id, camposGenericos, true);
        }
        catch (Exception e) {
            this.updateRegistroEntradaCatchException(usuario, id, camposGenericos, e);
        }
        finally {
            this.postUpdateRegistroEntrada(usuario, id, camposGenericos);
        }
    }

    public void postUpdateRegistroEntrada(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos) {
    }

    public void updateRegistroEntradaCatchException(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido actualizar el registro de entrada con id [").append(id.getIdRegistro()).append("] en el libro [").append(id.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preUpdateRegistroEntrada(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos) {
    }

    @Override
    public final void updateRegistroSalida(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos) {
        Assert.notNull((Object)this.findRegistroSalidaById(usuario, id), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de salida."));
        try {
            this.preUpdateRegistroSalida(usuario, id, camposGenericos);
            this.updateRegistro(usuario, id, camposGenericos, false);
        }
        catch (Exception e) {
            this.updateRegistroSalidaCatchException(usuario, id, camposGenericos, e);
        }
        finally {
            this.postUpdateRegistroSalida(usuario, id, camposGenericos);
        }
    }

    @Override
    public void updateRegistroSalidaIR(UsuarioVO usuario, IdentificadorRegistroVO id, List<CampoGenericoRegistroVO> camposGenericos) {
        Assert.notNull((Object)this.findRegistroSalidaById(usuario, id), (String)("El registro con identificador [" + id.getIdRegistro() + "] no es de salida."));
        try {
            this.preUpdateRegistroSalida(usuario, id, camposGenericos);
            this.updateRegistro(usuario, id, camposGenericos, true);
        }
        catch (Exception e) {
            this.updateRegistroSalidaCatchException(usuario, id, camposGenericos, e);
        }
        finally {
            this.postUpdateRegistroSalida(usuario, id, camposGenericos);
        }
    }

    public void postUpdateRegistroSalida(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos) {
    }

    public void updateRegistroSalidaCatchException(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos, Exception e) {
        StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no ha podido actualizar el registro de salida con id [").append(id.getIdRegistro()).append("] en el libro [").append(id.getIdLibro()).append("]");
        if (logger.isDebugEnabled()) {
            logger.debug((Object)sb.toString());
        }
        throw new RegistroException(sb.toString());
    }

    public void preUpdateRegistroSalida(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos) {
    }

    @Override
    public DocumentoRegistroVO attachDocument(IdentificadorRegistroVO id, DocumentoRegistroVO documento, UsuarioVO usuario) {
        DocumentoRegistroVO result = null;
        List listaDocumentos = this.attachDocumentsUnordered(id, Arrays.asList(documento), usuario);
        boolean encontrado = false;
        Iterator iterator = listaDocumentos.iterator();
        while (iterator.hasNext() && !encontrado) {
            DocumentoRegistroVO documentoIt = (DocumentoRegistroVO)iterator.next();
            encontrado = documentoIt.getName().equals(documento.getName());
            if (!encontrado) continue;
            result = documentoIt;
        }
        return result;
    }

    private List attachDocumentsUnordered(IdentificadorRegistroVO id, List documentos, UsuarioVO usuario) {
        DocumentosValidacionHelper.validateDocuments(documentos);
        BaseLibroVO libro = new BaseLibroVO(id.getIdLibro());
        libro = this.getLibroManager().abrirLibro(usuario, libro);
        List bookDocsWithPages = null;
        Map map = null;
        try {
            map = FolderFileSession.createDocuments((Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (Map)DocumentosHelper.getDocumentosISicres(documentos), (Integer)Integer.valueOf(usuario.getId()), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            BookConf bookConf = UtilsSession.bookConf((int)Integer.valueOf(id.getIdLibro()), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            BookTypeConf bookTypeConf = this.getBookType(id, usuario);
            map = FolderFileSession.saveDocuments((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (Map)map, (BookTypeConf)bookTypeConf, (BookConf)bookConf, (Integer)Integer.valueOf(usuario.getId()), (Locale)usuario.getConfiguracionUsuario().getLocale(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            bookDocsWithPages = FolderFileSession.getBookFolderDocsWithPages((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)Integer.valueOf(id.getIdLibro()), (int)Integer.valueOf(id.getIdRegistro()), (String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se han podido adjuntar los documentos para el libro [").append(id.getIdLibro()).append("] y registro [").append(id.getIdRegistro()).append("].");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException("Error de parseo", e);
        }
        return new ListOfAxDochToListOfDocumentoRegistroVOMapper(usuario.getConfiguracionUsuario().getIdEntidad()).map(bookDocsWithPages);
    }

    public List attachDocuments(IdentificadorRegistroVO id, List documentos, UsuarioVO usuario) {
        LinkedList<DocumentoRegistroVO> documentosAdjuntados = new LinkedList<DocumentoRegistroVO>();
        if (documentos != null) {
            for (Iterator it03 = documentos.iterator(); it03.hasNext();) {
                DocumentoRegistroVO documento=(DocumentoRegistroVO) it03.next();
                DocumentoRegistroVO documentoAdjuntado = this.attachDocument(id, documento, usuario);
                documentosAdjuntados.add(documentoAdjuntado);
            }
        }
        return documentosAdjuntados;
    }

    @Override
    public boolean existDocumentByName(IdentificadorRegistroVO id, String documentName, UsuarioVO usuario) {
        boolean result = false;
        AxDochEntity axDochEntity = new AxDochEntity();
        try {
            int docID = axDochEntity.lookForName(id.getIdLibro(), Integer.parseInt(id.getIdRegistro()), documentName, usuario.getConfiguracionUsuario().getIdEntidad());
            if (docID != -1) {
                result = true;
            }
        }
        catch (Exception e) {
            String message = "Error en la busqueda de documento por nombre (existDocumentByName)idRegistro:" + id.getIdRegistro() + " -idLibro:" + id.getIdLibro();
            logger.error((Object)message);
            throw new RegistroException(message, e);
        }
        return result;
    }

    @Override
    public ResultadoBusquedaRegistroVO findAllRegistroEntradaByCriterioWhereSql(UsuarioVO usuario, CriterioBusquedaRegistroSqlVO criterioBusqueda) {
        List<AxSfQueryResults> axSfQueryResults = this.queryForAxSfQueryResults(usuario, criterioBusqueda.getOffset().intValue(), criterioBusqueda.getLimit().intValue(), null, TipoLibroEnum.ENTRADA, criterioBusqueda.getSql());
        int total = this.queryForAxSfQueryResultsTotalCount(axSfQueryResults);
        ArrayList<RegistroEntradaVO> registers = new ArrayList<RegistroEntradaVO>();
        for (AxSfQueryResults results : axSfQueryResults) {
	    for (Iterator it03 = results.getResults().iterator(); it03.hasNext();) {
                AxSf axSf = (AxSf) it03.next();
                RegistroEntradaVO result = this.mapToRegistroEntradaVO(usuario, results.getBookId(), axSf);
                registers.add(result);
            }
        }
        return new ResultadoBusquedaRegistroVO(registers, total);
    }

    @Override
    public ResultadoBusquedaRegistroVO findAllRegistroSalidaByCriterioWhereSql(UsuarioVO usuario, CriterioBusquedaRegistroSqlVO criterioBusqueda) {
        List<AxSfQueryResults> axSfQueryResults = this.queryForAxSfQueryResults(usuario, criterioBusqueda.getOffset().intValue(), criterioBusqueda.getLimit().intValue(), null, TipoLibroEnum.SALIDA, criterioBusqueda.getSql());
        int total = this.queryForAxSfQueryResultsTotalCount(axSfQueryResults);
        ArrayList<BaseRegistroVO> registers = new ArrayList<BaseRegistroVO>();
        for (AxSfQueryResults results : axSfQueryResults) {
	    for (Iterator it03 = results.getResults().iterator(); it03.hasNext();) {
                AxSf axSf = (AxSf) it03.next();
                registers.add(this.getBaseRegistroVOBuilder().build(usuario, axSf, String.valueOf(results.getBookId()), new RegistroSalidaVO()));
            }
        }
        return new ResultadoBusquedaRegistroVO(registers, total);
    }

    @Override
    public byte[] getPaginaById(IdentificadorRegistroVO identificadorRegistro, String documentId, String pageId, UsuarioVO usuario) {
        byte[] page = null;
        try {
            page = FolderFileSession.getFile((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)Integer.valueOf(identificadorRegistro.getIdLibro()), (Integer)Integer.valueOf(identificadorRegistro.getIdRegistro()), (Integer)Integer.valueOf(pageId), (String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Error al recuperar la p\u00e1gina con identificador [").append(pageId).append("] del documento con id [").append(documentId).append("] del registro [").append(identificadorRegistro.getIdRegistro()).append("] para el libro [").append(identificadorRegistro.getIdLibro()).append("]");
            logger.error(sb.toString(), e);
            throw new RegistroException(sb.toString(), e);
        }
        return page;
    }

    @Override
    public byte[] getPaginaByIndex(IdentificadorRegistroVO identificadorRegistro, int documentIndex, int pageIndex, UsuarioVO usuario) {
        try {
            this.getLibroManager().abrirLibro(usuario, new BaseLibroVO(identificadorRegistro.getIdLibro()));
            List bookFolderDocsWithPages = FolderFileSession.getBookFolderDocsWithPages((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)Integer.valueOf(identificadorRegistro.getIdLibro()), (int)Integer.valueOf(identificadorRegistro.getIdRegistro()), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            AxDoch selectedDocument = (AxDoch)bookFolderDocsWithPages.get(documentIndex - 1);
            AxPageh selectedPage = (AxPageh)CollectionUtils.find((Collection)selectedDocument.getPages(), (Predicate)new BeanPropertyValueEqualsPredicate("sortOrder", (Object)new Integer(pageIndex)));
            return FolderFileSession.getFile((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)new Integer(selectedDocument.getArchId()), (Integer)new Integer(selectedDocument.getFdrId()), (Integer)selectedPage.getId(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Error al recuperar la p\u00e1gina con \u00edndice [").append(pageIndex).append("] del documento con \u00edndice [").append(documentIndex).append("] del registro [").append(identificadorRegistro.getIdRegistro()).append("] para el libro [").append(identificadorRegistro.getIdLibro()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException(sb.toString(), e);
        }
    }

    @Override
    public Boolean canCreateRegistro(UsuarioVO usuario, BaseRegistroVO registro) {
        try {
            FolderSession.canCreateOrUpdateFolder((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)new Integer(registro.getIdLibro()), registro.getDocumentos());
        }
        catch (Exception e) {
            logger.debug((Object)("El usuario[" + usuario.getLoginName() + "] no tiene permisos sobre el libro [" + registro.getIdLibro() + "]"), (Throwable)e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private ResultadoBusquedaDistribucionVO getDistribucionesPendientes(UsuarioVO usuario, Integer idRegistro) {
        CriterioBusquedaDistribucionVO criterio = new CriterioBusquedaDistribucionVO();
        criterio.setEstado(EstadoDistribucionEnum.PENDIENTE);
        criterio.setLimit(Long.valueOf(Integer.MAX_VALUE));
        criterio.setOffset(0L);
        return this.distribucionManager.getDistributionsByRegistry(usuario, criterio, idRegistro, 1);
    }

    protected void cancelRegistroById(UsuarioVO usuario, IdentificadorRegistroVO id) {
        BaseLibroVO libro = new BaseLibroVO(id.getIdLibro());
        PermisosLibroVO permisoLibro = this.getPermisosManager().getPermisosLibro(libro, usuario);
        Assert.state((boolean)(permisoLibro.isAdministrador() || usuario.getPermisos().getPermisosAplicacion().isSuperUsuario()), (String)"Para poder cancelar un registro se debe ser administrador del libro o superusuario.");
        try {
            String idEntidad = usuario.getConfiguracionUsuario().getIdEntidad();
            ResultadoBusquedaDistribucionVO distribucionesPendientes = this.getDistribucionesPendientes(usuario, Integer.valueOf(id.getIdRegistro()));
	    for (Iterator it03 = distribucionesPendientes.getDistributions().iterator(); it03.hasNext();) {
		DistribucionVO distribucion=(DistribucionVO) it03.next();
                logger.debug((Object)("Se eliminar\u00e1 la siguiente distribuci\u00f3n [" + distribucion.getId() + "] al cancelar el registro asociado"));
                this.distribucionManager.deleteDistribution(idEntidad, Integer.valueOf(distribucion.getId()));
            }
            this.updateRegistro(usuario, id, Arrays.asList(new CampoGenericoRegistroVO(String.valueOf(6), String.valueOf(EstadoRegistroEnum.ANULADO.getValue()))), false);
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Se ha producido un error al cancelar el registro con identificador [").append(id.getIdRegistro()).append("] del libro [").append(id.getIdLibro()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException(sb.toString(), e);
        }
    }

    protected void updateRegistro(UsuarioVO usuario, IdentificadorRegistroVO id, List camposGenericos, boolean validateIR) {
        BookUseCase buc = new BookUseCase();
        UseCaseConf useCaseConf = new UseCaseConf(usuario.getConfiguracionUsuario().getLocale());
        useCaseConf.setSessionID(usuario.getConfiguracionUsuario().getSessionID());
        useCaseConf.setEntidadId(usuario.getConfiguracionUsuario().getIdEntidad());
        AxSf register = null;
        try {
            BaseLibroVO libro = this.getLibroManager().abrirLibro(usuario, new BaseLibroVO(id.getIdLibro()));
            PermisosLibroVO permisoRegistro = this.getPermisosManager().getPermisosLibro(libro, usuario);
            if (validateIR ? !permisoRegistro.isModificacion() || !usuario.getPermisos().getPermisosAplicacion().isOperacionesIntercambioRegistral() : !permisoRegistro.isModificacion() || !usuario.getPermisos().getPermisosAplicacion().isModificarCamposProtegidos()) {
                throw new RuntimeException("No se tienen permisos para actualizar el registro " + id);
            }
            register = buc.getBookFolder(useCaseConf, Integer.valueOf(id.getIdLibro()), Integer.valueOf(id.getIdRegistro()).intValue());
            List atts = new ListOfCampoGenericoRegistroVOToListOfFlushFdrFieldMapper().map(camposGenericos);
            List<FlushFdrInter> listaInteresados = TercerosHelper.getListaInteresadosISicres(atts);
            this.checkExistAllFlushFdrFields(register, atts);
            this.lockRegistro(usuario, id);
            buc.saveOrUpdateFolder(usuario.getConfiguracionUsuario().getIdEntidad(), usuario.getConfiguracionUsuario().getSessionID(), usuario.getConfiguracionUsuario().getLocale(), Integer.valueOf(id.getIdLibro()), Integer.valueOf(id.getIdRegistro()).intValue(), new ArrayList(), atts, listaInteresados, new HashMap());
            this.unlockRegistro(usuario, id);
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Se ha producido un error al actualizar el registro con identificador [").append(id.getIdRegistro()).append("] del libro [").append(id.getIdLibro()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException(sb.toString(), e);
        }
    }

    public UnidadAdministrativaManager getUnidadAdministrativaManager() {
        return this.unidadAdministrativaManager;
    }

    public void setUnidadAdministrativaManager(UnidadAdministrativaManager unidadAdministrativaManager) {
        this.unidadAdministrativaManager = unidadAdministrativaManager;
    }

    public LibroManager getLibroManager() {
        return this.libroManager;
    }

    public void setLibroManager(LibroManager libroManager) {
        this.libroManager = libroManager;
    }

    public PermisosManager getPermisosManager() {
        return this.permisosManager;
    }

    public void setPermisosManager(PermisosManager permisosManager) {
        this.permisosManager = permisosManager;
    }

    public BaseRegistroVOBuilder getBaseRegistroVOBuilder() {
        return this.baseRegistroVOBuilder;
    }

    public void setBaseRegistroVOBuilder(BaseRegistroVOBuilder baseRegistroVOBuilder) {
        this.baseRegistroVOBuilder = baseRegistroVOBuilder;
    }

    public DistribucionManager getDistribucionManager() {
        return this.distribucionManager;
    }

    public void setDistribucionManager(DistribucionManager distribucionManager) {
        this.distribucionManager = distribucionManager;
    }

    @Override
    public List queryForAxSfQueryResults(UsuarioVO usuario, int offset, int limit, Integer bookID, TipoLibroEnum bookType, String filter) {
        return this.queryForAxSfQueryResults(usuario, offset, limit, bookID, bookType, filter, true);
    }

    public List queryForAxSfQueryResults(UsuarioVO usuario, int offset, int limit, Integer bookID, TipoLibroEnum bookType, String filter, boolean appendFilter) {
        Integer reportOption = new Integer(0);
        ArrayList<Integer> bookIds = new ArrayList<Integer>();
        if (null == bookID) {
            List<BaseLibroVO> booksByUser = this.getLibroManager().findLibrosByUser(usuario, bookType);
            for (BaseLibroVO libro : booksByUser) {
                bookIds.add(Integer.valueOf(libro.getId()));
            }
        } else {
            bookIds.add(bookID);
        }
        ArrayList<AxSfQueryResults> results = new ArrayList<AxSfQueryResults>();
	Integer aBookId2=null;
        try {
	    for (Iterator it03 = bookIds.iterator(); it03.hasNext();) {
		aBookId2=(Integer) it03.next();
                this.getLibroManager().abrirLibro(usuario, new BaseLibroVO(String.valueOf(aBookId2)));
                if (SecuritySession.canQuery((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)aBookId2)) {
                    CacheBag cacheBag = CacheFactory.getCacheInterface().getCacheEntry(usuario.getConfiguracionUsuario().getSessionID());
                    THashMap bookInformation = (THashMap)cacheBag.get((Object)aBookId2);
                    String filterAux = filter;
                    if (!StringUtils.isEmpty((String)filter)) {
                        String queryFilter;
                        if (appendFilter && !StringUtils.isEmpty((String)(queryFilter = (String)bookInformation.get((Object)"QueryFilter")))) {
                            filterAux = queryFilter + " AND " + filter;
                        }
                        bookInformation.put((Object)"QueryFilter", (Object)filterAux);
                    }
                    AxSfQuery axsfQuery = new AxSfQuery(aBookId2);
                    axsfQuery.setPageSize(limit);
                    int bookSize = FolderSession.openRegistersQuery((String)usuario.getConfiguracionUsuario().getSessionID(), (AxSfQuery)axsfQuery, Arrays.asList(aBookId2), (Integer)reportOption, (String)usuario.getConfiguracionUsuario().getIdEntidad());
                    if (bookSize <= 0) continue;
                    if (offset <= bookSize) {
                        AxSfQueryResults axSfQueryResults = FolderSession.navigateToRowRegistersQuery((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)aBookId2, (int)offset, (Locale)usuario.getConfiguracionUsuario().getLocale(), (String)usuario.getConfiguracionUsuario().getIdEntidad(), (String)"");
                        limit-=axSfQueryResults.getCurrentResultsSize();
                        results.add(axSfQueryResults);
                    }
                    offset = this.calculateOffset(offset, bookSize);
                    FolderSession.closeRegistersQuery((String)usuario.getConfiguracionUsuario().getSessionID(), (Integer)aBookId2);
                    continue;
                }
                StringBuffer sb = new StringBuffer("El usuario [").append(usuario.getLoginName()).append("] no tiene permisos de consulta sobre el libro [").append(aBookId2).append("]");
                logger.warn((Object)sb.toString());
                throw new RegistroException("No se pueden recuperar los registros de " + bookType.getName() + " para el usuario [" + usuario.getLoginName() + "]", new SecurityException(sb.toString()));
            }
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("Error al recuperar los registros de libro [").append(aBookId2).append("] de tipo [").append(bookType.getName()).append("] para el usuario [").append(usuario.getLoginName()).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new RegistroException(sb.toString(), e);
        }
        return results;
    }

    private int queryForAxSfQueryResultsTotalCount(List<AxSfQueryResults> axSfQueryResults) {
        int total = 0;
        for (AxSfQueryResults results : axSfQueryResults) {
            total+=results.getTotalQuerySize();
        }
        return total;
    }

    private BookTypeConf getBookType(IdentificadorRegistroVO id, UsuarioVO usuario) throws BookException, SessionException, ValidationException {
        BookTypeConf btc = new BookTypeConf();
        if (Repository.getInstance((String)usuario.getConfiguracionUsuario().getIdEntidad()).isInBook(id.getIdLibro()).booleanValue()) {
            btc.setBookType(1);
        } else {
            btc.setBookType(2);
        }
        btc = UtilsSession.bookTypeConf((int)btc.getBookType(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
        return btc;
    }

    private int calculateOffset(int offset, int bookSize) {
        int newOffset = 0;
        if (offset >= bookSize) {
            newOffset = offset - bookSize;
        }
        return newOffset;
    }

    private void checkExistAllFlushFdrFields(AxSf register, List<FlushFdrField> atts) {
        if (!CollectionUtils.isEmpty((Collection)atts)) {
            for (FlushFdrField field : atts) {
                if (register.getAttributesNames().contains(StringUtils.join((Object[])new String[]{"fld", String.valueOf(field.getFldid())})) || register.getProposedExtendedFields().contains(field.getFldid())) continue;
                StringBuffer sb = new StringBuffer("El campo [").append(field.getFldid()).append("] no existe para el registro [").append(register.getAttributeValue("fdrid")).append("]");
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)sb.toString());
                }
                throw new RegistroException(sb.toString());
            }
        }
    }

    @Override
    public BaseRegistroVO findRegistroByNumRegistro(UsuarioVO usuario, String numRegistro) {
        CriterioBusquedaRegistroSqlVO criterioBusqueda = new CriterioBusquedaRegistroSqlVO();
        criterioBusqueda.setLimit(1L);
        criterioBusqueda.setOffset(0L);
        criterioBusqueda.setSql("fld1='" + numRegistro + "'");
        ResultadoBusquedaRegistroVO resultado = this.findAllRegistroEntradaByCriterioWhereSql(usuario, criterioBusqueda);
        if (resultado == null || resultado.getRegisters().size() == 0) {
            logger.warn((Object)("No se ha podido encontrar el registro con numero de registro: " + numRegistro));
            throw new RegistroException("No se ha podido encontrar el registro con numero de registro: " + numRegistro);
        }
        BaseRegistroVO registro = (BaseRegistroVO)resultado.getRegisters().get(0);
        return registro;
    }
}
