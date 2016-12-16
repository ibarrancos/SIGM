package es.ieci.tecdoc.isicres.api.business.dao.legacy.impl;

import com.ieci.tecdoc.common.invesdoc.Iuserdepthdr;
import com.ieci.tecdoc.common.invesicres.ScrOfic;
import com.ieci.tecdoc.common.utils.ISicresQueries;
import es.ieci.tecdoc.isicres.api.business.dao.DepartamentoDAO;
import es.ieci.tecdoc.isicres.api.business.dao.legacy.impl.IsicresBaseHibernateDAOImpl;
import es.ieci.tecdoc.isicres.api.business.exception.DepartamentoException;
import es.ieci.tecdoc.isicres.api.business.manager.impl.mapper.BaseDepartamentoVOMapper;
import es.ieci.tecdoc.isicres.api.business.vo.BaseDepartamentoVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoOficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.DepartamentoUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.util.LinkedList;
import java.util.List;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.type.NullableType;
import net.sf.hibernate.type.Type;

public class DepartamentoLegacyDAOImpl
extends IsicresBaseHibernateDAOImpl
implements DepartamentoDAO {
    @Override
    public DepartamentoUsuarioVO getDepartamentoUsuario(UsuarioVO usuario) {
        DepartamentoUsuarioVO result = null;
        String idUsuario = usuario.getId();
        ScrOfic scrOfic = null;
        try {
            scrOfic = this.getDepartamentoUsuario(idUsuario);
            if (scrOfic != null) {
                result = new DepartamentoUsuarioVO();
                String idDepartamento = Integer.toString(scrOfic.getDeptid());
                result.setId(idDepartamento);
            }
        }
        catch (HibernateException e) {
            throw new DepartamentoException("No se puede recuperar el departamento del usuario ", (Throwable)e);
        }
        return result;
    }

    @Override
    public DepartamentoOficinaVO getDepartamentoOficina(OficinaVO oficina) {
        DepartamentoOficinaVO result = null;
        String idOficina = oficina.getId();
        try {
            ScrOfic scrOfic = this.getDepartamentoOficina(idOficina);
            if (scrOfic != null) {
                result = new DepartamentoOficinaVO();
                String idDepartamento = Integer.toString(scrOfic.getDeptid());
                result.setId(idDepartamento);
            }
        }
        catch (HibernateException e) {
            throw new DepartamentoException("No se puede recuperar el departamento de la oficina ", (Throwable)e);
        }
        return result;
    }

    @Override
    public List<BaseDepartamentoVO> getDepartamentosGrupoLdap(Integer idGrupoLdap) {
        LinkedList<BaseDepartamentoVO> results = new LinkedList<BaseDepartamentoVO>();
        BaseDepartamentoVOMapper mapper = new BaseDepartamentoVOMapper();
        try {
            List list = ISicresQueries.getUserDeptHdrByCrtrId((Session)this.getSession(), (Integer)idGrupoLdap);
            for (Iuserdepthdr iuserdepthdr : list) {
                BaseDepartamentoVO departamentoVO = mapper.map(iuserdepthdr);
                results.add(departamentoVO);
            }
        }
        catch (HibernateException e) {
            throw new DepartamentoException("No se ha podido recuperar la lista de departamentos asaociados al grupo ldap: [" + idGrupoLdap + "]", (Throwable)e);
        }
        return results;
    }

    @Override
    public List<BaseDepartamentoVO> findDepartamentos() {
        LinkedList<BaseDepartamentoVO> results = new LinkedList<BaseDepartamentoVO>();
        try {
            BaseDepartamentoVOMapper mapper = new BaseDepartamentoVOMapper();
            List depts = ISicresQueries.getDepts((Session)this.getSession(), (Integer)null);
            if (depts != null) {
                for (Iuserdepthdr iuserdepthdr : depts) {
                    BaseDepartamentoVO departamento = mapper.map(iuserdepthdr);
                    results.add(departamento);
                }
            }
        }
        catch (HibernateException e) {
            throw new DepartamentoException("No se puede recuperar el listado de departamentos", (Throwable)e);
        }
        return results;
    }

    @Override
    public BaseDepartamentoVO getDepartamentoById(Integer idDepartamento) {
        BaseDepartamentoVO departamento = null;
        BaseDepartamentoVOMapper mapper = new BaseDepartamentoVOMapper();
        try {
            Iuserdepthdr dept = ISicresQueries.getDept((Session)this.getSession(), (Integer)idDepartamento);
            departamento = mapper.map(dept);
        }
        catch (HibernateException e) {
            throw new DepartamentoException("No se puede recuperar el departamento con el identificador: [" + idDepartamento + "]", (Throwable)e);
        }
        return departamento;
    }

    protected ScrOfic getDepartamentoOficina(String idOficina) throws HibernateException {
        ScrOfic result = null;
        StringBuffer query = new StringBuffer();
        query.append("FROM ");
        query.append("com.ieci.tecdoc.common.invesicres.ScrOfic");
        query.append(" scr WHERE scr.id=?");
        List list = this.getSession().find(query.toString(), new Object[]{idOficina}, new Type[]{Hibernate.INTEGER});
        if (!(list == null || list.isEmpty())) {
            result = (ScrOfic)list.get(0);
        }
        return result;
    }

    protected ScrOfic getDepartamentoUsuario(String idUsuario) throws HibernateException {
        ScrOfic result = null;
        StringBuffer queryIuseruserhdr = new StringBuffer();
        queryIuseruserhdr.append("deptid in (select deptid from iuseruserhdr where id = ");
        queryIuseruserhdr.append(idUsuario);
        queryIuseruserhdr.append(")");
        Criteria criteria = this.getSession().createCriteria((Class)ScrOfic.class);
        criteria.add(Expression.sql((String)queryIuseruserhdr.toString()));
        List oficinasDepartamentoList = criteria.list();
        if (!(oficinasDepartamentoList == null || oficinasDepartamentoList.isEmpty())) {
            result = (ScrOfic)oficinasDepartamentoList.get(0);
        }
        return result;
    }
}
