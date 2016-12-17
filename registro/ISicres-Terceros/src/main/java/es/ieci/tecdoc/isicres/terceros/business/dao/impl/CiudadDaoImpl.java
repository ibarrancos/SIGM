package es.ieci.tecdoc.isicres.terceros.business.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ClassUtils;

import es.ieci.tecdoc.fwktd.server.dao.ibatis.IbatisGenericReadOnlyDaoImpl;
import es.ieci.tecdoc.isicres.terceros.business.dao.CiudadDao;
import es.ieci.tecdoc.isicres.terceros.business.vo.CiudadVO;
import es.ieci.tecdoc.isicres.terceros.business.vo.ProvinciaVO;

/**
 *
 * @author IECISA
 *
 */
public class CiudadDaoImpl extends
		IbatisGenericReadOnlyDaoImpl<CiudadVO, String> implements CiudadDao {

	public CiudadDaoImpl(Class<CiudadVO> aPersistentClass) {
		super(aPersistentClass);
	}

	public CiudadVO findByCodigo(String codigo) {
		return (CiudadVO) getSqlMapClientTemplate().queryForObject(
				StringUtils.join(
						new String[] {
								ClassUtils.getShortName(getPersistentClass()),
								"findByCodigo" }, "."), codigo);
	}

	public CiudadVO findByNombre(String nombre) {
		return (CiudadVO) getSqlMapClientTemplate().queryForObject(
				StringUtils.join(
						new String[] {
								ClassUtils.getShortName(getPersistentClass()),
								"findByNombre" }, "."), nombre);
	}

	@SuppressWarnings("unchecked")
	public List<CiudadVO> getCiudadesByProvincia(ProvinciaVO provincia) {
		return getSqlMapClientTemplate().queryForList(
				StringUtils.join(
						new String[] {
								ClassUtils.getShortName(getPersistentClass()),
								"getCiudadesByProvincia" }, "."), provincia);
	}

	public Integer getCiudadesByProvinciaCount(ProvinciaVO provincia) {
	    return (Integer)this.getSqlMapClientTemplate().queryForObject(StringUtils.join((Object[])new String[]{ClassUtils.getShortName((Class)this.getPersistentClass()), "getCiudadesByProvinciaCount"}, (String)"."), (Object)provincia);
	}
	public List<CiudadVO> getCiudadesByProvincia(ProvinciaVO provincia, int from, int to) {
	    return this.getSqlMapClientTemplate().queryForList(StringUtils.join((Object[])new String[]{ClassUtils.getShortName((Class)this.getPersistentClass()), "getCiudadesByProvincia"}, (String)"."), (Object)provincia, from, to);
	}
	public List<CiudadVO> getCiudades(int from, int to) {
	    return this.getSqlMapClientTemplate().queryForList(StringUtils.join((Object[])new String[]{ClassUtils.getShortName((Class)this.getPersistentClass()), "getCiudadVOs"}, (String)"."), from, to);
	}
	public Integer getCiudadesCount() {
	    return (Integer)this.getSqlMapClientTemplate().queryForObject(StringUtils.join((Object[])new String[]{ClassUtils.getShortName((Class)this.getPersistentClass()), "getCiudadesCount"}, (String)"."));
	}
}
