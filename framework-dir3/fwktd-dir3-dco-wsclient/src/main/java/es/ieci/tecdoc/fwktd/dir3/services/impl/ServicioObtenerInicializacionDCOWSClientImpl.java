package es.ieci.tecdoc.fwktd.dir3.services.impl;

import es.ieci.tecdoc.fwktd.dir3.exception.ObtencionFicheroActualizacionDCOException;
import es.ieci.tecdoc.fwktd.dir3.services.ServicioObtenerInicializacionDCO;
import es.ieci.tecdoc.fwktd.dir3.util.Base64Utils;
import es.ieci.tecdoc.fwktd.dir3.util.ZipUtils;
import es.map.directorio.manager.impl.SC01UNVolcadoDatosBasicos;
import es.map.directorio.manager.impl.SC11OFVolcadoDatosBasicos;
import es.map.directorio.manager.impl.wsexport.RespuestaWS;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioObtenerInicializacionDCOWSClientImpl
implements ServicioObtenerInicializacionDCO {
    protected SC01UNVolcadoDatosBasicos servicioVolcadoUnidades;
    protected SC11OFVolcadoDatosBasicos servicioVolcadoOficinas;
    protected String tempFilesDir;
    protected String login;
    protected String pass;
    protected String fileFormat;
    protected String oficinasQueryType;
    protected String unidadesQueryType;
    protected String relacionOficinasUnidOrgQueryType;
    protected String indicadorSIR;
    private final String VOLCADO_OFICINAS_FILE_NAME = "datosBasicosOficina.xml";
    private final String VOLCADO_UORGANICAS_FILE_NAME = "datosBasicosUOrganica.xml";
    private final String VOLCADO_RELACIONES_UORGANICAS_OFICINAS_FILE_NAME = "relacionesUO-OFI.xml";
    private static final Logger logger = LoggerFactory.getLogger((Class)ServicioObtenerInicializacionDCOWSClientImpl.class);

    public String getFicheroInicializarOficinasDCO() {
        String finalFileName = null;
        RespuestaWS respuesta = null;
        try {
            File tempZipFile = File.createTempFile("dec", "zip", new File(this.getTempFilesDir()));
            respuesta = this.getServicioVolcadoOficinas().exportar(this.getLogin(), this.getPass(), this.getFileFormat(), this.getOficinasQueryType(), "", "", "", "", "", this.getIndicadorSIR(), "", "", "", "", "", "", "", "", "", "");
            if (respuesta != null && StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01")) {
                Base64Utils.getInstance().decodeToFile(respuesta.getFichero(), tempZipFile.getAbsolutePath());
                List<String> filesUnzipped = ZipUtils.getInstance().unzipFile(tempZipFile.getAbsolutePath(), this.getTempFilesDir());
                ListIterator<String> itr = filesUnzipped.listIterator();
                while (itr.hasNext()) {
                    String fileName = itr.next();
                    if (!fileName.endsWith("datosBasicosOficina.xml")) continue;
                    finalFileName = fileName;
                }
            }
        }
        catch (ZipException zipEx) {
            logger.error("ServicioInicializacionDCOWSClientImpl::getFicheroInicializarUnidadesDCO - Error al descomprimir el fichero retornado por el DCO.", (Throwable)zipEx);
        }
        catch (IOException ioEx) {
            logger.error("ServicioInicializacionDCOWSClientImpl::getFicheroInicializarUnidadesDCO - Error al crear los ficheros temporales.", (Throwable)ioEx);
        }
        catch (Exception e) {
            logger.error("Error inesperado", (Throwable)e);
        }
        if (finalFileName == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se encuentra el fichero esperado con nombre: ").append("datosBasicosOficina.xml");
            logger.warn(sb.toString());
            StringBuffer sbMsgError = new StringBuffer();
            if (!(respuesta == null || StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01"))) {
                sbMsgError.append("El proceso ha finalizado con errores, cod. error: ").append(respuesta.getCodigo()).append(" - ").append(respuesta.getDescripcion());
            } else {
                sbMsgError.append("El proceso ha finalizado sin errores pero no se encuentra el fichero esperado con nombre: ").append("datosBasicosOficina.xml");
            }
            throw new ObtencionFicheroActualizacionDCOException(sbMsgError.toString());
        }
        return finalFileName;
    }

    public String getFicheroInicializarRelacionesRelacionesOficinaUnidOrgDCO() {
        String finalFileName = null;
        RespuestaWS respuesta = null;
        try {
            File tempZipFile = File.createTempFile("dec", "zip", new File(this.getTempFilesDir()));
            respuesta = this.getServicioVolcadoOficinas().exportar(this.getLogin(), this.getPass(), this.getFileFormat(), this.getRelacionOficinasUnidOrgQueryType(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            if (respuesta != null && StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01")) {
                Base64Utils.getInstance().decodeToFile(respuesta.getFichero(), tempZipFile.getAbsolutePath());
                List<String> filesUnzipped = ZipUtils.getInstance().unzipFile(tempZipFile.getAbsolutePath(), this.getTempFilesDir());
                ListIterator<String> itr = filesUnzipped.listIterator();
                while (itr.hasNext()) {
                    String fileName = itr.next();
                    if (!fileName.endsWith("relacionesUO-OFI.xml")) continue;
                    finalFileName = fileName;
                }
            }
        }
        catch (ZipException zipEx) {
            logger.error("ServicioInicializacionDCOWSClientImpl::getFicheroInicializarRelacionesRelacionesOficinaUnidOrgDCO - Error al descomprimir el fichero retornado por el DCO.", (Throwable)zipEx);
        }
        catch (IOException ioEx) {
            logger.error("ServicioInicializacionDCOWSClientImpl::getFicheroInicializarRelacionesRelacionesOficinaUnidOrgDCO - Error al crear los ficheros temporales.", (Throwable)ioEx);
        }
        catch (Exception e) {
            logger.error("Error inesperado", (Throwable)e);
        }
        if (finalFileName == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se encuentra el fichero esperado con nombre: ").append("relacionesUO-OFI.xml");
            logger.warn(sb.toString());
            StringBuffer sbMsgError = new StringBuffer();
            if (!(respuesta == null || StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01"))) {
                sbMsgError.append("El proceso ha finalizado con errores, cod. error: ").append(respuesta.getCodigo()).append(" - ").append(respuesta.getDescripcion());
            } else {
                sbMsgError.append("El proceso ha finalizado sin errores pero no se encuentra el fichero esperado con nombre: ").append("relacionesUO-OFI.xml");
            }
            throw new ObtencionFicheroActualizacionDCOException(sbMsgError.toString());
        }
        return finalFileName;
    }

    public String getFicheroInicializarUnidadesDCO() {
        String finalFileName = null;
        RespuestaWS respuesta = null;
        try {
            File tempZipFile = File.createTempFile("dec", "zip", new File(this.getTempFilesDir()));
            respuesta = this.getServicioVolcadoUnidades().exportar(this.getLogin(), this.getPass(), this.getFileFormat(), this.getUnidadesQueryType(), "", "", "", "", "", "", "", "", "");
            if (respuesta != null && StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01")) {
                Base64Utils.getInstance().decodeToFile(respuesta.getFichero(), tempZipFile.getAbsolutePath());
                List<String> filesUnzipped = ZipUtils.getInstance().unzipFile(tempZipFile.getAbsolutePath(), this.getTempFilesDir());
                ListIterator<String> itr = filesUnzipped.listIterator();
                while (itr.hasNext()) {
                    String fileName = itr.next();
                    if (!fileName.endsWith("datosBasicosUOrganica.xml")) continue;
                    finalFileName = fileName;
                }
            }
        }
        catch (ZipException zipEx) {
            logger.error("ServicioInicializacionDCOWSClientImpl::getFicheroInicializarUnidadesDCO - Error al descomprimir el fichero retornado por el DCO.", (Throwable)zipEx);
        }
        catch (IOException ioEx) {
            logger.error("ServicioInicializacionDCOWSClientImpl::getFicheroInicializarUnidadesDCO - Error al crear los ficheros temporales.", (Throwable)ioEx);
        }
        catch (Exception e) {
            logger.error("Error inesperado", (Throwable)e);
        }
        if (finalFileName == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se encuentra el fichero esperado con nombre: ").append("datosBasicosUOrganica.xml");
            logger.warn(sb.toString());
            StringBuffer sbMsgError = new StringBuffer();
            if (!(respuesta == null || StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01"))) {
                sbMsgError.append("El proceso ha finalizado con errores, cod. error: ").append(respuesta.getCodigo()).append(" - ").append(respuesta.getDescripcion());
            } else {
                sbMsgError.append("El proceso ha finalizado sin errores pero no se encuentra el fichero esperado con nombre: ").append("datosBasicosUOrganica.xml");
            }
            throw new ObtencionFicheroActualizacionDCOException(sbMsgError.toString());
        }
        return finalFileName;
    }

    public SC01UNVolcadoDatosBasicos getServicioVolcadoUnidades() {
        return this.servicioVolcadoUnidades;
    }

    public void setServicioVolcadoUnidades(SC01UNVolcadoDatosBasicos servicioVolcadoUnidades) {
        this.servicioVolcadoUnidades = servicioVolcadoUnidades;
    }

    public String getTempFilesDir() {
        return this.tempFilesDir;
    }

    public void setTempFilesDir(String tempFilesDir) {
        this.tempFilesDir = tempFilesDir;
    }

    public SC11OFVolcadoDatosBasicos getServicioVolcadoOficinas() {
        return this.servicioVolcadoOficinas;
    }

    public void setServicioVolcadoOficinas(SC11OFVolcadoDatosBasicos servicioVolcadoOficinas) {
        this.servicioVolcadoOficinas = servicioVolcadoOficinas;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFileFormat() {
        return this.fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getOficinasQueryType() {
        return this.oficinasQueryType;
    }

    public void setOficinasQueryType(String oficinasQueryType) {
        this.oficinasQueryType = oficinasQueryType;
    }

    public String getUnidadesQueryType() {
        return this.unidadesQueryType;
    }

    public void setUnidadesQueryType(String unidadesQueryType) {
        this.unidadesQueryType = unidadesQueryType;
    }

    public String getIndicadorSIR() {
        return this.indicadorSIR;
    }

    public void setIndicadorSIR(String indicadorSIR) {
        this.indicadorSIR = indicadorSIR;
    }

    public String getRelacionOficinasUnidOrgQueryType() {
        return this.relacionOficinasUnidOrgQueryType;
    }

    public void setRelacionOficinasUnidOrgQueryType(String relacionOficinasUnidOrgQueryType) {
        this.relacionOficinasUnidOrgQueryType = relacionOficinasUnidOrgQueryType;
    }
}
