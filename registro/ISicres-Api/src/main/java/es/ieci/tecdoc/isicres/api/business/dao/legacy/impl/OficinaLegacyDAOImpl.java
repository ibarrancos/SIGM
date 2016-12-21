package es.ieci.tecdoc.isicres.api.business.dao.legacy.impl;

import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.invesicres.ScrOficct;
import com.ieci.tecdoc.common.invesicres.ScrOficeu;
import com.ieci.tecdoc.common.invesicres.ScrOficgl;
import com.ieci.tecdoc.common.invesicres.ScrTmzofic;
import es.ieci.tecdoc.isicres.api.business.dao.OficinaDAO;
import es.ieci.tecdoc.isicres.api.business.dao.legacy.impl.IsicresBaseHibernateDAOImpl;
import es.ieci.tecdoc.isicres.api.business.exception.OficinaException;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.ZonaHorariaVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;

public class OficinaLegacyDAOImpl
extends IsicresBaseHibernateDAOImpl
implements OficinaDAO {
    @Override
    public OficinaVO getOficinaByCodigo(Locale locale, String codOficina) {
        OficinaVO result = null;
        ScrOfic scrOfic = null;
        try {
            scrOfic = this.getScrOficByCode(locale, codOficina);
            if (scrOfic != null) {
                ScrTmzofic scrTmzofic = this.getScrTmzofic(scrOfic.getId());
                result = this.oficinaAdapter(scrOfic, scrTmzofic);
            }
        }
        catch (HibernateException e) {
            throw new OficinaException("No se puede recuperar la informacion del tipo de la oficina con codigo [" + codOficina + "] ", (Throwable)e);
        }
        return result;
    }

    @Override
    public OficinaVO getOficinaById(Locale locale, String idOficina) {
        OficinaVO result = null;
        ScrOfic scrOfic = null;
        try {
            scrOfic = this.getScrOficById(locale, idOficina);
            if (scrOfic != null) {
                ScrTmzofic scrTmzofic = this.getScrTmzofic(scrOfic.getId());
                result = this.oficinaAdapter(scrOfic, scrTmzofic);
            }
        }
        catch (HibernateException e) {
            throw new OficinaException("No se puede recuperar la informacion del tipo de la oficina con id [" + idOficina + "] ", (Throwable)e);
        }
        return result;
    }

    @Override
    public List<OficinaVO> getOficinas(Locale locale) {
        ArrayList<OficinaVO> result = new ArrayList<OficinaVO>();
        try {
            List<ScrOfic> scrOficList = this.findScrOfic(locale);
            for (ScrOfic scrOfic : scrOficList) {
                ScrTmzofic scrTmzofic = this.getScrTmzofic(scrOfic.getId());
                OficinaVO oficina = this.oficinaAdapter(scrOfic, scrTmzofic);
                result.add(oficina);
            }
        }
        catch (HibernateException e) {
            throw new OficinaException("Error recuperando las oficinas", (Throwable)e);
        }
        return result;
    }

    protected ScrOfic getScrOficById(Locale locale, String id) throws HibernateException {
        ScrOfic result = null;
        StringBuffer query = new StringBuffer();
        query.append(" id = ");
        query.append(id);
        result = this.executeCriteriaReturnScrOfic(locale, query);
        return result;
    }

    @Override
    public OficinaVO getOficinaByIdDepartamento(Locale locale, String idDepartamento) {
        OficinaVO result = null;
        ScrOfic scrOfic = null;
        try {
            scrOfic = this.getScrOficByIdDepartamento(locale, idDepartamento);
            if (scrOfic != null) {
                ScrTmzofic scrTmzofic = this.getScrTmzofic(scrOfic.getId());
                result = this.oficinaAdapter(scrOfic, scrTmzofic);
            }
        }
        catch (HibernateException e) {
            throw new OficinaException("No se puede recuperar la informacion del tipo de la oficina con id departamento [" + idDepartamento + "] ", (Throwable)e);
        }
        return result;
    }

    protected ScrOfic getScrOficByIdDepartamento(Locale locale, String idDepartamento) throws HibernateException {
        ScrOfic result = null;
        StringBuffer query = new StringBuffer();
        query.append(" deptid = ");
        query.append(idDepartamento);
        result = this.executeCriteriaReturnScrOfic(locale, query);
        return result;
    }

    @Override
    public List<OficinaVO> getOficinasByUsuario(Locale locale, UsuarioVO usuario) {
        ArrayList<OficinaVO> result = new ArrayList<OficinaVO>();
        String idUsuario = usuario.getId();
        try {
            List<ScrOfic> scrOficList = this.findScrOficByUsuario(locale, idUsuario);
            for (ScrOfic scrOfic : scrOficList) {
                ScrTmzofic scrTmzofic = this.getScrTmzofic(scrOfic.getId());
                OficinaVO oficina = this.oficinaAdapter(scrOfic, scrTmzofic);
                result.add(oficina);
            }
        }
        catch (HibernateException e) {
            throw new OficinaException("Error recuperando las oficinas para el usuario con id [" + usuario.getId() + "-" + usuario.getLoginName() + "] ", (Throwable)e);
        }
        return result;
    }

    protected ScrOfic getScrOficByCode(Locale locale, String code) throws HibernateException {
        ScrOfic result = null;
        StringBuffer query = new StringBuffer();
        query.append(" code = '");
        query.append(code);
        query.append("'");
        result = this.executeCriteriaReturnScrOfic(locale, query);
        return result;
    }

    protected ScrTmzofic getScrTmzofic(Integer oficId) throws HibernateException {
        ScrTmzofic scrTmzofic = null;
        try {
            scrTmzofic = (ScrTmzofic)this.getSession().load((Class)ScrTmzofic.class, (Serializable)oficId);
        }
        catch (ObjectNotFoundException onF) {
            // empty catch block
        }
        return scrTmzofic;
    }

    protected ZonaHorariaVO zonaHorariaAdapter(ScrTmzofic scrTmzofic) {
        ZonaHorariaVO result = new ZonaHorariaVO();
        if (scrTmzofic != null) {
            result.setTimezone(scrTmzofic.getTmz());
        }
        return result;
    }

    protected OficinaVO oficinaAdapter(ScrOfic scrOfic, ScrTmzofic scrTmzofic) {
        OficinaVO result = new OficinaVO();
        result.setId(scrOfic.getId().toString());
        result.setAcronimoOficina(scrOfic.getAcron());
        result.setCodigoOficina(scrOfic.getCode());
        result.setName(scrOfic.getName());
        result.setIdDepartamento(Integer.toString(scrOfic.getDeptid()));
        if (scrTmzofic != null) {
            ZonaHorariaVO zonaHoraria = this.zonaHorariaAdapter(scrTmzofic);
            result.setZonaHoraria(zonaHoraria);
        }
        return result;
    }

    protected List findScrOficByUsuario(Locale locale, String idUsuario) throws HibernateException {
        List result = null;
        StringBuffer queryUsrOfic = new StringBuffer();
        String sqlUsrOfic = "id in (select idofic from scr_usrofic where iduser = ";
        queryUsrOfic.append(sqlUsrOfic);
        queryUsrOfic.append(idUsuario);
        queryUsrOfic.append(")");
        result = this.executeCriteriaReturnScrOficList(locale, queryUsrOfic);
        return result;
    }

    protected List findScrOfic(Locale locale) throws HibernateException {
        List result = null;
        StringBuffer queryUsrOfic = new StringBuffer();
        String sqlUsrOfic = "id in (select idofic from scr_usrofic) ";
        queryUsrOfic.append(sqlUsrOfic);
        result = this.executeCriteriaReturnScrOficList(locale, queryUsrOfic);
        return result;
    }

    protected ScrOfic executeCriteriaReturnScrOfic(Locale locale, StringBuffer query) throws HibernateException {
        ScrOfic result = null;
        List listado = null;
        listado = this.executeCriteriaReturnScrOficList(locale, query);
        Object scrOfic = DataAccessUtils.uniqueResult((Collection)listado);
        if (scrOfic != null) {
            result = new ScrOfic();
            BeanUtils.copyProperties((Object)scrOfic, (Object)result);
        }
        return result;
    }

    protected List executeCriteriaReturnScrOficList(Locale locale, StringBuffer query) throws HibernateException {
        List result = null;
        Criteria criteriaResults = this.getSession().createCriteria(OficinaLegacyDAOImpl.getScrOficLanguage(locale.getLanguage()));
        criteriaResults.add(Expression.sql((String)query.toString()));
        result = criteriaResults.list();
        return result;
    }

    protected static Class getScrOficLanguage(String language) {
        Class scrOficClass = ScrOfic.class;
        if (language.equals("es")) {
            scrOficClass = ScrOfic.class;
        }
        if (language.equals("eu")) {
            scrOficClass = ScrOficeu.class;
        }
        if (language.equals("gl")) {
            scrOficClass = ScrOficgl.class;
        }
        if (language.equals("ca")) {
            scrOficClass = ScrOficct.class;
        }
        return scrOficClass;
    }
}
