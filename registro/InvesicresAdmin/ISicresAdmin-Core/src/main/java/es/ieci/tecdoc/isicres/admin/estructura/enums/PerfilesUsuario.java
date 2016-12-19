package es.ieci.tecdoc.isicres.admin.estructura.enums;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.enums.ValuedEnum;

public class PerfilesUsuario
extends ValuedEnum {
    private static final long serialVersionUID = 1100856037647964072L;
    public static final PerfilesUsuario OPERADOR = new PerfilesUsuario("operador", 1);
    public static final PerfilesUsuario SUPERUSUARIO = new PerfilesUsuario("superusuario", 3);

    protected PerfilesUsuario(String name, int value) {
        super(name, value);
    }

    public static PerfilesUsuario getEnum(int valor) {
        return (PerfilesUsuario)PerfilesUsuario.getEnum((Class)PerfilesUsuario.class, (int)valor);
    }

    public static Map getEnumMap() {
        return PerfilesUsuario.getEnumMap((Class)PerfilesUsuario.class);
    }

    public static List getEnumList() {
        return PerfilesUsuario.getEnumList((Class)PerfilesUsuario.class);
    }

    public static Iterator iterator() {
        return PerfilesUsuario.iterator((Class)PerfilesUsuario.class);
    }
}
