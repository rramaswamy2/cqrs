package com.cqrs.eventstore.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class EventMapper implements ResultSetMapper<Map> {

    public Map map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        try {
            Map rowMap = new HashMap();
            rowMap.put("type", r.getString("type"));
            rowMap.put("data", new String(r.getBytes("data")));
            return rowMap;
        } catch (Exception e) {
            throw new SQLException("Data expected to be in valid JSON format", e);
        }
    }
}
