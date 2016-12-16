package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.impl;

import com.ibatis.sqlmap.client.SqlMapClient;
import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.BandejaSalidaIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIRSalidaEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaSalidaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriterioBusquedaIRSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIRSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoRegistroEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralSalidaVO;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class IbatisBandejaSalidaIntercambioRegistralDAOImpl
implements BandejaSalidaIntercambioRegistralDAO {
    private SqlMapClientTemplate sqlMapClientTemplate = new SqlMapClientTemplate();
    private static final Logger logger = Logger.getLogger((Class)IbatisBandejaSalidaIntercambioRegistralDAOImpl.class);
    private static final String SAVE_INTERCAMBIO_REGISTRAL_SALIDA = "IntercambioRegistralSalidaVO.addIntercambioRegistralSalidaVO";
    private static final String GET_BY_ID_INTERCAMBIO_REGISTRAL_SIR_OFICINA = "IntercambioRegistralSalidaVO.getIntercambiosRegistralesByIdIntercambioRegistralSirOficina";
    private static final String GET_BANDEJA_SALIDA_ITEM_REGISTRO = "IntercambioRegistralSalidaVO.getBandejaSalidaItemRegistro";
    private static final String GET_INTERCAMBIO_REGISTRAL_SALIDA_BY_ID = "IntercambioRegistralSalidaVO.getIntercambioRegistralSalidaById";
    private static final String GET_BANDEJA_SALIDA_BY_ESTADO_OFICINA = "IntercambioRegistralSalidaVO.getBandejaSalidaByEstadoOficina";
    private static final String GET_BANDEJA_SALIDA_BY_ID_INTERCAMBIO_REGISTRAL_SIR_OFICINA = "IntercambioRegistralSalidaVO.getBandejaSalidaByIdIntercambioRegistralSirOficina";
    private static final String GET_BANDEJA_SALIDA_BY_ESTADO_OFICINA_Y_LIBRO = "IntercambioRegistralSalidaVO.getBandejaSalidaByEstadoOficinaYLibro";
    private static final String UPDATE_INTERCAMBIO_REGISTRAL_SALIDA = "IntercambioRegistralSalidaVO.updateIntercambioRegistralSalidaVO";
    private static final String UPDATE_STATE_INTERCAMBIO_REGISTRAL_SALIDA = "IntercambioRegistralSalidaVO.updateStateIntercambioRegistralSalidaVO";
    private static final String DELETE_INTERCAMBIO_REGISTRAL_SALIDA_BY_ID_ARCH_ID_FDR = "IntercambioRegistralSalidaVO.deleteIntercambioRegistralSalidaByIdArchIdFolderVO";
    private static final String GET_INTERCAMBIOS_REGISTRALES = "IntercambioRegistralSalidaVO.getIntercambiosRegistralesSalida";
    private static final String GET_INTERCAMBIOS_REGISTRALES_BY_ID_REG_ID_LIBRO = "IntercambioRegistralSalidaVO.getIntercambiosRegistralesByIdRegistroIdLibroIdOficina";
    private static final String SAVE_DETALLE_ESTADO_INTERCAMBIO_REGISTRAL_SALIDA = "IntercambioRegistralSalidaVO.addDetalleEstadoIntercambioRegistralSalidaVO";
    private static final String GET_DETALLE_ESTADO_INTERCAMBIO_REGISTRAL_SALIDA_BY_ID_EXREG = "IntercambioRegistralSalidaVO.getHistorialEstadoByIntercambioRegistralSalida";
    private static final String FIND_ASIENTOS_REGISTRALES = "IntercambioRegistralSalidaVO.findIntercambiosRegistrales";
    private static final String COUNT_FIND_ASIENTOS_REGISTRALES = "IntercambioRegistralSalidaVO.countFindIntercambiosRegistrales";
    private static final String FIND_BANDEJA_SALIDA_BY_CRITERIOS = "IntercambioRegistralSalidaVO.findBandejaSalidaByCriterios";
    private static final String COUNT_FIND_BANDEJA_SALIDA_BY_CRITERIOS = "IntercambioRegistralSalidaVO.CountFindBandejaSalidaByCriterios";

    @Override
    public IntercambioRegistralSalidaVO get(Long id) {
        try {
            IntercambioRegistralSalidaVO intercambioRegistralSalida = (IntercambioRegistralSalidaVO)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralSalidaVO.getIntercambioRegistralSalidaById", (Object)id);
            return intercambioRegistralSalida;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void save(IntercambioRegistralSalidaVO intecambioRegistralSalida) {
        try {
            this.getSqlMapClientTemplate().insert("IntercambioRegistralSalidaVO.addIntercambioRegistralSalidaVO", (Object)intecambioRegistralSalida);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la insercci\u00f3n de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void saveDetalleEstado(EstadoIntercambioRegistralSalidaVO detalleEstadoIntercambioRegistral) {
        try {
            this.getSqlMapClientTemplate().insert("IntercambioRegistralSalidaVO.addDetalleEstadoIntercambioRegistralSalidaVO", (Object)detalleEstadoIntercambioRegistral);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la insercci\u00f3n de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void updateEstado(IntercambioRegistralSalidaVO intercambioRegistralSalida, EstadoIntercambioRegistralSalidaVO estado) {
        try {
            intercambioRegistralSalida.setEstado(estado.getEstado());
            intercambioRegistralSalida.setFechaEstado(GregorianCalendar.getInstance().getTime());
            intercambioRegistralSalida.setComentarios(estado.getComentarios());
            this.getSqlMapClientTemplate().update("IntercambioRegistralSalidaVO.updateStateIntercambioRegistralSalidaVO", (Object)intercambioRegistralSalida);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la actualizaci\u00f3n de estado de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<IntercambioRegistralSalidaVO> findByIdIntercambioRegistralSirYOficina(String idIntercambioRegistralSir, Integer idOficina) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("idIntercambioRegistralSir", idIntercambioRegistralSir);
            params.put("idOficina", idOficina);
            List result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getIntercambiosRegistralesByIdIntercambioRegistralSirOficina", params);
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de bandeja de salida por estado y oficina", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<BandejaSalidaItemVO> getBandejaSalidaByIdIntercambioRegistralSirYOficina(String idIntercambioRegistralSir, Integer idOficina) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("idIntercambioRegistralSir", idIntercambioRegistralSir);
            params.put("idOficina", idOficina);
            List bandejaSalida = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getBandejaSalidaByIdIntercambioRegistralSirOficina", params);
            return bandejaSalida;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de bandeja de salida por estado y oficina", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<BandejaSalidaItemVO> getBandejaSalidaByEstadoYOficina(Integer estado, Integer idOficina) {
        try {
            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("estado", estado);
            params.put("idOficina", idOficina);
            List bandejaSalida = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getBandejaSalidaByEstadoOficina", params);
            return bandejaSalida;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de bandeja de salida por estado y oficina", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<BandejaSalidaItemVO> getBandejaSalidaByEstadoOficinaYLibro(Integer estado, Integer idOficina, Integer idLibro) {
        try {
            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("estado", estado);
            params.put("idOficina", idOficina);
            params.put("idLibro", idLibro);
            List bandejaSalida = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getBandejaSalidaByEstadoOficinaYLibro", params);
            return bandejaSalida;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de bandeja de salida por estado y oficina", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public BandejaSalidaItemVO completarBandejaSalidaItem(BandejaSalidaItemVO bandejaSalidaItemVO) {
        try {
            BandejaSalidaItemVO bandejaSalida = (BandejaSalidaItemVO)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralSalidaVO.getBandejaSalidaItemRegistro", (Object)bandejaSalidaItemVO);
            bandejaSalidaItemVO.setNumeroRegistro(bandejaSalida.getNumeroRegistro());
            bandejaSalidaItemVO.setFechaRegistro(bandejaSalida.getFechaRegistro());
            bandejaSalidaItemVO.setEstadoRegistro(bandejaSalida.getEstadoRegistro());
            return bandejaSalidaItemVO;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la completacion de un elemento de la bandeja de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<IntercambioRegistralSalidaVO> getIntercambiosRegistralesSalida(Integer estado) {
        try {
            List intercambiosSalida = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getIntercambiosRegistralesSalida", (Object)estado);
            return intercambiosSalida;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de intercambios registrales por estado", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void deleteByIdArchIdFdr(Integer idLibro, Integer idRegistro, Integer idOficina) {
        try {
            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("idArch", idLibro);
            params.put("idFdr", idRegistro);
            params.put("idOficina", idOficina);
            this.getSqlMapClientTemplate().delete("IntercambioRegistralSalidaVO.deleteIntercambioRegistralSalidaByIdArchIdFolderVO", params);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la eliminacion de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public void updateIntercambioRegistralSalidaVO(IntercambioRegistralSalidaVO intercambioRegistralSalida) {
        try {
            this.getSqlMapClientTemplate().update("IntercambioRegistralSalidaVO.updateIntercambioRegistralSalidaVO", (Object)intercambioRegistralSalida);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la actualizaci\u00f3n de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<IntercambioRegistralSalidaVO> getIntercambiosRegistralesSalida(Integer idRegistro, Integer idLibro, Integer idOficina) {
        try {
            HashMap<String, Integer> params = new HashMap<String, Integer>();
            params.put("idLibro", idLibro);
            params.put("idRegistro", idRegistro);
            params.put("idOficina", idOficina);
            List intercambios = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getIntercambiosRegistralesByIdRegistroIdLibroIdOficina", params);
            return intercambios;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la getIntercambiosRegistralesSalida de un intercambio registral de salida", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<EstadoIntercambioRegistralSalidaVO> getDetalleEstadosIntercambioRegistralSalida(Long idExReg) {
        List result = null;
        try {
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.getHistorialEstadoByIntercambioRegistralSalida", (Object)idExReg);
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error en la busqueda de intercambios registrales por id de Intercambio", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
        return result;
    }

    @Override
    public List<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO estado, CriteriosBusquedaIRSalidaVO criterios) {
        logger.debug((Object)("Realizando b\u00fasqueda de asientos registrales en base a los siguientes criterios: '" + (Object)criterios + "'"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("estado", estado.getValue());
        map.put("criterios", criterios.getCriterios());
        map.put("orden", criterios.getOrderBy());
        try {
            List result = null;
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.findIntercambiosRegistrales", map);
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la b\u00fasqueda de intercambios registrales", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public List<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO estado, CriteriosBusquedaIRSalidaVO criterios, Integer idLibro) {
        logger.debug((Object)("Realizando b\u00fasqueda de asientos registrales en base a los siguientes criterios: '" + (Object)criterios + "'"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("estado", estado.getValue());
        map.put("criterios", criterios.getCriterios());
        map.put("orden", criterios.getOrderBy());
        map.put("idLibro", idLibro);
        try {
            List result = null;
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.findBandejaSalidaByCriterios", map);
            return result;
        }
        catch (DataAccessException e) {
            logger.error((Object)"Error al obtener la b\u00fasqueda de intercambios registrales", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Override
    public PaginatedArrayList<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO estado, CriteriosBusquedaIRSalidaVO criterios, PageInfo pageInfo) {
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
                result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.findIntercambiosRegistrales", map, skipResults, maxResults);
                int fullListSize = (maxResults == -999999 || result.size() < maxResults) && skipResults == 0 ? result.size() : ((Integer)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralSalidaVO.countFindIntercambiosRegistrales", map)).intValue();
                PaginatedArrayList resultados = new PaginatedArrayList(pageInfo);
                resultados.setFullListSize(fullListSize);
                resultados.setList(result);
                return resultados;
            }
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.findIntercambiosRegistrales", map);
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

    @Override
    public PaginatedArrayList<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO estado, CriteriosBusquedaIRSalidaVO criterios, Integer idLibro, PageInfo pageInfo) {
        logger.debug((Object)("Realizando b\u00fasqueda de asientos registrales en base a los siguientes criterios: '" + (Object)criterios + "'"));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("estado", estado.getValue());
        map.put("criterios", criterios.getCriterios());
        map.put("orden", criterios.getOrderBy());
        map.put("idLibro", idLibro);
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
                result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.findBandejaSalidaByCriterios", map, skipResults, maxResults);
                int fullListSize = (maxResults == -999999 || result.size() < maxResults) && skipResults == 0 ? result.size() : ((Integer)this.getSqlMapClientTemplate().queryForObject("IntercambioRegistralSalidaVO.CountFindBandejaSalidaByCriterios", map)).intValue();
                PaginatedArrayList resultados = new PaginatedArrayList(pageInfo);
                resultados.setFullListSize(fullListSize);
                resultados.setList(result);
                return resultados;
            }
            result = this.getSqlMapClientTemplate().queryForList("IntercambioRegistralSalidaVO.findBandejaSalidaByCriterios", map);
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
