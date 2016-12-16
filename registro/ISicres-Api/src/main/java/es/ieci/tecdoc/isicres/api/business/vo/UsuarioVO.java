package es.ieci.tecdoc.isicres.api.business.vo;

import java.util.List;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.GrupoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;

/**
 * @author Iecisa
 * @version $Revision$
 * 
 */

public class UsuarioVO extends BaseUsuarioVO {

	private static final long serialVersionUID = -6272248632850674808L;

	/**
	 * 
	 */
	protected ConfiguracionUsuarioVO configuracionUsuario;
	protected DepartamentoUsuarioVO departamentoUsuario;

	/**
	 * listado de grupos a los que pertenece el usuario
	 */
	protected List<GrupoUsuarioVO> gruposUsuario;

	/**
	 * Listado de oficinas a las que pertenece el usuario {@link OficinaVO}
	 */
	protected List<OficinaVO> oficinas;

	/**
	 * Información sobre los permisos efectivos que tiene el usuario
	 */
	protected PermisosUsuarioVO permisos;

	public ConfiguracionUsuarioVO getConfiguracionUsuario() {
		if (null == this.configuracionUsuario) {
			this.configuracionUsuario = new ConfiguracionUsuarioVO();
		}
		return configuracionUsuario;
	}

	public void setConfiguracionUsuario(
			ConfiguracionUsuarioVO configuracionUsuario) {
		this.configuracionUsuario = configuracionUsuario;
	}

	public PermisosUsuarioVO getPermisos() {
		return permisos;
	}

	public void setPermisos(PermisosUsuarioVO permisos) {
		this.permisos = permisos;
	}


	public List<GrupoUsuarioVO> getGruposUsuario() {
		return this.gruposUsuario;
	}

	public void setGruposUsuario(List<GrupoUsuarioVO> gruposUsuario) {
		this.gruposUsuario = gruposUsuario;
	}

	public List<OficinaVO> getOficinas() {
		return this.oficinas;
	}

	public void setOficinas(List<OficinaVO> oficinas) {
		this.oficinas = oficinas;
	}

	public DepartamentoUsuarioVO getDepartamentoUsuario() {
		return this.departamentoUsuario;
	}

	public void setDepartamentoUsuario(DepartamentoUsuarioVO departamentoUsuario) {
		this.departamentoUsuario = departamentoUsuario;
	}

}
