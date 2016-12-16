package es.ieci.tecdoc.isicres.api.business.manager.impl;

import es.ieci.tecdoc.isicres.api.business.dao.OficinaDAO;
import es.ieci.tecdoc.isicres.api.business.keys.ConstantKeys;
import es.ieci.tecdoc.isicres.api.business.manager.OficinaManager;
import es.ieci.tecdoc.isicres.api.business.vo.ConfiguracionUsuarioVO;
import es.ieci.tecdoc.isicres.api.business.vo.OficinaVO;
import es.ieci.tecdoc.isicres.api.business.vo.UsuarioVO;
import java.util.List;
import java.util.Locale;

public class OficinaManagerImpl
extends OficinaManager {
    protected OficinaDAO oficinaDAO;

    @Override
    public OficinaVO getOficinaByCodigo(UsuarioVO usuario, String codigoOficina) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<OficinaVO> findOficinasByUsuario(UsuarioVO usuario) {
        Locale locale = usuario.getConfiguracionUsuario().getLocale();
        return this.getOficinaDAO().getOficinasByUsuario(locale, usuario);
    }

    @Override
    public List<OficinaVO> findOficinasAdministradasByUsuario(UsuarioVO usuario) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OficinaVO getOficinaById(Locale locale, String idOficina) {
        OficinaVO result = null;
        result = this.getOficinaDAO().getOficinaById(locale, idOficina);
        return result;
    }

    @Override
    public List<OficinaVO> getOficinas(Locale locale) {
        if (locale == null) {
            Locale defaultLocale = new Locale(ConstantKeys.LOCALE_LENGUAGE_DEFAULT, ConstantKeys.LOCALE_COUNTRY_DEFAULT);
            return this.getOficinaDAO().getOficinas(defaultLocale);
        }
        return this.getOficinaDAO().getOficinas(locale);
    }

    public OficinaDAO getOficinaDAO() {
        return this.oficinaDAO;
    }

    public void setOficinaDAO(OficinaDAO oficinaDAO) {
        this.oficinaDAO = oficinaDAO;
    }
}
