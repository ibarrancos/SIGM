package es.ieci.tecdoc.isicres.admin.estructura.enums;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.enums.ValuedEnum;

public class PerfilesReport
extends ValuedEnum {
    private static final long serialVersionUID = 4764243529443650940L;
    public static final PerfilesReport OPERADOR = new PerfilesReport("operador", 1);
    public static final PerfilesReport SUPERUSUARIO = new PerfilesReport("superusuario", 4);

    protected PerfilesReport(String name, int value) {
        super(name, value);
    }

    public static PerfilesReport getEnum(int valor) {
        return (PerfilesReport)PerfilesReport.getEnum((Class)PerfilesReport.class, (int)valor);
    }

    public static Map getEnumMap() {
        return PerfilesReport.getEnumMap((Class)PerfilesReport.class);
    }

    public static List getEnumList() {
        return PerfilesReport.getEnumList((Class)PerfilesReport.class);
    }

    public static Iterator iterator() {
        return PerfilesReport.iterator((Class)PerfilesReport.class);
    }
}
