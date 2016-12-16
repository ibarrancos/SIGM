package es.ieci.tecdoc.isicres.api.business.manager.impl;

import es.ieci.tecdoc.isicres.api.business.dao.DepartamentoDAO;
import es.ieci.tecdoc.isicres.api.business.dao.GrupoUsuarioDAO;
import es.ieci.tecdoc.isicres.api.business.dao.OficinaDAO;
import es.ieci.tecdoc.isicres.api.business.dao.UsuarioDAO;
import es.ieci.tecdoc.isicres.api.business.helper.UsuarioHelper;
import es.ieci.tecdoc.isicres.api.business.manager.UsuarioManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.BeanUtils;

public class UsuarioManagerImpl
extends UsuarioManager {
    protected UsuarioDAO usuarioDAO;
    protected GrupoUsuarioDAO grupoUsuarioDAO;
    protected OficinaDAO oficinaDAO;
    protected DepartamentoDAO departamentoDAO;

    @Override
    public BaseUsuarioVO getUsuarioById(Integer id) {
        return this.getUsuarioDAO().getUsuarioById(id);
    }

    @Override
    public UsuarioVO getUsuario(Integer id) {
        UsuarioVO usuario = null;
        BaseUsuarioVO baseusuario = this.getUsuarioById(id);
        if (baseusuario != null) {
            usuario = new UsuarioVO();
            BeanUtils.copyProperties((Object)baseusuario, (Object)usuario);
            usuario.setGruposUsuario(this.getGruposUsuario(id));
            usuario.setOficinas(this.getOficinasUsuario(usuario));
            usuario.setDepartamentoUsuario(this.departamentoDAO.getDepartamentoUsuario(usuario));
        }
        return usuario;
    }

    @Override
    public BaseUsuarioVO getUsuarioByName(String name) {
        return this.getUsuarioDAO().getUsuarioByLogin(name);
    }

    @Override
    public List<BaseUsuarioVO> getUsuarios() {
        return this.getUsuarioDAO().getUsuarios();
    }

    @Override
    public List<OficinaVO> getOficinasUsuario(UsuarioVO usuario) {
        if (usuario.getConfiguracionUsuario() == null || usuario.getConfiguracionUsuario().getLocale() == null) {
            usuario = UsuarioHelper.validateUsusario(usuario);
        }
        return this.getOficinaDAO().getOficinasByUsuario(usuario.getConfiguracionUsuario().getLocale(), usuario);
    }

    @Override
    public List<GrupoUsuarioVO> getGruposUsuario(Integer idUsuario) {
        return this.getGrupoUsuarioDAO().getGruposPertenecientesUsuario(idUsuario);
    }

    @Override
    public ConfiguracionUsuarioVO getConfiguracionUsuario(Integer idUsuario) {
        return this.getUsuarioDAO().getConfiguracionUsuario(idUsuario);
    }

    @Override
    public PermisosUsuarioVO getPermisosUsuario(Integer idUsuario) {
        return this.usuarioDAO.getPermisosUsuario(idUsuario);
    }

    @Override
    public List<BaseUsuarioVO> getUsuariosLdap() {
        return this.getUsuarioDAO().getUsuariosLdap();
    }

    public UsuarioDAO getUsuarioDAO() {
        return this.usuarioDAO;
    }

    public void setUsuarioDAO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public GrupoUsuarioDAO getGrupoUsuarioDAO() {
        return this.grupoUsuarioDAO;
    }

    public void setGrupoUsuarioDAO(GrupoUsuarioDAO grupoUsuarioDAO) {
        this.grupoUsuarioDAO = grupoUsuarioDAO;
    }

    public OficinaDAO getOficinaDAO() {
        return this.oficinaDAO;
    }

    public void setOficinaDAO(OficinaDAO oficinaDAO) {
        this.oficinaDAO = oficinaDAO;
    }

    public DepartamentoDAO getDepartamentoDAO() {
        return this.departamentoDAO;
    }

    public void setDepartamentoDAO(DepartamentoDAO departamentoDAO) {
        this.departamentoDAO = departamentoDAO;
    }
}
