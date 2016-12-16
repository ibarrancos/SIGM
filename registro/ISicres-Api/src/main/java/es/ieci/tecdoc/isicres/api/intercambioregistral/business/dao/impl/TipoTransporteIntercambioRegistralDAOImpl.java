package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.TipoTransporteIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoTransporteIntercambioRegistralVO;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class TipoTransporteIntercambioRegistralDAOImpl
implements TipoTransporteIntercambioRegistralDAO {
    private static final String DELETE_SQLMAP = "TipoTransporteIntercambioRegistralVO.delete";
    private static final String GET_TIPO_TRANSPORTE_BY_DESC_SQLMAP = "TipoTransporteIntercambioRegistralVO.getTipoTransporteByDesc";
    private static final String GET_TIPO_TRANSPORTE_BY_CODIGO_SQLMAP = "TipoTransporteIntercambioRegistralVO.getTipoTransporteByCodigo";
    private static final String GET_COUNT_TIPO_TRANSPORTE_BY_CODIGO_SQLMAP = "TipoTransporteIntercambioRegistralVO.getCountTipoTransporteByCodigo";
    private static final String GET_TIPO_TRANSPORTE_BY_CODIGO_AND_ID_SCRTT_SQLMAP = "TipoTransporteIntercambioRegistralVO.getTipoTransporteByCodigoAndIDScrTT";
    private static final String GET_TIPO_TRANSPORTE_BY_DESC_LANGUAGES_SQLMAP = "TipoTransporteIntercambioRegistralVO.getTipoTransporteByDescLanguages";
    private static final String GET_TIPO_TRANSPORTE_BY_TIPO_TRANSPORTE_SICRES_SQLMAP = "TipoTransporteIntercambioRegistralVO.getTipoTransporteByIdTipoTransporteSicres";
    private static final String SAVE_SQLMAP = "TipoTransporteIntercambioRegistralVO.save";
    private static final String UPDATE_BY_ID_TIPO_TRANSPORTE_SQLMAP = "TipoTransporteIntercambioRegistralVO.updateByIdTipoTransporte";
    private SqlMapClientTemplate sqlMapClientTemplate = new SqlMapClientTemplate();
    private static final Logger logger = Logger.getLogger((Class)TipoTransporteIntercambioRegistralDAOImpl.class);

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByDesc(String descripcion) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByDesc(String) - start");
        }
        TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO = null;
        List lista = this.getSqlMapClientTemplate().queryForList("TipoTransporteIntercambioRegistralVO.getTipoTransporteByDesc", (Object)descripcion);
        if (CollectionUtils.isNotEmpty((Collection)lista)) {
            tipoTransporteIntercambioRegistralVO = (TipoTransporteIntercambioRegistralVO)lista.get(0);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByDesc(String) - end");
        }
        return tipoTransporteIntercambioRegistralVO;
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByCodigo(String codigo) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByCodigo(String) - start");
        }
        TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO = null;
        List lista = this.getSqlMapClientTemplate().queryForList("TipoTransporteIntercambioRegistralVO.getTipoTransporteByCodigo", (Object)codigo);
        if (CollectionUtils.isNotEmpty((Collection)lista)) {
            tipoTransporteIntercambioRegistralVO = (TipoTransporteIntercambioRegistralVO)lista.get(0);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByCodigo(String) - end");
        }
        return tipoTransporteIntercambioRegistralVO;
    }

    public int getCountTipoTransporteByCodigo(String codigo) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getCountTipoTransporteByCodigo(String) - start");
        }
        int result = (Integer)this.getSqlMapClientTemplate().queryForObject("TipoTransporteIntercambioRegistralVO.getCountTipoTransporteByCodigo", (Object)codigo);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getCountTipoTransporteByCodigo(String) - end");
        }
        return result;
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByCodigoAndIDScrTT(String codigo, Integer idScrTT) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByCodigoAndIDScrTT(String) - start");
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("codigo", codigo);
        map.put("idScrTT", idScrTT);
        TipoTransporteIntercambioRegistralVO result = (TipoTransporteIntercambioRegistralVO)this.getSqlMapClientTemplate().queryForObject("TipoTransporteIntercambioRegistralVO.getTipoTransporteByCodigoAndIDScrTT", map);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByCodigoAndIDScrTT(String) - end");
        }
        return result;
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByDescLanguages(String descripcion) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByDescLanguages(String) - start");
        }
        TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO = null;
        List lista = (List)this.getSqlMapClientTemplate().queryForObject("TipoTransporteIntercambioRegistralVO.getTipoTransporteByDescLanguages", (Object)descripcion);
        if (CollectionUtils.isNotEmpty((Collection)lista)) {
            tipoTransporteIntercambioRegistralVO = (TipoTransporteIntercambioRegistralVO)lista.get(0);
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByDescLanguages(String) - end");
        }
        return tipoTransporteIntercambioRegistralVO;
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByIdSicres(Integer idTipoTransporte) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByCodigo(String) - start");
        }
        TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO = (TipoTransporteIntercambioRegistralVO)this.getSqlMapClientTemplate().queryForObject("TipoTransporteIntercambioRegistralVO.getTipoTransporteByIdTipoTransporteSicres", (Object)idTipoTransporte);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"getTipoTransporteByCodigo(String) - end");
        }
        return tipoTransporteIntercambioRegistralVO;
    }

    public void updateByIdTipoTransporte(TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"updateCodigoTipoTransporte(TipoTransporteIntercambioRegistralVO) - start");
        }
        this.getSqlMapClientTemplate().update("TipoTransporteIntercambioRegistralVO.updateByIdTipoTransporte", (Object)tipoTransporteIntercambioRegistralVO);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"updateCodigoTipoTransporte(TipoTransporteIntercambioRegistralVO) - end");
        }
    }

    public TipoTransporteIntercambioRegistralVO save(TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"save(TipoTransporteIntercambioRegistralVO) - start");
        }
        TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVOSaved = null;
        Object object = this.getSqlMapClientTemplate().insert("TipoTransporteIntercambioRegistralVO.save", (Object)tipoTransporteIntercambioRegistralVO);
        if (object != null) {
            tipoTransporteIntercambioRegistralVOSaved = (TipoTransporteIntercambioRegistralVO)object;
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"save(TipoTransporteIntercambioRegistralVO) - end");
        }
        return tipoTransporteIntercambioRegistralVOSaved;
    }

    public void delete(Long id) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"delete(Integer) - start");
        }
        this.getSqlMapClientTemplate().delete("TipoTransporteIntercambioRegistralVO.delete", (Object)id);
        if (logger.isDebugEnabled()) {
            logger.debug((Object)"delete(Integer) - end");
        }
    }

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return this.sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    public final void setSqlMapClient(SqlMapClient aSqlMapClient) {
        this.sqlMapClientTemplate.setSqlMapClient(aSqlMapClient);
    }
}
