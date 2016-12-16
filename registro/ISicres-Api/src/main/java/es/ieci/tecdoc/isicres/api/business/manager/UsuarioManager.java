package es.ieci.tecdoc.isicres.api.business.manager;

import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.util.List;

public abstract class UsuarioManager {
    public abstract BaseUsuarioVO getUsuarioById(Integer var1);

    public abstract UsuarioVO getUsuario(Integer var1);

    public abstract BaseUsuarioVO getUsuarioByName(String var1);

    public abstract List<BaseUsuarioVO> getUsuarios();

    public abstract List<BaseUsuarioVO> getUsuariosLdap();

    public abstract List<OficinaVO> getOficinasUsuario(UsuarioVO var1);

    public abstract List<GrupoUsuarioVO> getGruposUsuario(Integer var1);

    public abstract ConfiguracionUsuarioVO getConfiguracionUsuario(Integer var1);

    public abstract PermisosUsuarioVO getPermisosUsuario(Integer var1);
}
