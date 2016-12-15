package es.ieci.tecdoc.fwktd.dir3.core.type;

import es.ieci.tecdoc.fwktd.core.enums.StringValuedEnum;
import es.ieci.tecdoc.fwktd.dir3.core.type.CriterioEnum;

public class CriterioRelacionUnidOrgOficinaEnum
extends CriterioEnum {
    private static final long serialVersionUID = -7550794989214066675L;
    public static final String TABLE_DIR_RELACION_UNID_ORG_OFICINA = "DIR_RELAC_UNID_ORG_OFIC";
    public static final CriterioRelacionUnidOrgOficinaEnum OFICINA_ID = new CriterioRelacionUnidOrgOficinaEnum("DIR_RELAC_UNID_ORG_OFIC", "Identificador de la oficina", "CODIGO_OFICINA");
    public static final CriterioRelacionUnidOrgOficinaEnum OFICINA_NOMBRE = new CriterioRelacionUnidOrgOficinaEnum("DIR_RELAC_UNID_ORG_OFIC", "Nombre de la oficina", "DENOMINACION_OFICINA");
    public static final CriterioRelacionUnidOrgOficinaEnum UO_ID = new CriterioRelacionUnidOrgOficinaEnum("DIR_RELAC_UNID_ORG_OFIC", "Identificador de la unidad org\u00e1nica", "CODIGO_UNIDAD_ORGANICA");
    public static final CriterioRelacionUnidOrgOficinaEnum UO_NOMBRE = new CriterioRelacionUnidOrgOficinaEnum("DIR_RELAC_UNID_ORG_OFIC", "Nombre de la unidad org\u00e1nica", "NOMBRE_UNIDAD_ORGANICA");

    protected CriterioRelacionUnidOrgOficinaEnum(String table, String name, String value) {
        super(table, name, value);
    }

    public static CriterioRelacionUnidOrgOficinaEnum getCriterio(String value) {
        return (CriterioRelacionUnidOrgOficinaEnum)StringValuedEnum.getEnum((Class)CriterioRelacionUnidOrgOficinaEnum.class, (String)value);
    }
}
