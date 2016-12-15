package es.ieci.tecdoc.fwktd.dir3.core.service;

import java.util.List;

import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioOficinaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioUnidadOrganicaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.dir3.core.vo.DatosBasicosOficina;
import es.ieci.tecdoc.fwktd.dir3.core.vo.DatosBasicosUnidadOrganica;
import es.ieci.tecdoc.fwktd.dir3.core.vo.DatosBasicosRelacionUnidOrgOficina;

/**
 * Interfaz del servicio de consulta del Directorio Com�n (DIR3).
 *
 * @author Iecisa
 * @version $Revision$
 *
 */
public interface ServicioConsultaDirectorioComun {

	/**
	 * Obtiene el n�mero de oficinas encontradas seg�n los criterios de
	 * b�squeda.
	 *
	 * @param criterios
	 *            Criterios de b�squeda.
	 * @return N�mero de oficinas encontradas.
	 */
	public int countOficinas(Criterios<CriterioOficinaEnum> criterios);

	/**
	 * Realiza una b�squeda de oficinas.
	 *
	 * @param criterios
	 *            Criterios de b�squeda.
	 * @return Oficinas encontradas.
	 */
	public List<DatosBasicosOficina> findOficinas(
			Criterios<CriterioOficinaEnum> criterios);

	/**
	 * Obtiene los datos b�sicos de una oficina.
	 *
	 * @param id
	 *            Identificador de la oficina.
	 * @return Datos b�sicos de la oficina.
	 */
	public DatosBasicosOficina getDatosBasicosOficina(String id);

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
	public List<DatosBasicosUnidadOrganica> findUnidadesOrganicas(
			Criterios<CriterioUnidadOrganicaEnum> criterios);

	/**
	 * Obtiene los datos b�sicos de una unidad org�nica.
	 *
	 * @param id
	 *            Identificador de la unidad org�nica.
	 * @return Datos b�sicos de la unidad org�nica.
	 */
	public DatosBasicosUnidadOrganica getDatosBasicosUnidadOrganica(String id);
	
	public List<DatosBasicosUnidadOrganica> findUnidadesOrganicasByEntidad(String var1, String var2, String var3);

	public DatosBasicosRelacionUnidOrgOficina getDatosBasicosRelacionUnidOrgOficinaByCodes(String var1, String var2);

}
