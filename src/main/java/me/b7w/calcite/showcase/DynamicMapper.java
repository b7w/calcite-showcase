package me.b7w.calcite.showcase;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

class DynamicMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
            String name = rs.getMetaData().getColumnName(i);
            Object value = rs.getObject(i);
            row.put(name, value);
        }
        return row;
    }
}
