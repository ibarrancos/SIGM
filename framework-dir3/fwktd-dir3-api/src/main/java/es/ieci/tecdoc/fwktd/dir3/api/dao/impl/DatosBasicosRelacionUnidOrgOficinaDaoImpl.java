package es.ieci.tecdoc.fwktd.dir3.api.dao.impl;

import es.ieci.tecdoc.fwktd.dir3.api.dao.DatosBasicosRelacionUnidOrgOficinaDao;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioOficinaEnum;
import es.ieci.tecdoc.fwktd.dir3.core.vo.Criterios;
import es.ieci.tecdoc.fwktd.server.dao.ibatis.IbatisGenericDaoImpl;
import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class DatosBasicosRelacionUnidOrgOficinaDaoImpl
extends IbatisGenericDaoImpl<DatosBasicosRelacionUnidOrgOficinaVO, String>
implements DatosBasicosRelacionUnidOrgOficinaDao {
    protected static final String COUNT_FIND_RELACIONES = "DatosBasicosRelacionUnidOrgOficinaVO.countRelacionesUnidOrgOficina";
    protected static final String FIND_RELACIONES = "DatosBasicosRelacionUnidOrgOficinaVO.findRelacionesUnidOrgOficina";
    protected static final String DELETE_RELACION = "DatosBasicosRelacionUnidOrgOficinaVO.deleteDatosBasicosRelacionUnidOrgOficinaVO";

    public DatosBasicosRelacionUnidOrgOficinaDaoImpl(Class<DatosBasicosRelacionUnidOrgOficinaVO> aPersistentClass) {
        super(aPersistentClass);
    }

    @Override
    public int countRelacionesUnidOrgOficina(Criterios<CriterioOficinaEnum> criterios) {
        if (criterios == null || CollectionUtils.isEmpty((Collection)criterios.getCriterios())) {
            logger.info("Obteniendo el n\u00famero de relaciones entre unid. org\u00e1nicas y oficinas sin criterios");
            return this.count();
        }
        logger.info("Obteniendo el n\u00famero de relaciones entre unid. org\u00e1nicas y oficinas en base a los siguientes criterios: {}", criterios);
        HashMap<String, List> map = new HashMap<String, List>();
        map.put("criterios", criterios.getCriterios());
        return (Integer)this.getSqlMapClientTemplate().queryForObject("DatosBasicosRelacionUnidOrgOficinaVO.countRelacionesUnidOrgOficina", map);
    }

    @Override
    public void deleteRelacionesUnidOrgOficina(String codOficina, String codUnidOrg) {
        StringBuffer sb = new StringBuffer();
        sb.append("Borrando la relaciones entre unid. org\u00e1nicas y oficinas en base a los siguientes criterios: {").append("c\u00f3digo oficina: [").append(codOficina).append("] y c\u00f3digo unid. org\u00e1nica: [").append(codUnidOrg).append("]}");
        logger.info(sb.toString());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("codigoOficina", codOficina);
        map.put("codigoUnidadOrganica", codUnidOrg);
        this.getSqlMapClientTemplate().delete("DatosBasicosRelacionUnidOrgOficinaVO.deleteDatosBasicosRelacionUnidOrgOficinaVO", map);
    }

    @Override
    public List<DatosBasicosRelacionUnidOrgOficinaVO> findRelacionesUnidOrgOficina(Criterios<CriterioOficinaEnum> criterios) {
        if (criterios == null || CollectionUtils.isEmpty((Collection)criterios.getCriterios())) {
            logger.info("Realizando b\u00fasqueda de relaciones entre unid. org\u00e1nicas y oficinas sin criterios");
            return this.getAll();
        }
        logger.info("Realizando b\u00fasqueda de relaciones entre unid. org\u00e1nicas y oficinas en base a los siguientes criterios: {}", criterios);
        HashMap<String, List> map = new HashMap<String, List>();
        map.put("criterios", criterios.getCriterios());
        map.put("orden", criterios.getOrderBy());
        PageInfo pageInfo = criterios.getPageInfo();
        if (pageInfo != null) {
            int skipResults = 0;
            int maxResults = -999999;
            if (pageInfo.getPageNumber() > 0 && pageInfo.getObjectsPerPage() > 0) {
                skipResults = (pageInfo.getPageNumber() - 1) * pageInfo.getObjectsPerPage();
                maxResults = pageInfo.getObjectsPerPage();
            } else if (pageInfo.getMaxNumItems() > 0) {
                maxResults = pageInfo.getMaxNumItems();
            }
            List list = this.getSqlMapClientTemplate().queryForList("DatosBasicosRelacionUnidOrgOficinaVO.findRelacionesUnidOrgOficina", map, skipResults, maxResults);
            int fullListSize = (maxResults == -999999 || list.size() < maxResults) && skipResults == 0 ? list.size() : ((Integer)this.getSqlMapClientTemplate().queryForObject("DatosBasicosRelacionUnidOrgOficinaVO.countRelacionesUnidOrgOficina", map)).intValue();
            PaginatedArrayList resultados = new PaginatedArrayList(pageInfo);
            resultados.setFullListSize(fullListSize);
            resultados.setList(list);
            return resultados;
        }
        return this.getSqlMapClientTemplate().queryForList("DatosBasicosRelacionUnidOrgOficinaVO.findRelacionesUnidOrgOficina", map);
    }
}
