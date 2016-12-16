package es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo;

import es.ieci.tecdoc.fwktd.sir.core.vo.AsientoRegistralVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InteresadoExReg;
import java.util.List;

public class InfoAsientoRegistralVO
extends AsientoRegistralVO {
    private static final long serialVersionUID = 1331989888965965890L;
    private List<InteresadoExReg> interesadosExReg = null;

    public List<InteresadoExReg> getInteresadosExReg() {
        return this.interesadosExReg;
    }

    public void setInteresadosExReg(List<InteresadoExReg> interesadosExReg) {
        this.interesadosExReg = interesadosExReg;
    }
}
