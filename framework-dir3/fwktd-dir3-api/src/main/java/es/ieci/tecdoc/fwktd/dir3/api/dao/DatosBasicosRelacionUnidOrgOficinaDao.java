package es.ieci.tecdoc.fwktd.dir3.api.dao;

import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioOficinaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.dao.BaseDao;
import java.util.List;

public interface DatosBasicosRelacionUnidOrgOficinaDao
extends BaseDao<DatosBasicosRelacionUnidOrgOficinaVO, String> {
    public int countRelacionesUnidOrgOficina(Criterios<CriterioOficinaEnum> var1);

    public List<DatosBasicosRelacionUnidOrgOficinaVO> findRelacionesUnidOrgOficina(Criterios<CriterioOficinaEnum> var1);

    public void deleteRelacionesUnidOrgOficina(String var1, String var2);
}
