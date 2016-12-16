package es.ieci.tecdoc.isicres.api.business.manager;

import es.ieci.tecdoc.isicres.api.business.vo.BaseDepartamentoVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.util.List;

public abstract class DepartamentoManager {
    public abstract DepartamentoUsuarioVO getDepartamentoUsuario(UsuarioVO var1);

    public abstract DepartamentoOficinaVO getDepartamentoOficina(OficinaVO var1);

    public abstract List<BaseDepartamentoVO> getDepartamentos();

    public abstract BaseDepartamentoVO getDepartamentoById(Integer var1);

    public abstract List<BaseDepartamentoVO> getDepartamentosGrupoLdap(Integer var1);
}
