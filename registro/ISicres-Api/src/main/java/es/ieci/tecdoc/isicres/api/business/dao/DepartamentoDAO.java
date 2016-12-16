package es.ieci.tecdoc.isicres.api.business.dao;

import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.BaseDepartamentoVO;
import java.util.List;

public interface DepartamentoDAO {
	
	public DepartamentoUsuarioVO getDepartamentoUsuario(UsuarioVO usuario);
	public DepartamentoOficinaVO getDepartamentoOficina (OficinaVO oficina);
	public List<BaseDepartamentoVO> findDepartamentos();
	public BaseDepartamentoVO getDepartamentoById(Integer var1);
	public List<BaseDepartamentoVO> getDepartamentosGrupoLdap(Integer var1);
	

}
