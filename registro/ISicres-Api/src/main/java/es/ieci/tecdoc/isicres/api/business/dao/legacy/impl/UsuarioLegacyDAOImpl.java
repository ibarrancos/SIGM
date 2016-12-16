package es.ieci.tecdoc.isicres.api.business.dao.legacy.impl;

import com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr;
import com.ieci.tecdoc.common.invesdoc.Iuseruserhdr;
import com.ieci.tecdoc.common.utils.ISicresQueries;
import es.ieci.tecdoc.isicres.api.business.dao.UsuarioDAO;
import es.ieci.tecdoc.isicres.api.business.dao.legacy.impl.IsicresBaseHibernateDAOImpl;
import es.ieci.tecdoc.isicres.api.business.exception.UsuarioException;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.UsuarioVOMapper;
import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosUsuarioVO;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class UsuarioLegacyDAOImpl
extends IsicresBaseHibernateDAOImpl
implements UsuarioDAO {
    private static final Logger logger = Logger.getLogger((Class)UsuarioLegacyDAOImpl.class);
    UsuarioVOMapper usuarioVOMapper = new UsuarioVOMapper();

    @Override
    public BaseUsuarioVO getUsuarioById(Integer id) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getUsuarioById(String) - start");
        }
        BaseUsuarioVO usuarioVO = null;
        try {
            try {
                Iuseruserhdr user = ISicresQueries.getUserUserHdr((Session)this.getSession(), (Integer)id);
                usuarioVO = this.usuarioVOMapper.map(user);
            }
            catch (ObjectNotFoundException e) {
                Iuserldapuserhdr ldapUser = ISicresQueries.getUserLdapUser((Session)this.getSession(), (Integer)id);
                usuarioVO = this.usuarioVOMapper.map(ldapUser);
            }
        }
        catch (HibernateException e) {
            throw new UsuarioException("No se puede recuperar el usuario con el identificador: [" + id + "]", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getUsuarioById(String) - end");
        }
        return usuarioVO;
    }

    @Override
    public BaseUsuarioVO getUsuarioByLogin(String login) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getUsuarioByName(String) - start");
        }
        BaseUsuarioVO usuarioVO = null;
        try {
            List users = ISicresQueries.getUserUserHdrByName((Session)this.getSession(), (String)login);
            if (CollectionUtils.isNotEmpty((Collection)users)) {
                Iuseruserhdr user = (Iuseruserhdr)users.get(0);
                usuarioVO = this.usuarioVOMapper.map(user);
            } else {
                List ldapUsers = ISicresQueries.getUserLdapUserByFullName((Session)this.getSession(), (String)login);
                if (CollectionUtils.isNotEmpty((Collection)ldapUsers)) {
                    Iuserldapuserhdr user = (Iuserldapuserhdr)ldapUsers.get(0);
                    usuarioVO = this.usuarioVOMapper.map(user);
                }
            }
        }
        catch (HibernateException e) {
            throw new UsuarioException("No se puede recuperar el usuario con el login: [" + login + "]", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getUsuarioByName(String) - end");
        }
        return usuarioVO;
    }

    @Override
    public List<BaseUsuarioVO> getUsuarios() {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getUsuarios() - start");
        }
        UsuarioVOMapper usuarioVOMapper = new UsuarioVOMapper();
        LinkedList<BaseUsuarioVO> lista = new LinkedList<BaseUsuarioVO>();
        try {
            List users = ISicresQueries.getUsers((Session)this.getSession());
            if (CollectionUtils.isNotEmpty((Collection)users)) {
                for (Iuseruserhdr iuseruserhdr : users) {
                    BaseUsuarioVO usuarioVO = usuarioVOMapper.map(iuseruserhdr);
                    if (usuarioVO == null) continue;
                    lista.add(usuarioVO);
                }
            }
        }
        catch (HibernateException e) {
            throw new UsuarioException("No se puede recuperar la lista de todos los usuarios", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getUsuarios() - end");
        }
        return lista;
    }

    @Override
    public List<BaseUsuarioVO> getUsuariosLdap() {
        UsuarioVOMapper usuarioVOMapper = new UsuarioVOMapper();
        LinkedList<BaseUsuarioVO> lista = new LinkedList<BaseUsuarioVO>();
        try {
            List users = ISicresQueries.getLdapUsers((Session)this.getSession());
            if (CollectionUtils.isNotEmpty((Collection)users)) {
                for (Iuserldapuserhdr iuseruserhdr : users) {
                    BaseUsuarioVO usuarioVO = usuarioVOMapper.map(iuseruserhdr);
                    if (usuarioVO == null) continue;
                    lista.add(usuarioVO);
                }
            }
        }
        catch (HibernateException e) {
            throw new UsuarioException("No se puede recuperar la lista de todos los usuarios ldap", (Throwable)e);
        }
        return lista;
    }

    @Override
    public ConfiguracionUsuarioVO getConfiguracionUsuario(Integer idUsuario) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getConfiguracionUsuario(String) - start");
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getConfiguracionUsuario(String) - end");
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public PermisosUsuarioVO getPermisosUsuario(Integer idUsuario) {
        throw new UnsupportedOperationException();
    }
}
