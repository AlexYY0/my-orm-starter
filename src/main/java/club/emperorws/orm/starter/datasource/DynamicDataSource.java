package club.emperorws.orm.starter.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 多数据源
 *
 * @author: EmperorWS
 * @date: 2023/5/17 18:16
 * @description: DynamicDataSource: 多数据源
 */
public class DynamicDataSource implements DataSource {

    /**
     * 只在创建对象的时候初始化，所以不会出现并发问题
     */
    private Map<Object, DataSource> dataSourceMap;

    /**
     * 默认的数据源
     */
    private String defaultDataSource;

    public DynamicDataSource(Map<Object, DataSource> dataSourceMap) {
        this(dataSourceMap, "master");
    }

    public DynamicDataSource(Map<Object, DataSource> dataSourceMap, String defaultDataSource) {
        this.dataSourceMap = dataSourceMap;
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * 具体决定使用哪个数据源，找出数据源的key
     *
     * @return 使用的数据源key
     */
    private Object determineCurrentLookupKey() {
        String dbType = DataSourceContextHolder.getDbType();
        return dbType == null ? defaultDataSource : dbType;
    }

    public DataSource getDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        return dataSourceMap.get(lookupKey);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDataSource().isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDataSource().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDataSource().setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDataSource().getParentLogger();
    }
}
