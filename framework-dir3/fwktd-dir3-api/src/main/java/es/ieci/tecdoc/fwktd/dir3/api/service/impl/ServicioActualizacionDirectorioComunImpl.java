package es.ieci.tecdoc.fwktd.dir3.api.service.impl;

import es.ieci.tecdoc.fwktd.dir3.api.helper.XmlDcoToObject;
import es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosOficinaManager;
import es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosRelacionUnidOrgOficinaManager;
import es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosUnidadOrganicaManager;
import es.ieci.tecdoc.fwktd.dir3.api.manager.EstadoActualizacionDCOManager;
import es.ieci.tecdoc.fwktd.dir3.api.manager.GenerateScriptSQLManager;
import es.ieci.tecdoc.fwktd.dir3.api.vo.EstadoActualizacionDcoVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.oficina.OficinasVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionesUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.unidad.OrganismosVO;
import es.ieci.tecdoc.fwktd.dir3.core.service.ServicioActualizacionDirectorioComun;
import es.ieci.tecdoc.fwktd.dir3.services.ServicioObtenerActualizacionesDCO;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioActualizacionDirectorioComunImpl
implements ServicioActualizacionDirectorioComun {
    protected ServicioObtenerActualizacionesDCO servicioObtenerActualizacionesDCO;
    protected DatosBasicosOficinaManager datosBasicosOficinaManager;
    protected DatosBasicosUnidadOrganicaManager datosBasicosUnidadOrganicaManager;
    protected EstadoActualizacionDCOManager estadoActualizacionDCOManager;
    protected DatosBasicosRelacionUnidOrgOficinaManager datosBasicosRelacionUnidOrgOficinaManager;
    protected GenerateScriptSQLManager generateScriptSQLOficinaManager;
    protected GenerateScriptSQLManager generateScriptSQLUnidadOrganicaManager;
    protected GenerateScriptSQLManager generateScriptSQLRelacionesOficinaUnidOrgManager;
    private static final Logger logger = LoggerFactory.getLogger((Class)ServicioActualizacionDirectorioComunImpl.class);

    public void actualizarDirectorioComun() {
        if (logger.isDebugEnabled()) {
            logger.debug("Comienza la actualizaci\u00f3n del sistema");
        }
        EstadoActualizacionDcoVO estadoActualizacion = this.estadoActualizacionDCOManager.getLastSuccessUpdate();
        if (logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Los datos de la \u00faltima actualizaci\u00f3n son: ").append((Object)estadoActualizacion);
            logger.debug(sb.toString());
        }
        String fileActualizarOficinas = this.getServicioObtenerActualizacionesDCO().getFicheroActualizarOficinasDCO(estadoActualizacion.getFechaActualizacion());
        String fileActualizarUnidades = this.getServicioObtenerActualizacionesDCO().getFicheroActualizarUnidadesDCO(estadoActualizacion.getFechaActualizacion());
        String fileActualizarRelacionesOfiUnid = this.getServicioObtenerActualizacionesDCO().getFicheroActualizarRelacionOficinasUnidadesDCO(estadoActualizacion.getFechaActualizacion());
        OficinasVO oficinasDCO = XmlDcoToObject.getInstance().getOficinasFromXmlFile(fileActualizarOficinas);
        OrganismosVO organismosDCO = XmlDcoToObject.getInstance().getOrganismosFromXmlFile(fileActualizarUnidades);
        RelacionesUnidOrgOficinaVO relacionesDCO = XmlDcoToObject.getInstance().getRelacionesUnidOrgOficinaFromXmlFile(fileActualizarRelacionesOfiUnid);
        this.getDatosBasicosOficinaManager().updateDatosBasicosOficinas(oficinasDCO);
        this.getDatosBasicosUnidadOrganicaManager().updateDatosBasicosUnidadesOrganicas(organismosDCO);
        this.getDatosBasicosRelacionUnidOrgOficinaManager().updateDatosBasicosRelacionesUnidOrgOficinaVO(relacionesDCO);
        if (logger.isDebugEnabled()) {
            logger.debug("Actualizados los datos de oficinas, organismos y sus relaciones");
        }
        estadoActualizacion.setFechaActualizacion(Calendar.getInstance().getTime());
        this.getEstadoActualizacionDCOManager().update(estadoActualizacion);
        if (logger.isDebugEnabled()) {
            logger.debug("Finaliza la actualizaci\u00f3n del sistema");
        }
    }

    public void generateScriptsActualizacionDirectorioComun() {
        if (logger.isDebugEnabled()) {
            logger.debug("Comienza la generaci\u00f3n de los script de actualizaci\u00f3n del sistema");
        }
        Date lastDateUpdate = this.estadoActualizacionDCOManager.getLastSuccessUpdate().getFechaActualizacion();
        if (logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("La fecha de la ultima actualizaci\u00f3n es: ").append(lastDateUpdate.toString());
            logger.debug(sb.toString());
        }
        String fileInicializarOficinas = this.getServicioObtenerActualizacionesDCO().getFicheroActualizarOficinasDCO(lastDateUpdate);
        String fileInicializarUnidades = this.getServicioObtenerActualizacionesDCO().getFicheroActualizarUnidadesDCO(lastDateUpdate);
        String fileInicializarRelaciones = this.getServicioObtenerActualizacionesDCO().getFicheroActualizarRelacionOficinasUnidadesDCO(lastDateUpdate);
        this.getGenerateScriptSQLOficinaManager().generateScriptActualizacion(fileInicializarOficinas);
        this.getGenerateScriptSQLUnidadOrganicaManager().generateScriptActualizacion(fileInicializarUnidades);
        this.getGenerateScriptSQLRelacionesOficinaUnidOrgManager().generateScriptActualizacion(fileInicializarRelaciones);
        if (logger.isDebugEnabled()) {
            logger.debug("Finaliza la generaci\u00f3n de los script de actualizaci\u00f3n del sistema");
        }
    }

    public ServicioObtenerActualizacionesDCO getServicioObtenerActualizacionesDCO() {
        return this.servicioObtenerActualizacionesDCO;
    }

    public void setServicioObtenerActualizacionesDCO(ServicioObtenerActualizacionesDCO servicioObtenerActualizacionesDCO) {
        this.servicioObtenerActualizacionesDCO = servicioObtenerActualizacionesDCO;
    }

    public DatosBasicosOficinaManager getDatosBasicosOficinaManager() {
        return this.datosBasicosOficinaManager;
    }

    public void setDatosBasicosOficinaManager(DatosBasicosOficinaManager datosBasicosOficinaManager) {
        this.datosBasicosOficinaManager = datosBasicosOficinaManager;
    }

    public DatosBasicosUnidadOrganicaManager getDatosBasicosUnidadOrganicaManager() {
        return this.datosBasicosUnidadOrganicaManager;
    }

    public void setDatosBasicosUnidadOrganicaManager(DatosBasicosUnidadOrganicaManager datosBasicosUnidadOrganicaManager) {
        this.datosBasicosUnidadOrganicaManager = datosBasicosUnidadOrganicaManager;
    }

    public EstadoActualizacionDCOManager getEstadoActualizacionDCOManager() {
        return this.estadoActualizacionDCOManager;
    }

    public void setEstadoActualizacionDCOManager(EstadoActualizacionDCOManager estadoActualizacionDCOManager) {
        this.estadoActualizacionDCOManager = estadoActualizacionDCOManager;
    }

    public GenerateScriptSQLManager getGenerateScriptSQLOficinaManager() {
        return this.generateScriptSQLOficinaManager;
    }

    public void setGenerateScriptSQLOficinaManager(GenerateScriptSQLManager generateScriptSQLOficinaManager) {
        this.generateScriptSQLOficinaManager = generateScriptSQLOficinaManager;
    }

    public GenerateScriptSQLManager getGenerateScriptSQLUnidadOrganicaManager() {
        return this.generateScriptSQLUnidadOrganicaManager;
    }

    public void setGenerateScriptSQLUnidadOrganicaManager(GenerateScriptSQLManager generateScriptSQLUnidadOrganicaManager) {
        this.generateScriptSQLUnidadOrganicaManager = generateScriptSQLUnidadOrganicaManager;
    }

    public DatosBasicosRelacionUnidOrgOficinaManager getDatosBasicosRelacionUnidOrgOficinaManager() {
        return this.datosBasicosRelacionUnidOrgOficinaManager;
    }

    public void setDatosBasicosRelacionUnidOrgOficinaManager(DatosBasicosRelacionUnidOrgOficinaManager datosBasicosRelacionUnidOrgOficinaManager) {
        this.datosBasicosRelacionUnidOrgOficinaManager = datosBasicosRelacionUnidOrgOficinaManager;
    }

    public GenerateScriptSQLManager getGenerateScriptSQLRelacionesOficinaUnidOrgManager() {
        return this.generateScriptSQLRelacionesOficinaUnidOrgManager;
    }

    public void setGenerateScriptSQLRelacionesOficinaUnidOrgManager(GenerateScriptSQLManager generateScriptSQLRelacionesOficinaUnidOrgManager) {
        this.generateScriptSQLRelacionesOficinaUnidOrgManager = generateScriptSQLRelacionesOficinaUnidOrgManager;
    }
}
