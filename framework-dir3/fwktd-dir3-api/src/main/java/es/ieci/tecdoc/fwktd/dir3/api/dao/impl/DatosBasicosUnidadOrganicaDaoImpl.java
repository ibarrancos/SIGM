package es.ieci.tecdoc.fwktd.dir3.api.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.ibatis.sqlmap.engine.execution.SqlExecutor;

import es.ieci.tecdoc.fwktd.dir3.api.dao.DatosBasicosUnidadOrganicaDao;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosUnidadOrganicaVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioUnidadOrganicaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.dao.ibatis.IbatisGenericDaoImpl;
import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;

/**
 * DAO de datos b�sicos de unidades org�nicas.
 *
 * @author Iecisa
 * @version $Revision$
 *
 */
public class DatosBasicosUnidadOrganicaDaoImpl extends
		IbatisGenericDaoImpl<DatosBasicosUnidadOrganicaVO, String>
		implements DatosBasicosUnidadOrganicaDao {

	protected static final String COUNT_FIND_UNIDADES_ORGANICAS = "DatosBasicosUnidadOrganicaVO.countFindUnidadesOrganicas";
	protected static final String FIND_UNIDADES_ORGANICAS = "DatosBasicosUnidadOrganicaVO.findUnidadesOrganicas";
	protected static final String FIND_UNIDADES_ORGANICAS_BY_ENTIDAD = "DatosBasicosUnidadOrganicaVO.findUnidadesOrganicasByEntidad";

	/**
	 * Constructor con par�metros de la clase. Establece el tipo de entidad a
	 * persistir/recuperar.
	 *
	 * @param aPersistentClass
	 *            tipo de objeto a persistir/recuperar
	 */
	public DatosBasicosUnidadOrganicaDaoImpl(
			Class<DatosBasicosUnidadOrganicaVO> aPersistentClass) {
		super(aPersistentClass);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.dao.DatosBasicosUnidadOrganicaDao#countUnidadesOrganicas(es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios)
	 */
	public int countUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios) {

		// Comprobar si se han definido criterios
		if ((criterios == null) || CollectionUtils.isEmpty(criterios.getCriterios())) {
			logger.info("Obteniendo el n�mero de unidades org�nicas sin criterios");
			return count();
		}

		logger.info("Obteniendo el n�mero de unidades org�nicas en base a los siguientes criterios: {}", criterios);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("criterios", criterios.getCriterios());

		return (Integer) getSqlMapClientTemplate().queryForObject(COUNT_FIND_UNIDADES_ORGANICAS, map);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see es.ieci.tecdoc.fwktd.dir3.api.dao.DatosBasicosUnidadOrganicaDao#findUnidadesOrganicas(es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios)
	 */
	@SuppressWarnings("unchecked")
	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios) {

 		// Comprobar si se han definido criterios
		if ((criterios == null) || CollectionUtils.isEmpty(criterios.getCriterios())) {
			logger.info("Realizando b�squeda de unidades org�nicas sin criterios");
			return getAll();
		}

		logger.info("Realizando b�squeda de unidades org�nicas en base a los siguientes criterios: {}", criterios);

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("criterios", criterios.getCriterios());
		map.put("orden", criterios.getOrderBy());

		// Comprobar si hay que paginar los resultados
		PageInfo pageInfo = criterios.getPageInfo();
		if (pageInfo != null) {

			// N�mero de resultados a ignorar
			int skipResults = SqlExecutor.NO_SKIPPED_RESULTS;

			// N�mero m�ximo de resultados.
			int maxResults = SqlExecutor.NO_MAXIMUM_RESULTS;

			if ((pageInfo.getPageNumber() > 0) && (pageInfo.getObjectsPerPage() > 0)) {
				skipResults = (pageInfo.getPageNumber() - 1) * pageInfo.getObjectsPerPage();
				maxResults = pageInfo.getObjectsPerPage();
			} else if (pageInfo.getMaxNumItems() > 0) {
				maxResults = pageInfo.getMaxNumItems();
			}

			// Obtener los resultados a mostrar en la p�gina
			List<DatosBasicosUnidadOrganicaVO> list = (List<DatosBasicosUnidadOrganicaVO>) getSqlMapClientTemplate().queryForList(FIND_UNIDADES_ORGANICAS, map,
					skipResults, maxResults);

			// Obtener el total de resultados
			int fullListSize;
			if (((maxResults == SqlExecutor.NO_MAXIMUM_RESULTS) || (list.size() < maxResults))
					&& (skipResults == SqlExecutor.NO_SKIPPED_RESULTS)) {
				fullListSize = list.size();
			} else {
				fullListSize = (Integer) getSqlMapClientTemplate().queryForObject(COUNT_FIND_UNIDADES_ORGANICAS, map);
			}

			// Informaci�n de los resultados paginados
			PaginatedArrayList<DatosBasicosUnidadOrganicaVO> resultados = new PaginatedArrayList<DatosBasicosUnidadOrganicaVO>(pageInfo);
			resultados.setFullListSize(fullListSize);
			resultados.setList(list);

			return resultados;

		} else {
			return (List<DatosBasicosUnidadOrganicaVO>) getSqlMapClientTemplate().queryForList(FIND_UNIDADES_ORGANICAS, map);
		}
	}

	@Override
	public List<DatosBasicosUnidadOrganicaVO> findUnidadesOrganicasByEntidad(DatosBasicosRelacionUnidOrgOficinaVO relacion) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("codeEntity", relacion.getCodigoOficina());
		params.put("codeUnid", relacion.getCodigoUnidadOrganica());
		params.put("nameUnid", relacion.getDenominacionUnidadOrganica());
		return this.getSqlMapClientTemplate().queryForList("DatosBasicosUnidadOrganicaVO.findUnidadesOrganicasByEntidad", params);
	}
}
