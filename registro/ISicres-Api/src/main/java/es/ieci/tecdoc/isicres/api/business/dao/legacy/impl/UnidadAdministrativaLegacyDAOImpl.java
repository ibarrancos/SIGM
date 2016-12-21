package es.ieci.tecdoc.isicres.api.business.dao.legacy.impl;

import com.ieci.tecdoc.common.invesicres.ScrOrg;
import com.ieci.tecdoc.common.invesicres.ScrOrgct;
import com.ieci.tecdoc.common.invesicres.ScrOrgeu;
import com.ieci.tecdoc.common.invesicres.ScrOrggl;
import com.ieci.tecdoc.common.invesicres.ScrTypeadm;
import com.ieci.tecdoc.common.invesicres.ScrTypeadmct;
import com.ieci.tecdoc.common.invesicres.ScrTypeadmeu;
import com.ieci.tecdoc.common.invesicres.ScrTypeadmgl;
import es.ieci.tecdoc.isicres.api.business.dao.UnidadAdministrativaDAO;
import es.ieci.tecdoc.isicres.api.business.dao.legacy.impl.IsicresBaseHibernateDAOImpl;
import es.ieci.tecdoc.isicres.api.business.exception.UnidadAdministrativaException;
import es.ieci.tecdoc.isicres.api.business.vo.BaseUnidadAdministrativaVO;
import es.ieci.tecdoc.isicres.api.business.vo.CriterioBusquedaTipoUnidadAdministrativaVO;
import es.ieci.tecdoc.isicres.api.business.vo.CriterioBusquedaUnidadAdministrativaByTipoVO;
import es.ieci.tecdoc.isicres.api.business.vo.CriterioBusquedaUnidadAdministrativaByUnidadVO;
import es.ieci.tecdoc.isicres.api.business.vo.CriterioBusquedaWhereSqlVO;
import es.ieci.tecdoc.isicres.api.business.vo.TipoUnidadAdministrativaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UnidadAdministrativaVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;

public class UnidadAdministrativaLegacyDAOImpl
extends IsicresBaseHibernateDAOImpl
implements UnidadAdministrativaDAO {
    private static final Logger log = Logger.getLogger((Class)UnidadAdministrativaLegacyDAOImpl.class);

    public List findAllTipoUnidadesAdmin(Locale locale, CriterioBusquedaTipoUnidadAdministrativaVO criterio) {
        ArrayList<TipoUnidadAdministrativaVO> result = new ArrayList<TipoUnidadAdministrativaVO>();
        TipoUnidadAdministrativaVO tipoUnidad = null;
        try {
            List listado = this.getScrTypeAdm(locale, criterio);
            Iterator it = listado.iterator();
            while (it.hasNext()) {
                ScrTypeadm typeAdm = new ScrTypeadm();
                BeanUtils.copyProperties(it.next(), (Object)typeAdm);
                tipoUnidad = this.adapterScrTypeAdmToTipoUnidadAdministrativaVO(typeAdm);
                result.add(tipoUnidad);
            }
        }
        catch (HibernateException e) {
            log.warn((Object)"UnidadAdministrativaLegacyDAOImpl.getTipoUnidadesAdmin no se pueden recuperar el tipo de unidades administrativas");
            throw new UnidadAdministrativaException("No se puede recuperar el lista de tipo de unidades administrativas", (Throwable)e);
        }
        return result;
    }

    public int findCountAllTipoUnidadesAdmin(Locale locale) {
        int result = 0;
        try {
            result = this.getCountScrTypeAdm(locale);
        }
        catch (HibernateException e) {
            log.warn((Object)"UnidadAdministrativaLegacyDAOImpl.getTipoUnidadesAdmin no se pueden recuperar el tipo de unidades administrativas");
            throw new UnidadAdministrativaException("No se puede recuperar el lista de tipo de unidades administrativas", (Throwable)e);
        }
        return result;
    }

    public TipoUnidadAdministrativaVO getTipoUnidadesAdminByCode(String code, Locale locale, CriterioBusquedaTipoUnidadAdministrativaVO criterio) {
        TipoUnidadAdministrativaVO tipoUnidad = null;
        try {
            List listado = this.getScrTypeAdmByCode(code, locale, criterio);
            if (listado != null && listado.size() > 0) {
                ScrTypeadm typeAdm = new ScrTypeadm();
                BeanUtils.copyProperties(listado.get(0), (Object)typeAdm);
                tipoUnidad = this.adapterScrTypeAdmToTipoUnidadAdministrativaVO(typeAdm);
                return tipoUnidad;
            }
        }
        catch (HibernateException e) {
            log.warn((Object)"UnidadAdministrativaLegacyDAOImpl.getTipoUnidadesAdmin no se pueden recuperar el tipo de unidades administrativas");
            throw new UnidadAdministrativaException("No se puede recuperar el lista de tipo de unidades administrativas", (Throwable)e);
        }
        return tipoUnidad;
    }

    public List findUnidadesAdmByTipo(Locale locale, CriterioBusquedaUnidadAdministrativaByTipoVO criterio) {
        List result = null;
        try {
            StringBuffer query = this.getQueryUnidadesAdmByTipo(criterio.getIdTipoUnidadAdministrativa());
            CriterioBusquedaTipoUnidadAdministrativaVO limitQuery = new CriterioBusquedaTipoUnidadAdministrativaVO();
            limitQuery.setLimit(criterio.getLimit());
            limitQuery.setOffset(criterio.getOffset());
            result = this.executeCriteriaReturnListUnidadAdministrativaVO(locale, query, limitQuery);
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.getUnidadesAdmByTipo no se pueden recuperar el listado de unidades administrativas segun su tipo: " + criterio.getIdTipoUnidadAdministrativa()));
            throw new UnidadAdministrativaException("No se puede recuperar el listado de unidades administrativas segun su tipo [" + criterio.getIdTipoUnidadAdministrativa() + "]", (Throwable)e);
        }
        return result;
    }

    public int findCountUnidadesAdmByTipo(Locale locale, CriterioBusquedaUnidadAdministrativaByTipoVO criterio) {
        int result = 0;
        try {
            StringBuffer query = this.getQueryUnidadesAdmByTipo(criterio.getIdTipoUnidadAdministrativa());
            result = this.executeCriteriaReturnCountScrOrgsList(locale, query);
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.getCountUnidadesAdmByTipo no se pueden recuperar el numero de unidades administrativas segun su tipo: " + criterio.getIdTipoUnidadAdministrativa()));
            throw new UnidadAdministrativaException("No se puede recuperar el numero de unidades administrativas segun su tipo [" + criterio.getIdTipoUnidadAdministrativa() + "]", (Throwable)e);
        }
        return result;
    }

    public List findUnidadesAdmWhereSQL(Locale locale, CriterioBusquedaWhereSqlVO criterio) {
        List result = null;
        try {
            StringBuffer query = this.getQueryUnidadByWhereSQL(criterio.getSql());
            CriterioBusquedaTipoUnidadAdministrativaVO limitQuery = new CriterioBusquedaTipoUnidadAdministrativaVO();
            limitQuery.setLimit(criterio.getLimit());
            limitQuery.setOffset(criterio.getOffset());
            result = this.executeCriteriaReturnListUnidadAdministrativaVO(locale, query, limitQuery);
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.findUnidadesAdmWhereSQL no se pueden recuperar el listado de unidades administrativas segun el where: " + criterio.getSql()));
            throw new UnidadAdministrativaException("No se puede recuperar el listado de unidades administrativas segun el where [" + criterio.getSql() + "]", (Throwable)e);
        }
        return result;
    }

    public int findCountUnidadesAdmWhereSQL(Locale locale, CriterioBusquedaWhereSqlVO criterio) {
        int result = 0;
        try {
            StringBuffer query = this.getQueryUnidadByWhereSQL(criterio.getSql());
            result = this.executeCriteriaReturnCountScrOrgsList(locale, query);
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.findCountUnidadesAdmWhereSQL no se pueden recuperar el numero de unidades administrativas segun su where: " + criterio.getSql()));
            throw new UnidadAdministrativaException("No se puede recuperar el numero de unidades administrativas segun su where [" + criterio.getSql() + "]", (Throwable)e);
        }
        return result;
    }

    public UnidadAdministrativaVO findUnidadByCode(Locale locale, String codigoUnidad) {
        UnidadAdministrativaVO result = null;
        ScrOrg scrOrg = null;
        try {
            StringBuffer query = this.getQueryUnidadByCode(codigoUnidad);
            scrOrg = this.executeCriteriaReturnScrOrg(locale, query);
            if (scrOrg != null) {
                result = this.fromScrOrgToUnidadAdministrativa(locale, scrOrg);
            }
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.getUnidadByCode no se pueden recuperar la UnidadAdministrativa con codigo [" + codigoUnidad + "]"));
            throw new UnidadAdministrativaException("No se puede recuperar la UnidadAdministrativa con codigo [" + codigoUnidad + "]", (Throwable)e);
        }
        return result;
    }

    public List findUnidadesAdmByUnidad(Locale locale, CriterioBusquedaUnidadAdministrativaByUnidadVO criterio) {
        List result = null;
        try {
            StringBuffer query = this.getQueryUnidadByUnidadPadre(criterio.getIdUnidadAdministrativaPadre());
            CriterioBusquedaTipoUnidadAdministrativaVO limitQuery = new CriterioBusquedaTipoUnidadAdministrativaVO();
            limitQuery.setLimit(criterio.getLimit());
            limitQuery.setOffset(criterio.getOffset());
            result = this.executeCriteriaReturnListUnidadAdministrativaVO(locale, query, limitQuery);
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.getUnidadesAdmByUnidad no se pueden recuperar el listado de unidades administrativas segun el id de unidad padre: " + criterio.getIdUnidadAdministrativaPadre()));
            throw new UnidadAdministrativaException("No se puede recuperar el listado de unidades administrativas segun el id de la unidad padre [" + criterio.getIdUnidadAdministrativaPadre() + "]", (Throwable)e);
        }
        return result;
    }

    public int findCountUnidadesAdmByUnidad(Locale locale, CriterioBusquedaUnidadAdministrativaByUnidadVO criterio) {
        int result = 0;
        try {
            StringBuffer query = this.getQueryUnidadByUnidadPadre(criterio.getIdUnidadAdministrativaPadre());
            result = this.executeCriteriaReturnCountScrOrgsList(locale, query);
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.getCountUnidadesAdmByUnidad no se pueden recuperar el numero de unidades administrativas segun su tipo: " + criterio.getIdUnidadAdministrativaPadre()));
            throw new UnidadAdministrativaException("No se puede recuperar el numero de unidades administrativas segun su tipo [" + criterio.getIdUnidadAdministrativaPadre() + "] ", (Throwable)e);
        }
        return result;
    }

    public UnidadAdministrativaVO findUnidadById(Locale locale, Integer idUnidad) {
        UnidadAdministrativaVO result = null;
        ScrOrg scrOrg = null;
        try {
            StringBuffer query = this.getQueryUnidadById(idUnidad);
            scrOrg = this.executeCriteriaReturnScrOrg(locale, query);
            if (scrOrg != null) {
                result = this.fromScrOrgToUnidadAdministrativa(locale, scrOrg);
            }
        }
        catch (HibernateException e) {
            log.warn((Object)("UnidadAdministrativaLegacyDAOImpl.findUnidadAdministrativaVOById no se pueden recuperar la UnidadAdministrativa con id [" + idUnidad + "]"));
            throw new UnidadAdministrativaException("No se puede recuperar la UnidadAdministrativa con id [" + idUnidad + "]", (Throwable)e);
        }
        return result;
    }

    protected BaseUnidadAdministrativaVO getPadreUnidadAdministrativa(Locale locale, ScrOrg scrOrg) throws HibernateException {
        BaseUnidadAdministrativaVO result = new BaseUnidadAdministrativaVO();
        ScrOrg scrOrgPadre = null;
        if (scrOrg.getIdFather() != null && scrOrg.getIdFather() != 0) {
            StringBuffer query = this.getQueryUnidadById(scrOrg.getIdFather());
            scrOrgPadre = this.executeCriteriaReturnScrOrg(locale, query);
            result = this.fromScrOrgToBaseUnidadAdministrativaVO(scrOrgPadre, null, null);
        }
        return result;
    }

    protected List getScrTypeAdm(Locale locale, CriterioBusquedaTipoUnidadAdministrativaVO criterio) throws HibernateException {
        List result = null;
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteriaResults = session.createCriteria(UnidadAdministrativaLegacyDAOImpl.getScrTypeAdmLanguage(locale.getLanguage()));
            if (criterio != null) {
                criteriaResults.setFirstResult(criterio.getOffset().intValue());
                criteriaResults.setMaxResults(criterio.getLimit().intValue());
            }
            result = criteriaResults.list();
            Object var7_6 = null;
        }
        catch (HibernateException var6_8) {
            Object var7_7 = null;
            this.closeSession(session);
            throw var6_8;
        }
        this.closeSession(session);
        {
        }
        return result;
    }

    protected List getScrTypeAdmByCode(String code, Locale locale, CriterioBusquedaTipoUnidadAdministrativaVO criterio) throws HibernateException {
        List result = null;
        Session session = null;
        String query = this.getQueryScrTypeByCode(code).toString();
        try {
            session = this.getSession();
            Criteria criteriaResults = session.createCriteria(UnidadAdministrativaLegacyDAOImpl.getScrTypeAdmLanguage(locale.getLanguage()));
            criteriaResults.add((Criterion)Expression.eq((String)"code", (Object)code));
            if (criterio != null) {
                criteriaResults.setFirstResult(criterio.getOffset().intValue());
                criteriaResults.setMaxResults(criterio.getLimit().intValue());
            }
            result = criteriaResults.list();
            Object var9_8 = null;
        }
        catch (HibernateException var8_10) {
            Object var9_9 = null;
            this.closeSession(session);
            throw var8_10;
        }
        this.closeSession(session);
        {
        }
        return result;
    }

    protected int getCountScrTypeAdm(Locale locale) throws HibernateException {
        int result = 0;
        Session session = null;
        try {
            session = this.getSession();
            StringBuffer querySize = new StringBuffer();
            querySize.append("SELECT COUNT(*) FROM ");
            querySize.append(UnidadAdministrativaLegacyDAOImpl.getScrTypeAdmLanguage(locale.getLanguage()).getName());
            result = (Integer)session.iterate(querySize.toString()).next();
            Object var6_5 = null;
        }
        catch (HibernateException var5_7) {
            Object var6_6 = null;
            this.closeSession(session);
            throw var5_7;
        }
        this.closeSession(session);
        {
        }
        return result;
    }

    protected ScrOrg executeCriteriaReturnScrOrg(Locale locale, StringBuffer query) throws HibernateException {
        ScrOrg result = null;
        List listado = null;
        listado = this.executeCriteriaReturnScrOrgsList(locale, query, null);
        Object scrOrg = DataAccessUtils.uniqueResult((Collection)listado);
        if (scrOrg != null) {
            result = new ScrOrg();
            BeanUtils.copyProperties((Object)scrOrg, (Object)result);
        }
        return result;
    }

    protected List executeCriteriaReturnScrOrgsList(Locale locale, StringBuffer query, CriterioBusquedaTipoUnidadAdministrativaVO criterio) throws HibernateException {
        List result = null;
        Session session = null;
        try {
            session = this.getSession();
            Criteria criteriaResults = session.createCriteria(UnidadAdministrativaLegacyDAOImpl.getScrOrgLanguage(locale.getLanguage()));
            criteriaResults.add(Expression.sql((String)query.toString()));
            if (criterio != null) {
                criteriaResults.setFirstResult(criterio.getOffset().intValue());
                criteriaResults.setMaxResults(criterio.getLimit().intValue());
            }
            result = criteriaResults.list();
            Object var8_7 = null;
        }
        catch (HibernateException var7_9) {
            Object var8_8 = null;
            this.closeSession(session);
            throw var7_9;
        }
        this.closeSession(session);
        {
        }
        return result;
    }

    protected int executeCriteriaReturnCountScrOrgsList(Locale locale, StringBuffer query) throws HibernateException {
        int result = 0;
        Session session = null;
        try {
            session = this.getSession();
            StringBuffer querySize = new StringBuffer();
            querySize.append("SELECT COUNT(*) FROM ");
            querySize.append(UnidadAdministrativaLegacyDAOImpl.getScrOrgLanguage(locale.getLanguage()).getName());
            querySize.append(" AS SCR WHERE ");
            querySize.append(query);
            result = (Integer)session.iterate(querySize.toString()).next();
            Object var7_6 = null;
        }
        catch (HibernateException var6_8) {
            Object var7_7 = null;
            this.closeSession(session);
            throw var6_8;
        }
        this.closeSession(session);
        {
        }
        return result;
    }

    protected List executeCriteriaReturnListUnidadAdministrativaVO(Locale locale, StringBuffer query, CriterioBusquedaTipoUnidadAdministrativaVO limitQuery) throws HibernateException {
        ArrayList<UnidadAdministrativaVO> result = new ArrayList<UnidadAdministrativaVO>();
        List<ScrOrg> listado = null;
        UnidadAdministrativaVO unidadAdministrativa = null;
        listado = this.executeCriteriaReturnScrOrgsList(locale, query, limitQuery);
        for (ScrOrg scrOrg2 : listado) {
            unidadAdministrativa = this.fromScrOrgToUnidadAdministrativa(locale, scrOrg2);
            result.add(unidadAdministrativa);
        }
        return result;
    }

    protected TipoUnidadAdministrativaVO adapterScrTypeAdmToTipoUnidadAdministrativaVO(ScrTypeadm scrtypeadm) {
        TipoUnidadAdministrativaVO result = new TipoUnidadAdministrativaVO();
        result.setId(scrtypeadm.getId().toString());
        result.setCodigo(scrtypeadm.getCode());
        result.setDescripcion(scrtypeadm.getDescription());
        return result;
    }

    protected UnidadAdministrativaVO fromScrOrgToUnidadAdministrativa(Locale locale, ScrOrg scrOrg) throws HibernateException {
        UnidadAdministrativaVO result = null;
        TipoUnidadAdministrativaVO tipoUnidad = null;
        tipoUnidad = this.adapterScrTypeAdmToTipoUnidadAdministrativaVO(scrOrg.getScrTypeadm());
        BaseUnidadAdministrativaVO unidadPadre = this.getPadreUnidadAdministrativa(locale, scrOrg);
        result = this.fromScrOrgToUnidadAdministrativa(scrOrg, tipoUnidad, unidadPadre);
        return result;
    }

    protected UnidadAdministrativaVO fromScrOrgToUnidadAdministrativa(ScrOrg scrOrg, TipoUnidadAdministrativaVO tipoUnidad, BaseUnidadAdministrativaVO unidadPadre) {
        UnidadAdministrativaVO result = new UnidadAdministrativaVO();
        result.setAcronimoUnidad(scrOrg.getAcron());
        if (scrOrg.getEnabled() == 1) {
            result.setActiva(true);
        } else {
            result.setActiva(false);
        }
        result.setCif(scrOrg.getCif());
        result.setCodigoUnidad(scrOrg.getCode());
        result.setFechaAlta(scrOrg.getCreationDate());
        result.setFechaBaja(scrOrg.getDisableDate());
        result.setId(scrOrg.getId().toString());
        result.setName(scrOrg.getName());
        result.setTipo(tipoUnidad);
        result.setUnidadPadre(unidadPadre);
        if (scrOrg.getEnabled() == 0) {
            result.setActiva(false);
        } else {
            result.setActiva(true);
        }
        return result;
    }

    protected BaseUnidadAdministrativaVO fromScrOrgToBaseUnidadAdministrativaVO(ScrOrg scrOrg, TipoUnidadAdministrativaVO tipoUnidad, BaseUnidadAdministrativaVO unidadPadre) {
        BaseUnidadAdministrativaVO result = new BaseUnidadAdministrativaVO();
        result.setAcronimoUnidad(scrOrg.getAcron());
        if (scrOrg.getEnabled() == 1) {
            result.setActiva(true);
        } else {
            result.setActiva(false);
        }
        result.setCif(scrOrg.getCif());
        result.setCodigoUnidad(scrOrg.getCode());
        result.setFechaAlta(scrOrg.getCreationDate());
        result.setFechaBaja(scrOrg.getDisableDate());
        result.setId(scrOrg.getId().toString());
        result.setName(scrOrg.getName());
        result.setTipo(tipoUnidad);
        if (scrOrg.getIdFather() != null && scrOrg.getIdFather() != 0) {
            result.setUnidadPadre(unidadPadre);
        }
        return result;
    }

    protected StringBuffer getQueryUnidadesAdmByTipo(String tipoUnidadAdm) {
        StringBuffer result = new StringBuffer();
        result.append("TYPE = ");
        result.append(tipoUnidadAdm);
        result.append(" AND (ID_FATHER = 0 OR ID_FATHER IS NULL)");
        return result;
    }

    protected StringBuffer getQueryUnidadByCode(String codigoUnidad) {
        StringBuffer result = new StringBuffer();
        result.append("CODE = '");
        result.append(codigoUnidad);
        result.append("'");
        return result;
    }

    protected StringBuffer getQueryScrTypeByCode(String code) {
        StringBuffer result = new StringBuffer();
        result.append("CODE = '");
        result.append(code);
        result.append("'");
        return result;
    }

    protected StringBuffer getQueryUnidadById(Integer idUnidad) {
        StringBuffer result = new StringBuffer();
        result.append("ID = ");
        result.append(idUnidad);
        return result;
    }

    protected StringBuffer getQueryUnidadByUnidadPadre(String idUnidadPadre) {
        StringBuffer result = new StringBuffer();
        result.append("ID_FATHER = ");
        result.append(idUnidadPadre);
        return result;
    }

    protected StringBuffer getQueryUnidadByWhereSQL(String where) {
        StringBuffer result = new StringBuffer();
        result.append(where);
        return result;
    }

    protected static Class getScrTypeAdmLanguage(String language) {
        Class scrTypeAdmClass = ScrTypeadm.class;
        if (language.equals("es")) {
            scrTypeAdmClass = ScrTypeadm.class;
        }
        if (language.equals("eu")) {
            scrTypeAdmClass = ScrTypeadmeu.class;
        }
        if (language.equals("gl")) {
            scrTypeAdmClass = ScrTypeadmgl.class;
        }
        if (language.equals("ca")) {
            scrTypeAdmClass = ScrTypeadmct.class;
        }
        return scrTypeAdmClass;
    }

    protected static Class getScrOrgLanguage(String language) {
        Class scrTypeAdmClass = ScrOrg.class;
        if (language.equals("es")) {
            scrTypeAdmClass = ScrOrg.class;
        }
        if (language.equals("eu")) {
            scrTypeAdmClass = ScrOrgeu.class;
        }
        if (language.equals("gl")) {
            scrTypeAdmClass = ScrOrggl.class;
        }
        if (language.equals("ca")) {
            scrTypeAdmClass = ScrOrgct.class;
        }
        return scrTypeAdmClass;
    }
}
