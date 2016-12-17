package es.ieci.tecdoc.isicres.terceros.business.dao;

import java.util.List;

import es.ieci.tecdoc.fwktd.server.dao.BaseReadOnlyDao;
import es.ieci.tecdoc.isicres.terceros.business.vo.CiudadVO;
import es.ieci.tecdoc.isicres.terceros.business.vo.ProvinciaVO;

/**
 *
 */
public interface CiudadDao extends BaseReadOnlyDao<CiudadVO, String> {

	public CiudadVO findByCodigo(String codigo);

	public CiudadVO findByNombre(String nombre);

	/**
	 * Devuelve las ciudades de la provincia <code>provincia</code>.
	 *
	 * @param provincia
	 * @return
	 */
	public List<CiudadVO> getCiudadesByProvincia(ProvinciaVO provincia);


	public List<CiudadVO> getCiudades(int var1, int var2);

	public Integer getCiudadesCount();

	public Integer getCiudadesByProvinciaCount(ProvinciaVO var1);

	public List<CiudadVO> getCiudadesByProvincia(ProvinciaVO var1, int var2, int var3);
}
