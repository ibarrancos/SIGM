package es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao;

import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoTransporteIntercambioRegistralVO;

public interface TipoTransporteIntercambioRegistralDAO {
    public TipoTransporteIntercambioRegistralVO getTipoTransporteByDesc(String var1);

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByDescLanguages(String var1);

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByCodigo(String var1);

    public int getCountTipoTransporteByCodigo(String var1);

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByCodigoAndIDScrTT(String var1, Integer var2);

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByIdSicres(Integer var1);

    public void updateByIdTipoTransporte(TipoTransporteIntercambioRegistralVO var1);

    public TipoTransporteIntercambioRegistralVO save(TipoTransporteIntercambioRegistralVO var1);

    public void delete(Long var1);
}
