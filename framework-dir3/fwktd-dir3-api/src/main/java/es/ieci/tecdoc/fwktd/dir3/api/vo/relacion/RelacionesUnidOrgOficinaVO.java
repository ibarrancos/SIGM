package es.ieci.tecdoc.fwktd.dir3.api.vo.relacion;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionUnidOrgOficinaVO;
import java.util.List;

@XStreamAlias(value="Fichero_RelacionesOficinasOrganismos")
public class RelacionesUnidOrgOficinaVO {
    @XStreamImplicit
    @XStreamAlias(value="OficinaOrganismo")
    private List<RelacionUnidOrgOficinaVO> relacionesUnidOrgOficinaVO;

    public List<RelacionUnidOrgOficinaVO> getRelacionesUnidOrgOficinaVO() {
        return this.relacionesUnidOrgOficinaVO;
    }

    public void setRelacionesUnidOrgOficinaVO(List<RelacionUnidOrgOficinaVO> relacionesUnidOrgOficinaVO) {
        this.relacionesUnidOrgOficinaVO = relacionesUnidOrgOficinaVO;
    }
}
