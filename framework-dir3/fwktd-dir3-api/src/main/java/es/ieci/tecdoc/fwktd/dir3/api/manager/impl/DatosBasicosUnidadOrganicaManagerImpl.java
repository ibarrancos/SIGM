package es.ieci.tecdoc.fwktd.dir3.api.manager.impl;

import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;

import es.ieci.tecdoc.fwktd.dir3.api.dao.DatosBasicosUnidadOrganicaDao;
import es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosUnidadOrganicaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.unidad.OrganismoVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.unidad.OrganismosVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioUnidadOrganicaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.dao.BaseDao;
import es.ieci.tecdoc.fwktd.server.manager.impl.BaseManagerImpl;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;

/**
 * Implementaci�n del manager de datos b�sicos de unidades org�nicas.
 *
 * @author Iecisa
 * @version $Revision$
 *
 */
public class DatosBasicosUnidadOrganicaManagerImpl extends
		BaseManagerImpl<DatosBasicosUnidadOrganicaVO, String> implements
		DatosBasicosUnidadOrganicaManager {

	private static final String VIGENTE = "V";
	/**
	 * Logger de la clase.
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(DatosBasicosUnidadOrganicaManagerImpl.class);

	/**
	 * Constructor.
	 *
	 * @param aGenericDao
	 */
	public DatosBasicosUnidadOrganicaManagerImpl(
			BaseDao<DatosBasicosUnidadOrganicaVO, String> aGenericDao) {
		super(aGenericDao);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager#countUnidadesOrganicas(es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios)
	 */
	public int countUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios) {

		logger.info("Obteniendo el n�mero de unidades org�nicas. Criterios: {}", criterios);

		// Obtiene el n�mero de unidades org�nicas en base a los criterios
		return ((DatosBasicosUnidadOrganicaDao)getDao()).countUnidadesOrganicas(criterios);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager#findUnidadesOrganicas(es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios)
	 */
	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios) {

		logger.info("Realizando b�squeda de unidades org�nicas. Criterios: {}", criterios);

		// Realiza la b�squeda de unidades org�nicas en base a los criterios
		return ((DatosBasicosUnidadOrganicaDao)getDao()).findUnidadesOrganicas(criterios);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager#saveDatosBasicosUnidadesOrganicas(es.ieci.tecdoc.fwktd.dir3.api.vo.unidad.OrganismosVO)
	 */
	public void saveDatosBasicosUnidadesOrganicas(OrganismosVO organismosDCO) {
		ListIterator<OrganismoVO> itr = organismosDCO.getOrganismos().listIterator();
		OrganismoVO organismo = null;
		while(itr.hasNext()){
			organismo = itr.next();
			if(VIGENTE.equals(organismo.getDatosVigencia().getEstado())){
				guardarUnidadOrganica(organismo);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager#updateDatosBasicosUnidadesOrganicas(es.ieci.tecdoc.fwktd.dir3.api.vo.unidad.OrganismosVO)
	 */
	public void updateDatosBasicosUnidadesOrganicas(OrganismosVO organismosDCO) {
		ListIterator<OrganismoVO> itr = organismosDCO.getOrganismos()
				.listIterator();
		OrganismoVO organismo = null;
		while (itr.hasNext()) {
			organismo = itr.next();

			// Buscamos datos sobre la unidad organica
			DatosBasicosUnidadOrganicaVO datosBasicosUnidadOrganica = getDatosBasicosUnidadOrganicaByCode(organismo
					.getDatosIdentificativos().getCodigoUnidadOrganica());

			if (null != datosBasicosUnidadOrganica) {
				// Actualizamos o Borramos
				actualizarOBorrarUnidadOrganica(organismo,
						datosBasicosUnidadOrganica);
			} else {
				// Guardamos datos
				guardarUnidadOrganica(organismo);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager#getDatosBasicosUnidadOrganicaByCode(java.lang.String)
	 */
	public DatosBasicosUnidadOrganicaVO getDatosBasicosUnidadOrganicaByCode(
			String codeUnidadOrganica) {
		DatosBasicosUnidadOrganicaVO result = null;
		try {
			// Buscamos datos basicos de la unidad organica
			result = ((DatosBasicosUnidadOrganicaDao) getDao()).get(codeUnidadOrganica);

		} catch (ObjectRetrievalFailureException oRFE) {
			if (logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer();
				sb.append(
						"No se ha encontrado datos para el organismo con c�digo: ")
						.append(codeUnidadOrganica);
				logger.debug(sb.toString());
			}
		}
		return result;
	}

	/**
	 * M�todo encargado de actualizar o borrar la
	 * {@link DatosBasicosUnidadOrganicaVO} seg�n el estado del
	 * {@link OrganismosVO} pasado como par�metro
	 *
	 * @param organismo
	 *            - Datos con el organismo a borrar
	 * @param datosBasicosUnidadOrganica
	 *            - Objeto que se borra
	 */
	private void actualizarOBorrarUnidadOrganica(OrganismoVO organismo,
			DatosBasicosUnidadOrganicaVO datosBasicosUnidadOrganica) {
		if(VIGENTE.equals(organismo.getDatosVigencia().getEstado())){
			setDatosBasicosOrganismo(datosBasicosUnidadOrganica,organismo);
			//Actualizamos los datos basicos de la unidad organica
			((DatosBasicosUnidadOrganicaDao) getDao()).update(datosBasicosUnidadOrganica);
		}else{
			//Borramos el organismo
			((DatosBasicosUnidadOrganicaDao) getDao()).delete(datosBasicosUnidadOrganica.getId());
		}
	}

	/**
	 * M�todo que almacena en BBDD los datos b�sicos de una unidad org�nica a
	 * partir del organismo pasado como par�metro
	 *
	 * @param organismo
	 *            - Datos del organismo
	 *
	 */
	private void guardarUnidadOrganica(OrganismoVO organismo) {
		if ("V".equals(organismo.getDatosVigencia().getEstado())) {
			DatosBasicosUnidadOrganicaVO datosBasicosUnidadOrganica = new DatosBasicosUnidadOrganicaVO();
			this.setDatosBasicosOrganismo(datosBasicosUnidadOrganica, organismo);
			((DatosBasicosUnidadOrganicaDao)this.getDao()).save(datosBasicosUnidadOrganica);
		}
	}

	/**
	 * M�todo encargado de seteatar los datos b�sicos de un objeto
	 * {@link OrganismoVO} a {@link DatosBasicosUnidadOrganicaVO}
	 *
	 * @param datosBasicosUnidadOrganica
	 *            - Unidad Organica en la que se copian los datos del organismo
	 * @param organismo
	 *            - Datos que se transforman en un
	 *            {@link DatosBasicosUnidadOrganicaVO}
	 *
	 */
	private void setDatosBasicosOrganismo(DatosBasicosUnidadOrganicaVO datosBasicosUnidadOrganica, OrganismoVO organismo)
	{
		datosBasicosUnidadOrganica.setId(organismo.getDatosIdentificativos().getCodigoUnidadOrganica());
		datosBasicosUnidadOrganica.setNombre(organismo.getDatosIdentificativos().getNombreUnidadOrganica());
		datosBasicosUnidadOrganica.setIdExternoFuente(organismo.getDatosIdentificativos().getCodigoExterno());
		datosBasicosUnidadOrganica.setIdUnidadOrganicaSuperior(organismo.getDatosDependencia().getCodigoUnidadSuperiorJerarquica());

	}

	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicasByEntidad(DatosBasicosRelacionUnidOrgOficinaVO relacion) {
		return ((DatosBasicosUnidadOrganicaDao)this.getDao()).findUnidadesOrganicasByEntidad(relacion);
	}
}
