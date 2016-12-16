package es.ieci.tecdoc.fwktd.dir3.services.impl;

import es.ieci.tecdoc.fwktd.dir3.exception.ObtencionFicheroActualizacionDCOException;
import es.ieci.tecdoc.fwktd.dir3.services.ServicioObtenerActualizacionesDCO;
import es.ieci.tecdoc.fwktd.dir3.util.Base64Utils;
import es.ieci.tecdoc.fwktd.dir3.util.ZipUtils;
import es.map.directorio.manager.impl.SC02UNIncrementalDatosBasicos;
import es.map.directorio.manager.impl.SC12OFIncrementalDatosBasicos;
import es.map.directorio.manager.impl.wsexport.RespuestaWS;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioObtenerActualizacionesDCOWSClientImpl
implements ServicioObtenerActualizacionesDCO {
    protected SC02UNIncrementalDatosBasicos servicioIncrementalUnidades;
    protected SC12OFIncrementalDatosBasicos servicioIncrementalOficinas;
    protected String tempFilesDir;
    protected String login;
    protected String pass;
    protected String fileFormat;
    protected String oficinasQueryType;
    protected String unidadesQueryType;
    protected String relacionOficinasUnidOrgQueryType;
    protected String indicadorSIR;
    private final String INCREMENTAL_OFICINAS_FILE_NAME = "datosBasicosOficinaIncremental.xml";
    private final String INCREMENTAL_UORGANICAS_FILE_NAME = "datosBasicosUOrganicaIncremental.xml";
    private final String RELACIONES_UORGANICAS_OFICINAS_FILE_NAME = "relacionesUO-OFIIncremental.xml";
    private static final Logger logger = LoggerFactory.getLogger((Class)ServicioObtenerActualizacionesDCOWSClientImpl.class);

    public String getFicheroActualizarOficinasDCO(Date fechaUltimaActualizacion) {
        String finalFileName = null;
        RespuestaWS respuesta = null;
        if (null == fechaUltimaActualizacion) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarOficinasDCO - Error la fecha de \u00faltima actualizaci\u00f3n es nula");
            throw new ObtencionFicheroActualizacionDCOException("No hay fecha de \u00faltima actualizaci\u00f3n");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            File tempZipFile = File.createTempFile("dec", "zip", new File(this.getTempFilesDir()));
            respuesta = this.getServicioIncrementalOficinas().exportar(this.getLogin(), this.getPass(), this.getFileFormat(), this.getOficinasQueryType(), dateFormat.format(fechaUltimaActualizacion), dateFormat.format(Calendar.getInstance().getTime()), "", "", "", "", "", this.getIndicadorSIR(), "", "", "", "", "", "", "", "", "", "");
            if (respuesta != null && StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01")) {
                Base64Utils.getInstance().decodeToFile(respuesta.getFichero(), tempZipFile.getAbsolutePath());
                List<String> filesUnzipped = ZipUtils.getInstance().unzipFile(tempZipFile.getAbsolutePath(), this.getTempFilesDir());
                ListIterator<String> itr = filesUnzipped.listIterator();
                while (itr.hasNext()) {
                    String fileName = itr.next();
                    if (!fileName.endsWith("datosBasicosOficinaIncremental.xml")) continue;
                    finalFileName = fileName;
                }
            }
        }
        catch (ZipException zipEx) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarOficinasDCO - Error al descomprimir el fichero retornado por el DCO.", (Throwable)zipEx);
        }
        catch (IOException ioEx) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarOficinasDCO - Error al crear los ficheros temporales.", (Throwable)ioEx);
        }
        catch (Exception e) {
            logger.error("Error inesperado", (Throwable)e);
        }
        if (finalFileName == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se encuentra el fichero esperado con nombre: ").append("datosBasicosOficinaIncremental.xml");
            logger.warn(sb.toString());
            StringBuffer sbMsgError = new StringBuffer();
            if (!(respuesta == null || StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01"))) {
                sbMsgError.append("El proceso ha finalizado con errores, cod. error: ").append(respuesta.getCodigo()).append(" - ").append(respuesta.getDescripcion());
            } else {
                sbMsgError.append("El proceso ha finalizado sin errores pero no se encuentra el fichero esperado con nombre: ").append("datosBasicosOficinaIncremental.xml");
            }
            throw new ObtencionFicheroActualizacionDCOException(sbMsgError.toString());
        }
        return finalFileName;
    }

    public String getFicheroActualizarUnidadesDCO(Date fechaUltimaActualizacion) {
        String finalFileName = null;
        RespuestaWS respuesta = null;
        if (null == fechaUltimaActualizacion) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarUnidadesDCO - Error la fecha de \u00faltima actualizaci\u00f3n es nula");
            throw new ObtencionFicheroActualizacionDCOException("No hay fecha de \u00faltima actualizaci\u00f3n");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            File tempZipFile = File.createTempFile("dec", "zip", new File(this.getTempFilesDir()));
            respuesta = this.getServicioIncrementalUnidades().exportar(this.getLogin(), this.getPass(), this.getFileFormat(), this.getUnidadesQueryType(), dateFormat.format(fechaUltimaActualizacion), dateFormat.format(Calendar.getInstance().getTime()), "", "", "", "", "", "", "", "", "");
            if (respuesta != null && StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01")) {
                Base64Utils.getInstance().decodeToFile(respuesta.getFichero(), tempZipFile.getAbsolutePath());
                List<String> filesUnzipped = ZipUtils.getInstance().unzipFile(tempZipFile.getAbsolutePath(), this.getTempFilesDir());
                ListIterator<String> itr = filesUnzipped.listIterator();
                while (itr.hasNext()) {
                    String fileName = itr.next();
                    if (!fileName.endsWith("datosBasicosUOrganicaIncremental.xml")) continue;
                    finalFileName = fileName;
                }
            }
        }
        catch (ZipException zipEx) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarUnidadesDCO - Error al descomprimir el fichero retornado por el DCO.", (Throwable)zipEx);
        }
        catch (IOException ioEx) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarUnidadesDCO - Error al crear los ficheros temporales.", (Throwable)ioEx);
        }
        catch (Exception e) {
            logger.error("Error inesperado", (Throwable)e);
        }
        if (finalFileName == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se encuentra el fichero esperado con nombre: ").append("datosBasicosUOrganicaIncremental.xml");
            logger.warn(sb.toString());
            StringBuffer sbMsgError = new StringBuffer();
            if (!(respuesta == null || StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01"))) {
                sbMsgError.append("El proceso ha finalizado con errores, cod. error: ").append(respuesta.getCodigo()).append(" - ").append(respuesta.getDescripcion());
            } else {
                sbMsgError.append("El proceso ha finalizado sin errores pero no se encuentra el fichero esperado con nombre: ").append("datosBasicosUOrganicaIncremental.xml");
            }
            throw new ObtencionFicheroActualizacionDCOException(sbMsgError.toString());
        }
        return finalFileName;
    }

    public String getFicheroActualizarRelacionOficinasUnidadesDCO(Date fechaUltimaActualizacion) {
        String finalFileName = null;
        RespuestaWS respuesta = null;
        if (null == fechaUltimaActualizacion) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarRelacionOficinasUnidadesDCO - Error la fecha de \u00faltima actualizaci\u00f3n es nula");
            throw new ObtencionFicheroActualizacionDCOException("No hay fecha de \u00faltima actualizaci\u00f3n");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            File tempZipFile = File.createTempFile("dec", "zip", new File(this.getTempFilesDir()));
            respuesta = this.getServicioIncrementalOficinas().exportar(this.getLogin(), this.getPass(), this.getFileFormat(), this.getRelacionOficinasUnidOrgQueryType(), dateFormat.format(fechaUltimaActualizacion), dateFormat.format(Calendar.getInstance().getTime()), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            if (respuesta != null && StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01")) {
                Base64Utils.getInstance().decodeToFile(respuesta.getFichero(), tempZipFile.getAbsolutePath());
                List<String> filesUnzipped = ZipUtils.getInstance().unzipFile(tempZipFile.getAbsolutePath(), this.getTempFilesDir());
                ListIterator<String> itr = filesUnzipped.listIterator();
                while (itr.hasNext()) {
                    String fileName = itr.next();
                    if (!fileName.endsWith("relacionesUO-OFIIncremental.xml")) continue;
                    finalFileName = fileName;
                }
            }
        }
        catch (ZipException zipEx) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarRelacionOficinasUnidadesDCO - Error al descomprimir el fichero retornado por el DCO.", (Throwable)zipEx);
        }
        catch (IOException ioEx) {
            logger.error("ServicioActualizacionDCOWSClientImpl::getFicheroActualizarRelacionOficinasUnidadesDCO - Error al crear los ficheros temporales.", (Throwable)ioEx);
        }
        catch (Exception e) {
            logger.error("Error inesperado", (Throwable)e);
        }
        if (finalFileName == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("No se encuentra el fichero esperado con nombre: ").append("relacionesUO-OFIIncremental.xml");
            logger.warn(sb.toString());
            StringBuffer sbMsgError = new StringBuffer();
            if (!(respuesta == null || StringUtils.equalsIgnoreCase((String)respuesta.getCodigo(), (String)"01"))) {
                sbMsgError.append("El proceso ha finalizado con errores, cod. error: ").append(respuesta.getCodigo()).append(" - ").append(respuesta.getDescripcion());
            } else {
                sbMsgError.append("El proceso ha finalizado sin errores pero no se encuentra el fichero esperado con nombre: ").append("relacionesUO-OFIIncremental.xml");
            }
            throw new ObtencionFicheroActualizacionDCOException(sbMsgError.toString());
        }
        return finalFileName;
    }

    public SC02UNIncrementalDatosBasicos getServicioIncrementalUnidades() {
        return this.servicioIncrementalUnidades;
    }

    public void setServicioIncrementalUnidades(SC02UNIncrementalDatosBasicos servicioIncrementalUnidades) {
        this.servicioIncrementalUnidades = servicioIncrementalUnidades;
    }

    public SC12OFIncrementalDatosBasicos getServicioIncrementalOficinas() {
        return this.servicioIncrementalOficinas;
    }

    public void setServicioIncrementalOficinas(SC12OFIncrementalDatosBasicos servicioIncrementalOficinas) {
        this.servicioIncrementalOficinas = servicioIncrementalOficinas;
    }

    public String getTempFilesDir() {
        return this.tempFilesDir;
    }

    public void setTempFilesDir(String tempFilesDir) {
        this.tempFilesDir = tempFilesDir;
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
