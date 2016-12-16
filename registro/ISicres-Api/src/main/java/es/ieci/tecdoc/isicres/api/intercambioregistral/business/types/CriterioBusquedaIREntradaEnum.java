package es.ieci.tecdoc.isicres.api.intercambioregistral.business.types;

import es.ieci.tecdoc.fwktd.core.enums.StringValuedEnum;

public class CriterioBusquedaIREntradaEnum
extends StringValuedEnum {
    private static final long serialVersionUID = -5596439714016691121L;
    public static final String TABLE_INTERCAMBIOS_ENTRADA = "SCR_EXREG_IN";
    public static final CriterioBusquedaIREntradaEnum ID_ARCHIVADOR = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "id_arch", "id_arch");
    public static final CriterioBusquedaIREntradaEnum ID_FOLDER = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "id_fdr", "id_fdr");
    public static final CriterioBusquedaIREntradaEnum ID_OFICINA = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "id_ofic", "id_ofic");
    public static final CriterioBusquedaIREntradaEnum EXCHANGE_DATE = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "exchange_date", "exchange_date");
    public static final CriterioBusquedaIREntradaEnum STATE = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "state", "state");
    public static final CriterioBusquedaIREntradaEnum STATE_DATE = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "state_date", "state_date");
    public static final CriterioBusquedaIREntradaEnum ID_EXCHANGE_SIR = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "id_exchange_sir", "id_exchange_sir");
    public static final CriterioBusquedaIREntradaEnum ID_EXCHANGE = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "id_exchange", "id_exchange");
    public static final CriterioBusquedaIREntradaEnum USERNAME = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "username", "username");
    public static final CriterioBusquedaIREntradaEnum CODE_TRAMUNIT = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "code_tramunit", "code_tramunit");
    public static final CriterioBusquedaIREntradaEnum NAME_TRAMUNIT = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "name_tramunit", "name_tramunit");
    public static final CriterioBusquedaIREntradaEnum CODE_ENTITY = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "code_entity", "code_entity");
    public static final CriterioBusquedaIREntradaEnum NAME_ENTITY = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "name_entity", "name_entity");
    public static final CriterioBusquedaIREntradaEnum NUM_REG_ORIGINAL = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "num_reg_orig", "num_reg_orig");
    public static final CriterioBusquedaIREntradaEnum CONTACTO_ORIGINAL = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "contacto_orig", "contacto_orig");
    public static final CriterioBusquedaIREntradaEnum COMMENTS = new CriterioBusquedaIREntradaEnum("SCR_EXREG_IN", "comments", "comments");
    private String table = null;

    protected CriterioBusquedaIREntradaEnum(String table, String name, String value) {
        super(name, value);
        this.setTable(table);
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public static CriterioBusquedaIREntradaEnum getCriterio(String value) {
        return (CriterioBusquedaIREntradaEnum)StringValuedEnum.getEnum((Class)CriterioBusquedaIREntradaEnum.class, (String)value);
    }
}
