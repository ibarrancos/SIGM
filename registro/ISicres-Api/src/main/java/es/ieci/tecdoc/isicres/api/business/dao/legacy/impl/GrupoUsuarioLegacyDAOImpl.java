package es.ieci.tecdoc.isicres.api.business.dao.legacy.impl;

import com.ieci.tecdoc.common.invesdoc.Iusergrouphdr;
import com.ieci.tecdoc.common.invesdoc.Iusergroupuser;
import com.ieci.tecdoc.common.invesdoc.Iuserldapgrphdr;
import com.ieci.tecdoc.common.invesdoc.Iuseruserhdr;
import com.ieci.tecdoc.common.utils.ISicresQueries;
import es.ieci.tecdoc.isicres.api.business.dao.GrupoUsuarioDAO;
import es.ieci.tecdoc.isicres.api.business.dao.legacy.impl.IsicresBaseHibernateDAOImpl;
import es.ieci.tecdoc.isicres.api.business.exception.GrupoUsuarioException;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.GrupoUsuarioVOMapper;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.UsuarioVOMapper;
import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;
import org.apache.log4j.Logger;

public class GrupoUsuarioLegacyDAOImpl
extends IsicresBaseHibernateDAOImpl
implements GrupoUsuarioDAO {
    private static final Logger logger = Logger.getLogger((Class)GrupoUsuarioLegacyDAOImpl.class);
    GrupoUsuarioVOMapper grupoUsuarioVOMapper = new GrupoUsuarioVOMapper();
    UsuarioVOMapper usuarioVOMapper = new UsuarioVOMapper();

    @Override
    public GrupoUsuarioVO getGrupoById(Integer id) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGrupoById(Integer) - start");
        }
        GrupoUsuarioVO grupoUsuarioVO = null;
        try {
            Iusergrouphdr iusergrouphdr = (Iusergrouphdr)this.getSession().load((Class)Iusergrouphdr.class, (Serializable)id);
            grupoUsuarioVO = this.grupoUsuarioVOMapper.map(iusergrouphdr);
        }
        catch (ObjectNotFoundException e) {
            logger.error((Object)"getGrupoById(Integer)", (Throwable)e);
        }
        catch (HibernateException e) {
            logger.error((Object)"getGrupoById(Integer)", (Throwable)e);
            throw new GrupoUsuarioException("No se puede obtener el grupo con el identificador [" + id + "]", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGrupoById(Integer) - end");
        }
        return grupoUsuarioVO;
    }

    @Override
    public List<GrupoUsuarioVO> getGrupos() {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGrupos() - start");
        }
        LinkedList<GrupoUsuarioVO> result = new LinkedList<GrupoUsuarioVO>();
        try {
            List<Iusergrouphdr> grupos = ISicresQueries.getGroups((Session)this.getSession(), (List)null);
            for (Iusergrouphdr iusergrouphdr : grupos) {
                GrupoUsuarioVO grupo = this.grupoUsuarioVOMapper.map(iusergrouphdr);
                result.add(grupo);
            }
        }
        catch (HibernateException e) {
            logger.error((Object)"getGrupos()", (Throwable)e);
            throw new GrupoUsuarioException("No se puede obtener la lista de grupos", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGrupos() - end");
        }
        return result;
    }

    @Override
    public List<GrupoUsuarioVO> getGruposLdap() {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGruposLdap() - start");
        }
        LinkedList<GrupoUsuarioVO> result = new LinkedList<GrupoUsuarioVO>();
        try {
            List<Iuserldapgrphdr> grupos = ISicresQueries.getLdapGroups((Session)this.getSession(), (List)null);
            for (Iuserldapgrphdr iuserldapgrphdr : grupos) {
                GrupoUsuarioVO grupo = this.grupoUsuarioVOMapper.map(iuserldapgrphdr);
                result.add(grupo);
            }
        }
        catch (HibernateException e) {
            logger.error((Object)"getGruposLdap()", (Throwable)e);
            throw new GrupoUsuarioException("No se puede obtener la lista de grupos", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGruposLdap() - end");
        }
        return result;
    }

    @Override
    public List<BaseUsuarioVO> getUsuariosDelGrupo(Integer idGrupo) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"findUsuariosDelGrupo(GrupoUsuarioVO) - start");
        }
        LinkedList<BaseUsuarioVO> result = new LinkedList<BaseUsuarioVO>();
        try {
            List<Iusergroupuser> lista = ISicresQueries.getIUserGroupUserByGroupId((Session)this.getSession(), (Integer)idGrupo);
            for (Iusergroupuser iusergroupuser : lista) {
                Iuseruserhdr iuseruserhdr = ISicresQueries.getUserUserHdr((Session)this.getSession(), (Integer)iusergroupuser.getUserid());
                result.add(this.usuarioVOMapper.map(iuseruserhdr));
            }
        }
        catch (HibernateException e) {
            logger.error((Object)"getGruposPertenecientesUsuario(Integer)", (Throwable)e);
            throw new GrupoUsuarioException("No se puede obtener la lista de grupos del usuario", (Throwable)e);
        }
        return result;
    }

    @Override
    public List<GrupoUsuarioVO> getGruposPertenecientesUsuario(Integer idUsuario) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGruposPertenecientesUsuario(Integer) - start");
        }
        LinkedList<GrupoUsuarioVO> result = new LinkedList<GrupoUsuarioVO>();
        try {
            List<Iusergroupuser> lista = ISicresQueries.getIUserGroupUser((Session)this.getSession(), (Integer)idUsuario);
            for (Iusergroupuser iusergroupuser : lista) {
                GrupoUsuarioVO grupoUsuarioVO = this.grupoUsuarioVOMapper.map(iusergroupuser);
                result.add(grupoUsuarioVO);
            }
        }
        catch (HibernateException e) {
            logger.error((Object)"getGruposPertenecientesUsuario(Integer)", (Throwable)e);
            throw new GrupoUsuarioException("No se puede obtener la lista de grupos del usuario", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGruposPertenecientesUsuario(Integer) - end");
        }
        return result;
    }

    @Override
    public List<GrupoUsuarioVO> getGruposNoPertenecientesUsuario(Integer idUsuario) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"findGruposNoPertenecientesUsuario(Integer) - start");
        }
        LinkedList<GrupoUsuarioVO> result = new LinkedList<GrupoUsuarioVO>();
        GrupoUsuarioVOMapper grupoUsuarioVOMapper = new GrupoUsuarioVOMapper();
        try {
            List gruposUsuario = ISicresQueries.getIUserGroupUser((Session)this.getSession(), (Integer)idUsuario);
            List<Iusergrouphdr> gruposNoPertenecientesUsuario = ISicresQueries.getGroups((Session)this.getSession(), (List)gruposUsuario);
            for (Iusergrouphdr iusergrouphdr : gruposNoPertenecientesUsuario) {
                GrupoUsuarioVO grupoUsuarioVO = grupoUsuarioVOMapper.map(iusergrouphdr);
                result.add(grupoUsuarioVO);
            }
        }
        catch (HibernateException e) {
            logger.error((Object)"getGruposNoPertenecientesUsuario(Integer)", (Throwable)e);
            throw new GrupoUsuarioException("No se puede obtener la lista de grupos a los que no pertenece el usuario", (Throwable)e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getGruposNoPertenecientesUsuario(Integer) - end");
        }
        return result;
    }
}
