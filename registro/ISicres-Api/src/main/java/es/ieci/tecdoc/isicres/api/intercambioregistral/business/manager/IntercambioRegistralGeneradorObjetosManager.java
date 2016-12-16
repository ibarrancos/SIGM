package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager;

import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralFormVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.UnidadTramitacionIntercambioRegistralVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.fwktd.sir.core.vo.InteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InfoAsientoRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InteresadoExReg;

/**
 * Manager para construir objetos necesitados por el SIR
 */
public interface IntercambioRegistralGeneradorObjetosManager {

	/**
	 * Compone el <code>AsientoRegistralFormVO</code> necesario para enviar un intercambio registral
	 * La unidad de tramitación destino se obtiene a partir del destino del registro
	 * No debería ser necesario utilizarlo
	 * @deprecated
	 * @param intercambioSalidaVO
	 * @param entidad
	 * @return
	 */
	public AsientoRegistralFormVO getAsientoRegistralIntercambioRegistralVO(IntercambioRegistralSalidaVO intercambioSalidaVO, String entidad);

	/**
	 * Compone el <code>AsientoRegistralFormVO</code> necesario para enviar un intercambio registral
	 * @param intercambioSalidaVO
	 * @param unidadTramitacionDestino
	 * @return
	 */
	public AsientoRegistralFormVO getAsientoRegistralIntercambioRegistralVO(
			IntercambioRegistralSalidaVO intercambioSalidaVO, UnidadTramitacionIntercambioRegistralVO unidadTramitacionDestino);

	public InteresadoExReg getInteresadoExReg(InteresadoVO var1);

	public InfoAsientoRegistralVO getInfoAsientoRegistralVO(AsientoRegistralVO var1);
}
