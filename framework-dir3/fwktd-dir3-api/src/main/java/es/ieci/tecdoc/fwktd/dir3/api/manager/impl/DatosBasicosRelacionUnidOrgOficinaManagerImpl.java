package es.ieci.tecdoc.fwktd.dir3.api.manager.impl;

import es.ieci.tecdoc.fwktd.dir3.api.dao.DatosBasicosRelacionUnidOrgOficinaDao;
import es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosRelacionUnidOrgOficinaManager;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionesUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioEnum;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioOficinaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioRelacionUnidOrgOficinaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.type.OperadorCriterioEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterio;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.dao.BaseDao;
import es.ieci.tecdoc.fwktd.server.dao.BaseReadOnlyDao;
import es.ieci.tecdoc.fwktd.server.manager.impl.BaseManagerImpl;
import java.util.List;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatosBasicosRelacionUnidOrgOficinaManagerImpl
extends BaseManagerImpl<DatosBasicosRelacionUnidOrgOficinaVO, String>
implements DatosBasicosRelacionUnidOrgOficinaManager {
    private static final String VIGENTE = "V";
    private static final Logger logger = LoggerFactory.getLogger((Class)DatosBasicosRelacionUnidOrgOficinaManagerImpl.class);

    public DatosBasicosRelacionUnidOrgOficinaManagerImpl(BaseDao<DatosBasicosRelacionUnidOrgOficinaVO, String> aGenericDao) {
        super(aGenericDao);
    }

    @Override
    public int countRelaciones(Criterios<CriterioOficinaEnum> criterios) {
        logger.info("Obteniendo el n\u00famero de relaciones entre las oficinas y las unidades org\u00e1nicas. Criterios: {}", criterios);
        return ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).countRelacionesUnidOrgOficina(criterios);
    }

    @Override
    public List<DatosBasicosRelacionUnidOrgOficinaVO> findRelaciones(Criterios<CriterioOficinaEnum> criterios) {
        logger.info("Realizando b\u00fasqueda de relaciones entre las oficinas y las unidades org\u00e1nicas. Criterios: {}", criterios);
        return ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).findRelacionesUnidOrgOficina(criterios);
    }

    @Override
    public DatosBasicosRelacionUnidOrgOficinaVO getRelacionesByOficinaAndUnidad(String codOficina, String codUnidad) {
        DatosBasicosRelacionUnidOrgOficinaVO result = null;
        Criterios criterios = new Criterios().addCriterio(new Criterio((CriterioEnum)CriterioRelacionUnidOrgOficinaEnum.OFICINA_ID, OperadorCriterioEnum.EQUAL, (Object)codOficina)).addCriterio(new Criterio((CriterioEnum)CriterioRelacionUnidOrgOficinaEnum.UO_ID, OperadorCriterioEnum.EQUAL, (Object)codUnidad));
        List<DatosBasicosRelacionUnidOrgOficinaVO> listado = ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).findRelacionesUnidOrgOficina(criterios);
        if (!listado.isEmpty()) {
            result = listado.get(0);
        }
        return result;
    }

    @Override
    public void saveDatosBasicosRelacionesUnidOrgOficinaVO(RelacionesUnidOrgOficinaVO relacionesUnidOrgOficina) {
        ListIterator<RelacionUnidOrgOficinaVO> itr = relacionesUnidOrgOficina.getRelacionesUnidOrgOficinaVO().listIterator();
        RelacionUnidOrgOficinaVO relacionUnidOrgOficina = null;
        while (itr.hasNext()) {
            relacionUnidOrgOficina = itr.next();
            if (!"V".equals(relacionUnidOrgOficina.getEstadoRelacion())) continue;
            this.guardarRelacionUnidOrgOficina(relacionUnidOrgOficina);
        }
    }

    private void guardarRelacionUnidOrgOficina(RelacionUnidOrgOficinaVO relacionUnidOrgOficina) {
        DatosBasicosRelacionUnidOrgOficinaVO datosBasicos = this.setDatosRelacionUniOrgOficinaVO(relacionUnidOrgOficina);
        ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).save((Object)datosBasicos);
    }

    private DatosBasicosRelacionUnidOrgOficinaVO setDatosRelacionUniOrgOficinaVO(RelacionUnidOrgOficinaVO relacionUnidOrgOficina) {
        DatosBasicosRelacionUnidOrgOficinaVO datosBasicos = new DatosBasicosRelacionUnidOrgOficinaVO();
        datosBasicos.setId("0");
        datosBasicos.setCodigoOficina(relacionUnidOrgOficina.getCodigoOficina());
        datosBasicos.setCodigoUnidadOrganica(relacionUnidOrgOficina.getCodigoUnidadOrganica());
        datosBasicos.setDenominacionOficina(relacionUnidOrgOficina.getDenominacionOficina());
        datosBasicos.setDenominacionUnidadOrganica(relacionUnidOrgOficina.getDenominacionUnidadOrganica());
        datosBasicos.setEstadoRelacion(relacionUnidOrgOficina.getEstadoRelacion());
        return datosBasicos;
    }

    @Override
    public void updateDatosBasicosRelacionesUnidOrgOficinaVO(RelacionesUnidOrgOficinaVO relacionesUnidOrgOficina) {
        ListIterator<RelacionUnidOrgOficinaVO> itr = relacionesUnidOrgOficina.getRelacionesUnidOrgOficinaVO().listIterator();
        RelacionUnidOrgOficinaVO relacionUnidOrgOficina = null;
        while (itr.hasNext()) {
            relacionUnidOrgOficina = itr.next();
            DatosBasicosRelacionUnidOrgOficinaVO datosBasicosUnidadOrganica = this.getRelacionesByOficinaAndUnidad(relacionUnidOrgOficina.getCodigoOficina(), relacionUnidOrgOficina.getCodigoUnidadOrganica());
            if (null != datosBasicosUnidadOrganica) {
                this.actualizarOBorrarRelacionUnidOrgOficina(relacionUnidOrgOficina, datosBasicosUnidadOrganica);
                continue;
            }
            if (!"V".equals(relacionUnidOrgOficina.getEstadoRelacion())) continue;
            ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).save((Object)this.setDatosRelacionUniOrgOficinaVO(relacionUnidOrgOficina));
        }
    }

    private void actualizarOBorrarRelacionUnidOrgOficina(RelacionUnidOrgOficinaVO relacionUnidOrgOficina, DatosBasicosRelacionUnidOrgOficinaVO datosBasicosUnidadOrganica) {
        if ("V".equals(relacionUnidOrgOficina.getEstadoRelacion())) {
            ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).update((Object)this.setDatosRelacionUniOrgOficinaVO(relacionUnidOrgOficina));
        } else {
            ((DatosBasicosRelacionUnidOrgOficinaDao)this.getDao()).deleteRelacionesUnidOrgOficina(relacionUnidOrgOficina.getCodigoOficina(), relacionUnidOrgOficina.getCodigoUnidadOrganica());
        }
    }
}
