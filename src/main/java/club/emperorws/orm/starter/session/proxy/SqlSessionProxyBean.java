package club.emperorws.orm.starter.session.proxy;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.mapping.RowBounds;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.result.BatchResult;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.session.ExecutorType;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.session.SqlSessionFactory;
import club.emperorws.orm.starter.util.SqlSessionUtils;
import club.emperorws.orm.util.ExceptionUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static club.emperorws.orm.starter.util.Assert.notNull;
import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * 动态代理后可直接使用的SqlSession单例Bean
 *
 * @author: EmperorWS
 * @date: 2023/5/15 18:14
 * @description: SqlSessionProxyBean: 动态代理后可直接使用的SqlSession单例Bean
 */
public class SqlSessionProxyBean implements SqlSession {

    private final SqlSessionFactory sqlSessionFactory;

    private final ExecutorType executorType;

    private final SqlSession sqlSessionProxy;

    public SqlSessionProxyBean(SqlSessionFactory sqlSessionFactory) {
        this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }

    public SqlSessionProxyBean(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {

        notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
        notNull(executorType, "Property 'executorType' is required");

        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = executorType;
        this.sqlSessionProxy = (SqlSession) newProxyInstance(SqlSessionFactory.class.getClassLoader(), new Class[]{SqlSession.class}, new SqlSessionProxy());
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return this.sqlSessionFactory;
    }

    public ExecutorType getExecutorType() {
        return this.executorType;
    }

    /**
     * 返回单行值的结果（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return sql执行的单个返回结果
     */
    @Override
    public <T> T selectOne(SqlSource sqlSource) {
        return this.sqlSessionProxy.selectOne(sqlSource);
    }

    /**
     * 返回单行值的结果（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return sql执行的单个返回结果
     */
    @Override
    public <T> T selectOne(SqlSource sqlSource, Object parameter) {
        return this.sqlSessionProxy.selectOne(sqlSource, parameter);
    }

    /**
     * 返回多行值的结果（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回List集合
     */
    @Override
    public <E> List<E> selectList(SqlSource sqlSource) {
        return this.sqlSessionProxy.selectList(sqlSource);
    }

    /**
     * 返回多行值的结果（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回List集合
     */
    @Override
    public <E> List<E> selectList(SqlSource sqlSource, Object parameter) {
        return this.sqlSessionProxy.selectList(sqlSource, parameter);
    }

    /**
     * 返回多行值的结果（有请求参数、内存分页信息）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param rowBounds 内存分页信息
     * @return 返回List集合
     */
    @Override
    public <E> List<E> selectList(SqlSource sqlSource, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectList(sqlSource, parameter, rowBounds);
    }

    /**
     * 返回多行值的结果List--->转为Map（没有请求参数）
     * Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
     *
     * @param sqlSource 执行sql的语句
     * @param mapKey    哪一个属性作为键
     * @return 返回map
     */
    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, String mapKey) {
        return this.sqlSessionProxy.selectMap(sqlSource, mapKey);
    }

    /**
     * 返回多行值的结果List--->转为Map（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param mapKey    哪一个属性作为键
     * @return 返回map
     */
    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey) {
        return this.sqlSessionProxy.selectMap(sqlSource, parameter, mapKey);
    }

    /**
     * 返回多行值的结果List--->转为Map（有请求参数、内存分页信息）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param mapKey    哪一个属性作为键
     * @param rowBounds 内存分页信息
     * @return 返回map
     */
    @Override
    public <K, V> Map<K, V> selectMap(SqlSource sqlSource, Object parameter, String mapKey, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectMap(sqlSource, parameter, mapKey, rowBounds);
    }

    /**
     * 游标查询，暂不实现ignore
     *
     * @param sqlSource 执行sql的语句
     * @return 返回游标对象
     */
    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource) {
        return this.sqlSessionProxy.selectCursor(sqlSource);
    }

    /**
     * 游标查询，暂不实现ignore
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回游标对象
     */
    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter) {
        return this.sqlSessionProxy.selectCursor(sqlSource, parameter);
    }

    /**
     * 游标查询，暂不实现ignore
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param rowBounds 内存分页信息
     * @return 返回游标对象
     */
    @Override
    public <T> Cursor<T> selectCursor(SqlSource sqlSource, Object parameter, RowBounds rowBounds) {
        return this.sqlSessionProxy.selectCursor(sqlSource, parameter, rowBounds);
    }

    /**
     * 没有返回值的查询
     * using a {@code ResultHandler}.
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param handler   ResultHandler每行结果的处理器
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, Object parameter, ResultHandler handler) {
        this.sqlSessionProxy.select(sqlSource, parameter, handler);
    }

    /**
     * Retrieve a single row mapped from the statement
     * using a {@code ResultHandler}.
     *
     * @param sqlSource 执行sql的语句
     * @param handler   ResultHandler每行结果的处理器
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, ResultHandler handler) {
        this.sqlSessionProxy.select(sqlSource, handler);
    }

    /**
     * 没有返回值的查询
     * using a {@code ResultHandler} and {@code RowBounds}.
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @param rowBounds RowBound instance to limit the query results
     * @param handler   ResultHandler每行结果的处理器
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void select(SqlSource sqlSource, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        this.sqlSessionProxy.select(sqlSource, parameter, rowBounds, handler);
    }

    /**
     * 执行insert语句（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回insert执行受影响的行数
     */
    @Override
    public int insert(SqlSource sqlSource) {
        return this.sqlSessionProxy.insert(sqlSource);
    }

    /**
     * 执行insert语句（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回insert执行受影响的行数
     */
    @Override
    public int insert(SqlSource sqlSource, Object parameter) {
        return this.sqlSessionProxy.insert(sqlSource, parameter);
    }

    /**
     * 执行update语句（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回update执行受影响的行数
     */
    @Override
    public int update(SqlSource sqlSource) {
        return this.sqlSessionProxy.update(sqlSource);
    }

    /**
     * 执行update语句（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回update执行受影响的行数
     */
    @Override
    public int update(SqlSource sqlSource, Object parameter) {
        return this.sqlSessionProxy.update(sqlSource, parameter);
    }

    /**
     * 执行delete语句（没有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @return 返回delete执行受影响的行数
     */
    @Override
    public int delete(SqlSource sqlSource) {
        return this.sqlSessionProxy.delete(sqlSource);
    }

    /**
     * 执行update语句（有请求参数）
     *
     * @param sqlSource 执行sql的语句
     * @param parameter 整合的请求参数
     * @return 返回delete执行受影响的行数
     */
    @Override
    public int delete(SqlSource sqlSource, Object parameter) {
        return this.sqlSessionProxy.delete(sqlSource, parameter);
    }

    /**
     * Flushes batch statements and commits database connection.
     * Note that database connection will not be committed if no updates/deletes/inserts were called.
     * To force the commit call {@link SqlSession#commit(boolean)}
     */
    @Override
    public void commit() {
        //TransactionManagerProxyBean.me().commit();
        throw new UnsupportedOperationException("Manual commit is not allowed over a System managed SqlSession");
    }

    /**
     * Flushes batch statements and commits database connection.
     *
     * @param force forces connection commit
     */
    @Override
    public void commit(boolean force) {
        //TransactionManagerProxyBean.me().commit(force);
        throw new UnsupportedOperationException("Manual commit is not allowed over a System managed SqlSession");
    }

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     * To force the rollback call {@link SqlSession#rollback(boolean)}
     */
    @Override
    public void rollback() {
        //TransactionManagerProxyBean.me().rollback();
        throw new UnsupportedOperationException("Manual rollback is not allowed over a System managed SqlSession");
    }

    /**
     * Discards pending batch statements and rolls database connection back.
     * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
     *
     * @param force forces connection rollback
     */
    @Override
    public void rollback(boolean force) {
        //TransactionManagerProxyBean.me().rollback(force);
        throw new UnsupportedOperationException("Manual rollback is not allowed over a System managed SqlSession");
    }

    /**
     * Flushes batch statements.
     *
     * @return BatchResult list of updated records
     */
    @Override
    public List<BatchResult> flushStatements() {
        return this.sqlSessionProxy.flushStatements();
    }

    /**
     * Closes the session.
     */
    @Override
    public void close() {
        //TransactionManagerProxyBean.me().close();
        throw new UnsupportedOperationException("Manual close is not allowed over a System managed SqlSession");
    }

    /**
     * 返回单例配置文件信息Configuration
     *
     * @return Configuration
     */
    @Override
    public Configuration getConfiguration() {
        return this.sqlSessionProxy.getConfiguration();
    }

    /**
     * 获取需要的Mapper对象
     *
     * @param type Mapper的class类型
     * @return Mapper对象
     */
    @Override
    public <T> T getMapper(Class<T> type) {
        return getConfiguration().getMapper(type, this);
    }

    /**
     * 获取数据库连接
     * <p>ignore：connection获取后，会立马被代理关闭，所以不可用</p>
     *
     * @return Connection
     */
    @Override
    public Connection getConnection() {
        return this.sqlSessionProxy.getConnection();
    }

    /**
     * SqlSession的动态代理，主要影响的是SqlSession的获取
     */
    private class SqlSessionProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession sqlSession = SqlSessionUtils.getSqlSession(SqlSessionProxyBean.this.sqlSessionFactory, SqlSessionProxyBean.this.executorType);
            try {
                Object result = method.invoke(sqlSession, args);
                //没有被统一管理的SqlSession（没有被事务管理器管理的Session）手动commit
                if (!SqlSessionUtils.isSqlSessionManaged(sqlSession, SqlSessionProxyBean.this.sqlSessionFactory)) {
                    sqlSession.commit(true);
                }
                return result;
            } catch (Throwable t) {
                //没有被管理的SqlSession，发生异常时，没有commit，直接close，dbcp数据库连接池默认会直接rollback
                throw ExceptionUtil.unwrapThrowable(t);
            } finally {
                //没有被事务管理器管理，则直接关闭SqlSession
                if (sqlSession != null) {
                    SqlSessionUtils.closeSqlSession(sqlSession, SqlSessionProxyBean.this.sqlSessionFactory);
                }
            }
        }
    }
}
