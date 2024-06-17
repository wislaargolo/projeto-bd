package br.ufrn.imd.bd.dao.util;

import br.ufrn.imd.bd.dao.AbstractDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;


public class ResultSetUtil {

    public static <T> T getValue(ResultSet rs, String columnName, Class<T> type) throws SQLException {
        if (!hasColumn(rs, columnName) || rs.getObject(columnName) == null) {
            return null;
        }
        return type.cast(rs.getObject(columnName));
    }

    public static <T> T getEntity(ResultSet rs, AbstractDAO<T, Long> dao, String prefix, String idColumnName) throws SQLException {
        String fullColumnName = prefix + idColumnName;
        if (!hasColumn(rs, fullColumnName) || rs.getObject(fullColumnName) == null) {
            return null;
        }
        return dao.mapearResultado(rs, prefix);
    }

    public static <T> T getEntity(ResultSet rs, AbstractDAO<T, Long> dao, String idColumnName) throws SQLException {
        if (!hasColumn(rs, idColumnName) || rs.getObject(idColumnName) == null) {
            return null;
        }
        return dao.mapearResultado(rs);
    }

    public static boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static <E extends Enum<E>> E getEnumValue(ResultSet rs, String columnName, Class<E> enumType) throws SQLException {
        String value = getValue(rs, columnName, String.class);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean getBooleanFromInteger(ResultSet rs, String columnName, String prefixo) throws SQLException {
        Integer value = getValue(rs, prefixo + columnName, Integer.class);
        return value != null && value == 1;
    }

    public static LocalDate getLocalDate(ResultSet rs, String columnName) throws SQLException {
        if (!hasColumn(rs, columnName) || rs.getObject(columnName) == null) {
            return null;
        }
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}

