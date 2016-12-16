package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao;

import es.ieci.tecdoc.fwktd.server.pagination.PageInfo;
import es.ieci.tecdoc.fwktd.server.pagination.PaginatedArrayList;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.BandejaSalidaItemVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.CriteriosBusquedaIRSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaEnumVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.EstadoIntercambioRegistralSalidaVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.IntercambioRegistralSalidaVO;
import java.util.List;

public interface BandejaSalidaIntercambioRegistralDAO {
    public void save(IntercambioRegistralSalidaVO var1);

    public IntercambioRegistralSalidaVO get(Long var1);

    public List<IntercambioRegistralSalidaVO> findByIdIntercambioRegistralSirYOficina(String var1, Integer var2);

    public void updateEstado(IntercambioRegistralSalidaVO var1, EstadoIntercambioRegistralSalidaVO var2);

    public List<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO var1, CriteriosBusquedaIRSalidaVO var2);

    public List<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO var1, CriteriosBusquedaIRSalidaVO var2, Integer var3);

    public PaginatedArrayList<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO var1, CriteriosBusquedaIRSalidaVO var2, PageInfo var3);

    public PaginatedArrayList<BandejaSalidaItemVO> findByCriterios(EstadoIntercambioRegistralSalidaEnumVO var1, CriteriosBusquedaIRSalidaVO var2, Integer var3, PageInfo var4);

    public BandejaSalidaItemVO completarBandejaSalidaItem(BandejaSalidaItemVO var1);

    public List<BandejaSalidaItemVO> getBandejaSalidaByEstadoYOficina(Integer var1, Integer var2);

    public List<BandejaSalidaItemVO> getBandejaSalidaByIdIntercambioRegistralSirYOficina(String var1, Integer var2);

    public List<BandejaSalidaItemVO> getBandejaSalidaByEstadoOficinaYLibro(Integer var1, Integer var2, Integer var3);

    public List<IntercambioRegistralSalidaVO> getIntercambiosRegistralesSalida(Integer var1);

    public List<IntercambioRegistralSalidaVO> getIntercambiosRegistralesSalida(Integer var1, Integer var2, Integer var3);

    public void deleteByIdArchIdFdr(Integer var1, Integer var2, Integer var3);

    public void updateIntercambioRegistralSalidaVO(IntercambioRegistralSalidaVO var1);

    public void saveDetalleEstado(EstadoIntercambioRegistralSalidaVO var1);

    public List<EstadoIntercambioRegistralSalidaVO> getDetalleEstadosIntercambioRegistralSalida(Long var1);
}
