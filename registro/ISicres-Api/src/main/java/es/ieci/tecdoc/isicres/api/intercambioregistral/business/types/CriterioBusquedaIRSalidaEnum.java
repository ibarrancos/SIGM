package es.ieci.tecdoc.isicres.api.intercambioregistral.business.types;

import es.ieci.tecdoc.fwktd.core.enums.StringValuedEnum;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.types.CriterioBusquedaIREntradaEnum;

public class CriterioBusquedaIRSalidaEnum
extends StringValuedEnum {
    private static final long serialVersionUID = -5596439714016691121L;
    public static final String TABLE_INTERCAMBIOS_ENTRADA = "SCR_EXREG";
    public static final CriterioBusquedaIRSalidaEnum ID_ARCHIVADOR = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "id_arch", "id_arch");
    public static final CriterioBusquedaIRSalidaEnum ID_FOLDER = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "id_fdr", "id_fdr");
    public static final CriterioBusquedaIRSalidaEnum ID_OFICINA = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "id_ofic", "id_ofic");
    public static final CriterioBusquedaIRSalidaEnum EXCHANGE_DATE = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "exchange_date", "exchange_date");
    public static final CriterioBusquedaIRSalidaEnum STATE = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "state", "state");
    public static final CriterioBusquedaIRSalidaEnum STATE_DATE = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "state_date", "state_date");
    public static final CriterioBusquedaIRSalidaEnum ID_EXCHANGE_SIR = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "id_exchange_sir", "id_exchange_sir");
    public static final CriterioBusquedaIRSalidaEnum ID_EXCHANGE = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "id_exchange", "id_exchange");
    public static final CriterioBusquedaIRSalidaEnum USERNAME = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "username", "username");
    public static final CriterioBusquedaIRSalidaEnum CODE_TRAMUNIT = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "code_tramunit", "code_tramunit");
    public static final CriterioBusquedaIRSalidaEnum NAME_TRAMUNIT = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "name_tramunit", "name_tramunit");
    public static final CriterioBusquedaIRSalidaEnum CODE_ENTITY = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "code_entity", "code_entity");
    public static final CriterioBusquedaIRSalidaEnum NAME_ENTITY = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "name_entity", "name_entity");
    public static final CriterioBusquedaIRSalidaEnum NUM_REG_ORIGINAL = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "num_reg_orig", "num_reg_orig");
    public static final CriterioBusquedaIRSalidaEnum CONTACTO_ORIGINAL = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "contacto_orig", "contacto_orig");
    public static final CriterioBusquedaIRSalidaEnum COMMENTS = new CriterioBusquedaIRSalidaEnum("SCR_EXREG", "comments", "comments");
    public static final CriterioBusquedaIREntradaEnum TYPE_ORIGINAL = new CriterioBusquedaIREntradaEnum("SCR_EXREG", "type_orig", "type_orig");
    private String table = null;

    protected CriterioBusquedaIRSalidaEnum(String table, String name, String value) {
        super(name, value);
        this.setTable(table);
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public static CriterioBusquedaIRSalidaEnum getCriterio(String value) {
        return (CriterioBusquedaIRSalidaEnum)StringValuedEnum.getEnum((Class)CriterioBusquedaIRSalidaEnum.class, (String)value);
    }
}
