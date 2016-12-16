package es.ieci.tecdoc.isicres.api.business.dao;

import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import java.util.List;

public interface GrupoUsuarioDAO {
    public GrupoUsuarioVO getGrupoById(Integer var1);

    public List<GrupoUsuarioVO> getGrupos();

    public List<GrupoUsuarioVO> getGruposLdap();

    public List<BaseUsuarioVO> getUsuariosDelGrupo(Integer var1);

    public List<GrupoUsuarioVO> getGruposPertenecientesUsuario(Integer var1);

    public List<GrupoUsuarioVO> getGruposNoPertenecientesUsuario(Integer var1);
}
