package ieci.tecdoc.sgm.pe.struts.action;

import ieci.tecdoc.sgm.core.services.pago.Liquidacion;
import ieci.tecdoc.sgm.core.services.pago.Tasa;
import ieci.tecdoc.sgm.pe.struts.FormCreator;
import ieci.tecdoc.sgm.pe.struts.PagoElectronicoManagerHelper;
import ieci.tecdoc.sgm.pe.struts.cert.UserCertificateUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ObtenerJustificanteAction
extends Action {
    private static final Logger logger = Logger.getLogger((Class)ObtenerJustificanteAction.class);
    private static final String ERROR_FORWARD = "error";
    private static final String JUSTIFICANTE_CONTENT_TYPE = "application/pdf";
    private static final int BUFFER_SIZE = 2048;

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Lifted jumps to return sites
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        if (UserCertificateUtil.getUserData(request) == null) {
            return mapping.findForward("error");
        }
        cReferencia = request.getParameter("referencia");
        oFile = null;
        oInputStream = null;
        oOutputStream = null;
        try {
            block22 : {
                block20 : {
                    oLiquidacion = PagoElectronicoManagerHelper.obtenerDatosLiquidacion(request, cReferencia);
                    if (!"01".equals(oLiquidacion.getEstado())) {
                        actionForward = mapping.findForward("error");
                        var15_14 = null;
                        if (oInputStream != null) {
                            try {
                                oInputStream.close();
                            }
                            catch (IOException e) {
                                ObtenerJustificanteAction.logger.error((Object)"Error cerrando input stream.");
                            }
                        }
                        if (oFile == null) return actionForward;
                        if (oFile.exists() == false) return actionForward;
                        if (oFile.isFile() == false) return actionForward;
                        if (oFile.delete() != false) return actionForward;
                        sbError = new StringBuffer("Error borrando archivo temporal: ");
                        sbError.append(oFile.getAbsolutePath());
                        ObtenerJustificanteAction.logger.error((Object)sbError.toString());
                        return actionForward;
                    }
                    cXML = PagoElectronicoManagerHelper.obtenerDocumentoPago(request, cReferencia);
                    oFile = FormCreator.crearJustificantePago(oLiquidacion.getTasa(), cXML, request);
                    response.setContentType("application/pdf");
                    oInputStream = new FileInputStream(oFile);
                    oOutputStream = response.getOutputStream();
                    buffer = new byte[2048];
                    count = 0;
                    n = 0;
                    while (-1 != (n = oInputStream.read(buffer))) {
                        oOutputStream.write(buffer, 0, n);
                        count+=n;
                    }
                    var15_15 = null;
                    if (oInputStream == null) break block20;
                    ** try [egrp 3[TRYBLOCK] [8 : 231->239)] { 
lbl44: // 1 sources:
                    oInputStream.close();
                    ** GOTO lbl48
lbl46: // 1 sources:
                    catch (IOException e) {
                        ObtenerJustificanteAction.logger.error((Object)"Error cerrando input stream.");
                    }
                }
                if (oFile == null) return null;
                if (oFile.exists() == false) return null;
                if (oFile.isFile() == false) return null;
                if (oFile.delete() != false) return null;
                sbError = new StringBuffer("Error borrando archivo temporal: ");
                sbError.append(oFile.getAbsolutePath());
                ObtenerJustificanteAction.logger.error((Object)sbError.toString());
                return null;
                catch (Throwable e) {
                    ObtenerJustificanteAction.logger.error((Object)e.getMessage(), e);
                    try {
                        response.sendError(500);
                    }
                    catch (IOException e1) {
                        block21 : {
                            buffer = null;
                            var15_16 = null;
                            if (oInputStream != null) {
                                ** try [egrp 3[TRYBLOCK] [8 : 231->239)] { 
lbl68: // 1 sources:
                                oInputStream.close();
                                break block21;
lbl70: // 1 sources:
                                catch (IOException e) {
                                    ObtenerJustificanteAction.logger.error((Object)"Error cerrando input stream.");
                                }
                            }
                        }
                        if (oFile == null) return buffer;
                        if (oFile.exists() == false) return buffer;
                        if (oFile.isFile() == false) return buffer;
                        if (oFile.delete() != false) return buffer;
                        sbError = new StringBuffer("Error borrando archivo temporal: ");
                        sbError.append(oFile.getAbsolutePath());
                        ObtenerJustificanteAction.logger.error((Object)sbError.toString());
                        return buffer;
                    }
                }
                var15_17 = null;
                if (oInputStream != null) {
                    ** try [egrp 3[TRYBLOCK] [8 : 231->239)] { 
lbl84: // 1 sources:
                    oInputStream.close();
                    break block22;
lbl86: // 1 sources:
                    catch (IOException e) {
                        ObtenerJustificanteAction.logger.error((Object)"Error cerrando input stream.");
                    }
                }
            }
            if (oFile == null) return null;
            if (oFile.exists() == false) return null;
            if (oFile.isFile() == false) return null;
            if (oFile.delete() != false) return null;
            sbError = new StringBuffer("Error borrando archivo temporal: ");
            sbError.append(oFile.getAbsolutePath());
            ObtenerJustificanteAction.logger.error((Object)sbError.toString());
            return null;
        }
        catch (Throwable var14_28) {
            block23 : {
                var15_18 = null;
                if (oInputStream != null) {
                    ** try [egrp 3[TRYBLOCK] [8 : 231->239)] { 
lbl102: // 1 sources:
                    oInputStream.close();
                    break block23;
lbl104: // 1 sources:
                    catch (IOException e) {
                        ObtenerJustificanteAction.logger.error((Object)"Error cerrando input stream.");
                    }
                }
            }
            if (oFile == null) throw var14_28;
            if (oFile.exists() == false) throw var14_28;
            if (oFile.isFile() == false) throw var14_28;
            if (oFile.delete() != false) throw var14_28;
            sbError = new StringBuffer("Error borrando archivo temporal: ");
            sbError.append(oFile.getAbsolutePath());
            ObtenerJustificanteAction.logger.error((Object)sbError.toString());
            throw var14_28;
        }
    }
}
