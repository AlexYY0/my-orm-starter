package club.emperorws.orm.starter.session.proxy;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.session.SqlSessionFactory;
import club.emperorws.orm.session.defaults.DefaultSqlSessionFactory;

import static club.emperorws.orm.starter.util.Assert.notNull;

/**
 * 动态代理后可直接使用的SqlSessionFactory单例Bean
 *
 * @author: EmperorWS
 * @date: 2023/5/15 18:17
 * @description: SqlSessionFactoryProxyBean: 动态代理后可直接使用的SqlSessionFactory单例Bean
 */
public class SqlSessionFactoryProxyBean {

    private Configuration configuration;

    private SqlSessionFactory sqlSessionFactory;

    private SqlSessionProxyBean sqlSessionProxyBean;

    /**
     * 单例对象
     */
    private static SqlSessionFactoryProxyBean INSTANCE = new SqlSessionFactoryProxyBean();

    public static SqlSessionFactoryProxyBean me() {
        return INSTANCE;
    }

    /**
     * 初始化方法，单线程只执行一次，所以不担心并发问题
     */
    public void init(Configuration configuration) {
        INSTANCE.configuration = configuration;
        INSTANCE.sqlSessionFactory = INSTANCE.buildSqlSessionFactory(configuration);
        INSTANCE.createSqlSession(INSTANCE.sqlSessionFactory);
    }

    public SqlSessionFactory buildSqlSessionFactory(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }

    public void createSqlSession(SqlSessionFactory sqlSessionFactory) {
        if (this.sqlSessionProxyBean == null || sqlSessionFactory != this.sqlSessionProxyBean.getSqlSessionFactory()) {
            this.sqlSessionProxyBean = createSqlSessionProxyBean(sqlSessionFactory);
        }
    }

    public SqlSessionProxyBean createSqlSessionProxyBean(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionProxyBean(sqlSessionFactory);
    }

    /**
     * 获取SqlSessionFactory
     *
     * @return SqlSessionFactory
     */
    public SqlSessionFactory getObject() {
        return sqlSessionFactory;
    }

    public void setSqlSessionTemplate(SqlSessionProxyBean sqlSessionProxyBean) {
        this.sqlSessionProxyBean = sqlSessionProxyBean;
        this.sqlSessionFactory = sqlSessionProxyBean.getSqlSessionFactory();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public final SqlSessionFactory getSqlSessionFactory() {
        return (this.sqlSessionProxyBean != null ? this.sqlSessionProxyBean.getSqlSessionFactory() : null);
    }

    public SqlSession getSqlSession() {
        return this.sqlSessionProxyBean;
    }

    public SqlSessionProxyBean getSqlSessionProxyBean() {
        return this.sqlSessionProxyBean;
    }

    protected void checkConfig() {
        notNull(this.sqlSessionProxyBean, "Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
    }
}
