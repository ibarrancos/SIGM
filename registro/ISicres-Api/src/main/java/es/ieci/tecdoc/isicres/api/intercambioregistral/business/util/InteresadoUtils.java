package es.ieci.tecdoc.isicres.api.intercambioregistral.business.util;

import es.ieci.tecdoc.fwktd.sir.core.types.CanalNotificacionEnum;
import es.ieci.tecdoc.fwktd.sir.core.types.TipoDocumentoIdentificacionEnum;
import es.ieci.tecdoc.fwktd.sir.core.vo.InteresadoVO;
import es.ieci.tecdoc.isicres.api.intercambioregistral.business.vo.InteresadoExReg;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;

public class InteresadoUtils {
    private static final String BLANK = " ";

    public static List<String> getDatosInteresadoArray(InteresadoExReg interesado) {
        ArrayList<String> resultDataInteresado = new ArrayList<String>();
        StringBuffer nombreInteresado = InteresadoUtils.getNombreApellidosInteresado((InteresadoVO)interesado);
        if (StringUtils.isNotBlank((String)nombreInteresado.toString())) {
            resultDataInteresado.add(nombreInteresado.toString());
        } else if (StringUtils.isNotEmpty((String)interesado.getRazonSocialInteresado())) {
            resultDataInteresado.add(interesado.getRazonSocialInteresado());
        }
        StringBuffer identificacionInteresado = InteresadoUtils.getIdentificacionInteresado((InteresadoVO)interesado);
        if (StringUtils.isNotEmpty((String)identificacionInteresado.toString())) {
            resultDataInteresado.add(identificacionInteresado.toString());
        }
        StringBuffer direccionInteresado = new StringBuffer();
        direccionInteresado.append(InteresadoUtils.getDireccionInteresado(interesado));
        if (StringUtils.isNotEmpty((String)direccionInteresado.toString())) {
            InteresadoUtils.addDireccionInteresado(resultDataInteresado, direccionInteresado.toString());
        }
        return resultDataInteresado;
    }

    public static List<String> getDatosRepresentanteArray(InteresadoExReg interesado) {
        StringBuffer direccion;
        ArrayList<String> listaCamposRepresentante = new ArrayList<String>();
        StringBuffer nombre = InteresadoUtils.getNombreApellidosRepresentante((InteresadoVO)interesado);
        if (StringUtils.isNotBlank((String)nombre.toString())) {
            listaCamposRepresentante.add(nombre.toString());
        } else if (StringUtils.isNotEmpty((String)interesado.getRazonSocialRepresentante())) {
            listaCamposRepresentante.add(interesado.getRazonSocialRepresentante());
        }
        StringBuffer identificacion = InteresadoUtils.getIdentificacionRepresentante((InteresadoVO)interesado);
        if (StringUtils.isNotEmpty((String)identificacion.toString())) {
            listaCamposRepresentante.add(identificacion.toString());
        }
        if (StringUtils.isNotEmpty((String)(direccion = new StringBuffer().append(InteresadoUtils.getDireccionRepresentante(interesado))).toString())) {
            InteresadoUtils.addDireccionInteresado(listaCamposRepresentante, direccion.toString());
        }
        return listaCamposRepresentante;
    }

    private static StringBuffer getIdentificacionInteresado(InteresadoVO interesado) {
        StringBuffer identificacionInteresado = new StringBuffer("");
        if (interesado.getTipoDocumentoIdentificacionInteresado() != null) {
            identificacionInteresado.append(interesado.getTipoDocumentoIdentificacionInteresado().getValue());
        }
        if (!StringUtils.isEmpty((String)interesado.getDocumentoIdentificacionInteresado())) {
            if (StringUtils.isNotEmpty((String)identificacionInteresado.toString())) {
                identificacionInteresado.append(": ");
            }
            identificacionInteresado.append(interesado.getDocumentoIdentificacionInteresado());
        }
        return identificacionInteresado;
    }

    private static StringBuffer getIdentificacionRepresentante(InteresadoVO interesado) {
        StringBuffer identificacion = new StringBuffer("");
        if (interesado.getTipoDocumentoIdentificacionRepresentante() != null) {
            identificacion.append(interesado.getTipoDocumentoIdentificacionRepresentante().getValue());
        }
        if (!StringUtils.isEmpty((String)interesado.getDocumentoIdentificacionRepresentante())) {
            if (StringUtils.isNotEmpty((String)identificacion.toString())) {
                identificacion.append(": ");
            }
            identificacion.append(interesado.getDocumentoIdentificacionRepresentante());
        }
        return identificacion;
    }

    private static StringBuffer getNombreApellidosInteresado(InteresadoVO interesado) {
        StringBuffer nombreInteresado = new StringBuffer();
        if (!StringUtils.isEmpty((String)interesado.getNombreInteresado())) {
            nombreInteresado.append(interesado.getNombreInteresado()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getPrimerApellidoInteresado())) {
            nombreInteresado.append(interesado.getPrimerApellidoInteresado()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getSegundoApellidoInteresado())) {
            nombreInteresado.append(interesado.getSegundoApellidoInteresado());
        }
        return nombreInteresado;
    }

    private static StringBuffer getNombreApellidosRepresentante(InteresadoVO interesado) {
        StringBuffer nombreInteresado = new StringBuffer();
        if (!StringUtils.isEmpty((String)interesado.getNombreRepresentante())) {
            nombreInteresado.append(interesado.getNombreRepresentante()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getPrimerApellidoRepresentante())) {
            nombreInteresado.append(interesado.getPrimerApellidoRepresentante()).append(" ");
        }
        if (!StringUtils.isEmpty((String)interesado.getSegundoApellidoRepresentante())) {
            nombreInteresado.append(interesado.getSegundoApellidoRepresentante());
        }
        return nombreInteresado;
    }

    private static void addDireccionInteresado(List<String> resultDataInteresado, String direccionInteresado) {
        if (direccionInteresado.length() > 95) {
            List<String> partesDireccion = InteresadoUtils.recortarCadenaDireccion(direccionInteresado);
            Iterator<String> it = partesDireccion.iterator();
            while (it.hasNext()) {
                resultDataInteresado.add(it.next());
            }
        } else {
            resultDataInteresado.add(direccionInteresado.toString());
        }
    }

    private static List<String> recortarCadenaDireccion(String direccionInteresado) {
        List partesDireccion = new ArrayList();
        partesDireccion = direccionInteresado.split(" ").length - 1 > 0 ? InteresadoUtils.recortarCadenaConEspacios(direccionInteresado) : InteresadoUtils.obtenerSubCadenasDireccionInteresado(direccionInteresado.toString());
        return partesDireccion;
    }

    private static List<String> recortarCadenaConEspacios(String direccionInteresado) {
        ArrayList<String> result = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(direccionInteresado.toString());
        String partesDeDireccionSinEspacio = null;
        String cadenaCompuestaDePartesDireccion = new String();
        while (tokens.hasMoreTokens()) {
            partesDeDireccionSinEspacio = tokens.nextToken();
            if (partesDeDireccionSinEspacio.length() < 95) {
                cadenaCompuestaDePartesDireccion = InteresadoUtils.tratarTokenDireccionMenorALengthSaveInBBDD(result, partesDeDireccionSinEspacio, cadenaCompuestaDePartesDireccion);
                continue;
            }
            cadenaCompuestaDePartesDireccion = InteresadoUtils.tratarTokenDireccionMayorALengthSaveInBBDD(result, partesDeDireccionSinEspacio, cadenaCompuestaDePartesDireccion);
        }
        if (StringUtils.isNotEmpty((String)cadenaCompuestaDePartesDireccion)) {
            result.add(cadenaCompuestaDePartesDireccion);
        }
        return result;
    }

    private static String tratarTokenDireccionMayorALengthSaveInBBDD(List<String> partesDireccion, String partesDeDireccionSinEspacio, String cadenaCompuestaDePartesDireccion) {
        if (StringUtils.isNotBlank((String)cadenaCompuestaDePartesDireccion)) {
            partesDireccion.add(cadenaCompuestaDePartesDireccion);
            cadenaCompuestaDePartesDireccion = new String();
        }
        List<String> auxDireccion = InteresadoUtils.obtenerSubCadenasDireccionInteresado(partesDeDireccionSinEspacio);
        partesDireccion.addAll(auxDireccion);
        return cadenaCompuestaDePartesDireccion;
    }

    private static List<String> obtenerSubCadenasDireccionInteresado(String direccionInteresado) {
        ArrayList<String> subCadenasDireccion = new ArrayList<String>();
        int init = 0;
        int end = 95 > direccionInteresado.length() ? direccionInteresado.length() : 95;
        do {
            subCadenasDireccion.add(StringUtils.substring((String)direccionInteresado, (int)init, (int)end));
            init = end;
            if (end + 95 > direccionInteresado.length()) {
                end = direccionInteresado.length();
                continue;
            }
            end+=95;
        } while (direccionInteresado.length() - end > 95);
        if (StringUtils.isNotEmpty((String)StringUtils.substring((String)direccionInteresado, (int)init, (int)end))) {
            subCadenasDireccion.add(StringUtils.substring((String)direccionInteresado, (int)init, (int)end));
        }
        return subCadenasDireccion;
    }

    private static String tratarTokenDireccionMenorALengthSaveInBBDD(List<String> partesDireccion, String partesDeDireccionSinEspacio, String cadenaCompuestaDePartesDireccion) {
        if (cadenaCompuestaDePartesDireccion.length() + " ".length() + partesDeDireccionSinEspacio.length() > 95) {
            partesDireccion.add(cadenaCompuestaDePartesDireccion);
            cadenaCompuestaDePartesDireccion = partesDeDireccionSinEspacio;
        } else {
            cadenaCompuestaDePartesDireccion = InteresadoUtils.obtenerCadenaCompuestaDireccion(partesDeDireccionSinEspacio, cadenaCompuestaDePartesDireccion);
        }
        return cadenaCompuestaDePartesDireccion;
    }

    private static String obtenerCadenaCompuestaDireccion(String partesDeDireccionSinEspacio, String cadenaCompuestaDePartesDireccion) {
        cadenaCompuestaDePartesDireccion = StringUtils.isNotBlank((String)cadenaCompuestaDePartesDireccion) ? cadenaCompuestaDePartesDireccion + " " + partesDeDireccionSinEspacio : partesDeDireccionSinEspacio;
        return cadenaCompuestaDePartesDireccion;
    }

    public static String getDireccionInteresado(InteresadoExReg interesado) {
        StringBuffer direccionInteresado = new StringBuffer("");
        if (interesado.getCanalPreferenteComunicacionInteresado() == CanalNotificacionEnum.DIRECCION_POSTAL) {
            if (StringUtils.isNotBlank((String)interesado.getNombrePaisInteresado())) {
                direccionInteresado.append(interesado.getNombrePaisInteresado());
            }
            if (StringUtils.isNotBlank((String)interesado.getCodigoProvinciaInteresado())) {
                direccionInteresado.append(" - ").append(interesado.getNombreProvinciaInteresado());
            }
            if (StringUtils.isNotBlank((String)interesado.getNombreMunicipioInteresado())) {
                direccionInteresado.append(" - ").append(interesado.getNombreMunicipioInteresado());
            }
            if (StringUtils.isNotBlank((String)interesado.getCodigoPostalInteresado())) {
                direccionInteresado.append(" - ").append(interesado.getCodigoPostalInteresado());
            }
            if (StringUtils.isNotBlank((String)interesado.getDireccionInteresado())) {
                direccionInteresado.append(" - ").append(interesado.getDireccionInteresado());
            }
        } else {
            if (StringUtils.isNotBlank((String)interesado.getDireccionElectronicaHabilitadaInteresado())) {
                direccionInteresado.append(" ").append(interesado.getDireccionElectronicaHabilitadaInteresado());
            }
            if (StringUtils.isNotBlank((String)interesado.getCorreoElectronicoInteresado())) {
                direccionInteresado.append(" ").append(interesado.getCorreoElectronicoInteresado());
            }
            if (StringUtils.isNotBlank((String)interesado.getTelefonoInteresado())) {
                direccionInteresado.append(" ").append(interesado.getTelefonoInteresado());
            }
        }
        return direccionInteresado.toString();
    }

    public static String getDireccionRepresentante(InteresadoExReg interesado) {
        StringBuffer direccionRepresentante = new StringBuffer("");
        if (interesado.getCanalPreferenteComunicacionRepresentante() == CanalNotificacionEnum.DIRECCION_POSTAL) {
            if (StringUtils.isNotBlank((String)interesado.getNombrePaisRepresentante())) {
                direccionRepresentante.append(interesado.getNombrePaisRepresentante());
            }
            if (StringUtils.isNotBlank((String)interesado.getNombreProvinciaRepresentante())) {
                direccionRepresentante.append(" - ").append(interesado.getNombreProvinciaRepresentante());
            }
            if (StringUtils.isNotBlank((String)interesado.getNombreMunicipioRepresentante())) {
                direccionRepresentante.append(" - ").append(interesado.getNombreMunicipioRepresentante());
            }
            if (StringUtils.isNotBlank((String)interesado.getCodigoPostalRepresentante())) {
                direccionRepresentante.append(" - ").append(interesado.getCodigoPostalRepresentante());
            }
            if (StringUtils.isNotBlank((String)interesado.getDireccionRepresentante())) {
                direccionRepresentante.append(" - ").append(interesado.getDireccionRepresentante());
            }
        } else {
            if (StringUtils.isNotBlank((String)interesado.getDireccionElectronicaHabilitadaRepresentante())) {
                direccionRepresentante.append(" ").append(interesado.getDireccionElectronicaHabilitadaRepresentante());
            }
            if (StringUtils.isNotBlank((String)interesado.getCorreoElectronicoRepresentante())) {
                direccionRepresentante.append(" ").append(interesado.getCorreoElectronicoRepresentante());
            }
            if (StringUtils.isNotBlank((String)interesado.getTelefonoRepresentante())) {
                direccionRepresentante.append(" ").append(interesado.getTelefonoRepresentante());
            }
        }
        return direccionRepresentante.toString();
    }
}
