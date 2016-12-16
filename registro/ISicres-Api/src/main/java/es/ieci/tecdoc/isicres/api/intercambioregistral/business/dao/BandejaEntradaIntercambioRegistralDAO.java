package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao;

import java.util.List;

import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaEntradaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralEntradaVO;
import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;

/**
 * DAO para leer y actualizar datos de la bandeja de entrada de intercambio registral
 *
 */
public interface BandejaEntradaIntercambioRegistralDAO {

	/**
	 * Guarda un intercambio registral que acabamos de aceptar
	 * @param intecambioRegistralEntrada
	 */
	public void save(IntercambioRegistralEntradaVO intecambioRegistralEntrada);

	/**
	 * Metodo que obtiene la informacion del estado del intercambio
	 * @param {{@link List} - Listado de objetos IntercambioRegistralEntradaVO
	 */
	public List<IntercambioRegistralEntradaVO> getInfoEstado(IntercambioRegistralEntradaVO intecambioRegistralEntrada);

	/**
	 * Obtiene la bandeja de entrada para el siguiente <code>estado</code>
	 * @param estado
	 * @return
	 */
	public List<BandejaEntradaItemVO> getBandejaEntradaByEstado(Integer estado,Integer idOficina);

	/**
	 * Completa datos del elemento <code>bandejaEntradaItemVO</code> que no se cargan
	 * al leer la bandeja de entrada completa
	 * @param bandejaEntradaItemVO
	 * @return
	 */
	public BandejaEntradaItemVO completarBandejaEntradaItem(BandejaEntradaItemVO bandejaEntradaItemVO);

	public void delete(IntercambioRegistralEntradaVO var1);

	public void updateEstado(IntercambioRegistralEntradaVO var1, EstadoIntercambioRegistralEntradaEnumVO var2);

	public List<BandejaEntradaItemVO> findByCriterios(EstadoIntercambioRegistralEntradaEnumVO var1, CriteriosBusquedaIREntradaVO var2);

	public PaginatedArrayList<BandejaEntradaItemVO> findByCriterios(EstadoIntercambioRegistralEntradaEnumVO var1, CriteriosBusquedaIREntradaVO var2, PageInfo var3);

	public IntercambioRegistralEntradaVO getIntercambioRegistralEntradaByRegistro(Integer var1, Integer var2, Integer var3);

	public List<IntercambioRegistralEntradaVO> getIntercambioRegistralEntradaByIdIntercambioRegistralSir(Integer var1, String var2);
}
