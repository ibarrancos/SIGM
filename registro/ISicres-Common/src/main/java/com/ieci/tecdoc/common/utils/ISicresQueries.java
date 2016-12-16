package com.ieci.tecdoc.common.utils;

import com.ieci.tecdoc.common.invesdoc.Idocarchdet;
import com.ieci.tecdoc.common.invesdoc.Idocfmt;
import com.ieci.tecdoc.common.invesdoc.Iuserdepthdr;
import com.ieci.tecdoc.common.invesdoc.Iusergroupuser;
import com.ieci.tecdoc.common.invesdoc.Iuserldapgrphdr;
import com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr;
import com.ieci.tecdoc.common.invesdoc.Iuserobjperm;
import com.ieci.tecdoc.common.invesdoc.Iuseruserhdr;
import com.ieci.tecdoc.common.invesdoc.Iuserusersys;
import com.ieci.tecdoc.common.invesicres.ScrBookadmin;
import com.ieci.tecdoc.common.invesicres.ScrBookasoc;
import com.ieci.tecdoc.common.invesicres.ScrCaaux;
import com.ieci.tecdoc.common.invesicres.ScrDistreg;
import com.ieci.tecdoc.common.invesicres.ScrDistribucionActual;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.invesicres.ScrRegstate;
import com.ieci.tecdoc.common.invesicres.ScrTt;
import com.ieci.tecdoc.common.invesicres.ScrValdate;
import com.ieci.tecdoc.common.invesicres.ScrValnum;
import com.ieci.tecdoc.common.invesicres.ScrValstr;
import com.ieci.tecdoc.common.keys.HibernateKeys;
import java.io.Serializable;
import java.util.List;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.type.NullableType;
import net.sf.hibernate.type.Type;
import org.apache.log4j.Logger;

public class ISicresQueries
implements HibernateKeys {
    private static Logger _logger = Logger.getLogger((Class)ISicresQueries.class);

    public static ScrTt getScrTt(Session session, String transport) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrTt");
        query.append(" scr WHERE scr.transport=?");
        List list = session.find(query.toString(), new Object[]{transport}, new Type[]{Hibernate.STRING});
        ScrTt scr = null;
        if (list == null || list.isEmpty()) {
            throw new HibernateException("There is no ScrTt.");
        }
        scr = (ScrTt)list.get(0);
        return scr;
    }

    public static List getScrReportArch(Session session, Integer bookId, Integer reportId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrReportarch");
        query.append(" scr WHERE scr.idArch=? AND scr.idReport=?");
        return session.find(query.toString(), new Object[]{bookId, reportId}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrReportOfic(Session session, Integer oficId, Integer reportId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrReportofic");
        query.append(" scr WHERE scr.idReport=? AND scr.scrOfic.id=?");
        return session.find(query.toString(), new Object[]{reportId, oficId}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrReportPerf(Session session, Integer reportId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrReportperf");
        query.append(" scr WHERE scr.idReport=?");
        return session.find(query.toString(), new Object[]{reportId}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrTypeproc(Session session, String procName) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrTypeproc");
        query.append(" scr WHERE scr.code=?");
        return session.find(query.toString(), new Object[]{procName}, new Type[]{Hibernate.STRING});
    }

    public static ScrRegstate getScrRegstate(Session session, Integer bookId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrRegstate");
        query.append(" scr WHERE scr.idocarchhdr.id=?");
        List list = session.find(query.toString(), new Object[]{bookId}, new Type[]{Hibernate.INTEGER});
        ScrRegstate scr = null;
        if (list == null || list.isEmpty()) {
            throw new HibernateException("There is no ScrRegstate.");
        }
        scr = (ScrRegstate)list.get(0);
        return scr;
    }

    public static List getScrBookofic(Session session, Integer bookId, Integer scrOficId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrBookofic");
        query.append(" scr WHERE scr.idBook=? AND scr.idOfic=?");
        return session.find(query.toString(), new Object[]{bookId, scrOficId}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static ScrOfic getScrOficByCode(Session session, String code) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrOfic");
        query.append(" scr WHERE scr.code=?");
        List list = session.find(query.toString(), new Object[]{code}, new Type[]{Hibernate.STRING});
        ScrOfic scrOfic = null;
        if (!(list == null || list.isEmpty())) {
            scrOfic = (ScrOfic)list.get(0);
        }
        return scrOfic;
    }

    public static ScrOfic getScrOficByDeptId(Session session, Integer deptId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrOfic");
        query.append(" scr WHERE scr.deptid=?");
        List list = session.find(query.toString(), new Object[]{deptId}, new Type[]{Hibernate.INTEGER});
        ScrOfic scrOfic = null;
        if (!(list == null || list.isEmpty())) {
            scrOfic = (ScrOfic)list.get(0);
        }
        return scrOfic;
    }

    public static ScrOfic getScrOficById(Session session, Integer id) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrOfic");
        query.append(" scr WHERE scr.id=?");
        List list = session.find(query.toString(), new Object[]{id}, new Type[]{Hibernate.INTEGER});
        ScrOfic scrOfic = null;
        if (!(list == null || list.isEmpty())) {
            scrOfic = (ScrOfic)list.get(0);
        }
        return scrOfic;
    }

    public static List getScrReport(Session session, int reportType, int bookType) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrReport");
        query.append(" scr WHERE scr.typeReport=? AND scr.typeArch=? ORDER BY scr.id");
        return session.find(query.toString(), new Object[]{new Integer(reportType), new Integer(bookType)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static ScrBookadmin getScrBookadmin(Session session, Integer bookId, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        ScrBookadmin scr = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrBookadmin");
        query.append(" scr WHERE scr.idbook=? AND scr.iduser=?");
        List list = session.find(query.toString(), new Object[]{bookId, userId}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            scr = (ScrBookadmin)list.get(0);
        }
        return scr;
    }

    public static ScrBookasoc getScrBookasoc(Session session, Integer bookId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrBookasoc");
        query.append(" scr WHERE scr.idBook=?");
        List list = session.find(query.toString(), new Object[]{bookId}, new Type[]{Hibernate.INTEGER});
        ScrBookasoc scr = null;
        if (list == null || list.isEmpty()) {
            throw new HibernateException("There is no ScrBookasoc.");
        }
        scr = (ScrBookasoc)list.get(0);
        return scr;
    }

    public static List getScrExtdist(Session session, Integer idOrg) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrExtdist");
        query.append(" scr WHERE scr.idOrgs=?");
        return session.find(query.toString(), new Object[]{idOrg}, new Type[]{Hibernate.INTEGER});
    }

    public static List getIuserobjpermForDept(Session session, Integer bookId, Integer deptId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        Integer DstType = new Integer(2);
        List list = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserobjperm");
        query.append(" iuser WHERE iuser.dstid=? AND iuser.objid=? AND iuser.dsttype=?");
        list = session.find(query.toString(), new Object[]{deptId, bookId, DstType}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            for (int i = list.size() - 1; i >= 0; --i) {
                Iuserobjperm iuser = (Iuserobjperm)list.get(i);
                int Aperm = iuser.getAperm();
                if (Aperm == 32 || Aperm == 128 || Aperm == 256) continue;
                list.remove(i);
            }
        }
        return list;
    }

    public static List getIuserobjpermForDeptFormat(Session session, Integer bookId, Integer deptId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        Integer DstType = new Integer(2);
        List list = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserobjperm");
        query.append(" iuser WHERE iuser.dstid=? AND iuser.objid=? AND iuser.dsttype=?");
        list = session.find(query.toString(), new Object[]{deptId, bookId, DstType}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            for (int i = list.size() - 1; i >= 0; --i) {
                Iuserobjperm iuser = (Iuserobjperm)list.get(i);
                int Aperm = iuser.getAperm();
                if (Aperm == 1) continue;
                list.remove(i);
            }
        }
        return list;
    }

    public static List getIuserobjpermForUser(Session session, Integer bookId, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        Integer DstType = new Integer(1);
        List list = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserobjperm");
        query.append(" iuser WHERE iuser.dstid=? AND iuser.objid=? AND iuser.dsttype=?");
        list = session.find(query.toString(), new Object[]{userId, bookId, DstType}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            for (int i = list.size() - 1; i >= 0; --i) {
                Iuserobjperm iuser = (Iuserobjperm)list.get(i);
                int Aperm = iuser.getAperm();
                if (Aperm == 32 || Aperm == 128 || Aperm == 256) continue;
                list.remove(i);
            }
        }
        return list;
    }

    public static List getIuserobjpermForUserFormat(Session session, Integer bookId, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        Integer DstType = new Integer(1);
        List list = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserobjperm");
        query.append(" iuser WHERE iuser.dstid=? AND iuser.objid=? AND iuser.dsttype=?");
        list = session.find(query.toString(), new Object[]{userId, bookId, DstType}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            for (int i = list.size() - 1; i >= 0; --i) {
                Iuserobjperm iuser = (Iuserobjperm)list.get(i);
                int Aperm = iuser.getAperm();
                if (Aperm == 1) continue;
                list.remove(i);
            }
        }
        return list;
    }

    public static List getIuserobjpermForGroupFormat(Session session, Integer bookId, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        Integer DstType = new Integer(3);
        List list = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserobjperm");
        query.append(" iuser WHERE iuser.dstid=? AND iuser.objid=? AND iuser.dsttype=?");
        list = session.find(query.toString(), new Object[]{userId, bookId, DstType}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            for (int i = list.size() - 1; i >= 0; --i) {
                Iuserobjperm iuser = (Iuserobjperm)list.get(i);
                int Aperm = iuser.getAperm();
                if (Aperm == 1) continue;
                list.remove(i);
            }
        }
        return list;
    }

    public static List getIuserObjHdr(Session session, Integer bookId, Integer fmtId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        List list = null;
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserobjhdr");
        query.append(" iuser WHERE iuser.extid1=? AND iuser.extid2=? AND iuser.type=7 AND iuser.prodid=3");
        list = session.find(query.toString(), new Object[]{bookId, fmtId}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
        return list;
    }

    public static List getIdocarchdet(Session session, Integer bookId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocarchdet");
        query.append(" idoc WHERE idoc.archid=?");
        return session.find(query.toString(), new Object[]{bookId}, new Type[]{Hibernate.INTEGER});
    }

    public static Idocarchdet getIdocarchdet(Session session, Integer bookId, Integer dettype) throws HibernateException {
        Idocarchdet result = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocarchdet");
        query.append(" idoc WHERE idoc.archid=? and idoc.dettype=?");
        List list = session.find(query.toString(), new Object[]{bookId, dettype}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            result = (Idocarchdet)list.get(0);
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("No existe informaci\u00f3n en la tabla Idocarchdet para el libro con ID [").append(bookId).append("] y dettype [").append(dettype).append("]");
            _logger.error((Object)sb.toString());
        }
        return result;
    }

    public static List getIdocprefwfmt(Session session, Integer bookId, Integer userId, Integer fmttype) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocprefwfmt");
        query.append(" idoc WHERE idoc.archid=? AND idoc.userid=? AND idoc.fmttype=?");
        return session.find(query.toString(), new Object[]{bookId, userId, fmttype}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getIdocpreffmt(Session session, Integer bookId, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocpreffmt");
        query.append(" idoc WHERE idoc.archid=? AND idoc.userid=?");
        return session.find(query.toString(), new Object[]{bookId, userId}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getIdocdeffmt(Session session, Integer bookId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocfmt");
        query.append(" idoc WHERE idoc.archid=? AND SUBTYPE=3 ");
        return session.find(query.toString(), new Object[]{bookId}, new Type[]{Hibernate.INTEGER});
    }

    public static List getIdocdeffmt1(Session session, Integer bookId, Integer fmtType, int accesstype) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocfmt");
        query.append(" idoc WHERE idoc.archid=? AND idoc.type =? AND idoc.accesstype = " + accesstype);
        return session.find(query.toString(), new Object[]{bookId, fmtType}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static Idocfmt getIdocfmt(Session session, Integer fmtId) throws HibernateException {
        Idocfmt idoc = (Idocfmt)session.load((Class)Idocfmt.class, (Serializable)fmtId);
        if (idoc != null) {
            return idoc;
        }
        throw new HibernateException("There is no Idocfmt");
    }

    public static List getIdocfdrstat(Session session, Integer bookID, int fdrid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Idocfdrstat");
        query.append(" scr WHERE scr.archid=? AND scr.fdrid=?");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fdrid)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrDistReg(Session session, Integer bookID, int fdrid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDistreg");
        query.append(" scr WHERE scr.idArch=? AND scr.idFdr=? ORDER BY scr.id");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fdrid)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getUpdHistReg(Session session, Integer bookID, String num_reg) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrModifreg");
        query.append(" scr WHERE scr.idArch=? AND scr.numReg=? ORDER BY scr.id");
        return session.find(query.toString(), new Object[]{bookID, num_reg}, new Type[]{Hibernate.INTEGER, Hibernate.STRING});
    }

    public static ScrValstr getScrValstrbyId(Session session, Integer bookID, Integer id_change) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrValstr");
        query.append(" scr WHERE scr.id=?");
        List list = session.find(query.toString(), new Object[]{id_change}, new Type[]{Hibernate.INTEGER});
        ScrValstr scr = null;
        if (!(list == null || list.isEmpty())) {
            scr = (ScrValstr)list.get(0);
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("No existe relaci\u00f3n entre la modificaci\u00f3n con ID [").append(id_change).append("] de ScrModifReg y la tabla ScrValstr");
            _logger.error((Object)sb.toString());
        }
        return scr;
    }

    public static ScrValnum getScrValnumbyId(Session session, Integer bookID, Integer id_change) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrValnum");
        query.append(" scr WHERE scr.id=?");
        List list = session.find(query.toString(), new Object[]{id_change}, new Type[]{Hibernate.INTEGER});
        ScrValnum scr = null;
        if (!(list == null || list.isEmpty())) {
            scr = (ScrValnum)list.get(0);
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("No existe relaci\u00f3n entre la modificaci\u00f3n con ID [").append(id_change).append("] de ScrModifReg y la tabla ScrValnum");
            _logger.error((Object)sb.toString());
        }
        return scr;
    }

    public static ScrValdate getScrValdatebyId(Session session, Integer bookID, Integer id_change) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrValdate");
        query.append(" scr WHERE scr.id=?");
        List list = session.find(query.toString(), new Object[]{id_change}, new Type[]{Hibernate.INTEGER});
        ScrValdate scr = null;
        if (!(list == null || list.isEmpty())) {
            scr = (ScrValdate)list.get(0);
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("No existe relaci\u00f3n entre la modificaci\u00f3n con ID [").append(id_change).append("] de ScrModifReg y la tabla ScrValdate");
            _logger.error((Object)sb.toString());
        }
        return scr;
    }

    public static List getScrRegOrigDoc(Session session, Integer bookID, int fdrid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrRegorigdoc");
        query.append(" scr WHERE scr.idarch=? AND scr.idfdr=? ORDER BY scr.id");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fdrid)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrDistreg(Session session, Integer bookID, int fdrid, int typeDest, int idDest) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDistreg");
        query.append(" scr WHERE scr.idArch=? AND scr.idFdr=? AND scr.typeDest=? AND scr.idDest=?");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fdrid), new Integer(typeDest), new Integer(idDest)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static ScrDistreg getScrDistreg(Session session, Integer distID) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDistreg");
        query.append(" scr WHERE scr.id=?");
        List list = session.find(query.toString(), new Object[]{distID}, new Type[]{Hibernate.INTEGER});
        if (list != null && list.size() == 1) {
            return (ScrDistreg)list.get(0);
        }
        throw new HibernateException("There is no ScrDistreg.");
    }

    public static List getScrDistregByIdDistFather(Session session, int idDist) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDistreg");
        query.append(" scr WHERE scr.iddistfather=?");
        return session.find(query.toString(), new Object[]{new Integer(idDist)}, new Type[]{Hibernate.INTEGER});
    }

    public static List getAsocRegsFdrPrim(Session session, Integer bookID, int fdrid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrRegasoc");
        query.append(" scr WHERE scr.idArchprim=? AND scr.idFdrprim=?");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fdrid)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrRegAsocEx(Session session) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrRegasocex");
        query.append(" scr WHERE scr.type=0");
        return session.find(query.toString());
    }

    public static List getAsocRegsFdrSec(Session session, Integer bookID, int fdrid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrRegasoc");
        query.append(" scr WHERE scr.idArchsec=? AND scr.idFdrsec=?");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fdrid)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrCities(Session session, Integer provID) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrCity");
        query.append(" scr WHERE scr.idProv=? ORDER BY scr.name");
        return session.find(query.toString(), new Object[]{provID}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrProv(Session session) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrProv");
        query.append(" scr ORDER BY scr.name");
        return session.find(query.toString());
    }

    public static List getScrDom(Session session, Integer domID) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDom");
        query.append(" scr WHERE scr.id=?");
        return session.find(query.toString(), new Object[]{domID}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrProcReg(Session session, Integer idRegistro) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrProcreg");
        query.append(" scr WHERE scr.idDist=?");
        return session.find(query.toString(), new Object[]{idRegistro}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrDistregstate(Session session, Integer idRegistro) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDistregstate");
        query.append(" scr WHERE scr.idDist=?");
        query.append(" ORDER BY scr.id");
        return session.find(query.toString(), new Object[]{idRegistro}, new Type[]{Hibernate.INTEGER});
    }

    public static ScrCaaux getScrCaaux(Session session, String fldValue) throws HibernateException {
        Integer fldValueInt = new Integer(fldValue);
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrCaaux");
        query.append(" scr WHERE scr.idMatter=?");
        List list = session.find(query.toString(), new Object[]{fldValueInt}, new Type[]{Hibernate.INTEGER});
        ScrCaaux result = null;
        if (!(list == null || list.isEmpty())) {
            result = (ScrCaaux)list.get(0);
        }
        return result;
    }

    public static List getScrRegInt(Session session, Integer bookID, int fldId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrRegint");
        query.append(" scr WHERE scr.idArch=? and scr.idFdr=? ORDER BY scr.ord");
        return session.find(query.toString(), new Object[]{bookID, new Integer(fldId)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrAddress(Session session, int personId, int idAddress) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrAddress");
        query.append(" scr WHERE scr.idPerson=? and scr.id=?");
        return session.find(query.toString(), new Object[]{new Integer(personId), new Integer(idAddress)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getScrAddrtel(Session session, int idAddress) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrAddrtel");
        query.append(" scr WHERE scr.id=?");
        return session.find(query.toString(), new Object[]{new Integer(idAddress)}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrDom(Session session, int idAddress) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDom");
        query.append(" scr WHERE scr.id=?");
        return session.find(query.toString(), new Object[]{new Integer(idAddress)}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrAddressDom(Session session, int personId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrAddress");
        query.append(" scr WHERE scr.type=0 and scr.idPerson=?");
        return session.find(query.toString(), new Object[]{new Integer(personId)}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrAddressAddrtel(Session session, int personId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrAddress");
        query.append(" scr WHERE scr.type=1 and scr.idPerson=?");
        return session.find(query.toString(), new Object[]{new Integer(personId)}, new Type[]{Hibernate.INTEGER});
    }

    public static List getScrCaDocIDMatter(Session session, Integer id) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrCadoc");
        query.append(" scr WHERE scr.idMatter=?");
        List list = session.find(query.toString(), new Object[]{id}, new Type[]{Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            return list;
        }
        throw new HibernateException("There is no ScrCadoc.");
    }

    public static List getIUserGroupUser(Session session, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iusergroupuser");
        query.append(" scr WHERE scr.userid=?");
        List list = session.find(query.toString(), new Object[]{userId}, new Type[]{Hibernate.INTEGER});
        return list;
    }

    public static List getIUserGroupUserByGroupId(Session session, Integer groupId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iusergroupuser");
        query.append(" scr WHERE scr.groupid=?");
        List list = session.find(query.toString(), new Object[]{groupId}, new Type[]{Hibernate.INTEGER});
        return list;
    }

    public static List getUserLdapUser(Session session, String guid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr");
        query.append(" iuser WHERE iuser.ldapguid=?");
        return session.find(query.toString(), new Object[]{guid}, new Type[]{Hibernate.STRING});
    }

    public static List<Iuserldapuserhdr> getUserLdapUserByFullName(Session session, String fullName) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr");
        query.append(" iuser WHERE iuser.ldapfullname=?");
        return session.find(query.toString(), new Object[]{fullName}, new Type[]{Hibernate.STRING});
    }

    public static Iuserldapuserhdr getUserLdapUser(Session session, Integer userId) throws HibernateException {
        Iuserldapuserhdr user = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr");
        query.append(" iuser WHERE iuser.id=?");
        List users = session.find(query.toString(), new Object[]{userId}, new Type[]{Hibernate.INTEGER});
        if (users != null && users.size() > 0) {
            user = (Iuserldapuserhdr)users.get(0);
        }
        return user;
    }

    public static Iuseruserhdr getUserUserHdr(Session session, Integer id) throws HibernateException {
        Iuseruserhdr user = (Iuseruserhdr)session.load((Class)Iuseruserhdr.class, (Serializable)id);
        return user;
    }

    public static List getUserUserHdrByName(Session session, String login) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuseruserhdr");
        query.append(" iuser WHERE iuser.name=?");
        List list = session.find(query.toString(), (Object)login, (Type)Hibernate.STRING);
        return list;
    }

    public static List getUserUserType(Session session, int userId, int prodid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserusertype");
        query.append(" iuser WHERE iuser.userid=? and iuser.prodid=?");
        return session.find(query.toString(), new Object[]{new Integer(userId), new Integer(prodid)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getUserGenPerms(Session session, int type, int userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iusergenperm");
        query.append(" iuser WHERE iuser.dsttype=? and iuser.dstid=?");
        return session.find(query.toString(), new Object[]{new Integer(type), new Integer(userId)}, new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
    }

    public static List getUserDeptHdr(Session session, int pgrp) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserdepthdr");
        query.append(" iuser WHERE iuser.crtrid=?");
        return session.find(query.toString(), new Object[]{new Integer(pgrp)}, new Type[]{Hibernate.INTEGER});
    }

    public static Iuserdepthdr getUserDeptHdrByDeptId(Session session, Integer deptId) throws HibernateException {
        Iuserdepthdr dept = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserdepthdr");
        query.append(" iuser WHERE iuser.id=?");
        List list = session.find(query.toString(), new Object[]{deptId}, new Type[]{Hibernate.INTEGER});
        if (list != null && list.size() > 0) {
            dept = (Iuserdepthdr)list.get(0);
        }
        return dept;
    }

    public static List<Iuserdepthdr> getUserDeptHdrByCrtrId(Session session, Integer crtrId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserdepthdr");
        query.append(" iuser WHERE iuser.crtrid=?");
        List list = session.find(query.toString(), new Object[]{crtrId}, new Type[]{Hibernate.INTEGER});
        return list;
    }

    public static List getUserLdapPgrp(Session session, String guid) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapgrphdr");
        query.append(" iuser WHERE iuser.ldapguid=?");
        return session.find(query.toString(), new Object[]{guid}, new Type[]{Hibernate.STRING});
    }

    public static Iuserldapgrphdr getUserLdapPgrp(Session session, Integer groupId) throws HibernateException {
        Iuserldapgrphdr ldapGroup = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapgrphdr");
        query.append(" iuser WHERE iuser.id=?");
        List resultList = session.find(query.toString(), new Object[]{groupId}, new Type[]{Hibernate.INTEGER});
        if (resultList != null && resultList.size() > 0) {
            ldapGroup = (Iuserldapgrphdr)resultList.get(0);
        }
        return ldapGroup;
    }

    public static List getUserConfig(Session session, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrUserconfig");
        query.append(" scr WHERE scr.userid=?");
        return session.find(query.toString(), new Object[]{userId}, new Type[]{Hibernate.INTEGER});
    }

    public static List getUsrLoc(Session session, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrUsrloc");
        query.append(" scr WHERE scr.userid=?");
        return session.find(query.toString(), new Object[]{userId}, new Type[]{Hibernate.INTEGER});
    }

    public static List getDepts(Session session, Integer deptId) throws HibernateException {
        List list = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserdepthdr");
        query.append(" scr ");
        if (deptId != null) {
            query.append(" WHERE scr.id !=?");
            query.append(" ORDER BY scr.name");
            list = session.find(query.toString(), new Object[]{deptId}, new Type[]{Hibernate.INTEGER});
        } else {
            query.append(" ORDER BY scr.name");
            list = session.find(query.toString());
        }
        return list;
    }

    public static Iuserdepthdr getDept(Session session, Integer deptId) throws HibernateException {
        Iuserdepthdr iuserdepthdr = (Iuserdepthdr)session.load((Class)Iuserdepthdr.class, (Serializable)deptId);
        return iuserdepthdr;
    }

    public static List getGroups(Session session, List groups) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iusergrouphdr");
        query.append(" scr ");
        if (!(groups == null || groups.isEmpty())) {
            query.append(" WHERE ");
            Integer idGroup = null;
            for (int i1 = 0; i1 < groups.size(); ++i1) {
                idGroup = new Integer(((Iusergroupuser)groups.get(i1)).getGroupid());
                query.append(" scr.id! = ");
                query.append(idGroup);
                if (i1 >= groups.size() - 1) continue;
                query.append(" and ");
            }
        }
        query.append(" ORDER BY scr.name");
        return session.find(query.toString());
    }

    public static List getLdapGroups(Session session, List groups) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapgrphdr");
        query.append(" scr ");
        if (!(groups == null || groups.isEmpty())) {
            query.append(" WHERE ");
            Integer idGroup = null;
            for (int i1 = 0; i1 < groups.size(); ++i1) {
                idGroup = (Integer)groups.get(i1);
                query.append(" scr.id! = ");
                query.append(idGroup);
                if (i1 >= groups.size() - 1) continue;
                query.append(" and ");
            }
        }
        query.append(" ORDER BY scr.ldapfullname");
        return session.find(query.toString());
    }

    public static List getUsers(Session session, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuseruserhdr");
        query.append(" scr WHERE scr.id!=? ORDER BY scr.name");
        return session.find(query.toString(), new Object[]{userId}, new Type[]{Hibernate.INTEGER});
    }

    public static List getUsers(Session session) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuseruserhdr");
        query.append(" scr ORDER BY scr.name");
        return session.find(query.toString());
    }

    public static List getLdapUsers(Session session, Integer userId) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr");
        query.append(" scr WHERE scr.id!=? ORDER By scr.ldapfullname");
        return session.find(query.toString(), new Object[]{userId}, new Type[]{Hibernate.INTEGER});
    }

    public static List<Iuserldapuserhdr> getLdapUsers(Session session) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserldapuserhdr");
        query.append(" ORDER By scr.ldapfullname");
        return session.find(query.toString());
    }

    public static List getTypeDocs(Session session) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrTypedoc");
        query.append(" scr ORDER BY scr.id");
        return session.find(query.toString());
    }

    public static List getTypeDocs(Session session, Integer typePerson) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrTypedoc");
        query.append(" scr WHERE scr.typePerson = ? or scr.typePerson = 0 ORDER BY scr.id");
        return session.find(query.toString(), new Object[]{typePerson}, new Type[]{Hibernate.INTEGER});
    }

    public static List getTypeAddresses(Session session) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrTypeaddress");
        query.append(" scr ORDER BY scr.id");
        return session.find(query.toString());
    }

    public static Iuserusersys getIuserusersys(Session session) throws HibernateException {
        Iuserusersys result = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM  ");
        query.append("com.ieci.tecdoc.common.invesdoc.Iuserusersys");
        List datosSistema = session.find(query.toString());
        if (datosSistema != null && datosSistema.size() > 0) {
            result = (Iuserusersys)datosSistema.get(0);
        }
        return result;
    }

    public static ScrDistribucionActual getScrDistribucionActual(Session session, Integer id) throws HibernateException {
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrDistribucionActual");
        query.append(" scr WHERE scr.iddist=?");
        List list = session.find(query.toString(), new Object[]{id}, new Type[]{Hibernate.INTEGER});
        ScrDistribucionActual scr = null;
        if (!(list == null || list.isEmpty())) {
            scr = (ScrDistribucionActual)list.get(0);
        }
        return scr;
    }
}
