package com.ieci.tecdoc.isicres.usecase.book.xml;

import com.ieci.tecdoc.common.invesicres.ScrDistreg;
import com.ieci.tecdoc.common.invesicres.ScrDistregstate;
import com.ieci.tecdoc.common.isicres.DtrFdrResults;
import com.ieci.tecdoc.isicres.desktopweb.Keys;
import com.ieci.tecdoc.isicres.desktopweb.utils.RBUtil;
import com.ieci.tecdoc.isicres.usecase.book.xml.XMLUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLDistReg
implements Keys {
    private static Logger _logger = Logger.getLogger((Class)XMLDistReg.class);
    private static SimpleDateFormat dateFormat = null;

    public static Document createXMLDistReg(List<DtrFdrResults> list, Integer bookID, int fdrid, Locale locale) {
        dateFormat = XMLUtils.getDateFormatView(locale);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Sicreslist");
        XMLDistReg.addContext(list.size(), locale, root);
        XMLDistReg.addHeadMinuta(locale, root);
        XMLDistReg.addBodyMinuta(locale, root);
        int i = 0;
        for (DtrFdrResults result2 : list) {
            XMLDistReg.addMinuta(i++, bookID, fdrid, result2, locale, root);
        }
        return document;
    }

    public static Document createXMLDistRegWithRemarkDistribution(List<DtrFdrResults> list, Integer bookID, int fdrid, Locale locale, String nameBook, String numReg) {
        dateFormat = XMLUtils.getDateFormatView(locale);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("Sicreslist");
        XMLDistReg.addContext(list.size(), locale, root);
        XMLDistReg.addHead(locale, root, nameBook, numReg);
        XMLDistReg.addHeadMinutaWithRemarkDistribution(locale, root);
        XMLDistReg.addBodyMinuta(locale, root);
        int i = 0;
        for (DtrFdrResults result2 : list) {
            XMLDistReg.addMinutaWithRemarkDistribution(i++, bookID, fdrid, result2, locale, root);
        }
        return document;
    }

    private static void addContext(int size, Locale locale, Element parent) {
        Element context = parent.addElement("CONTEXT");
        context.addElement("INIT").addText("1");
        context.addElement("PASO").addText(Integer.toString(size));
        context.addElement("END").addText(Integer.toString(size));
        context.addElement("TOTAL").addText(Integer.toString(size));
    }

    private static void addHead(Locale locale, Element parent, String nameBook, String numReg) {
        Element context = parent.addElement("HEAD");
        context.addElement("NameArch").addText(nameBook);
        context.addElement("FolderNumber").addText(numReg);
    }

    private static void addHeadMinuta(Locale locale, Element parent) {
        Element headMinuta = parent.addElement("HEADMINUTA");
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col11")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col12")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col13")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col8")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col14")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col9")));
    }

    private static void addHeadMinutaWithRemarkDistribution(Locale locale, Element parent) {
        Element headMinuta = parent.addElement("HEADMINUTA");
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col11")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col12")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col13")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col8")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col9")));
        headMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.headminuta.col15")));
    }

    private static void addBodyMinuta(Locale locale, Element parent) {
        Element bodyMinuta = parent.addElement("BODYMINUTA");
        bodyMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.bodyminuta.col8")));
        bodyMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.bodyminuta.col9")));
        bodyMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.bodyminuta.col11")));
        bodyMinuta.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.bodyminuta.col12")));
    }

    private static void addMinuta(int index, Integer bookID, int fdrid, DtrFdrResults result, Locale locale, Element parent) {
        Element minuta = parent.addElement("MINUTA").addAttribute("Id", Integer.toString(index));
        Element head = minuta.addElement("HEAD");
        head.addElement("COL").addText(dateFormat.format(result.getScrDistReg().getDistDate()));
        head.addElement("COL").add(DocumentHelper.createCDATA((String)result.getSourceDescription()));
        head.addElement("COL").add(DocumentHelper.createCDATA((String)result.getTargetDescription()));
        head.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.minuta.dist.state." + result.getScrDistReg().getState())));
        if (result.isFlowProcess()) {
            head.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.minuta.head.tramitation")));
        } else {
            head.addElement("COL").add(DocumentHelper.createCDATA((String)""));
        }
        head.addElement("COL").addText(dateFormat.format(result.getScrDistReg().getStateDate()));
        Element body = minuta.addElement("BODY");
        Element row = null;
	for (Iterator it03 = result.getScrDistRegState().iterator(); it03.hasNext();) {
	    ScrDistregstate scr2 = (ScrDistregstate) it03.next();
            row = body.addElement("ROW").addAttribute("Id", result.getScrDistReg().getId().toString()).addAttribute("IdArch", bookID.toString()).addAttribute("IdFdr", Integer.toString(fdrid));
            row.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.minuta.dist.state." + scr2.getState())));
            row.addElement("COL").addText(dateFormat.format(scr2.getStateDate()));
            row.addElement("COL").addText(scr2.getUsername());
            String scrMessage = scr2.getMessage();
            if (StringUtils.isEmpty((String)scrMessage)) {
                scrMessage = "";
            }
            row.addElement("COL").addText(scrMessage);
        }
    }

    private static void addMinutaWithRemarkDistribution(int index, Integer bookID, int fdrid, DtrFdrResults result, Locale locale, Element parent) {
        Element minuta = parent.addElement("MINUTA").addAttribute("Id", Integer.toString(index));
        Element head = minuta.addElement("HEAD");
        head.addElement("COL").addText(dateFormat.format(result.getScrDistReg().getDistDate()));
        head.addElement("COL").add(DocumentHelper.createCDATA((String)result.getSourceDescription()));
        head.addElement("COL").add(DocumentHelper.createCDATA((String)result.getTargetDescription()));
        head.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.minuta.dist.state." + result.getScrDistReg().getState())));
        head.addElement("COL").addText(dateFormat.format(result.getScrDistReg().getStateDate()));
        Element comentarioDistr = head.addElement("COL").addAttribute("TextLong", "1");
        comentarioDistr.add(DocumentHelper.createCDATA((String)result.getScrDistReg().getMessage()));
        Element body = minuta.addElement("BODY");
        Element row = null;
	for (Iterator it03 = result.getScrDistRegState().iterator(); it03.hasNext();) {
	    ScrDistregstate scr2 = (ScrDistregstate) it03.next();
            row = body.addElement("ROW").addAttribute("Id", result.getScrDistReg().getId().toString()).addAttribute("IdArch", bookID.toString()).addAttribute("IdFdr", Integer.toString(fdrid));
            row.addElement("COL").add(DocumentHelper.createCDATA((String)RBUtil.getInstance(locale).getProperty("bookusecase.distributionhistory.minuta.dist.state." + scr2.getState())));
            row.addElement("COL").addText(dateFormat.format(scr2.getStateDate()));
            row.addElement("COL").addText(scr2.getUsername());
            String scrMessage = scr2.getMessage();
            if (StringUtils.isEmpty((String)scrMessage)) {
                scrMessage = "";
            }
            row.addElement("COL").addText(scrMessage);
        }
    }
}
