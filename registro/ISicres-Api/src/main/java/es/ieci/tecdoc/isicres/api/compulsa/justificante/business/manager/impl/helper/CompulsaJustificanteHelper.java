package es.ieci.tecdoc.isicres.api.compulsa.justificante.business.manager.impl.helper;

import com.ieci.tecdoc.common.invesdoc.Idocarchdet;
import com.ieci.tecdoc.common.invesicres.ScrCa;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.invesicres.ScrOrg;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.common.keys.IDocKeys;
import com.ieci.tecdoc.common.utils.ISicresQueries;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.session.folder.FolderFileSession;
import com.ieci.tecdoc.utils.HibernateUtil;
import com.ieci.tecdoc.utils.cache.CacheBag;
import com.ieci.tecdoc.utils.cache.CacheFactory;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfTemplate;
import es.ieci.tecdoc.isicres.api.business.manager.ContextoAplicacionManagerFactory;
import es.ieci.tecdoc.isicres.api.business.vo.BaseRegistroVO;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ContextoAplicacionVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.business.manager.impl.CompulsaJustificanteKeys;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.business.vo.ISicresCompulsaJustificanteDatosEspecificosVO;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.business.vo.ISicresCompulsaJustificanteVO;
import es.ieci.tecdoc.isicres.api.compulsa.justificante.exception.ISicresCompulsaJustificanteException;
import es.ieci.tecdoc.isicres.terceros.business.vo.InteresadoVO;
import gnu.trove.THashMap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.NullableType;
import net.sf.hibernate.type.Type;
import org.apache.log4j.Logger;

public class CompulsaJustificanteHelper {
    private static final Logger logger = Logger.getLogger((Class)CompulsaJustificanteHelper.class);
    static CompulsaJustificanteHelper instance;

    public static synchronized CompulsaJustificanteHelper getInstance() {
        if (instance == null) {
            instance = new CompulsaJustificanteHelper();
        }
        return instance;
    }

    public Image getBackgroundForData(String imagePath) {
        Image image;
        try {
            image = Image.getInstance((String)imagePath);
        }
        catch (BadElementException e) {
            String msgError = "Se esta intentado leer una imagen no valida.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        catch (MalformedURLException e) {
            String msgError = "El path al fichero de la imagen no es correcto.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        catch (IOException e) {
            String msgError = "Ha ocurrido un error al leer el fichero de imagen.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        return image;
    }

    public Image getPageWithRoomForData(PdfImportedPage page, ISicresCompulsaJustificanteDatosEspecificosVO datosEspecificosVO) {
        Image pageImage;
        try {
            pageImage = Image.getInstance((PdfTemplate)page);
        }
        catch (BadElementException e) {
            String msgError = "Se esta intentando crear una imagen de una pagina del PDF no valida.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        if (datosEspecificosVO.getBand() == 1) {
            pageImage.setAbsolutePosition(datosEspecificosVO.getBandSize(), 0.0f);
            pageImage.scaleAbsoluteWidth(page.getWidth() - datosEspecificosVO.getBandSize());
            pageImage.scaleAbsoluteHeight(page.getHeight());
        } else {
            pageImage.setAbsolutePosition(0.0f, 0.0f);
            pageImage.scaleAbsoluteWidth(page.getWidth());
            pageImage.scaleAbsoluteHeight(page.getHeight() - datosEspecificosVO.getBandSize());
        }
        return pageImage;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addBackgroundImageForData(Document document, Image image, ISicresCompulsaJustificanteDatosEspecificosVO datosEspecificosVO) {
        if (datosEspecificosVO.getBand() == 1) {
            image.setRotationDegrees(90.0f);
            int i = 0;
            while (i < (int)document.getPageSize().getHeight()) {
                image.setAbsolutePosition(0.0f, (float)i);
                image.scaleAbsoluteHeight(datosEspecificosVO.getBandSize());
                try {
                    document.add((Element)image);
                }
                catch (DocumentException e) {
                    String msgError = "Error al a\u00f1adir una imagen al documento PDF.";
                    logger.error((Object)msgError, (Throwable)e);
                    throw new ISicresCompulsaJustificanteException(msgError);
                }
                i = (int)((float)i + image.getWidth());
            }
            return;
        }
        for (int i = 0; i < (int)document.getPageSize().getWidth(); ++i) {
            image.setAbsolutePosition((float)i, (float)((int)document.getPageSize().getWidth()) - datosEspecificosVO.getBandSize());
            image.scaleAbsoluteHeight(datosEspecificosVO.getBandSize());
            try {
                document.add((Element)image);
                continue;
            }
            catch (DocumentException e) {
                String msgError = "Error al a\u00f1adir una imagen al documento PDF.";
                logger.error((Object)msgError, (Throwable)e);
                throw new ISicresCompulsaJustificanteException(msgError);
            }
        }
    }

    public ISicresCompulsaJustificanteDatosEspecificosVO getCompulsaDatosEspecificos(Locale locale) {
        ISicresCompulsaJustificanteDatosEspecificosVO compulsaDatosEspecificosVO = new ISicresCompulsaJustificanteDatosEspecificosVO();
        compulsaDatosEspecificosVO.setMargen(new Float(RBUtil.getInstance((Locale)locale).getProperty("pdf.water.mark.position.x")).floatValue());
        compulsaDatosEspecificosVO.setFont(RBUtil.getInstance((Locale)locale).getProperty("pdf.water.mark.font"));
        compulsaDatosEspecificosVO.setFontSize(new Float(RBUtil.getInstance((Locale)locale).getProperty("pdf.water.mark.size")).floatValue());
        compulsaDatosEspecificosVO.setEncoding(RBUtil.getInstance((Locale)locale).getProperty("pdf.water.mark.encoding"));
        compulsaDatosEspecificosVO.setBand(new Integer(RBUtil.getInstance((Locale)locale).getProperty("pdf.water.mark.band.vh")));
        compulsaDatosEspecificosVO.setBandSize(new Integer(RBUtil.getInstance((Locale)locale).getProperty("pdf.water.mark.band.size")).intValue());
        return compulsaDatosEspecificosVO;
    }

    public String getParsedData(ISicresCompulsaJustificanteVO iSicresCompulsaVO) {
        String dataText;
        Session session;
        try {
            dataText = this.readStream(new FileInputStream(iSicresCompulsaVO.getDatosPath()));
        }
        catch (FileNotFoundException e) {
            String msgError = "Error al leer el fichero que se usara de plantilla para los Datos de Informaci\u00f3n de la compulsa.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        try {
            session = HibernateUtil.currentSession((String)iSicresCompulsaVO.getEntidad());
        }
        catch (HibernateException e) {
            String msgError = "Error al obtener la sesi\u00f3n de Hibernate.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        StringBuffer sb = new StringBuffer(dataText.length());
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher((CharSequence)dataText);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String value = this.parseTag(iSicresCompulsaVO, session, tag);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public void addDataTextToDocument(PdfContentByte pdfContentByte, ISicresCompulsaJustificanteDatosEspecificosVO datosEspecificosVO, String dataText) {
        BufferedReader reader = new BufferedReader(new StringReader(dataText));
        try {
            String lineStr;
            BaseFont baseFont = BaseFont.createFont((String)datosEspecificosVO.getFont(), (String)datosEspecificosVO.getEncoding(), (boolean)false);
            float xPoint = datosEspecificosVO.getMargen();
            pdfContentByte.setFontAndSize(baseFont, datosEspecificosVO.getFontSize());
            pdfContentByte.beginText();
            while ((lineStr = reader.readLine()) != null) {
                pdfContentByte.setTextMatrix(0.0f, 1.0f, -1.0f, 0.0f, xPoint, datosEspecificosVO.getMargen());
                pdfContentByte.showText(lineStr);
                xPoint+=datosEspecificosVO.getFontSize();
            }
            pdfContentByte.endText();
        }
        catch (DocumentException e) {
            String msgError = "Error al a\u00f1adir texto al documento PDF.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        catch (IOException e) {
            String msgError = "Error al a\u00f1adir texto al documento PDF.";
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
    }

    public String getParsedPageNumbers(String dataText, int totalPagesNumber, int currentPageNumber) {
        StringBuffer sb = new StringBuffer(dataText.length());
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher((CharSequence)dataText);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String value = "";
            if (tag.equals(CompulsaJustificanteKeys.TAG_TOTAL_PAGES)) {
                value = Integer.toString(totalPagesNumber);
            }
            if (tag.equals(CompulsaJustificanteKeys.TAG_CURRENT_PAGE)) {
                value = Integer.toString(currentPageNumber);
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String parseTag(ISicresCompulsaJustificanteVO iSicresCompulsaVO, Session session, String tag) {
        Integer asuntoId;
        Integer organismoId;
        Integer oficinaId;
        AxSf axsf = null;
        if (tag.equals(CompulsaJustificanteKeys.TAG_ASUNTO) && (asuntoId = this.getInteger(this.getAxsf(session, axsf).getAttributeValue("fld16"))) != 0) {
            return this.getAsunto(session, asuntoId);
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_NUM_REGISTRO)) {
            return this.getAxsf(session, axsf).getAttributeValue("fld1").toString();
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_OFICINA_REGISTRO) && (oficinaId = this.getInteger(this.getAxsf(session, axsf).getAttributeValue("fld5"))) != 0) {
            return this.getNameOficina(session, oficinaId);
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_COD_OFICINA_REGISTRO) && (oficinaId = this.getInteger(this.getAxsf(session, axsf).getAttributeValue("fld5"))) != 0) {
            return this.getCodeOficina(session, oficinaId);
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_USUARIO)) {
            return this.getAxsf(session, axsf).getAttributeValue("fld3").toString();
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_ORIGEN) && (organismoId = this.getInteger(this.getAxsf(session, axsf).getAttributeValue("fld7"))) != 0) {
            return this.getOrganismo(session, organismoId);
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_DESTINO) && (organismoId = this.getInteger(this.getAxsf(session, axsf).getAttributeValue("fld8"))) != 0) {
            return this.getOrganismo(session, organismoId);
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_REMITENTE)) {
            iSicresCompulsaVO.getRegistro().getInteresadoPrincipal().getNombre();
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_CERTIFICADO)) {
            return iSicresCompulsaVO.getCNcertificado();
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_CERTIFICADO_SOLO_NOMBRE)) {
            return this.getNombreFirmante(iSicresCompulsaVO.getCNcertificado());
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_FECHA_COMPULSA)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return sdf.format(iSicresCompulsaVO.getFechaCompulsa());
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_TOTAL_PAGES)) {
            return "${" + tag + "}";
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_CURRENT_PAGE)) {
            return "${" + tag + "}";
        }
        if (tag.equals(CompulsaJustificanteKeys.TAG_LOCATOR)) {
            return iSicresCompulsaVO.getLocator();
        }
        return "";
    }

    private String getAsunto(Session session, Integer asuntoId) {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrCa");
        query.append(" scr WHERE scr.id=?");
        try {
            List asuntos = session.find(query.toString(), new Object[]{asuntoId}, new Type[]{Hibernate.INTEGER});
            if (!(asuntos == null || asuntos.isEmpty())) {
                ScrCa asunto = (ScrCa)asuntos.get(0);
                return asunto.getMatter();
            }
        }
        catch (HibernateException e) {
            String msgError = "Error al obtener el literal del asunto con Id:" + asuntoId.toString();
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        return "";
    }

    protected AxSf getAxsf(Session session, AxSf axsf) {
        AxSf result = null;
        if (axsf == null) {
            try {
                ContextoAplicacionVO contextoAplicacion = ContextoAplicacionManagerFactory.getInstance().getContextoAplicacionVO();
                BaseRegistroVO registroActual = contextoAplicacion.getRegistroActual();
                String entidad = contextoAplicacion.getUsuarioActual().getConfiguracionUsuario().getIdEntidad();
                boolean load = false;
                Locale locale = contextoAplicacion.getUsuarioActual().getConfiguracionUsuario().getLocale();
                Integer idLibro = Integer.parseInt(registroActual.getIdLibro());
                Integer idRegistro = Integer.parseInt(registroActual.getIdRegistro());
                String sessionID = contextoAplicacion.getUsuarioActual().getConfiguracionUsuario().getSessionID();
                CacheBag cacheBag = CacheFactory.getCacheInterface().getCacheEntry(sessionID);
                THashMap bookInformation = (THashMap)cacheBag.get((Object)idLibro);
                Idocarchdet idoc = (Idocarchdet)bookInformation.get((Object)IDocKeys.IDOCARCHDET_FLD_DEF_ASOBJECT);
                result = FolderFileSession.getAxSf((Session)session, (Integer)idLibro, (Integer)idRegistro, (Idocarchdet)idoc, (String)locale.getLanguage(), (String)entidad, (boolean)load);
            }
            catch (Exception ex) {
                String msgError = "Error en el proceso de compulsa, no se ha podido recuperar Axsf.";
                logger.error((Object)msgError, (Throwable)ex);
                throw new ISicresCompulsaJustificanteException(msgError, ex);
            }
        } else {
            result = axsf;
        }
        return result;
    }

    private String getNameOficina(Session session, Integer oficinaId) {
        try {
            ScrOfic oficina = ISicresQueries.getScrOficById((Session)session, (Integer)oficinaId);
            if (oficina != null && oficina.getName() != null && oficina.getName().length() != 0) {
                return oficina.getName();
            }
        }
        catch (HibernateException e) {
            String msgError = "Error al obtener el literal de la oficina con Id:" + oficinaId.toString();
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        return "";
    }

    private String getCodeOficina(Session session, Integer oficinaId) {
        try {
            ScrOfic oficina = ISicresQueries.getScrOficById((Session)session, (Integer)oficinaId);
            if (oficina != null && oficina.getName() != null && oficina.getName().length() != 0) {
                return oficina.getCode();
            }
        }
        catch (HibernateException e) {
            String msgError = "Error al obtener el c\u00f3digo de la oficina con Id:" + oficinaId.toString();
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        return "";
    }

    private String getOrganismo(Session session, Integer organismoId) {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrOrg");
        query.append(" scr WHERE scr.id=?");
        try {
            List organismos = session.find(query.toString(), new Object[]{organismoId}, new Type[]{Hibernate.INTEGER});
            if (!(organismos == null || organismos.isEmpty())) {
                ScrOrg organismo = (ScrOrg)organismos.get(0);
                return organismo.getName();
            }
        }
        catch (HibernateException e) {
            String msgError = "Error al obtener el literal del organismo con Id:" + organismoId.toString();
            logger.error((Object)msgError, (Throwable)e);
            throw new ISicresCompulsaJustificanteException(msgError);
        }
        return "";
    }

    private String getNombreFirmante(String certificado) {
        String result = certificado;
        if (certificado.lastIndexOf(" - ") != -1) {
            result = certificado.substring(0, certificado.lastIndexOf(" - "));
        }
        return result;
    }

    private Integer getInteger(Object value) {
        try {
            int aux_value = Integer.parseInt(value.toString());
            return new Integer(aux_value);
        }
        catch (Exception e) {
            return new Integer(0);
        }
    }

    private String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            InputStreamReader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while (c != -1) {
                c = r.read();
                sb.append((char)c);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
