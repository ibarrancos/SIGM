package es.ieci.tecdoc.fwktd.dir3.api.manager;

import java.util.List;

import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosUnidadOrganicaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.unidad.OrganismosVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioUnidadOrganicaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.manager.BaseManager;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;

/**
 * Interfaz para los managers de datos b�sicos de unidades org�nicas.
 *
 * @author Iecisa
 * @version $Revision$
 *
 */
public interface DatosBasicosUnidadOrganicaManager extends
		BaseManager<DatosBasicosUnidadOrganicaVO, String> {

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


	/**
	 * Realiza una b�squeda de unidades org�nicas seg�n el c�digo de unidad indicado
	 *
	 * @param codeUnidadOrganica - C�digo de unidad organica a buscar
	 * @return {@link DatosBasicosUnidadOrganicaVO} o NULO en caso de no encontrar nada
	 */
	public DatosBasicosUnidadOrganicaVO getDatosBasicosUnidadOrganicaByCode(
			String codeUnidadOrganica);
	/**
	 * Guarda los datos basicos de las unidades obtenidas del DCO
	 *
	 * @param organismosDCO
	 */
	public void saveDatosBasicosUnidadesOrganicas(OrganismosVO organismosDCO);

	/**
	 * Actualiza los datos basicos de las unidades obtenidas del DCO
	 * @param organismosDCO
	 */
	public void updateDatosBasicosUnidadesOrganicas(OrganismosVO organismosDCO);

	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicasByEntidad(DatosBasicosRelacionUnidOrgOficinaVO var1);
}
