package es.ieci.tecdoc.isicres.api.business.manager.impl;

import es.ieci.tecdoc.isicres.api.business.dao.GrupoUsuarioDAO;
import es.ieci.tecdoc.isicres.api.business.manager.GrupoManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import java.util.List;

public class GrupoManagerImpl
extends GrupoManager {
    protected GrupoUsuarioDAO grupoUsuarioDAO;

    @Override
    public GrupoUsuarioVO getGrupoById(Integer idGrupo) {
        return this.grupoUsuarioDAO.getGrupoById(idGrupo);
    }

    @Override
    public List<GrupoUsuarioVO> getGrupos() {
        return this.grupoUsuarioDAO.getGrupos();
    }

    @Override
    public List<BaseUsuarioVO> getUsuariosDelGrupo(Integer idGrupo) {
        return this.grupoUsuarioDAO.getUsuariosDelGrupo(idGrupo);
    }

    @Override
    public List<GrupoUsuarioVO> getGruposPertenecientesUsuario(Integer idUsuario) {
        return this.grupoUsuarioDAO.getGruposPertenecientesUsuario(idUsuario);
    }

    @Override
    public List<GrupoUsuarioVO> getGruposNoPertenecientesUsuario(Integer idUsuario) {
        return this.grupoUsuarioDAO.getGruposNoPertenecientesUsuario(idUsuario);
    }

    public GrupoUsuarioDAO getGrupoUsuarioDAO() {
        return this.grupoUsuarioDAO;
    }

    public void setGrupoUsuarioDAO(GrupoUsuarioDAO grupoUsuarioDAO) {
        this.grupoUsuarioDAO = grupoUsuarioDAO;
    }
}
