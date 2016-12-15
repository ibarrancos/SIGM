package es.ieci.tecdoc.fwktd.dir3.api.manager.impl;

import es.ieci.tecdoc.fwktd.dir3.api.helper.ScriptFilesHelper;
import es.ieci.tecdoc.fwktd.dir3.api.helper.XmlDcoToObject;
import es.ieci.tecdoc.fwktd.dir3.api.manager.DatosBasicosRelacionUnidOrgOficinaManager;
import es.ieci.tecdoc.fwktd.dir3.api.manager.GenerateScriptSQLManager;
import es.ieci.tecdoc.fwktd.dir3.api.vo.DatosBasicosRelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionUnidOrgOficinaVO;
import es.ieci.tecdoc.fwktd.dir3.api.vo.relacion.RelacionesUnidOrgOficinaVO;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateScriptSQLRelacionesOficinaUnidOrgManagerImpl
implements GenerateScriptSQLManager {
    private static final Logger logger = LoggerFactory.getLogger((Class)GenerateScriptSQLRelacionesOficinaUnidOrgManagerImpl.class);
    protected DatosBasicosRelacionUnidOrgOficinaManager datosBasicosRelacionUnidOrgOficinaManager;
    protected String scriptsFilesDir;
    private static final String VIGENTE = "V";
    private static final String DOBLE_ESCAPE_COMILLA_SIMPLE = "''";
    private static final String ESCAPE_COMILLA_SIMPLE = "'";
    private String RELACION_INSERT = "INSERT INTO DIR_RELAC_UNID_ORG_OFIC (CODIGO_OFICINA, DENOMINACION_OFICINA, CODIGO_UNIDAD_ORGANICA, DENOM_UNIDAD_ORGANICA) VALUES('%1$s','%3$s','%2$s','%4$s');\n";
    private String RELACION_UPDATE = "UPDATE DIR_RELAC_UNID_ORG_OFIC  SET DENOMINACION_OFICINA = '%3$s', DENOM_UNIDAD_ORGANICA = '%4$s' WHERE CODIGO_OFICINA = '%1$s'; AND CODIGO_UNIDAD_ORGANICA = '%2$s'; \n";
    private String RELACION_DELETE = "DELETE FROM DIR_RELAC_UNID_ORG_OFIC WHERE CODIGO_OFICINA = '%1$s'; AND CODIGO_UNIDAD_ORGANICA = '%2$s'; \n";

    @Override
    public void generateScriptInicializacion(String fileXMLData) {
        RelacionesUnidOrgOficinaVO relacionesDCO = XmlDcoToObject.getInstance().getRelacionesUnidOrgOficinaFromXmlFile(fileXMLData);
        String outputFileRelacionesUnidOrgOficina = ScriptFilesHelper.getScriptFileName(this.getScriptsFilesDir(), "relacionesUnidOrgOfic", "inicializacion");
        try {
            this.writeSentencesInFileInit(relacionesDCO, outputFileRelacionesUnidOrgOficina);
        }
        catch (IOException e) {
            logger.error("Error al generar el script de inicializaci\u00f3n de las relaciones entre las unid. org\u00e1nicas y las oficinas del DCO", (Throwable)e);
        }
    }

    @Override
    public void generateScriptActualizacion(String fileXMLDataUpdate) {
        RelacionesUnidOrgOficinaVO relacionesDCO = XmlDcoToObject.getInstance().getRelacionesUnidOrgOficinaFromXmlFile(fileXMLDataUpdate);
        String outputFileRelacionesUnidOrgOficina = ScriptFilesHelper.getScriptFileName(this.getScriptsFilesDir(), "relacionesUnidOrgOfic", "actualizacion");
        try {
            this.writeSentencesInFileUpdate(relacionesDCO, outputFileRelacionesUnidOrgOficina);
        }
        catch (IOException e) {
            logger.error("Error al generar el script de actualizacion de Oficinas del DCO", (Throwable)e);
        }
    }

    @Override
    public List<String> generateSententesSQLUpdate(String fileXMLDataUpdate) {
        RelacionesUnidOrgOficinaVO relacionesDCO = XmlDcoToObject.getInstance().getRelacionesUnidOrgOficinaFromXmlFile(fileXMLDataUpdate);
        Iterator<RelacionUnidOrgOficinaVO> iterator = relacionesDCO.getRelacionesUnidOrgOficinaVO().iterator();
        ArrayList<String> stmtList = new ArrayList<String>();
        while (iterator.hasNext()) {
            RelacionUnidOrgOficinaVO relacion = iterator.next();
            stmtList.add(this.getSentenceSQL(relacion));
        }
        return stmtList;
    }

    private void writeSentencesInFileUpdate(RelacionesUnidOrgOficinaVO relacionesDCO, String outputFileRelacionesUnidOrgOficina) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileRelacionesUnidOrgOficina));
        try {
            for (RelacionUnidOrgOficinaVO relacion : relacionesDCO.getRelacionesUnidOrgOficinaVO()) {
                writer.write(this.getSentenceSQL(relacion));
            }
        }
        finally {
            writer.close();
        }
    }

    private String getSentenceSQL(RelacionUnidOrgOficinaVO relacion) {
        DatosBasicosRelacionUnidOrgOficinaVO datosBasicosRelacion;
        String result = "V".equals(relacion.getEstadoRelacion()) ? ((datosBasicosRelacion = this.getDatosBasicosRelacionUnidOrgOficinaManager().getRelacionesByOficinaAndUnidad(relacion.getCodigoOficina(), relacion.getCodigoUnidadOrganica())) != null ? this.getRelacionStmt(relacion, this.RELACION_UPDATE) : this.getRelacionStmt(relacion, this.RELACION_INSERT)) : this.getRelacionStmt(relacion, this.RELACION_DELETE);
        return result;
    }

    private void writeSentencesInFileInit(RelacionesUnidOrgOficinaVO relacionesDCO, String outputFileRelacionesUnidOrgOficina) throws IOException {
        Iterator<RelacionUnidOrgOficinaVO> iterator = relacionesDCO.getRelacionesUnidOrgOficinaVO().iterator();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileRelacionesUnidOrgOficina));
        try {
            while (iterator.hasNext()) {
                RelacionUnidOrgOficinaVO relacion = iterator.next();
                if (!"V".equals(relacion.getEstadoRelacion())) continue;
                writer.write(this.getRelacionStmt(relacion, this.RELACION_INSERT));
            }
        }
        finally {
            writer.close();
        }
    }

    private String getRelacionStmt(RelacionUnidOrgOficinaVO relacion, String stmtSource) {
        return String.format(stmtSource, relacion.getCodigoOficina(), relacion.getCodigoUnidadOrganica(), relacion.getDenominacionOficina().replace((CharSequence)"'", (CharSequence)"''"), relacion.getDenominacionUnidadOrganica().replace((CharSequence)"'", (CharSequence)"''"));
    }

    public String getScriptsFilesDir() {
        return this.scriptsFilesDir;
    }

    public void setScriptsFilesDir(String scriptsFilesDir) {
        this.scriptsFilesDir = scriptsFilesDir;
    }

    public DatosBasicosRelacionUnidOrgOficinaManager getDatosBasicosRelacionUnidOrgOficinaManager() {
        return this.datosBasicosRelacionUnidOrgOficinaManager;
    }

    public void setDatosBasicosRelacionUnidOrgOficinaManager(DatosBasicosRelacionUnidOrgOficinaManager datosBasicosRelacionUnidOrgOficinaManager) {
        this.datosBasicosRelacionUnidOrgOficinaManager = datosBasicosRelacionUnidOrgOficinaManager;
    }
}
