package es.ieci.tecdoc.fwktd.dir3.api.manager;

import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionesUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioOficinaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.manager.BaseManager;
import java.util.List;

public interface DatosBasicosRelacionUnidOrgOficinaManager
extends BaseManager<DatosBasicosRelacionUnidOrgOficinaVO, String> {
    public int countRelaciones(Criterios<CriterioOficinaEnum> var1);

    public List<DatosBasicosRelacionUnidOrgOficinaVO> findRelaciones(Criterios<CriterioOficinaEnum> var1);

    public DatosBasicosRelacionUnidOrgOficinaVO getRelacionesByOficinaAndUnidad(String var1, String var2);

    public void saveDatosBasicosRelacionesUnidOrgOficinaVO(RelacionesUnidOrgOficinaVO var1);

    public void updateDatosBasicosRelacionesUnidOrgOficinaVO(RelacionesUnidOrgOficinaVO var1);
}
