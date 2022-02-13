package me.b7w.calcite.showcase;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.avatica.InternalProperty;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

@Component
public class CalciteClient {

    private static final Logger LOG = LoggerFactory.getLogger(CalciteClient.class);

    private final Map<String, DataSource> dataSources;
    private final NamedParameterJdbcTemplate template;

    public CalciteClient(Map<String, DataSource> dataSources) throws ClassNotFoundException {
        this.dataSources = dataSources;
        Class.forName("org.apache.calcite.jdbc.Driver");
        AbstractDataSource dataSource = new AbstractDataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                Properties info = connectionProperties();
                Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
                registerTargetDataSources((CalciteConnection) connection);
                return connection;
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return getConnection();
            }
        };
        template = new NamedParameterJdbcTemplate(dataSource);
    }

    public <T> List<T> queryJdbc(String sql, RowMapper<T> mapper) {
        Properties info = connectionProperties();
        try (Connection connection = DriverManager.getConnection("jdbc:calcite:", info);) {
            CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
            registerTargetDataSources(calciteConnection);
            try (Statement statement = calciteConnection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    RowMapperResultSetExtractor<T> extractor = new RowMapperResultSetExtractor<>(mapper);
                    return extractor.extractData(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new UncategorizedSQLException("Calcite", e.getSQLState(), e);
        }
    }

    public <T> Stream<T> queryTemplate(String sql, RowMapper<T> mapper) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        return template.queryForStream(sql, parameters, mapper);
    }

    /**
     * Some magic for correct SQL parsing
     */
    private Properties connectionProperties() {
        Properties info = new Properties();
        info.put(InternalProperty.CASE_SENSITIVE.name(), Boolean.FALSE.toString());
        info.put(InternalProperty.UNQUOTED_CASING.name(), Casing.TO_LOWER.name());
        info.put(InternalProperty.QUOTED_CASING.name(), Casing.TO_LOWER.name());
        info.put(InternalProperty.QUOTING.name(), Quoting.DOUBLE_QUOTE.name());
        return info;
    }

    private void registerTargetDataSources(CalciteConnection connection) throws SQLException {
        SchemaPlus rootSchema = connection.getRootSchema();
        for (Map.Entry<String, DataSource> ds : dataSources.entrySet()) {
            HikariDataSource dataSource = ((HikariDataSource) ds.getValue());
            // Test connection, otherwise getting same errors but deep in calcite
            try (Connection c = dataSource.getConnection()) {
                if (!c.isValid(2)) {
                    throw new RuntimeException(String.format("Datasource %s not properly setup", ds.getKey()));
                }
            }
            String name = ds.getKey().toLowerCase(Locale.ROOT).replace("datasource", "");
            LOG.debug("Register schema {} for datasource {}", name, dataSource);
            JdbcSchema schema = JdbcSchema.create(rootSchema, name, dataSource, null, null);
            rootSchema.add(name, schema);
        }
    }

}
