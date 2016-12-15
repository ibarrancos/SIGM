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
import es.ieci.tecdoc.fwktd.dir3.core.service.ServicioInicializacionDirectorioComun;
import es.ieci.tecdoc.fwktd.dir3.services.ServicioObtenerInicializacionDCO;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioInicializacionDirectorioComunImpl
implements ServicioInicializacionDirectorioComun {
    protected ServicioObtenerInicializacionDCO servicioObtenerInicializacionDCO;
    protected DatosBasicosOficinaManager datosBasicosOficinaManager;
    protected DatosBasicosUnidadOrganicaManager datosBasicosUnidadOrganicaManager;
    protected DatosBasicosRelacionUnidOrgOficinaManager datosBasicosRelacionUnidOrgOficinaManager;
    protected EstadoActualizacionDCOManager estadoActualizacionDCOManager;
    protected GenerateScriptSQLManager generateScriptSQLOficinaManager;
    protected GenerateScriptSQLManager generateScriptSQLUnidadOrganicaManager;
    protected GenerateScriptSQLManager generateScriptSQLRelacionesOficinaUnidOrgManager;
    private static final Logger logger = LoggerFactory.getLogger((Class)ServicioInicializacionDirectorioComunImpl.class);

    public void inicializarDirectorioComun() {
        if (logger.isDebugEnabled()) {
            logger.debug("Comienza la inicializaci\u00f3n del sistema");
        }
        String fileInicializarOficinas = this.getServicioObtenerInicializacionDCO().getFicheroInicializarOficinasDCO();
        String fileInicializarUnidades = this.getServicioObtenerInicializacionDCO().getFicheroInicializarUnidadesDCO();
        String fileInicializarRelacionesUnidOrgOficina = this.getServicioObtenerInicializacionDCO().getFicheroInicializarRelacionesRelacionesOficinaUnidOrgDCO();
        OficinasVO oficinasDCO = XmlDcoToObject.getInstance().getOficinasFromXmlFile(fileInicializarOficinas);
        this.getDatosBasicosOficinaManager().saveDatosBasicosOficinas(oficinasDCO);
        if (logger.isDebugEnabled()) {
            logger.debug("Oficinas inicializadas");
        }
        OrganismosVO organismosDCO = XmlDcoToObject.getInstance().getOrganismosFromXmlFile(fileInicializarUnidades);
        this.getDatosBasicosUnidadOrganicaManager().saveDatosBasicosUnidadesOrganicas(organismosDCO);
        if (logger.isDebugEnabled()) {
            logger.debug("Organismos inicializados");
        }
        RelacionesUnidOrgOficinaVO relacionesUnidOrgOficinaDCO = XmlDcoToObject.getInstance().getRelacionesUnidOrgOficinaFromXmlFile(fileInicializarRelacionesUnidOrgOficina);
        this.getDatosBasicosRelacionUnidOrgOficinaManager().saveDatosBasicosRelacionesUnidOrgOficinaVO(relacionesUnidOrgOficinaDCO);
        if (logger.isDebugEnabled()) {
            logger.debug("Relaciones entre las oficinas y las unid. org\u00e1nicas inicializados");
        }
        EstadoActualizacionDcoVO estadoActualizacion = this.getDataEstadoActualizacionDCO();
        this.getEstadoActualizacionDCOManager().save((Object)estadoActualizacion);
        if (logger.isDebugEnabled()) {
            logger.debug("EstadoActualizacion inicializados");
            logger.debug("Finalizada la inicializaci\u00f3n del sistema");
        }
    }

    private EstadoActualizacionDcoVO getDataEstadoActualizacionDCO() {
        EstadoActualizacionDcoVO estadoActualizacion = new EstadoActualizacionDcoVO();
        estadoActualizacion.setId("0");
        estadoActualizacion.setFechaActualizacion(Calendar.getInstance().getTime());
        estadoActualizacion.setEstado("OK");
        return estadoActualizacion;
    }

    public void generateScriptsInicializacionDirectorioComun() {
        if (logger.isDebugEnabled()) {
            logger.debug("Comienza la generaci\u00f3n de los script de inicializaci\u00f3n del sistema");
        }
        String fileInicializarOficinas = this.getServicioObtenerInicializacionDCO().getFicheroInicializarOficinasDCO();
        String fileInicializarUnidades = this.getServicioObtenerInicializacionDCO().getFicheroInicializarUnidadesDCO();
        String fileInicializarRelacionesUnidOrgOficina = this.getServicioObtenerInicializacionDCO().getFicheroInicializarRelacionesRelacionesOficinaUnidOrgDCO();
        this.getGenerateScriptSQLOficinaManager().generateScriptInicializacion(fileInicializarOficinas);
        this.getGenerateScriptSQLUnidadOrganicaManager().generateScriptInicializacion(fileInicializarUnidades);
        this.getGenerateScriptSQLRelacionesOficinaUnidOrgManager().generateScriptInicializacion(fileInicializarRelacionesUnidOrgOficina);
        if (logger.isDebugEnabled()) {
            logger.debug("Finaliza la generaci\u00f3n de los script de inicializaci\u00f3n del sistema");
        }
    }

    private String composeScriptFileName(String scriptsFilesDir2, String init2, String oficinas2) {
        return null;
    }

    public ServicioObtenerInicializacionDCO getServicioObtenerInicializacionDCO() {
        return this.servicioObtenerInicializacionDCO;
    }

    public void setServicioObtenerInicializacionDCO(ServicioObtenerInicializacionDCO servicioObtenerInicializacionDCO) {
        this.servicioObtenerInicializacionDCO = servicioObtenerInicializacionDCO;
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

    public EstadoActualizacionDCOManager getEstadoActualizacionDCOManager() {
        return this.estadoActualizacionDCOManager;
    }

    public void setEstadoActualizacionDCOManager(EstadoActualizacionDCOManager estadoActualizacionDCOManager) {
        this.estadoActualizacionDCOManager = estadoActualizacionDCOManager;
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
