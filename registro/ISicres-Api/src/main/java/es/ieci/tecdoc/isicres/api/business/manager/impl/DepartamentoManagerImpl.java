package es.ieci.tecdoc.isicres.api.business.manager.impl;

import es.ieci.tecdoc.isicres.api.business.dao.DepartamentoDAO;
import es.ieci.tecdoc.isicres.api.business.manager.DepartamentoManager;
import es.ieci.tecdoc.isicres.api.business.vo.BaseDepartamentoVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.util.List;

public class DepartamentoManagerImpl
extends DepartamentoManager {
    protected DepartamentoDAO departamentoDAO;

    @Override
    public DepartamentoUsuarioVO getDepartamentoUsuario(UsuarioVO usuario) {
        return this.departamentoDAO.getDepartamentoUsuario(usuario);
    }

    @Override
    public DepartamentoOficinaVO getDepartamentoOficina(OficinaVO oficina) {
        return this.departamentoDAO.getDepartamentoOficina(oficina);
    }

    @Override
    public BaseDepartamentoVO getDepartamentoById(Integer idDepartamento) {
        return this.departamentoDAO.getDepartamentoById(idDepartamento);
    }

    @Override
    public List<BaseDepartamentoVO> getDepartamentos() {
        return this.departamentoDAO.findDepartamentos();
    }

    @Override
    public List<BaseDepartamentoVO> getDepartamentosGrupoLdap(Integer idGrupoLdap) {
        return this.departamentoDAO.getDepartamentosGrupoLdap(idGrupoLdap);
    }

    public DepartamentoDAO getDepartamentoDAO() {
        return this.departamentoDAO;
    }

    public void setDepartamentoDAO(DepartamentoDAO departamentoDAO) {
        this.departamentoDAO = departamentoDAO;
    }
}
