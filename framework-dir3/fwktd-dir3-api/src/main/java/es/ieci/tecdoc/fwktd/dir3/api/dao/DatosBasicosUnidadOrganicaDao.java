package es.ieci.tecdoc.fwktd.dir3.api.dao;

import java.util.List;

import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosUnidadOrganicaVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioUnidadOrganicaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.dao.BaseDao;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;

/**
 * Interfaz de los DAOs de datos b�sicos de unidades org�nicas.
 *
 * @author Iecisa
 * @version $Revision$
 *
 */
public interface DatosBasicosUnidadOrganicaDao extends
		BaseDao<DatosBasicosUnidadOrganicaVO, String> {

	/**
	 * Obtiene el n�mero de unidades org�nicas encontradas seg�n los criterios
	 * de b�squeda.
	 *
	 * @param criterios
	 *            Criterios de b�squeda.
	 * @return N�mero de unidades org�nicas encontradas.
	 */
	public int countUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios);

	/**
	 * Realiza una b�squeda de unidades org�nicas.
	 *
	 * @param criterios
	 *            Criterios de b�squeda.
	 * @return Unidades org�nicas encontradas.
	 */
	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios);

	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicasByEntidad(DatosBasicosRelacionUnidOrgOficinaVO var1);
}
