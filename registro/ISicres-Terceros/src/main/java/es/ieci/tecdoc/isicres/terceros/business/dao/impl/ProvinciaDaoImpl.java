package es.ieci.tecdoc.isicres.terceros.business.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ClassUtils;

import es.ieci.tecdoc.fwktd.server.dao.ibatis.IbatisGenericReadOnlyDaoImpl;
import es.ieci.tecdoc.isicres.terceros.business.dao.ProvinciaDao;
import es.ieci.tecdoc.isicres.terceros.business.vo.ProvinciaVO;

/**
 *
 * @author IECISA
 *
 */
public class ProvinciaDaoImpl extends
		IbatisGenericReadOnlyDaoImpl<ProvinciaVO, String> implements
		ProvinciaDao {

	public ProvinciaDaoImpl(Class<ProvinciaVO> aPersistentClass) {
		super(aPersistentClass);
	}

	public ProvinciaVO findByCodigo(String codigo) {
		return (ProvinciaVO) getSqlMapClientTemplate().queryForObject(
				StringUtils.join(
						new String[] {
								ClassUtils.getShortName(getPersistentClass()),
								"findByCodigo" }, "."), codigo);
	}

	public ProvinciaVO findByNombre(String nombre) {
		return (ProvinciaVO) getSqlMapClientTemplate().queryForObject(
				StringUtils.join(
						new String[] {
								ClassUtils.getShortName(getPersistentClass()),
								"findByNombre" }, "."), nombre);
	}
	public List<ProvinciaVO> getProvincias(int from, int to) {
	    return this.getSqlMapClientTemplate().queryForList(StringUtils.join((Object[])new String[]{ClassUtils.getShortName((Class)this.getPersistentClass()), "getProvinciaVOs"}, (String)"."), from, to);
	}
	public Integer getProvinciasCount() {
	    return (Integer)this.getSqlMapClientTemplate().queryForObject(StringUtils.join((Object[])new String[]{ClassUtils.getShortName((Class)this.getPersistentClass()), "getProvinciasCount"}, (String)"."));
	}

}
