package es.ieci.tecdoc.isicres.api.business.dao;

import es.ieci.tecdoc.isicres.api.business.vo.BaseUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.PermisosUsuarioVO;
import java.util.List;

public interface UsuarioDAO {
    public BaseUsuarioVO getUsuarioById(Integer var1);

    public BaseUsuarioVO getUsuarioByLogin(String var1);

    public List<BaseUsuarioVO> getUsuarios();

    public List<BaseUsuarioVO> getUsuariosLdap();

    public ConfiguracionUsuarioVO getConfiguracionUsuario(Integer var1);

    public PermisosUsuarioVO getPermisosUsuario(Integer var1);
}
