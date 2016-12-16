package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.BandejaEntradaIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaEntradaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIREntradaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralEntradaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralEntradaVO;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class IbatisBandejaEntradaIntercambioRegistralDAOImpl
implements BandejaEntradaIntercambioRegistralDAO {
    private SqlMapClientTemplate sqlMapClientTemplate = new SqlMapClientTemplate();
    private static final Logger logger = Logger.getLogger((Class)IbatisBandejaEntradaIntercambioRegistralDAOImpl.class);
    private static final String GET_INTERCAMBIO_REGISTRAL_ENTRADA_BY_REGISTRO_SQLMAP = "IntercambioRegistralEntradaVO.getIntercambioRegistralEntradaByRegistro";
    private static final String GET_INTERCAMBIO_REGISTRAL_ENTRADA_BY_ID_INTERCAMBIO_SIR_IDOFI = "IntercambioRegistralEntradaVO.getIntercambioRegistralEntradaByIdIntercambioRegistralSirOfi";
    private static final String DELETE_INTERCAMBIO_REGISTRAL_ENTRADA = "IntercambioRegistralEntradaVO.deleteIntercambioRegistralEntradaVO";
    private static final String SAVE_INTERCAMBIO_REGISTRAL_ENTRADA = "IntercambioRegistralEntradaVO.addIntercambioRegistralEntradaVO";
    private static final String GET_BANDEJA_ENTRADA_BY_ESTADO = "IntercambioRegistralEntradaVO.getBandejaEntradaByEstado";
    private static final String GET_BANDEJA_ENTRADA_ITEM_REGISTRO = "IntercambioRegistralEntradaVO.getBandejaEntradaItemRegistro";
    private static final String GET_INFO_ESTADO_BY_REGISTRO = "IntercambioRegistralEntradaVO.getBandejaEntradaByRegistro";
    private static final String FIND_ASIENTOS_REGISTRALES = "IntercambioRegistralEntradaVO.findIntercambiosRegistrales";
    private static final String COUNT_FIND_ASIENTOS_REGISTRALES = "IntercambioRegistralEntradaVO.countFindIntercambiosRegistrales";
    private static final String UPDATE_ESTADO_INTERCAMBIO_ENTRADA = "IntercambioRegistralEntradaVO.updateIntercambioRegistralEntradaVO";

    @Override
    public void save(IntercambioRegistralEntradaVO intercambioRegistralEntrada) {
        try {
            this.getSqlMapClientTemplate().insert("IntercambioRegistralEntradaVO.addIntercambioRegistralEntradaVO", (Object)intercambioRegistralEntrada);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la insercci\u00f3n de un intercambio registral de entrada", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void delete(IntercambioRegistralEntradaVO intercambioRegistralEntrada) {
        try {
            this.getSqlMapClientTemplate().delete("IntercambioRegistralEntradaVO.deleteIntercambioRegistralEntradaVO", (Object)intercambioRegistralEntrada);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la insercci\u00f3n de un intercambio registral de entrada", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void updateEstado(IntercambioRegistralEntradaVO intercambioRegistralEntrada, EstadoIntercambioRegistralEntradaEnumVO estado) {
        try {
            intercambioRegistralEntrada.setEstado(estado);
            intercambioRegistralEntrada.setFechaEstado(GregorianCalendar.getInstance().getTime());
            this.getSqlMapClientTemplate().update("IntercambioRegistralEntradaVO.updateIntercambioRegistralEntradaVO", (Object)intercambioRegistralEntrada);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la actualizaci\u00f3n de estado de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<IntercambioRegistralEntradaVO> getInfoEstado(IntercambioRegistralEntradaVO intecambioRegistralEntrada) {
        try {
            List result = null;
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralEntradaVO.getBandejaEntradaByRegistro", (Object)intecambioRegistralEntrada);
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la informaci\u00f3n del estado del Intercambio Registral por registro", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public IntercambioRegistralEntradaVO getIntercambioRegistralEntradaByRegistro(Integer idLibro, Integer idRegistro, Integer estado) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("getIntercambioRegistralEntradaByRegistro(Integer idLibro, Integer idRegistro, Integer idOficina,Integer estado) - start (): [" + idLibro + "," + idRegistro + "," + estado + "]"));
        }
        try {
            IntercambioRegistralEntradaVO result = null;
            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("estado", estado);
            params.put("idLibro", idLibro);
            params.put("idRegistro", idRegistro);
            result = (IntercambioRegistralEntradaVO)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralEntradaVO.getIntercambioRegistralEntradaByRegistro", params);
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"getIntercambioRegistralEntradaByRegistro(Integer, Integer, Integer, Integer) - end");
            }
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la informaci\u00f3n del Intercambio Registral por registro", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<IntercambioRegistralEntradaVO> getIntercambioRegistralEntradaByIdIntercambioRegistralSir(Integer idOficina, String idIntercambioRegistralSir) {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("getIntercambioRegistralEntradaByRegistro(Integer idOficina,String idIntercambioRegistralSir) - start (): [" + idOficina + "," + idIntercambioRegistralSir + "]"));
        }
        try {
            List result = null;
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("idOficina", idOficina);
            params.put("idIntercambioRegistralSir", idIntercambioRegistralSir);
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralEntradaVO.getIntercambioRegistralEntradaByIdIntercambioRegistralSirOfi", params);
            if (logger.isDebugEnabled()) {
                logger.debug((Object)"getIntercambioRegistralEntradaByRegistro(Integer, String) - end");
            }
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la informaci\u00f3n del Intercambio Registral por idOficina e idIntercmabioRegistralSir", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<BandejaEntradaItemVO> getBandejaEntradaByEstado(Integer estado, Integer idOficina) {
        try {
            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("estado", estado);
            params.put("idOfic", idOficina);
            List bandejaEntrada = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralEntradaVO.getBandejaEntradaByEstado", params);
            return bandejaEntrada;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de bandeja de entrada por estado y oficina", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public BandejaEntradaItemVO completarBandejaEntradaItem(BandejaEntradaItemVO bandejaEntradaItemVO) {
        try {
            BandejaEntradaItemVO bandejaEntrada = (BandejaEntradaItemVO)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralEntradaVO.getBandejaEntradaItemRegistro", (Object)bandejaEntradaItemVO);
            bandejaEntradaItemVO.setNumeroRegistro(bandejaEntrada.getNumeroRegistro());
            bandejaEntradaItemVO.setOrigen(bandejaEntrada.getOrigen());
            bandejaEntradaItemVO.setFechaRegistro(bandejaEntrada.getFechaRegistro());
            bandejaEntradaItemVO.setOrigenName(bandejaEntrada.getOrigenName());
            return bandejaEntradaItemVO;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la completacion de un elemento de la bandeja de entrada", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<BandejaEntradaItemVO> findByCriterios(EstadoIntercambioRegistralEntradaEnumVO estado, CriteriosBusquedaIREntradaVO criterios) {
        logger.debug((Object)("Realizando b\u00fasqueda de asientos registrales en base a los siguientes criterios: '" + (Object)criterios + "'"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("estado", estado.getValue());
        map.put("criterios", criterios.getCriterios());
        map.put("orden", criterios.getOrderBy());
        try {
            List result = null;
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralEntradaVO.findIntercambiosRegistrales", map);
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la b\u00fasqueda de intercambios registrales", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public PaginatedArrayList<BandejaEntradaItemVO> findByCriterios(EstadoIntercambioRegistralEntradaEnumVO estado, CriteriosBusquedaIREntradaVO criterios, PageInfo pageInfo) {
        logger.debug((Object)("Realizando b\u00fasqueda de asientos registrales en base a los siguientes criterios: '" + (Object)criterios + "'"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("estado", estado.getValue());
        map.put("criterios", criterios.getCriterios());
        map.put("orden", criterios.getOrderBy());
        try {
            List result = null;
            if (pageInfo != null) {
                int skipResults = 0;
                int maxResults = -999999;
                if (pageInfo.getPageNumber() > 0 && pageInfo.getObjectsPerPage() > 0) {
                    skipResults = (pageInfo.getPageNumber() - 1) * pageInfo.getObjectsPerPage();
                    maxResults = pageInfo.getObjectsPerPage();
                } else if (pageInfo.getMaxNumItems() > 0) {
                    maxResults = pageInfo.getMaxNumItems();
                }
                result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralEntradaVO.findIntercambiosRegistrales", map, skipResults, maxResults);
                int fullListSize = (maxResults == -999999 || result.size() < maxResults) && skipResults == 0 ? result.size() : ((Integer)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralEntradaVO.countFindIntercambiosRegistrales", map)).intValue();
                PaginatedArrayList resultados = new PaginatedArrayList(pageInfo);
                resultados.setFullListSize(fullListSize);
                resultados.setList(result);
                return resultados;
            }
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralEntradaVO.findIntercambiosRegistrales", map);
            PaginatedArrayList resultados = new PaginatedArrayList(pageInfo);
            resultados.setFullListSize(result.size());
            resultados.setList(result);
            return resultados;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la b\u00fasqueda de intercambios registrales", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    public final void setSqlMapClient(SqlMapClient aSqlMapClient) {
        this.sqlMapClientTemplate.setSqlMapClient(aSqlMapClient);
    }

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return this.sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }
}
