package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager;

import java.util.List;

import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EntidadRegistralDCO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EntidadRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadAdministrativaIntercambioRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionDCO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionIntercambioRegistralVO;

public interface ConfiguracionIntercambioRegistralManager {


	/**
	 * Obtiene la unidad administrativa a la que pertenecen los codigos del directorio comun pasados como parametro
	 * En caso de que este mapeada la unidad de tramitacion, se obtiene ese, si no, el de entidad registral.
	 * @param codeEntidadRegistral
	 * @param codeUnidadTramitacion
	 * @return
	 */
	public UnidadAdministrativaIntercambioRegistralVO getUnidadAdministrativaByCodigosComunes(String codeEntidadRegistral, String codeUnidadTramitacion);

	/**
	 * Obtiene la entidad registral mapeada en el modulo comun para este <code>idScrOfic</code>
	 * @param idSrcOfic
	 * @return
	 */
	public EntidadRegistralVO getEntidadRegistralVOByIdScrOfic(String idOfic);

	/**
	 * Obtiene la unidad de tramitacion mapeada en el modulo comun para este <code>idScrOrgs</code>
	 * @param idScrOrgs
	 * @return
	 */
	public UnidadTramitacionIntercambioRegistralVO getUnidadTramitacionIntercambioRegistralVOByIdScrOrgs(String idScrOrgs );

	/**
	 * Obtiene la unidad de tramitacion mapeada en el modulo comun para este <code>codeSrcOrgs</code>
	 * @param codeScrOrgs
	 * @return
	 */
	public UnidadTramitacionIntercambioRegistralVO getUnidadTramitacionIntercambioRegistralVOByCodeScrOrgs(String codeScrOrgs );

	/**
	 * M�todo de b�squeda de Entidades Registrales en el DCO
	 * @param code
	 * @param nombre
	 * @return Lista de <code>EntidadRegistralDCO</code> del DCO que concuerden con el code y nombre pasado
	 */
	public List<EntidadRegistralDCO> buscarEntidadesRegistralesDCO(String code, String nombre);

	/**
	 * M�todo de b�squeda de Unidades de Tramitaci�n en el DCO
	 * @param code
	 * @param nombre
	 * @return Lista de <code>UnidadTramitacionDCO</code> del DCO que concuerden con el code y nombre pasado
	 */
	public List<UnidadTramitacionDCO> buscarUnidadesTramitacionDCO(String code, String nombre);

	public UnidadAdministrativaIntercambioRegistralVO getUnidadAdmimistrativaByCodigoEntidadRegistralYUnidadTramitacion(String var1, String var2);
	public List<UnidadAdministrativaIntercambioRegistralVO> getUnidadAdmimistrativaByCodigoEntidadRegistral(String var1);
	public List<UnidadTramitacionDCO> buscarUnidadesTramitacionDCOByEntidad(String var1, String var2, String var3);
	public boolean existRelacionUnidOrgaOficina(String var1, String var2);
}
