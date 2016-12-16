package es.ieci.tecdoc.isicres.api.business.manager.impl;

import com.ieci.tecdoc.common.AuthenticationUser;
import com.ieci.tecdoc.common.invesdoc.Iuseruserhdr;
import com.ieci.tecdoc.common.invesdoc.Iuserusertype;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.isicres.SessionInformation;
import com.ieci.tecdoc.common.utils.ISicresGenPerms;
import com.ieci.tecdoc.idoc.authentication.InvesDocAuthenticationPolicy;
import com.ieci.tecdoc.idoc.utils.ConfiguratorInvesicres;
import com.ieci.tecdoc.isicres.session.security.SecuritySession;
import com.ieci.tecdoc.isicres.session.utils.UtilsSession;
import com.ieci.tecdoc.utils.HibernateUtil;
import com.ieci.tecdoc.utils.cache.CacheBag;
import com.ieci.tecdoc.utils.cache.CacheFactory;
import es.ieci.tecdoc.fwktd.core.spring.configuration.jdbc.datasource.MultiEntityContextHolder;
import es.ieci.tecdoc.isicres.api.business.exception.LoginException;
import es.ieci.tecdoc.isicres.api.business.helper.UsuarioHelper;
import es.ieci.tecdoc.isicres.api.business.manager.ContextoAplicacionManager;
import es.ieci.tecdoc.isicres.api.business.manager.ContextoAplicacionManagerFactory;
import es.ieci.tecdoc.isicres.api.business.manager.LoginManager;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.ScrOficToOficinaVOMapper;
import es.ieci.tecdoc.isicres.api.business.vo.BaseOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import net.sf.hibernate.Session;
import org.apache.log4j.Logger;

public class LoginLegacyManagerImpl
implements LoginManager {
    private static final Logger logger = Logger.getLogger((Class)LoginLegacyManagerImpl.class);

    protected ContextoAplicacionManager getContextoAplicacionManager() {
        return ContextoAplicacionManagerFactory.getInstance();
    }

    public UsuarioVO login(UsuarioVO usuario) {
        usuario = UsuarioHelper.validateUsusario(usuario);
        try {
            String sessionID = SecuritySession.login((String)usuario.getLoginName(), (String)usuario.getPassword(), (String)usuario.getConfiguracionUsuario().getOficina().getCodigoOficina(), (Locale)usuario.getConfiguracionUsuario().getLocale(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            usuario.getConfiguracionUsuario().setSessionID(sessionID);
            CacheBag cacheBag = CacheFactory.getCacheInterface().getCacheEntry(sessionID);
            AuthenticationUser userLogin = SecuritySession.getUserLogin((String)sessionID);
            usuario.setId(String.valueOf(userLogin.getId()));
            ScrOfic scrOfic = (ScrOfic)cacheBag.get((Object)"com.ieci.tecdoc.common.invesicres.ScrOfic");
            if (scrOfic != null) {
                usuario.getConfiguracionUsuario().setOficina(new ScrOficToOficinaVOMapper().map(scrOfic));
            }
            SessionInformation sessionInformation = UtilsSession.getSessionInformation((String)sessionID, (Locale)usuario.getConfiguracionUsuario().getLocale(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
            usuario.setFullName(sessionInformation.getUserName());
            usuario.getConfiguracionUsuario().setProfile(String.valueOf(((Iuserusertype)cacheBag.get((Object)"com.ieci.tecdoc.common.invesdoc.Iuserusertype")).getType()));
            MultiEntityContextHolder.setEntity((String)usuario.getConfiguracionUsuario().getIdEntidad());
            ContextoAplicacionManager contextoAplicacionManager = this.getContextoAplicacionManager();
            contextoAplicacionManager.setUsuarioActual(usuario);
            OficinaVO oficinaActual = usuario.getConfiguracionUsuario().getOficina();
            contextoAplicacionManager.setOficinaActual(oficinaActual);
            PermisosAplicacionVO permisosAplicacion = new PermisosAplicacionVO();
            ISicresGenPerms perms = (ISicresGenPerms)cacheBag.get((Object)"GenPermsUser");
            Iuserusertype userType = (Iuserusertype)cacheBag.get((Object)"com.ieci.tecdoc.common.invesdoc.Iuserusertype");
            boolean superUsuario = userType.getType() == 3;
            permisosAplicacion.setSuperUsuario(superUsuario);
            boolean altaTerceros = perms.canCreatePersons();
            permisosAplicacion.setAltaTerceros(altaTerceros);
            boolean modificacionTerceros = perms.canUpdatePersons();
            permisosAplicacion.setModificacionTerceros(modificacionTerceros);
            boolean distribucionManual = perms.isCanDistRegisters();
            permisosAplicacion.setDistribucionManual(distribucionManual);
            boolean aceptarDistribucion = perms.isCanAcceptRegisters();
            permisosAplicacion.setAceptarDistribucion(aceptarDistribucion);
            boolean rechazarDistribucion = perms.isCanRejectRegisters();
            permisosAplicacion.setRechazarDistribucion(rechazarDistribucion);
            boolean archivarDistribucion = perms.isCanArchiveRegisters();
            permisosAplicacion.setArchivarDistribucion(archivarDistribucion);
            boolean cambiarDestinoDistribucion = perms.isCanChangeDestRegisters();
            permisosAplicacion.setCambiarDestinoDistribucion(cambiarDestinoDistribucion);
            boolean cambiarDestinoDistribucionRechazada = perms.isCanChangeDestRejectRegisters();
            permisosAplicacion.setCambiarDestinoDistribucionRechazada(cambiarDestinoDistribucionRechazada);
            boolean altaFechaRegistro = perms.canIntroRegDate();
            permisosAplicacion.setAltaFechaRegistro(altaFechaRegistro);
            boolean modificarFechaRegistro = perms.canUpdateRegDate();
            permisosAplicacion.setModificarFechaRegistro(modificarFechaRegistro);
            boolean modificarCamposProtegidos = perms.canUpdateProtectedFields();
            permisosAplicacion.setModificarCamposProtegidos(modificarCamposProtegidos);
            boolean consultarDocuAnexa = perms.isCanShowDocuments();
            permisosAplicacion.setConsultarDocuAnexa(consultarDocuAnexa);
            boolean borrarDocuAnexa = perms.isCanDeleteDocuments();
            permisosAplicacion.setBorrarDocuAnexa(borrarDocuAnexa);
            boolean gestionUnidadesAdministrativas = perms.getCanModifyAdminUnits();
            permisosAplicacion.setGestionUnidadesAdministrativas(gestionUnidadesAdministrativas);
            boolean gestionInformes = perms.getCanModifyReports();
            permisosAplicacion.setGestionInformes(gestionInformes);
            boolean gestionTiposAsunto = perms.getCanModifyIssueTypes();
            permisosAplicacion.setGestionTiposAsunto(gestionTiposAsunto);
            boolean gestionUsuarios = perms.getCanModifyUsers();
            permisosAplicacion.setGestionUsuarios(gestionUsuarios);
            boolean gestionTiposTransporte = perms.getCanModifyTransportTypes();
            permisosAplicacion.setGestionTiposTransporte(gestionTiposTransporte);
            boolean operacionesIntercambioRegistral = perms.canAccessRegInterchange();
            permisosAplicacion.setOperacionesIntercambioRegistral(operacionesIntercambioRegistral);
            PermisosUsuarioVO permisosUsuario = new PermisosUsuarioVO();
            permisosUsuario.setPermisosAplicacion(permisosAplicacion);
            usuario.setPermisos(permisosUsuario);
            ConfiguratorInvesicres.getInstance((String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
        catch (Exception e) {
            logger.error((Object)("Error en el login del usuario [" + usuario.getLoginName() + "]"), (Throwable)e);
            throw new LoginException("Error en el login del usuario [" + usuario.getLoginName() + "]", e);
        }
        return usuario;
    }

    public AuthenticationUser getUserInfo(String id, String entidad) {
        AuthenticationUser authenticationUser = null;
        try {
            Session session = HibernateUtil.currentSession((String)entidad);
            Iuseruserhdr user = (Iuseruserhdr)session.load((Class)Iuseruserhdr.class, (Serializable)Integer.valueOf(id));
            authenticationUser = new AuthenticationUser();
            authenticationUser.setId(user.getId());
            authenticationUser.setName(user.getName());
            authenticationUser.setDeptid(new Integer(user.getDeptid()));
            InvesDocAuthenticationPolicy authenticationPolicy = new InvesDocAuthenticationPolicy();
            List deptList = authenticationPolicy.getUserDeptList(Integer.valueOf(id), entidad);
            authenticationUser.setDeptList(deptList);
        }
        catch (Exception e) {
            StringBuffer sb = new StringBuffer("No se ha podido cargar la informaci\u00f3n del usuario con identificador [").append(id).append("]");
            logger.error((Object)sb.toString(), (Throwable)e);
            throw new LoginException(sb.toString());
        }
        return authenticationUser;
    }

    public void logout(UsuarioVO usuario) {
        try {
            SecuritySession.logout((String)usuario.getConfiguracionUsuario().getSessionID(), (String)usuario.getConfiguracionUsuario().getIdEntidad());
        }
        catch (Exception e) {
            logger.error((Object)("Error en el logout del usuario [" + usuario.getLoginName() + "]"), (Throwable)e);
            throw new LoginException("Error en el logout del usuario [" + usuario.getLoginName() + "]", e);
        }
    }
}
