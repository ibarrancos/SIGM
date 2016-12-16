package es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.impl;

import es.ieci.tecdoc.isicres.api.intercambioregistral.business.dao.TipoTransporteIntercambioRegistralDAO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.manager.TipoTransporteIntercambioRegistralManager;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.TipoTransporteIntercambioRegistralVO;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

public class TipoTransporteIntercambioRegistralManagerImpl
implements TipoTransporteIntercambioRegistralManager {
    protected TipoTransporteIntercambioRegistralDAO tipoTransporteIntercambioRegistralDAO;
    protected DataFieldMaxValueIncrementer tipoTransporteIntercambioRegistralIncrementer;

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByDesc(String descripcion) {
        TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO = this.tipoTransporteIntercambioRegistralDAO.getTipoTransporteByDesc(descripcion);
        if (tipoTransporteIntercambioRegistralVO == null) {
            tipoTransporteIntercambioRegistralVO = this.tipoTransporteIntercambioRegistralDAO.getTipoTransporteByDescLanguages(descripcion);
        }
        return tipoTransporteIntercambioRegistralVO;
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByCodigo(String codigo) {
        return this.tipoTransporteIntercambioRegistralDAO.getTipoTransporteByCodigo(codigo);
    }

    public int getCountTipoTransporteByCodigo(String codigo) {
        return this.tipoTransporteIntercambioRegistralDAO.getCountTipoTransporteByCodigo(codigo);
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByCodigoAndIDScrTT(String codigo, Integer idScrTT) {
        return this.tipoTransporteIntercambioRegistralDAO.getTipoTransporteByCodigoAndIDScrTT(codigo, idScrTT);
    }

    public TipoTransporteIntercambioRegistralVO getTipoTransporteByIdSicres(Integer idTipoTransporte) {
        return this.tipoTransporteIntercambioRegistralDAO.getTipoTransporteByIdSicres(idTipoTransporte);
    }

    public void updateByIdTipoTransporte(TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO) {
        this.tipoTransporteIntercambioRegistralDAO.updateByIdTipoTransporte(tipoTransporteIntercambioRegistralVO);
    }

    public TipoTransporteIntercambioRegistralVO save(TipoTransporteIntercambioRegistralVO tipoTransporteIntercambioRegistralVO) {
        tipoTransporteIntercambioRegistralVO.setId(this.tipoTransporteIntercambioRegistralIncrementer.nextLongValue());
        return this.tipoTransporteIntercambioRegistralDAO.save(tipoTransporteIntercambioRegistralVO);
    }

    public void delete(Long id) {
        this.tipoTransporteIntercambioRegistralDAO.delete(id);
    }

    public void deleteByDesc(String descripcion) {
    }

    public TipoTransporteIntercambioRegistralDAO getTipoTransporteIntercambioRegistralDAO() {
        return this.tipoTransporteIntercambioRegistralDAO;
    }

    public void setTipoTransporteIntercambioRegistralDAO(TipoTransporteIntercambioRegistralDAO tipoTransporteIntercambioRegistralDAO) {
        this.tipoTransporteIntercambioRegistralDAO = tipoTransporteIntercambioRegistralDAO;
    }

    public DataFieldMaxValueIncrementer getTipoTransporteIntercambioRegistralIncrementer() {
        return this.tipoTransporteIntercambioRegistralIncrementer;
    }

    public void setTipoTransporteIntercambioRegistralIncrementer(DataFieldMaxValueIncrementer tipoTransporteIntercambioRegistralIncrementer) {
        this.tipoTransporteIntercambioRegistralIncrementer = tipoTransporteIntercambioRegistralIncrementer;
    }
}
