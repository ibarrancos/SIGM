package es.ieci.tecdoc.isicres.api.business.manager;

import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import java.util.List;

public abstract class GrupoManager {
    public abstract GrupoUsuarioVO getGrupoById(Integer var1);

    public abstract List<GrupoUsuarioVO> getGrupos();

    public abstract List<BaseUsuarioVO> getUsuariosDelGrupo(Integer var1);

    public abstract List<GrupoUsuarioVO> getGruposPertenecientesUsuario(Integer var1);

    public abstract List<GrupoUsuarioVO> getGruposNoPertenecientesUsuario(Integer var1);
}
