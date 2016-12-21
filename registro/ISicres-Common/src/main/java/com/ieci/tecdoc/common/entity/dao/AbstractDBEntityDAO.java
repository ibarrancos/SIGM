package com.ieci.tecdoc.common.entity.dao;

import com.ieci.tecdoc.common.entity.dao.DBEntityDAO;
import com.ieci.tecdoc.common.entity.dao.DBEntityDAOKeys;
import com.ieci.tecdoc.common.extension.StringClobType;
import com.ieci.tecdoc.common.invesdoc.Idocvtblctlg;
import com.ieci.tecdoc.common.invesicres.ScrAddrtel;
import com.ieci.tecdoc.common.invesicres.ScrDistreg;
import com.ieci.tecdoc.common.invesicres.ScrReport;
import com.ieci.tecdoc.common.isicres.AxSf;
import com.ieci.tecdoc.common.isicres.AxSfQuery;
import com.ieci.tecdoc.common.isicres.AxSfQueryField;
import com.ieci.tecdoc.common.utils.BBDDUtils;
import com.ieci.tecdoc.common.utils.Configurator;
import com.ieci.tecdoc.common.utils.ScrRegisterInter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class AbstractDBEntityDAO
extends DBEntityDAOKeys
implements DBEntityDAO {
    private static final Logger log = Logger.getLogger((Class)AbstractDBEntityDAO.class);
    protected static final String IGUAL = "=";
    protected static final String MAYOR = ">";
    protected static final String MENOR_IGUAL = "<=";
    protected static final String DISTINTO = "!=";
    protected static final String ENTRE = "..";
    protected static final String QUERY_OR = "|";
    protected static final String MENOR = "<";
    protected static final String MAYOR_IGUAL = ">=";
    protected static final String MIN_TIME = " 00:00:00";
    protected static final String MAX_TIME = " 23:59:59";
    protected static final String MIN_TIME_QUOTE = " 00:00:00'";
    protected static final String MAX_TIME_QUOTE = " 23:59:59'";

    public abstract String getDataBaseType();

    public abstract String getVersion(String var1);

    public abstract String getLikeCharacter();

    public abstract int getNextIdForInter(Integer var1, String var2) throws SQLException;

    public abstract void deleteIdsGenerationTable(Integer var1, String var2);

    public String getReportInsert(String tableName, Integer bookId, String fieldList, String where) {
        return "INSERT INTO " + tableName + " SELECT " + bookId + "," + fieldList + " FROM A" + bookId + "SF " + where;
    }

    public String getReportInsert(String tableName, Integer bookId, String fieldList) {
        return "INSERT INTO " + tableName + " SELECT " + bookId + "," + fieldList + " FROM A" + bookId + "SF ";
    }

    public String getAditionFields(int bookType) {
        String result = null;
        result = bookType == 1 ? ",FLD5_TEXT,FLD7_TEXT,FLD8_TEXT,FLD13_TEXT,FLD16_TEXT" : ",FLD5_TEXT,FLD7_TEXT,FLD8_TEXT,FLD12_TEXT";
        return result.toString();
    }

    public Object[][] getRelationsTupla(String tableName, int opcion, String entidad) throws SQLException {
        Object[][] result;
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        String selectSentence = null;
        result = null;
        try {
            selectSentence = opcion == 4 || opcion == 6 ? "SELECT FLD4, FLD8  FROM " + tableName + " ORDER BY FLD4, FLD8" : "SELECT FLD4, FLD7  FROM " + tableName + " ORDER BY FLD4, FLD7";
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSentence);
            int size = this.getTableOrViewSize(tableName, entidad);
            result = new Object[size][2];
            int count = 0;
            while (resultSet.next()) {
                result[count][0] = resultSet.getDate(1);
                result[count][1] = resultSet.getString(2) != null ? new Integer(resultSet.getString(2)) : null;
                ++count;
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), e);
            throw new SQLException("Error ejecutando [" + selectSentence + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public int getNewNumRelations(int typebook, int typerel, int idofic, int idunit, int relyear, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        int result = 0;
        StringBuffer query = new StringBuffer();
        try {
            query.append("SELECT MAX(NREL)  FROM SCR_RELATIONS ");
            query.append(" scr WHERE scr.typebook=" + typebook + " AND scr.typerel=" + typerel + " AND scr.idofic=" + idofic);
            query.append(" AND scr.idunit=" + idunit + " AND scr.relyear=" + relyear);
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query.toString());
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), e);
            throw new SQLException("Error ejecutando [" + query.toString() + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public List getPrivOrgs(int idofic, String entidad) throws SQLException {
        ArrayList<Integer> privOrgs;
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        privOrgs = null;
        StringBuffer query = new StringBuffer();
        try {
            query.append("SELECT IDORGS FROM SCR_PRIVORGS WHERE IDOFIC != " + idofic);
            query.append(" AND IDORGS NOT IN (SELECT IDORGS FROM SCR_PRIVORGS WHERE IDOFIC = " + idofic + ")");
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query.toString());
            privOrgs = new ArrayList<Integer>();
            while (resultSet.next()) {
                privOrgs.add(new Integer(resultSet.getInt(1)));
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), e);
            throw new SQLException("Error ejecutando [" + query.toString() + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return privOrgs;
    }

    public int getOtherOffice(int userId, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        int result = 0;
        StringBuffer query = new StringBuffer();
        try {
            query.append("SELECT COUNT(*) FROM SCR_OFIC WHERE ");
            query.append("ID IN (SELECT IDOFIC FROM SCR_USROFIC WHERE IDUSER=" + userId + ")");
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query.toString());
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), e);
            throw new SQLException("Error ejecutando [" + query.toString() + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public int getSizeIncompletRegister(Integer bookId, Integer oficId, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM A" + bookId + "SF WHERE FLD6=1 AND FLD5=" + oficId);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [SELECT COUNT(*) FROM A" + bookId + "SF WHERE FLD6=1 AND FLD5=" + oficId + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [SELECT COUNT(*) FROM A" + bookId + "SF WHERE FLD6=1 AND FLD5=" + oficId + "]"), e);
            throw new SQLException("Error ejecutando [SELECT COUNT(*) FROM A" + bookId + "SF WHERE FLD6=1 AND FLD5=" + oficId + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public int getTableOrViewSize(String tableName, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [SELECT COUNT(*) FROM " + tableName + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [SELECT COUNT(*) FROM " + tableName + "]"), e);
            throw new SQLException("Error ejecutando [SELECT COUNT(*) FROM " + tableName + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public void insertTableOrView(String sentence, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            statement.executeUpdate(sentence);
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), e);
            throw new SQLException("Error ejecutando [" + sentence + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void createTableOrView(String sentence, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            statement.executeUpdate(sentence);
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), e);
            throw new SQLException("Error ejecutando [" + sentence + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void insertTableOrView(String insert, AxSf axsf, AxSfQuery axsfQuery, String entidad) throws SQLException {
        PreparedStatement pstatement = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            pstatement = connection.prepareStatement(insert);
            this.assignAxSFPreparedStatement(axsfQuery, axsf, pstatement);
            pstatement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + insert + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + insert + "]"), e);
            throw new SQLException("Error ejecutando [" + insert + "]");
        }
        finally {
            BBDDUtils.close(pstatement);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void createTableOrView(String create, String insert, AxSf axsf, AxSfQuery axsfQuery, String entidad) throws SQLException {
        PreparedStatement pstatement = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            statement.executeUpdate(create);
            pstatement = connection.prepareStatement(insert);
            this.assignAxSFPreparedStatement(axsfQuery, axsf, pstatement);
            pstatement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + create + "] [" + insert + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + create + "] [" + insert + "]"), e);
            throw new SQLException("Error ejecutando [" + create + "] [" + insert + "]");
        }
        finally {
            BBDDUtils.close(pstatement);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void createTableOrView(String create, AxSf axsf, AxSfQuery axsfQuery, String entidad) throws SQLException {
        Statement pstatement = null;
        Statement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            statement.executeUpdate(create);
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + create + "] "), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + create + "] "), e);
            throw new SQLException("Error ejecutando [" + create + "] ");
        }
        finally {
            BBDDUtils.close(pstatement);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void createTableOrView(String insert, Date beginDate, Date endDate, int unit, String entidad) throws SQLException {
        PreparedStatement pstatement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            pstatement = connection.prepareStatement(insert);
            pstatement.setTimestamp(1, BBDDUtils.getTimestamp(beginDate));
            pstatement.setTimestamp(2, BBDDUtils.getTimestamp(endDate));
            if (unit != -1) {
                pstatement.setInt(3, unit);
            }
            pstatement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + insert + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + insert + "]"), e);
            throw new SQLException("Error ejecutando [" + insert + "]");
        }
        finally {
            BBDDUtils.close(pstatement);
            BBDDUtils.close(connection);
        }
    }

    public void insertAudit(Integer id, Object oldValue, Object newValue, String entidad) throws SQLException {
        block27 : {
            PreparedStatement statement = null;
            Connection connection = null;
            String query = null;
            try {
                connection = BBDDUtils.getConnection(entidad);
                Object valueToInspect = oldValue;
                if (valueToInspect == null) {
                    valueToInspect = newValue;
                }
                if (valueToInspect != null) {
                    log.info((Object)("AUDITANDO CLASE " + valueToInspect.getClass().getName()));
                    if (valueToInspect instanceof String) {
                        query = "INSERT INTO SCR_VALSTR(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        statement.setString(2, (String)newValue);
                        statement.setString(3, (String)oldValue);
                    }
                    if (valueToInspect instanceof Date) {
                        query = "INSERT INTO SCR_VALDATE(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        statement.setTimestamp(2, BBDDUtils.getTimestamp((Date)newValue));
                        statement.setTimestamp(3, BBDDUtils.getTimestamp((Date)oldValue));
                    }
                    if (valueToInspect instanceof Timestamp) {
                        query = "INSERT INTO SCR_VALDATE(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        statement.setTimestamp(2, (Timestamp)newValue);
                        statement.setTimestamp(3, (Timestamp)oldValue);
                    }
                    if (valueToInspect instanceof Integer) {
                        query = "INSERT INTO SCR_VALNUM(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        if (newValue != null) {
                            statement.setInt(2, Integer.parseInt(newValue.toString()));
                        } else {
                            statement.setInt(2, 0);
                        }
                        if (oldValue != null) {
                            statement.setInt(3, Integer.parseInt(oldValue.toString()));
                        } else {
                            statement.setInt(3, 0);
                        }
                    }
                    if (valueToInspect instanceof Long) {
                        query = "INSERT INTO SCR_VALNUM(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        if (newValue != null) {
                            statement.setLong(2, Long.parseLong(newValue.toString()));
                        } else {
                            statement.setInt(2, 0);
                        }
                        if (oldValue != null) {
                            statement.setLong(3, Long.parseLong(oldValue.toString()));
                        } else {
                            statement.setInt(3, 0);
                        }
                    }
                    if (valueToInspect instanceof BigDecimal) {
                        query = "INSERT INTO SCR_VALNUM(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, id);
                        if (newValue != null) {
                            statement.setInt(2, Integer.parseInt(newValue.toString()));
                        } else {
                            statement.setInt(2, 0);
                        }
                        if (oldValue != null) {
                            statement.setInt(3, Integer.parseInt(oldValue.toString()));
                        } else {
                            statement.setInt(3, 0);
                        }
                    }
                    statement.executeUpdate();
                    break block27;
                }
                query = "INSERT INTO SCR_VALSTR(ID,VALUE,OLDVALUE) VALUES (?,?,?)";
                statement = connection.prepareStatement(query);
                statement.setInt(1, id);
                statement.setString(2, null);
                statement.setString(3, null);
            }
            catch (SQLException e) {
                log.warn((Object)("Resulta imposible auditar [" + query + "]"), (Throwable)e);
            }
            catch (Throwable e) {
                log.warn((Object)("Resulta imposible iauditar [" + query + "]"), e);
            }
            finally {
                BBDDUtils.close(statement);
                BBDDUtils.close(connection);
            }
        }
    }

    public int createScrOficinaRow(int year, int bookType, int ofic, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_CNTOFICINA_OFIC);
            statement.setInt(1, year);
            statement.setInt(2, ofic);
            statement.setInt(3, bookType);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_cntcentral [" + INSERT_SCR_CNTOFICINA_OFIC + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_cntcentral [" + INSERT_SCR_CNTOFICINA_OFIC + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public int insertScrRelations(int typebook, int typerel, int relyear, int relmonth, int relday, int idofic, Date reldate, int idunit, int nrel, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_RELATIONS);
            statement.setInt(1, typebook);
            statement.setInt(2, typerel);
            statement.setInt(3, relyear);
            statement.setInt(4, relmonth);
            statement.setInt(5, relday);
            statement.setInt(6, idofic);
            statement.setDate(7, BBDDUtils.getDate(reldate));
            statement.setInt(8, idunit);
            statement.setInt(9, nrel);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_relations [" + INSERT_SCR_RELATIONS + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_relations [" + INSERT_SCR_RELATIONS + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public void deleteDistributeForUpdate(int idArch, int idFdr, int idOrg, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        String where = "FROM SCR_DISTREG WHERE ID_ARCH=? AND ID_FDR=? AND STATE=1 AND TYPE_ORIG=2 AND ID_ORIG=?";
        StringBuffer query = null;
        try {
            query = new StringBuffer();
            query.append("DELETE FROM SCR_PROCREG WHERE ID_DIST IN ");
            query.append("(SELECT ID ");
            query.append(where);
            query.append(")");
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, idArch);
            statement.setInt(2, idFdr);
            statement.setInt(3, idOrg);
            statement.executeUpdate();
            BBDDUtils.close(statement);
            query = new StringBuffer();
            query.append("DELETE FROM SCR_DISTREGSTATE WHERE ID_DIST IN ");
            query.append("(SELECT ID ");
            query.append(where);
            query.append(")");
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, idArch);
            statement.setInt(2, idFdr);
            statement.setInt(3, idOrg);
            statement.executeUpdate();
            BBDDUtils.close(statement);
            query = new StringBuffer();
            query.append("DELETE ");
            query.append(where);
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, idArch);
            statement.setInt(2, idFdr);
            statement.setInt(3, idOrg);
            statement.executeUpdate();
            BBDDUtils.close(statement);
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible deleteDistributeForUpdate [" + query + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible deleteDistributeForUpdate [" + query + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void deleteDistributeForUpdate(int idArch, int idFdr, int idOrg, String entidad, int state) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        String where = "FROM SCR_DISTREG WHERE ID_ARCH=? AND ID_FDR=? AND STATE=? AND TYPE_ORIG=2 AND ID_ORIG=?";
        StringBuffer query = null;
        try {
            query = new StringBuffer();
            query.append("DELETE FROM SCR_PROCREG WHERE ID_DIST IN ");
            query.append("(SELECT ID ");
            query.append(where);
            query.append(")");
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, idArch);
            statement.setInt(2, idFdr);
            statement.setInt(3, state);
            statement.setInt(4, idOrg);
            statement.executeUpdate();
            BBDDUtils.close(statement);
            query = new StringBuffer();
            query.append("DELETE FROM SCR_DISTREGSTATE WHERE ID_DIST IN ");
            query.append("(SELECT ID ");
            query.append(where);
            query.append(")");
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, idArch);
            statement.setInt(2, idFdr);
            statement.setInt(3, state);
            statement.setInt(4, idOrg);
            statement.executeUpdate();
            BBDDUtils.close(statement);
            query = new StringBuffer();
            query.append("DELETE ");
            query.append(where);
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(query.toString());
            statement.setInt(1, idArch);
            statement.setInt(2, idFdr);
            statement.setInt(3, state);
            statement.setInt(4, idOrg);
            statement.executeUpdate();
            BBDDUtils.close(statement);
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible deleteDistributeForUpdate [" + query + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible deleteDistributeForUpdate [" + query + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public int createScrCentralRow(int year, int bookType, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_CNTCENTRAL);
            statement.setInt(1, year);
            statement.setInt(2, bookType);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_cntcentral [" + INSERT_SCR_CNTCENTRAL + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_cntcentral [" + INSERT_SCR_CNTCENTRAL + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public void lockScrDistReg(int id, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(LOCK_SCR_DISTREG);
            statement.setInt(1, id);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible bloquear SCR_DISTREG [" + LOCK_SCR_DISTREG + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible bloquear SCR_DISTREG [" + LOCK_SCR_DISTREG + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void lockScrCentral(int year, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(LOCK_SCR_CNTCENTRAL);
            statement.setInt(1, year);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible bloquear scr_cntcentral [" + LOCK_SCR_CNTCENTRAL + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible bloquear scr_cntcentral [" + LOCK_SCR_CNTCENTRAL + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void lockScrOficina(int year, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(LOCK_SCR_CNTOFICINA);
            statement.setInt(1, year);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible bloquear scr_cntoficina [" + LOCK_SCR_CNTCENTRAL + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible bloquear scr_cntoficina [" + LOCK_SCR_CNTCENTRAL + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void lockScrRelations(int typeBook, int typeRel, int idofic, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SCR_LOCK_RELATIONS);
            statement.setInt(1, typeBook);
            statement.setInt(2, typeRel);
            statement.setInt(3, idofic);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible bloquear scr_lockrelation [" + SCR_LOCK_RELATIONS + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible bloquear scr_lockrelation [" + SCR_LOCK_RELATIONS + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void updateScrCntCentral(int year, int bookType, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_SCR_CNTCENTRAL);
            statement.setInt(1, year);
            statement.setInt(2, bookType);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible actualizar scr_cntcentral [" + UPDATE_SCR_CNTCENTRAL + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible actualizar scr_cntcentral [" + UPDATE_SCR_CNTCENTRAL + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void updateScrCntOficina(int year, int bookType, int ofic, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_SCR_CNTOFICINA);
            statement.setInt(1, year);
            statement.setInt(2, bookType);
            statement.setInt(3, ofic);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible actualizar scr_cntoficina [" + UPDATE_SCR_CNTOFICINA + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible actualizar scr_cntoficina [" + UPDATE_SCR_CNTOFICINA + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public int getNumRegFromScrCntCentral(int year, int bookType, String entidad) throws SQLException {
        int id;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        id = -1;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_NUMREG_SCR_CNTCENTRAL);
            statement.setInt(1, year);
            statement.setInt(2, bookType);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible obtener el num_reg de scr_central [" + SELECT_NUMREG_SCR_CNTCENTRAL + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible obtener el num_reg de scr_central [" + SELECT_NUMREG_SCR_CNTCENTRAL + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return id;
    }

    public void deleteUserConfig(int userId, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(DELETE_USER_CONFIG);
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible eliminar los campos persistentes [" + DELETE_USER_CONFIG + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible eliminar los campos persistentes [" + DELETE_USER_CONFIG + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public int getNumRegFromScrCntOficina(int year, int bookType, int ofic, String entidad) throws SQLException {
        int id;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        id = -1;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_NUMREG_SCR_CNTOFICINA);
            statement.setInt(1, year);
            statement.setInt(2, bookType);
            statement.setInt(3, ofic);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible obtener el num_reg de scr_oficina [" + SELECT_NUMREG_SCR_CNTOFICINA + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible obtener el num_reg de scr_oficina [" + SELECT_NUMREG_SCR_CNTOFICINA + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return id;
    }

    public int getNextIdForRegister(Integer bookID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        int id = -1;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_LOCK_NEXT_ID_3);
            statement.setInt(1, bookID);
            int affected = statement.executeUpdate();
            BBDDUtils.close(statement);
            if (affected == 0) {
                statement = connection.prepareStatement(INSERT_NEXT_ID_3);
                statement.setInt(1, bookID);
                affected = statement.executeUpdate();
                BBDDUtils.close(statement);
            }
            statement = connection.prepareStatement(SELECT_NEXT_ID_3);
            statement.setInt(1, bookID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            BBDDUtils.close(statement);
            statement = connection.prepareStatement(UPDATE_NEXT_ID_3);
            statement.setInt(1, bookID);
            statement.executeUpdate();
            BBDDUtils.close(statement);
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de registro.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de registro.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return id;
    }

    public int getNextIdForUser(String guid, String fullName, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        int id = -1;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_NEXT_ID_IUSER);
            statement.executeUpdate();
            BBDDUtils.close(statement);
            statement = connection.prepareStatement(SELECT_NEXT_ID_IUSER);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            id = id <= 0 ? 1 : --id;
            BBDDUtils.close(statement);
            statement = connection.prepareStatement(INSERT_NEXT_ID_IUSER);
            statement.setInt(1, id);
            statement.setString(2, guid);
            statement.setString(3, fullName);
            statement.executeUpdate();
            BBDDUtils.close(statement);
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de usuario.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de usuario.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return id;
    }

    public int getNextDocID(Integer bookID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        int id = -1;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_LOCK_NEXT_ID_7);
            statement.setInt(1, bookID);
            int affected = statement.executeUpdate();
            BBDDUtils.close(statement);
            if (affected == 0) {
                statement = connection.prepareStatement(INSERT_NEXT_ID_7);
                statement.setInt(1, bookID);
                affected = statement.executeUpdate();
                BBDDUtils.close(statement);
            }
            statement = connection.prepareStatement(SELECT_NEXT_ID_7);
            statement.setInt(1, bookID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            BBDDUtils.close(statement);
            statement = connection.prepareStatement(UPDATE_NEXT_ID_7);
            statement.setInt(1, bookID);
            statement.executeUpdate();
            BBDDUtils.close(statement);
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de registro.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de registro.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return id;
    }

    public int getNextIdForExtendedField(Integer bookID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        int id = -1;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_LOCK_NEXT_ID_5);
            statement.setInt(1, bookID);
            int affected = statement.executeUpdate();
            BBDDUtils.close(statement);
            if (affected == 0) {
                statement = connection.prepareStatement(INSERT_NEXT_ID_5);
                statement.setInt(1, bookID);
                affected = statement.executeUpdate();
                BBDDUtils.close(statement);
            }
            statement = connection.prepareStatement(SELECT_NEXT_ID_5);
            statement.setInt(1, bookID);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            BBDDUtils.close(statement);
            statement = connection.prepareStatement(UPDATE_NEXT_ID_5);
            statement.setInt(1, bookID);
            statement.executeUpdate();
            BBDDUtils.close(statement);
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de registro.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener el nuevo id de registro.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return id;
    }

    public Timestamp getUserLastConnection(Integer idUser, String entidad) {
        Timestamp date = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_USER_LASTCONNECTION);
            statement.setInt(1, idUser);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                date = resultSet.getTimestamp(1);
                BBDDUtils.close(statement);
                statement = connection.prepareStatement(DELETE_USER_LASTCONNECTION);
                statement.setInt(1, idUser);
                statement.executeUpdate();
                BBDDUtils.close(statement);
            } else {
                BBDDUtils.close(statement);
                date = this.getDBServerDate(entidad);
            }
            statement = connection.prepareStatement(INSERT_USER_LASTCONNECTION);
            statement.setInt(1, idUser);
            statement.setTimestamp(2, this.getDBServerDate(entidad));
            statement.executeUpdate();
            BBDDUtils.close(statement);
            log.info((Object)("LAST CONNECTION: " + date));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener la fecha de \u00faltima conexi\u00f3n del usuario.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener la fecha de \u00faltima conexi\u00f3n del usuario.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return date;
    }

    public boolean getDistAccept(Integer bookId, int fdrid, Integer idOfic, String entidad) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        boolean lock = false;
        int officeId = 0;
        int state = 0;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_DIST_DISTACCEPT);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrid);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                officeId = resultSet.getInt(OFFICEID_FIELD);
                state = resultSet.getInt(STATE_FIELD);
                if (officeId != idOfic || state != 0) {
                    lock = true;
                }
            }
            log.info((Object)("DIST ACCEPT: " + bookId + " " + fdrid + " " + idOfic + " " + officeId + " " + state + " " + lock));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener la informaci\u00f3n del registro bloqueado aceptado tras una distribuci\u00f3n.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener la informaci\u00f3n del registro bloqueado aceptado tras una distribuci\u00f3n.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return lock;
    }

    public boolean getDistAcceptExist(Integer bookId, int fdrid, String entidad) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        boolean exist = false;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_DIST_DISTACCEPT);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrid);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                exist = true;
            }
            log.info((Object)("DIST ACCEPT EXIST: " + bookId + " " + fdrid + " " + exist));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener la informaci\u00f3n del registro bloqueado aceptado tras una distribuci\u00f3n.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener la informaci\u00f3n del registro bloqueado aceptado tras una distribuci\u00f3n.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return exist;
    }

    public void updateInsertDistAccept(boolean exist, Integer bookId, int fdrId, int officeId, int state, String accUser, Date accDate, Date distDate, String entidad) {
        block8 : {
            PreparedStatement statement = null;
            Connection connection = null;
            try {
                connection = BBDDUtils.getConnection(entidad);
                if (exist) {
                    statement = connection.prepareStatement(UPDATE_DIST_DISTACCEPT);
                    statement.setInt(1, officeId);
                    statement.setInt(2, state);
                    statement.setString(3, accUser.toUpperCase());
                    statement.setDate(4, BBDDUtils.getDate(accDate));
                    statement.setInt(5, bookId);
                    statement.setInt(6, fdrId);
                    statement.executeUpdate();
                    log.info((Object)("DIST ACCEPT UPDATE: " + bookId + " " + fdrId + " " + officeId + " " + state + " " + accUser + " " + accDate + " " + distDate));
                    break block8;
                }
                statement = connection.prepareStatement(INSERT_DIST_DISTACCEPT);
                statement.setInt(1, bookId);
                statement.setInt(2, fdrId);
                statement.setInt(3, officeId);
                statement.setTimestamp(4, BBDDUtils.getTimestamp(distDate));
                statement.setTimestamp(5, BBDDUtils.getTimestamp(accDate));
                statement.setString(6, accUser.toUpperCase());
                statement.setInt(7, state);
                statement.executeUpdate();
                log.info((Object)("DIST ACCEPT INSERT: " + bookId + " " + fdrId + " " + officeId + " " + state + " " + accUser + " " + accDate + " " + distDate));
            }
            catch (SQLException e) {
                log.warn((Object)"Resulta imposible actualizar o insertar en SCR_DISTACCEPT. ", (Throwable)e);
            }
            catch (Throwable e) {
                log.warn((Object)"Resulta imposible actualizar o insertar en SCR_DISTACCEPT. ", e);
            }
            finally {
                BBDDUtils.close(statement);
                BBDDUtils.close(connection);
            }
        }
    }

    public void updateRole(int userId, int prodId, int type, String entidad) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_IUSERUSERTYPE);
            statement.setInt(1, type);
            statement.setInt(2, userId);
            statement.setInt(3, prodId);
            statement.executeUpdate();
            log.info((Object)("UPDATE ROLE: " + type + " " + userId + " " + prodId));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible actualizar o insertar en IUSERUSERHDR. ", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible actualizar o insertar en IUSERUSERHDR. ", e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void updateLdapFullName(int id, String ldapFullName, String entidad) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_IUSERLDAPUSERHDR);
            statement.setString(1, ldapFullName);
            statement.setInt(2, id);
            statement.executeUpdate();
            log.info((Object)("UPDATE LDAPFULLNAME: " + ldapFullName + " " + id));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible actualizar en IUSERLDAPUSERHDR. ", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible actualizar en IUSERLDAPUSERHDR. ", e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public void deleteDistAccept(Integer bookId, int fdrid, String entidad) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(DELETE_DIST_DISTACCEPT);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrid);
            statement.executeUpdate();
            log.info((Object)("DELETE DIST ACCEPT: " + bookId + " " + fdrid));
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible borrar la informaci\u00f3n de distribuci\u00f3n [" + DELETE_DIST_DISTACCEPT + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible borrar la informaci\u00f3n de distribuci\u00f3n [" + DELETE_DIST_DISTACCEPT + "]"), e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public int lastRegister(Integer fdrid, Integer idUser, Integer bookId, String entidad) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        int lastFdrid = 0;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_USER_LASTREGISTER);
            statement.setInt(1, bookId);
            statement.setInt(2, idUser);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                lastFdrid = resultSet.getInt(FDRID_FIELD);
                BBDDUtils.close(statement);
                if (fdrid != null) {
                    statement = connection.prepareStatement(UPDATE_USER_LASTREGISTER);
                    statement.setInt(1, fdrid);
                    statement.setInt(2, bookId);
                    statement.setInt(3, idUser);
                    statement.executeUpdate();
                    BBDDUtils.close(statement);
                }
            } else if (fdrid != null) {
                BBDDUtils.close(statement);
                statement = connection.prepareStatement(INSERT_USER_LASTREGISTER);
                statement.setInt(1, bookId);
                statement.setInt(2, fdrid);
                statement.setInt(3, idUser);
                statement.executeUpdate();
                BBDDUtils.close(statement);
            }
            log.info((Object)("LAST REGISTER: " + bookId + " " + fdrid + " " + idUser + " " + lastFdrid));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener la informaci\u00f3n del \u00faltimo registro creado por el usuario.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener la informaci\u00f3n del \u00faltimo registro creado por el usuario.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return lastFdrid;
    }

    public Timestamp getDBServerDate(String entidad) {
        Timestamp date;
        date = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(this.getDateQuery());
            while (resultSet.next()) {
                date = resultSet.getTimestamp(1);
                log.info((Object)("CURRENT DATE RECOVERED: " + date));
            }
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible obtener la fecha del sistema.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible obtener la fecha del sistema.", e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return date;
    }

    public String findAxSFLastForUserSENTENCE(String tableName, String filter, boolean orderby) {
        String where = this.createWhereClausule(null, null, filter, orderby);
        String sentence = MessageFormat.format(AXSF_FIND_LASTFORUSER, tableName, where);
        if (log.isDebugEnabled()) {
            log.debug((Object)("findAxSFLastForUserSENTENCE QUERY :" + sentence));
        }
        return sentence;
    }

    public String findAxSFAllSENTENCE(AxSf axsf, String tableName, AxSfQuery axsfQuery, int begin, int end, String filter, boolean orderby) {
        String where = this.createWhereClausule(axsf, axsfQuery, filter, orderby);
        String sentence = MessageFormat.format(AXSF_FINDALL_SENTENCE, tableName, where);
        if (log.isDebugEnabled()) {
            log.debug((Object)("findAxSFAllSENTENCE QUERY :" + sentence));
        }
        return sentence;
    }

    public String findAxSFAllSizeSENTENCE(AxSf axsf, String tableName, AxSfQuery axsfQuery, String filter, boolean orderby) {
        String where = this.createWhereClausule(axsf, axsfQuery, filter, orderby);
        String sentence = MessageFormat.format(AXSF_FINDALL_SIZE_SENTENCE, tableName, where);
        if (log.isDebugEnabled()) {
            log.debug((Object)("findAxSFAllSizeSENTENCE QUERY :" + sentence));
        }
        return sentence;
    }

    public void assignAxSFPreparedStatement(AxSfQuery axsfQuery, AxSf axsfP, PreparedStatement ps) throws SQLException {
        if (!(axsfQuery == null || axsfQuery.getFields() == null || axsfQuery.getFields().isEmpty())) {
            int index = 1;
	    for (Iterator it01 = axsfQuery.getFields().iterator(); it01.hasNext();) {
                AxSfQueryField field2 = (AxSfQueryField) it01.next();

                if (field2.getFldId().equals("fld9")) {
                    field2.setBookId(axsfQuery.getBookId());
                }
                if (field2.getOperator().equals("|") || field2.getOperator().equals("..")) {
                    if (!field2.getFldId().equals("fld9")) {
                        List list = (List)field2.getValue();
                        int i = 0;
                        Iterator it2 = list.iterator();
                        while (it2.hasNext()) {
                            if (!field2.getOperator().equals("..")) {
                                this.assignAttribute(field2, axsfP, ps, index, it2.next());
                                if (axsfP.getAttributeClass(field2.getFldId()).equals(Date.class) && field2.getOperator().equals("|")) {
                                    ++index;
                                }
                            } else if (axsfP.getAttributeClass(field2.getFldId()).equals(Date.class) && i == 1) {
                                GregorianCalendar gc = new GregorianCalendar();
                                gc.setTime((Date)it2.next());
                                gc.set(13, 0);
                                if (field2.getOperator().equals("|")) {
                                    gc.set(11, 0);
                                    gc.set(12, 0);
                                    gc.set(14, 0);
                                    gc.set(13, 0);
                                } else {
                                    gc.set(11, 23);
                                    gc.set(12, 59);
                                    gc.set(13, 59);
                                    gc.set(14, 999);
                                }
                                ps.setObject(index, BBDDUtils.getTimestamp(gc.getTime()));
                            } else {
                                this.assignAttribute(field2, axsfP, ps, index, it2.next());
                            }
                            ++index;
                            ++i;
                        }
                        continue;
                    }
                    this.assignAttribute(field2, axsfP, ps, index, field2.getBookId());
                    ++index;
                    continue;
                }
                this.assignAttribute(field2, axsfP, ps, index);
                if (axsfP.getAttributeClass(field2.getFldId()).equals(Date.class) && (field2.getOperator().equals("=") || field2.getOperator().equals("!="))) {
                    ++index;
                }
                ++index;
            }
        }
    }

    public abstract int getContador4PERSONS(Integer var1, String var2) throws SQLException;

    public abstract int getContador4SCRADDRESS(Integer var1, String var2) throws SQLException;

    public String createWhereClausuleReport(AxSf axsf, AxSfQuery axsfQuery, String filter) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ESPACIO);
        if (filter != null) {
            buffer.append(WHERE);
            buffer.append(ESPACIO);
            buffer.append(filter);
            buffer.append(ESPACIO);
        }
        if (!(axsfQuery == null || axsfQuery.getFields() == null || axsfQuery.getFields().isEmpty())) {
            if (filter == null) {
                buffer.append(WHERE);
                buffer.append(ESPACIO);
            } else {
                buffer.append(AND);
                buffer.append(ESPACIO);
            }
            int i = 0;
	    for (Iterator it02 = axsfQuery.getFields().iterator(); it02.hasNext();) {
                AxSfQueryField field2 = (AxSfQueryField) it02.next();
                List list;
                Iterator it2;
                String aux;
                int j;
                int size;
                if (field2.getOperator().equals("|")) {
                    list = (List)field2.getValue();
                    if (!list.isEmpty()) {
                        j = 0;
                        size = list.size();
                        buffer.append(PAR_IZQ);
                        if (!field2.getFldId().equals("fld9")) {
                            it2 = list.iterator();
                            while (it2.hasNext()) {
                                aux = null;
                                if (axsf != null && axsf.getAttributeClass(field2.getFldId()).equals(Date.class)) {
                                    aux = this.getDateField(field2.getFldId(), "=", 2, FORMATTER.format((Date)it2.next()));
                                    buffer.append(aux);
                                } else {
                                    buffer.append(field2.getFldId());
                                    buffer.append("=");
                                    aux = field2.getFldId() + field2.getOperator() + field2.getValue();
                                    if (field2.getValue().getClass().equals((Object)Integer.class)) {
                                        buffer.append(it2.next());
                                    } else {
                                        buffer.append("'" + it2.next() + "'");
                                    }
                                }
                                if (j < size - 1) {
                                    buffer.append(ESPACIO);
                                    buffer.append(OR);
                                    buffer.append(ESPACIO);
                                }
                                ++j;
                            }
                        } else {
                            it2 = list.iterator();
                            while (it2.hasNext()) {
                                if (j == 0) {
                                    buffer.append(axsfQuery.getWhereField9());
                                    buffer.append("=");
                                }
                                axsfQuery.setSentenceField9OrBetween((String)it2.next(), OR);
                                ++j;
                            }
                            buffer.append(axsfQuery.getSentenceField9OrBetween());
                            buffer.append(axsfQuery.getWhereIdarchField9());
                            buffer.append(axsfQuery.getBookId().toString());
                            buffer.append(PAR_DER);
                            axsfQuery.initializeSentenceField9OrBetween();
                        }
                        buffer.append(PAR_DER);
                    }
                } else if (field2.getOperator().equals("..")) {
                    list = (List)field2.getValue();
                    if (!list.isEmpty()) {
                        j = 0;
                        size = list.size();
                        buffer.append(PAR_IZQ);
                        if (!field2.getFldId().equals("fld9")) {
                            buffer.append(field2.getFldId());
                            buffer.append(ESPACIO);
                            buffer.append(BETWEEN);
                            buffer.append(ESPACIO);
                            it2 = list.iterator();
                            while (it2.hasNext()) {
                                aux = null;
                                if (axsf != null && axsf.getAttributeClass(field2.getFldId()).equals(Date.class)) {
                                    aux = j > 0 ? this.getTimeStampField((Date)it2.next(), 4) : this.getTimeStampField((Date)it2.next(), 3);
                                    buffer.append(aux);
                                } else {
                                    aux = field2.getFldId() + field2.getOperator() + field2.getValue();
                                    if (field2.getValue().getClass().equals((Object)Integer.class)) {
                                        buffer.append(it2.next());
                                    } else {
                                        buffer.append("'" + it2.next() + "'");
                                    }
                                }
                                if (j < size - 1) {
                                    buffer.append(ESPACIO);
                                    buffer.append(AND);
                                    buffer.append(ESPACIO);
                                }
                                ++j;
                            }
                        } else {
                            buffer.append(axsfQuery.getWhereField9());
                            buffer.append(ESPACIO);
                            buffer.append(BETWEEN);
                            buffer.append(ESPACIO);
                            it2 = list.iterator();
                            while (it2.hasNext()) {
                                axsfQuery.setSentenceField9OrBetween((String)it2.next(), BETWEEN);
                            }
                            buffer.append(axsfQuery.getSentenceField9OrBetween());
                            buffer.append(axsfQuery.getWhereIdarchField9());
                            buffer.append(axsfQuery.getBookId().toString());
                            buffer.append(PAR_DER);
                            axsfQuery.initializeSentenceField9OrBetween();
                        }
                        buffer.append(PAR_DER);
                    }
                } else if (field2.getOperator().equals("%")) {
                    buffer.append(PAR_IZQ);
                    if (field2.getFldId().equals("fld9")) {
                        buffer.append(axsfQuery.getWhereField9());
                    } else {
                        buffer.append(field2.getFldId());
                    }
                    buffer.append(ESPACIO);
                    buffer.append(LIKE);
                    buffer.append(ESPACIO);
                    if (field2.getFldId().equals("fld9")) {
                        axsfQuery.setSentenceField9((String)field2.getValue());
                        buffer.append(axsfQuery.getSentenceField9());
                        buffer.append(axsfQuery.getBookId().toString());
                        buffer.append(")");
                    } else {
                        buffer.append("'" + field2.getValue() + "'");
                    }
                    buffer.append(PAR_DER);
                } else if (field2.getOperator().equals("dde")) {
                    buffer.append(PAR_IZQ);
                    buffer.append(field2.getFldId());
                    buffer.append(ESPACIO);
                    buffer.append(axsfQuery.getWhereOprDependOfIn());
                    buffer.append("'" + field2.getValue() + "'");
                    buffer.append(axsfQuery.getWhereOprDependOfConnect());
                    buffer.append(")");
                    buffer.append(PAR_DER);
                } else if ("!=".equals(field2.getOperator())) {
                    buffer.append(PAR_IZQ);
                    buffer.append(field2.getFldId());
                    buffer.append(ESPACIO);
                    buffer.append(IS_NULL);
                    buffer.append(ESPACIO);
                    buffer.append(OR);
                    buffer.append(ESPACIO);
                    this.generateQueryByFieldReport(axsf, axsfQuery, buffer, field2);
                    buffer.append(PAR_DER);
                } else {
                    this.generateQueryByFieldReport(axsf, axsfQuery, buffer, field2);
                }
                if (i < axsfQuery.getFields().size() - 1) {
                    buffer.append(ESPACIO);
                    buffer.append(AND);
                    buffer.append(ESPACIO);
                }
                ++i;
                buffer.append(ESPACIO);
            }
            buffer.append(ESPACIO);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("createWhereClausuleReport WHERE :" + buffer));
        }
        return buffer.toString();
    }

    private void generateQueryByFieldReport(AxSf axsf, AxSfQuery axsfQuery, StringBuffer buffer, AxSfQueryField field) {
        String aux = null;
        if (axsf != null && axsf.getAttributeClass(field.getFldId()).equals(Date.class)) {
            aux = this.getDateField(field.getFldId(), field.getOperator(), 2, FORMATTER.format((Date)field.getValue()));
            buffer.append(aux);
        } else {
            aux = field.getFldId() + field.getOperator() + field.getValue();
            if (!field.getFldId().equals("fld9")) {
                buffer.append(field.getFldId());
                buffer.append(field.getOperator());
                if (field.getValue().getClass().equals((Object)Integer.class)) {
                    buffer.append(field.getValue());
                } else {
                    buffer.append("'" + field.getValue() + "'");
                }
            } else {
                buffer.append(axsfQuery.getWhereField9());
                buffer.append(field.getOperator());
                axsfQuery.setSentenceField9((String)field.getValue());
                buffer.append(axsfQuery.getSentenceField9());
                buffer.append(axsfQuery.getBookId().toString());
                buffer.append(")");
            }
        }
    }

    public String createWhereClausule(AxSf axsf, AxSfQuery axsfQuery, String filter, boolean orderby) {
        StringBuffer buffer = new StringBuffer();
        boolean fld32 = true;
        buffer.append(ESPACIO);
        if (filter != null) {
            buffer.append(WHERE);
            buffer.append(ESPACIO);
            buffer.append(filter);
            buffer.append(ESPACIO);
        }
        if (axsfQuery != null && axsfQuery.getSelectDefWhere2() != null) {
            if (filter != null) {
                buffer.append(ESPACIO);
                buffer.append(AND);
                buffer.append(ESPACIO);
                buffer.append(axsfQuery.getSelectDefWhere2());
                buffer.append(ESPACIO);
            } else {
                buffer.append(WHERE);
                buffer.append(ESPACIO);
                buffer.append(axsfQuery.getSelectDefWhere2());
                buffer.append(ESPACIO);
            }
        }
        if (!(axsfQuery == null || axsfQuery.getFields() == null || axsfQuery.getFields().isEmpty())) {
            if (filter == null && axsfQuery.getSelectDefWhere2() == null) {
                buffer.append(WHERE);
                buffer.append(ESPACIO);
            } else {
                buffer.append(AND);
                buffer.append(ESPACIO);
            }
            int i = 0;
	    for (Iterator it03 = axsfQuery.getFields().iterator(); it03.hasNext();) {
                AxSfQueryField field2 = (AxSfQueryField) it03.next();
                List list;
                if (field2.getOperator().equals("|")) {
                    list = (List)field2.getValue();
                    if (!list.isEmpty()) {
                        int j = 0;
                        int size = list.size();
                        buffer.append(PAR_IZQ);
                        Iterator it2 = list.iterator();
                        while (it2.hasNext()) {
                            if (field2.getFldId().equals("fld9")) {
                                if (j == 0) {
                                    buffer.append(axsfQuery.getWhereField9());
                                    buffer.append("=");
                                }
                                axsfQuery.setSentenceField9OrBetween((String)it2.next(), OR);
                            } else {
                                if (axsf != null && axsf.getAttributeClass(field2.getFldId()).equals(Date.class)) {
                                    buffer.append(this.getDateField(field2.getFldId(), "=", 1, null));
                                } else {
                                    buffer.append(field2.getFldId());
                                    buffer.append("=");
                                    buffer.append(INTERROGACION);
                                }
                                if (j < size - 1) {
                                    buffer.append(ESPACIO);
                                    buffer.append(OR);
                                    buffer.append(ESPACIO);
                                }
                            }
                            ++j;
                            if (field2.getFldId().equals("fld9")) continue;
                            it2.next();
                        }
                        if (field2.getFldId().equals("fld9")) {
                            buffer.append(axsfQuery.getSentenceField9OrBetween());
                            buffer.append(axsfQuery.getWhereIdarchField9());
                            buffer.append(INTERROGACION);
                            buffer.append(PAR_DER);
                            axsfQuery.initializeSentenceField9OrBetween();
                        }
                        buffer.append(PAR_DER);
                    }
                } else if (field2.getOperator().equals("..")) {
                    list = (List)field2.getValue();
                    if (!list.isEmpty()) {
                        if (!field2.getFldId().equals("fld9")) {
                            buffer.append(PAR_IZQ);
                            buffer.append(field2.getFldId());
                            buffer.append(ESPACIO);
                            buffer.append(BETWEEN);
                            buffer.append(ESPACIO);
                            buffer.append(INTERROGACION);
                            buffer.append(ESPACIO);
                            buffer.append(AND);
                            buffer.append(ESPACIO);
                            buffer.append(INTERROGACION);
                            buffer.append(PAR_DER);
                        } else {
                            buffer.append(axsfQuery.getWhereField9());
                            buffer.append(ESPACIO);
                            buffer.append(BETWEEN);
                            buffer.append(ESPACIO);
                            Iterator it2 = list.iterator();
                            while (it2.hasNext()) {
                                axsfQuery.setSentenceField9OrBetween((String)it2.next(), BETWEEN);
                            }
                            buffer.append(axsfQuery.getSentenceField9OrBetween());
                            buffer.append(axsfQuery.getWhereIdarchField9());
                            buffer.append(INTERROGACION);
                            buffer.append(PAR_DER);
                            axsfQuery.initializeSentenceField9OrBetween();
                        }
                    }
                } else if (field2.getOperator().equals("%")) {
                    buffer.append(PAR_IZQ);
                    if (field2.getFldId().equals("fld9")) {
                        buffer.append(axsfQuery.getWhereField9());
                    } else {
                        buffer.append(field2.getFldId());
                    }
                    buffer.append(ESPACIO);
                    buffer.append(LIKE);
                    buffer.append(ESPACIO);
                    if (field2.getFldId().equals("fld9")) {
                        axsfQuery.setSentenceField9((String)field2.getValue());
                        buffer.append(axsfQuery.getSentenceField9());
                    }
                    buffer.append(INTERROGACION);
                    if (field2.getFldId().equals("fld9")) {
                        buffer.append(")");
                    }
                    buffer.append(PAR_DER);
                } else if (field2.getOperator().equals("dde")) {
                    buffer.append(PAR_IZQ);
                    buffer.append(field2.getFldId());
                    buffer.append(ESPACIO);
                    buffer.append(axsfQuery.getWhereOprDependOfIn());
                    buffer.append(INTERROGACION);
                    buffer.append(axsfQuery.getWhereOprDependOfConnect());
                    buffer.append(")");
                    buffer.append(PAR_DER);
                } else if ("!=".equals(field2.getOperator())) {
                    buffer.append(PAR_IZQ);
                    buffer.append(field2.getFldId());
                    buffer.append(ESPACIO);
                    buffer.append(IS_NULL);
                    buffer.append(ESPACIO);
                    buffer.append(OR);
                    buffer.append(ESPACIO);
                    fld32 = this.generateQueryByField(axsf, axsfQuery, buffer, fld32, field2);
                    buffer.append(PAR_DER);
                } else {
                    fld32 = this.generateQueryByField(axsf, axsfQuery, buffer, fld32, field2);
                }
                if (i < axsfQuery.getFields().size() - 1 && (!field2.getFldId().equals("fld32") || field2.getFldId().equals("fld32") && fld32)) {
                    buffer.append(ESPACIO);
                    if (StringUtils.isNotEmpty((String)field2.getNexo())) {
                        buffer.append(field2.getNexo());
                    } else {
                        buffer.append(AND);
                    }
                    buffer.append(ESPACIO);
                }
                ++i;
                buffer.append(ESPACIO);
            }
            buffer.append(ESPACIO);
            if (orderby) {
                buffer.append(axsfQuery.getOrderBy());
            }
        } else if (axsfQuery != null && orderby) {
            buffer.append(axsfQuery.getOrderBy());
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("createWhereClausule WHERE :" + buffer));
        }
        return buffer.toString();
    }

    private boolean generateQueryByField(AxSf axsf, AxSfQuery axsfQuery, StringBuffer buffer, boolean fld32, AxSfQueryField field) {
        if (axsf != null && axsf.getAttributeClass(field.getFldId()).equals(Date.class)) {
            buffer.append(this.getDateField(field.getFldId(), field.getOperator(), 1, null));
        } else if (!field.getFldId().equals("fld32")) {
            if (field.getFldId().equals("fld9")) {
                buffer.append(axsfQuery.getWhereField9());
            } else {
                buffer.append(field.getFldId());
            }
            buffer.append(field.getOperator());
            if (field.getFldId().equals("fld9")) {
                axsfQuery.setSentenceField9((String)field.getValue());
                buffer.append(axsfQuery.getSentenceField9());
            }
            buffer.append(INTERROGACION);
            if (field.getFldId().equals("fld9")) {
                buffer.append(")");
            }
            fld32 = false;
        } else if (((String)field.getValue()).equals("xxx")) {
            buffer.append(axsfQuery.getWhereDistNotRegister());
            buffer.append(field.getOperator());
            buffer.append(INTERROGACION);
            buffer.append(")");
            fld32 = true;
        }
        return fld32;
    }

    public String getTimeStampField(Date date, int index) {
        String resultString = null;
        resultString = index == 0 ? this.getTimeStampFormat(FORMATTERSTAMP.format(date) + ":00") : (index == 1 ? this.getTimeStampFormat(FORMATTERSTAMP.format(date) + ":59") : (index == 2 ? this.getTimeStampFormat(FORMATTERSTAMPAL.format(date)) : (index == 3 ? this.getTimeStampFormat(FORMATTER.format(date) + " 00:00:00") : this.getTimeStampFormat(FORMATTER.format(date) + " 23:59:00"))));
        return resultString;
    }

    protected abstract String getTimeStampFormat(String var1);

    protected abstract String getDateField(String var1, String var2, int var3, String var4);

    protected abstract String getDateQuery();

    protected void assignAttribute(AxSfQueryField field, AxSf axsfP, PreparedStatement ps, int index) throws SQLException {
        if (field.getFldId().equals("fld9")) {
            this.assignAttribute(field, axsfP, ps, index, field.getBookId());
        } else if (field.getFldId().equals("fld32")) {
            this.assignAttribute(field, axsfP, ps, index, new Integer(0));
        } else {
            this.assignAttribute(field, axsfP, ps, index, field.getValue());
        }
    }

    protected void assignAttribute(AxSfQueryField field, AxSf axsfP, PreparedStatement ps, int index, Object value) throws SQLException {
        if (axsfP.getAttributeClass(field.getFldId()).equals(Date.class) && (field.getOperator().equals("=") || field.getOperator().equals("!=") || field.getOperator().equals("|"))) {
            Date date = null;
            date = field.getOperator().equals("|") ? (Date)value : (Date)field.getValue();
            String dateFormatter = FORMATTER.format(date) + " 00:00:00";
            ps.setObject(index, dateFormatter);
            dateFormatter = FORMATTER.format(date) + " 23:59:59";
            ps.setObject(++index, dateFormatter);
        } else if (axsfP.getAttributeClass(field.getFldId()).equals(Date.class) && field.getOperator().equals("..")) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime((Date)value);
            gc.set(13, 0);
            gc.set(14, 0);
            gc.set(11, 0);
            gc.set(12, 0);
            ps.setObject(index, BBDDUtils.getTimestamp(gc.getTime()));
        } else if (axsfP.getAttributeClass(field.getFldId()).equals(Date.class) && (field.getOperator().equals(">") || field.getOperator().equals("<="))) {
            String dateFormatter = FORMATTER.format((Date)field.getValue()) + " 23:59:59";
            ps.setObject(index, dateFormatter);
        } else if (axsfP.getAttributeClass(field.getFldId()).equals(Date.class)) {
            String dateFormatter = FORMATTER.format((Date)field.getValue()) + " 00:00:00";
            ps.setObject(index, dateFormatter);
        } else {
            ps.setObject(index, value);
        }
    }

    public int getDistributionSize(String sentence, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sentence);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), e);
            throw new SQLException("Error ejecutando [" + sentence + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public int getPersonListSize(String sentence, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        int result = 0;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sentence);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + sentence + "]"), e);
            throw new SQLException("Error ejecutando [" + sentence + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public List getIdArchDistribution(String query, String entidad) throws SQLException {
        ArrayList<Integer> idArchs;
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        idArchs = new ArrayList<Integer>();
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                idArchs.add(new Integer(resultSet.getInt(1)));
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + query + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + query + "]"), e);
            throw new SQLException("Error ejecutando [" + query.toString() + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return idArchs;
    }

    public int insertScrSharedFiles(int fileId, int ownerBookId, int ownerRegId, int bookId, int regId, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_SHAREDFILES);
            statement.setInt(1, fileId);
            statement.setInt(2, ownerBookId);
            statement.setInt(3, ownerRegId);
            statement.setInt(4, bookId);
            statement.setInt(5, regId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_sharedfiles [" + INSERT_SCR_SHAREDFILES + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_sharedfiles [" + INSERT_SCR_SHAREDFILES + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public String getHashDocument(Integer bookId, int fdrid, int pageId, String hash, boolean selDel, String entidad) {
        String result;
        block11 : {
            PreparedStatement statement = null;
            Connection connection = null;
            ResultSet resultSet = null;
            result = null;
            try {
                connection = BBDDUtils.getConnection(entidad);
                if (selDel) {
                    statement = connection.prepareStatement(SELECT_HASH_PAGE);
                    statement.setInt(1, bookId);
                    statement.setInt(2, fdrid);
                    statement.setInt(3, pageId);
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        StringClobType stringClobType = new StringClobType();
                        String[] names = new String[]{"HASH"};
                        Object o = stringClobType.nullSafeGet(resultSet, names, null);
                        if (o == null) continue;
                        result = (String)o;
                    }
                    log.info((Object)("SELECT HASH FROM SCR_PAGEINFO: " + bookId + " " + fdrid + " " + pageId));
                    break block11;
                }
                statement = connection.prepareStatement(UPDATE_HASH_PAGE);
                statement.setInt(2, bookId);
                statement.setInt(3, fdrid);
                statement.setInt(4, pageId);
                StringClobType stringClobType = new StringClobType();
                stringClobType.nullSafeSet(statement, hash, 1);
                statement.executeUpdate();
                log.info((Object)("UPDATE HASH FROM SCR_PAGEINFO: " + bookId + " " + fdrid + " " + pageId));
            }
            catch (SQLException e) {
                if (selDel) {
                    log.warn((Object)("Resulta imposible obtener el hash del documento [" + SELECT_HASH_PAGE + "]"), (Throwable)e);
                    break block11;
                }
                log.warn((Object)("Resulta imposible actualizar el hash del documento [" + DELETE_HASH_PAGE + "]"), (Throwable)e);
            }
            catch (Throwable e) {
                if (selDel) {
                    log.warn((Object)("Resulta imposible obtener el hash del documento [" + SELECT_HASH_PAGE + "]"), e);
                    break block11;
                }
                log.warn((Object)("Resulta imposible actualizar el hash del documento [" + DELETE_HASH_PAGE + "]"), e);
            }
            finally {
                BBDDUtils.close(statement);
                BBDDUtils.close(resultSet);
                BBDDUtils.close(connection);
            }
        }
        return result;
    }

    public void deleteHashDocument(Integer bookId, int fdrid, int pageId, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Object result = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(DELETE_HASH_PAGE);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrid);
            statement.setInt(3, pageId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible eliminar la informaci\u00f3n de la p\u00e1gina [" + DELETE_HASH_PAGE + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible eliminar la informaci\u00f3n de la pagina [" + DELETE_HASH_PAGE + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public ZipInputStream getReportData(int reportId, String temporalDirectory, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Object buffer = null;
        StringBuffer query = new StringBuffer();
        int size = 0;
        try {
            ZipInputStream zis;
            InputStream fin = null;
            query.append("SELECT DATA FROM SCR_REPORTS WHERE ID=" + reportId);
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query.toString());
            buffer = new byte[4096];
            FileOutputStream output = new FileOutputStream(temporalDirectory);
            while (resultSet.next()) {
                fin = resultSet.getBinaryStream(1);
                while ((size = fin.read((byte[])buffer)) != -1) {
                    output.write((byte[])buffer, 0, size);
                }
            }
            output.close();
            ZipInputStream zipInputStream = zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(temporalDirectory)));
            return zipInputStream;
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + query.toString() + "]"), e);
            throw new SQLException("Error ejecutando [" + query.toString() + "]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
    }

    public void saveOrUpdateUserConfig(String result, Integer idUser, int type, String entidad) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            if (type == 0) {
                statement = connection.prepareStatement(INSERT_USER_CONFIG);
                statement.setInt(1, idUser);
                statement.setString(2, result);
                statement.executeUpdate();
            } else {
                statement = connection.prepareStatement(UPDATE_USER_CONFIG);
                statement.setString(1, result);
                statement.setInt(2, idUser);
                statement.executeUpdate();
            }
            log.info((Object)("USER_CONF: " + idUser + " " + result));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible actualizar la configuraci\u00f3n del usuario.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible actualizar la configuraci\u00f3n del usuario.", e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public String getUsrEmail(Integer idUser, String entidad) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        String userEmail = "";
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_SCR_EMAIL_USRLOC);
            statement.setInt(1, idUser);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userEmail = resultSet.getString(1);
            }
            log.info((Object)("User id: " + idUser + " email:" + userEmail));
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible obtener la direcci\u00f3n de correo del usuario." + idUser), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible obtener la direcci\u00f3n de correo del usuario." + idUser), e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return userEmail;
    }

    public String getOficEmail(Integer idOfic, String entidad) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        String oficEmail = "";
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_SCR_EMAIL_OFIC);
            statement.setInt(1, idOfic);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                oficEmail = resultSet.getString(1);
            }
            log.info((Object)("Ofic id: " + idOfic + " email:" + oficEmail));
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible obtener la direcci\u00f3n de correo de la oficina." + idOfic), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible obtener la direcci\u00f3n de correo de la oficina." + idOfic), e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return oficEmail;
    }

    public List getListAddrTel(String entidad, int idAddress) {
        ArrayList<ScrAddrtel> result;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        result = new ArrayList<ScrAddrtel>();
        try {
            con = BBDDUtils.getConnection(entidad);
            ps = con.prepareStatement("select * from scr_addrtel where id=" + idAddress);
            rs = ps.executeQuery();
            while (rs.next()) {
                ScrAddrtel scrAddrtel = new ScrAddrtel();
                scrAddrtel.setId(rs.getInt("id"));
                scrAddrtel.setAddress(rs.getString("address"));
                scrAddrtel.setPreference(rs.getInt("preference"));
                result.add(scrAddrtel);
            }
        }
        catch (Exception e) {
            log.warn((Object)("Resulta imposible obtener la direcci\u00f3n de correo: " + idAddress), (Throwable)e);
        }
        finally {
            BBDDUtils.close(ps);
            BBDDUtils.close(rs);
            BBDDUtils.close(con);
        }
        return result;
    }

    public String findAxSFToCloseSentence(String tableName, String filter) {
        String where = this.createWhereClausule(null, null, filter, false);
        String sentence = MessageFormat.format(AXSF_FINDALL_SENTENCE, tableName, where);
        if (log.isDebugEnabled()) {
            log.debug((Object)("findAxSFToCloseSentence QUERY :" + sentence));
        }
        return sentence;
    }

    public void updateUserConfigIdOficPref(Integer idUser, Integer idOficPref, String entidad) {
        PreparedStatement statement = null;
        Connection connection = null;
        int idOP = idOficPref == null ? 0 : idOficPref;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(UPDATE_USER_CONFIG_IDOFICPREF);
            statement.setInt(1, idOP);
            statement.setInt(2, idUser);
            statement.executeUpdate();
            log.info((Object)("USER_OFIC_PREF: " + idUser + " " + idOP));
        }
        catch (SQLException e) {
            log.warn((Object)"Resulta imposible actualizar la oficina preferente del usuario.", (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)"Resulta imposible actualizar la oficina preferente del usuario.", e);
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public List getScrRegisterInter(Integer bookId, int fdrid, boolean orderByOrd, String entidad) {
        ArrayList<ScrRegisterInter> scrRegInts;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        ScrRegisterInter scrRegisterInter = null;
        scrRegInts = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            String query = SELECT_SCR_REGINT;
            if (orderByOrd) {
                query = query + ORDER_SCR_REGINT;
            }
            statement = connection.prepareStatement(query);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrid);
            resultSet = statement.executeQuery();
            scrRegInts = new ArrayList<ScrRegisterInter>();
            while (resultSet.next()) {
                scrRegisterInter = new ScrRegisterInter();
                scrRegisterInter.setId(new Integer(resultSet.getInt(1)));
                scrRegisterInter.setArchId(new Integer(resultSet.getInt(2)));
                scrRegisterInter.setFdrId(new Integer(resultSet.getInt(3)));
                scrRegisterInter.setName(resultSet.getString(4));
                scrRegisterInter.setPersonId(new Integer(resultSet.getInt(5)));
                scrRegisterInter.setAddressId(new Integer(resultSet.getInt(6)));
                scrRegisterInter.setOrder(new Integer(resultSet.getInt(7)));
                scrRegInts.add(scrRegisterInter);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando SELECT_SCR_REGINT [" + SELECT_SCR_REGINT + "]"), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando SELECT_SCR_REGINT [" + SELECT_SCR_REGINT + "]"), e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return scrRegInts;
    }

    public int insertScrRegInt(int id, int archId, int fdrId, String name, int personId, int addressId, int ord, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_REGINT);
            statement.setInt(1, id);
            statement.setInt(2, archId);
            statement.setInt(3, fdrId);
            statement.setString(4, name);
            statement.setInt(5, personId);
            if (addressId != 0) {
                statement.setInt(6, addressId);
            } else {
                statement.setNull(6, 4);
            }
            statement.setInt(7, ord);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_regint [" + INSERT_SCR_REGINT + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_regint [" + INSERT_SCR_REGINT + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public void deleteScrRegInt(int bookId, int fdrId, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(DELETE_SCR_REGINT);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible eliminar los campos persistentes [" + DELETE_SCR_REGINT + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible eliminar los campos persistentes [" + DELETE_SCR_REGINT + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public String getDescriptionByLocale(Integer id, boolean isScrTypeAdm, boolean isScrCa, String language, String tableNameAux, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        String selectSentence = null;
        String description = null;
        String tableName = null;
        tableName = !language.equals("ca") ? tableNameAux + language.toUpperCase() : tableNameAux + "CT";
        try {
            selectSentence = isScrTypeAdm ? "SELECT DESCRIPTION FROM " + tableName + " WHERE  id=" + id : (isScrCa ? "SELECT MATTER FROM " + tableName + " WHERE  id=" + id : "SELECT NAME FROM " + tableName + " WHERE  id=" + id);
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSentence);
            if (resultSet.next()) {
                description = resultSet.getString(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), e);
            throw new SQLException("Error ejecutando [" + selectSentence + "]");
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return description;
    }

    public List getReportsListByLocale(int reportType, int bookType, String language, String tableNameAux, String entidad) throws SQLException {
        ArrayList<ScrReport> scrReportList;
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        scrReportList = new ArrayList<ScrReport>();
        String selectSentence = null;
        String tableName = null;
        tableName = !language.equals("ca") ? tableNameAux + language.toUpperCase() : tableNameAux + "CT";
        try {
            selectSentence = "SELECT * FROM " + tableName + " WHERE  TYPE_REPORT=" + reportType + " AND TYPE_ARCH=" + bookType;
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSentence);
            while (resultSet.next()) {
                ScrReport scrReport = new ScrReport();
                scrReport.setId(new Integer(resultSet.getInt(1)));
                scrReport.setTypeReport(resultSet.getInt(3));
                scrReport.setTypeArch(resultSet.getInt(4));
                scrReport.setAllArch(resultSet.getInt(5));
                scrReport.setAllOfics(resultSet.getInt(6));
                scrReport.setAllPerfs(resultSet.getInt(7));
                scrReport.setDescription(resultSet.getString(8));
                scrReportList.add(scrReport);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), e);
            throw new SQLException("Error ejecutando [" + selectSentence + "]");
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return scrReportList;
    }

    public Idocvtblctlg getIdocvtblctlg(int id, String language, String tableNameAux, String entidad) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Idocvtblctlg idocvtblctlg = null;
        String selectSentence = null;
        String tableName = null;
        tableName = !language.equals("ca") ? tableNameAux + language.toUpperCase() : tableNameAux + "CT";
        try {
            selectSentence = "SELECT * FROM " + tableName + " WHERE  ID=" + id;
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSentence);
            if (resultSet.next()) {
                idocvtblctlg = new Idocvtblctlg();
                idocvtblctlg.setId(new Integer(resultSet.getInt(1)));
                idocvtblctlg.setName(resultSet.getString(2));
                idocvtblctlg.setBtblid(resultSet.getInt(3));
                idocvtblctlg.setType(resultSet.getInt(4));
                idocvtblctlg.setInfo(resultSet.getString(5));
                idocvtblctlg.setFlags(resultSet.getInt(6));
                idocvtblctlg.setRemarks(resultSet.getString(7));
                idocvtblctlg.setCrtrid(resultSet.getInt(8));
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [" + selectSentence + "]"), e);
            throw new SQLException("Error ejecutando [" + selectSentence + "]");
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return idocvtblctlg;
    }

    public String getDocUID(Integer bookId, Integer fdrId, Integer pageId, String entidad) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        String docUID = "";
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_SCR_PAGEREPOSITORY);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrId);
            statement.setInt(3, pageId);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                docUID = resultSet.getString(1);
            }
            log.info((Object)(" docUID:" + docUID));
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible obtener el DOCUID del documento con bookId= " + bookId + "fdrId= " + fdrId + "pageId= " + pageId), (Throwable)e);
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible obtener el DOCUID del documento con bookId= " + bookId + "fdrId= " + fdrId + "pageId= " + pageId), e);
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return docUID;
    }

    public int insertScrPageRepository(int bookID, int fdrID, int pageID, String docUID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_PAGEREPOSITORY);
            statement.setInt(1, bookID);
            statement.setInt(2, fdrID);
            statement.setInt(3, pageID);
            statement.setString(4, docUID);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_pagerepository [" + INSERT_SCR_PAGEREPOSITORY + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_pagerepository [" + INSERT_SCR_PAGEREPOSITORY + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public void deleteScrPageRepository(int bookId, int fdrId, int pageID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(DELETE_SCR_PAGEREPOSITORY);
            statement.setInt(1, bookId);
            statement.setInt(2, fdrId);
            statement.setInt(3, pageID);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible eliminar la informaci\u00f3n de scrpagerepository[" + DELETE_SCR_PAGEREPOSITORY + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible eliminar la informaci\u00f3n de scrpagerepository[" + DELETE_SCR_PAGEREPOSITORY + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
    }

    public String getDocumentRepositoryUID(String isicresDocUID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        String documentRepositoryUID = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_REPOSITORY_DOCUMENT_ID);
            statement.setInt(1, Integer.parseInt(isicresDocUID));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                documentRepositoryUID = resultSet.getString(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_REPOSITORY_DOCUMENT_ID + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible ejecutat [" + SELECT_REPOSITORY_DOCUMENT_ID + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return documentRepositoryUID;
    }

    public String insertScrDocumentRepository(String documentRepositoryUID, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        String isicresDocUID = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isicresDocUID = String.valueOf(resultSet.getInt(1));
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_DOCUMENTREPOSITORY);
            statement.setInt(1, Integer.parseInt(isicresDocUID));
            statement.setString(2, documentRepositoryUID);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible ejecutar [" + INSERT_SCR_DOCUMENTREPOSITORY + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible ejecutar [" + INSERT_SCR_DOCUMENTREPOSITORY + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return isicresDocUID;
    }

    public Integer getRepositoryByBookType(Integer bookType, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Integer result = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_REPOSITORY_BOOK_TYPE);
            statement.setInt(1, bookType);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = new Integer(resultSet.getInt(1));
            }
            BBDDUtils.close(resultSet);
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public String getRepositoryConfiguration(Integer id, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        String result = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(SELECT_REPOSITORY_CONFIGURATION_DATA);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible ejecutar [" + SELECT_NEXT_ID_SCR_DOCUMENTREPOSITORY + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(resultSet);
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public int insertScrDocLocator(int bookId, int folderId, int pageID, String locator, String entidad) throws SQLException {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.prepareStatement(INSERT_SCR_DOCLOCATOR);
            statement.setInt(1, bookId);
            statement.setInt(2, folderId);
            statement.setInt(3, pageID);
            statement.setString(4, locator);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            log.warn((Object)("Resulta imposible insertar scr_doclocator [" + INSERT_SCR_DOCLOCATOR + "]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Resulta imposible insertar scr_doclocator [" + INSERT_SCR_DOCLOCATOR + "]"), e);
            throw new SQLException(e.getMessage());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(connection);
        }
        return 1;
    }

    public Timestamp getMaxDateClose(Integer bookId, String entidad, Integer oficId) throws SQLException {
        Statement statement = null;
        Connection connection = null;
        ResultSet resultSet = null;
        Timestamp result = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT MAX(FLD2) FROM A" + bookId + "SF WHERE FLD6=5 and FLD5=" + oficId);
            if (resultSet.next()) {
                result = resultSet.getTimestamp(1);
            }
        }
        catch (SQLException e) {
            log.warn((Object)("Error ejecutando [SELECT MAX(FLD2) FROM A" + bookId + "SF WHERE FLD6=5 and FLD5=" + oficId + " ]"), (Throwable)e);
            throw e;
        }
        catch (Throwable e) {
            log.warn((Object)("Error ejecutando [SELECT MAX(FLD2) FROM A" + bookId + "SF WHERE FLD6=5]"), e);
            throw new SQLException("Error ejecutando [SELECT MAX(FLD2) FROM A" + bookId + "SF WHERE FLD6=5 and FLD5=" + oficId + " ]");
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }

    public String getFieldsTableTemporalDistributionOrderBy() {
        return "(BOOKID, FDRID, FLD1, FLD2, FLD8, ASUNTO, RESUMEN, BOOKNAME, FLD8_TEXT, ASUNTO_TEXT, DIST_ID, DIST_DATE, DIST_TYPE_ORIG, DIST_ID_ORIG, DIST_TYPE_DEST, DIST_ID_DEST, DIST_STATE, DIST_STATE_DATE, DIST_MESSAGE)";
    }

    public String createQueryForTableTemporalDistributionOrderBy(Integer bookId, boolean isInBook, String language) {
        StringBuffer result = new StringBuffer();
        String camposAxSF = isInBook ? "AXSF.FDRID, AXSF.FLD1, AXSF.FLD2, AXSF.FLD8, AXSF.FLD16, AXSF.FLD17" : "AXSF.FDRID, AXSF.FLD1, AXSF.FLD2, AXSF.FLD8, AXSF.FLD12, AXSF.FLD13";
        result.append("SELECT ").append(bookId).append(" , ").append(camposAxSF).append(" , ").append(this.getSelectAditionFieldsDistribution(isInBook, bookId, language)).append(", DISTREG.ID, DISTREG.DIST_DATE, DISTREG.TYPE_ORIG, DISTREG.ID_ORIG, DISTREG.TYPE_DEST, DISTREG.ID_DEST, DISTREG.STATE, DISTREG.STATE_DATE, DISTREG.MESSAGE");
        result.append(" FROM A" + bookId + "SF AXSF, SCR_DISTREG DISTREG ");
        result.append("WHERE AXSF.FDRID = DISTREG.ID_FDR AND ").append(bookId).append(" = DISTREG.ID_ARCH");
        return result.toString();
    }

    public String getSelectAditionFieldsDistribution(boolean isInBook, Integer bookID, String language) {
        StringBuffer result = new StringBuffer();
        result.append(this.getQueryNameBook(bookID, language));
        result.append(", ");
        result.append(this.getQueryDescriptDestino(language));
        result.append(", ");
        result.append(this.getQueryDescriptTipoAsunto(isInBook, language));
        return result.toString();
    }

    private String getQueryDescriptTipoAsunto(boolean isInBook, String language) {
        StringBuffer result = new StringBuffer();
        String getCampoConsulta = Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/QueryResultsTableRepresentation/SubjectTypes/@name") ? "MATTER" : "CODE";
        result.append("(SELECT ").append(getCampoConsulta).append(" FROM ");
        if (!StringUtils.equalsIgnoreCase((String)language, (String)"es")) {
            result.append("SCR_CA_").append(language.toUpperCase());
        } else {
            result.append("SCR_CA");
        }
        result.append(" B WHERE B.ID=AXSF.").append(isInBook ? "FLD16" : "FLD12").append(") AS ASUNTO_TEXT");
        return result.toString();
    }

    private String getQueryDescriptDestino(String language) {
        StringBuffer result = new StringBuffer();
        if (Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/QueryResultsTableRepresentation/AdministrativeUnits/@abbreviation")) {
            result.append("(SELECT acron FROM ");
        } else if (Configurator.getInstance().getPropertyBoolean("/ISicres-Configuration/ISicres-DesktopWeb/QueryResultsTableRepresentation/AdministrativeUnits/@name")) {
            result.append("(SELECT name FROM ");
        } else {
            result.append("(SELECT code FROM ");
        }
        if (!StringUtils.equalsIgnoreCase((String)language, (String)"es")) {
            result.append("SCR_ORGS_").append(language.toUpperCase());
        } else {
            result.append("SCR_ORGS");
        }
        result.append(" B WHERE B.ID=AXSF.FLD8) AS FLD8_TEXT");
        return result.toString();
    }

    private String getQueryNameBook(Integer bookID, String language) {
        StringBuffer result = new StringBuffer();
        result.append("(SELECT name FROM ");
        if (!StringUtils.equalsIgnoreCase((String)language, (String)"es")) {
            result.append("IDOCARCHHDR_").append(language.toUpperCase());
        } else {
            result.append("IDOCARCHHDR");
        }
        result.append(" IDOC WHERE IDOC.ID=").append(bookID).append(") AS BOOKNAME");
        return result.toString();
    }

    public List getListDistributionOrderBy(int firstRow, int maxResults, String entidad, String orderBy, String tableName) throws SQLException {
        ArrayList<ScrDistreg> result;
        result = new ArrayList<ScrDistreg>();
        ScrDistreg scrDistReg = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = BBDDUtils.getConnection(entidad);
            StringBuffer query = new StringBuffer();
            query.append("SELECT * FROM ").append(tableName).append(" ORDER BY ").append(orderBy);
            statement = connection.prepareStatement(query.toString(), 1004, 1007);
            resultSet = statement.executeQuery();
            resultSet.absolute(firstRow + 1);
            resultSet.previous();
            for (int numRow = 0; resultSet.next() && numRow < maxResults; ++numRow) {
                scrDistReg = new ScrDistreg();
                scrDistReg.setId(resultSet.getInt("DIST_ID"));
                scrDistReg.setIdArch(resultSet.getInt("BOOKID"));
                scrDistReg.setIdFdr(resultSet.getInt("FDRID"));
                scrDistReg.setDistDate(resultSet.getTimestamp("DIST_DATE"));
                scrDistReg.setTypeOrig(resultSet.getInt("DIST_TYPE_ORIG"));
                scrDistReg.setIdOrig(resultSet.getInt("DIST_ID_ORIG"));
                scrDistReg.setTypeDest(resultSet.getInt("DIST_TYPE_DEST"));
                scrDistReg.setIdDest(resultSet.getInt("DIST_ID_DEST"));
                scrDistReg.setState(resultSet.getInt("DIST_STATE"));
                scrDistReg.setStateDate(resultSet.getTimestamp("DIST_STATE_DATE"));
                scrDistReg.setMessage(resultSet.getString("DIST_MESSAGE"));
                result.add(scrDistReg);
            }
        }
        catch (SQLException e) {
            StringBuffer sb = new StringBuffer();
            sb.append("Error obteniendo la distribuci\u00f3n a partir de la tabla: ").append(tableName).append(" y con el orden ").append(orderBy);
            log.warn((Object)sb.toString());
            throw e;
        }
        catch (Throwable e) {
            StringBuffer sb = new StringBuffer();
            sb.append("Error obteniendo la distribuci\u00f3n a partir de la tabla: ").append(tableName).append(" y con el orden ").append(orderBy);
            log.warn((Object)sb.toString());
            throw new SQLException(sb.toString());
        }
        finally {
            BBDDUtils.close(statement);
            BBDDUtils.close(resultSet);
            BBDDUtils.close(connection);
        }
        return result;
    }
}
