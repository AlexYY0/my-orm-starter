package club.emperorws.orm.starter.util;

import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;
import club.emperorws.orm.session.ExecutorType;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.session.SqlSessionFactory;
import club.emperorws.orm.starter.session.proxy.SqlSessionHolder;
import club.emperorws.orm.starter.transaction.TransactionSynchronization;

/**
 * SqlSession的相关工具
 *
 * @author: EmperorWS
 * @date: 2023/5/16 11:32
 * @description: SqlSessionUtils: SqlSession的相关工具
 */
public class SqlSessionUtils {

    private static final Log logger = LogFactory.getLog(SqlSessionUtils.class);

    private static final String NO_EXECUTOR_TYPE_SPECIFIED = "No ExecutorType specified";
    private static final String NO_SQL_SESSION_FACTORY_SPECIFIED = "No SqlSessionFactory specified";
    private static final String NO_SQL_SESSION_SPECIFIED = "No SqlSession specified";

    private SqlSessionUtils() {
        // do nothing
    }

    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        ExecutorType executorType = sessionFactory.getConfiguration().getDefaultExecutorType();
        return getSqlSession(sessionFactory, executorType);
    }

    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, ExecutorType executorType) {
        Assert.notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);
        Assert.notNull(executorType, NO_EXECUTOR_TYPE_SPECIFIED);

        SqlSessionHolder holder = (SqlSessionHolder) OrmContextHolder.getResource(sessionFactory);

        SqlSession session = sessionHolder(executorType, holder);
        if (session != null) {
            return session;
        }
        session = sessionFactory.openSession(executorType);
        registerSessionHolder(sessionFactory, executorType, session);
        return session;
    }

    public static void closeSqlSession(SqlSessionFactory sessionFactory) {
        SqlSessionHolder holder = (SqlSessionHolder) OrmContextHolder.getResource(sessionFactory);
        if (holder != null) {
            holder.released();
        }
    }

    public static void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {
        Assert.notNull(session, NO_SQL_SESSION_SPECIFIED);
        Assert.notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);

        SqlSessionHolder holder = (SqlSessionHolder) OrmContextHolder.getResource(sessionFactory);
        if ((holder != null) && (holder.getSqlSession() == session)) {
            holder.released();
        } else {
            session.close();
        }
    }

    /**
     * 判断SqlSession是否为统一管理
     *
     * @param session        原SqlSession
     * @param sessionFactory SqlSessionFactory
     * @return SqlSession是否为统一管理
     */
    public static boolean isSqlSessionManaged(SqlSession session, SqlSessionFactory sessionFactory) {
        Assert.notNull(session, NO_SQL_SESSION_SPECIFIED);
        Assert.notNull(sessionFactory, NO_SQL_SESSION_FACTORY_SPECIFIED);

        SqlSessionHolder holder = (SqlSessionHolder) OrmContextHolder.getResource(sessionFactory);
        return (holder != null) && (holder.getSqlSession() == session);
    }

    /*************************************************private function***************************************************/

    private static SqlSession sessionHolder(ExecutorType executorType, SqlSessionHolder holder) {
        SqlSession session = null;
        //SqlSession由事务管理器管理
        if (holder != null && holder.isSynchronizedWithTransaction()) {
            if (holder.getExecutorType() != executorType) {
                throw new OrmException("Cannot change the ExecutorType when there is an existing transaction");
            }
            holder.requested();
            logger.debug(() -> "Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction");
            session = holder.getSqlSession();
        }
        //SqlSession没有事务管理器管理
        return session;
    }

    private static void registerSessionHolder(SqlSessionFactory sessionFactory, ExecutorType executorType, SqlSession session) {
        SqlSessionHolder holder;
        //是事务的。。。才进入
        if (OrmContextHolder.isActualTransactionActive()) {
            if (!OrmContextHolder.isSynchronizationActive()) {
                OrmContextHolder.initSynchronization();
            }
            logger.debug(() -> "Registering transaction synchronization for SqlSession [" + session + "]");
            holder = new SqlSessionHolder(session, executorType);
            OrmContextHolder.bindResource(sessionFactory, holder);
            OrmContextHolder.registerSynchronization(new SqlSessionSynchronization(holder, sessionFactory));
            holder.setSynchronizedWithTransaction(true);
            holder.requested();
        } else {
            logger.debug(() -> "SqlSession [" + session + "] was not registered for synchronization because synchronization is not active");
        }
    }

    /*************************************************inner class***************************************************/

    /**
     * SqlSession事务管理链对象
     */
    private static final class SqlSessionSynchronization implements TransactionSynchronization {

        private final SqlSessionHolder holder;

        private final SqlSessionFactory sessionFactory;

        private boolean holderActive = true;

        public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory) {
            Assert.notNull(holder, "Parameter 'holder' must be not null");
            Assert.notNull(sessionFactory, "Parameter 'sessionFactory' must be not null");

            this.holder = holder;
            this.sessionFactory = sessionFactory;
        }

        /**
         * 事务排序，暂时不需要，ignore
         *
         * @return 排序
         */
        @Override
        public int getOrder() {
            return 1000 - 1;
        }

        @Override
        public void commit() {
            if (OrmContextHolder.isActualTransactionActive()) {
                this.holder.getSqlSession().commit();
            }
        }

        @Override
        public void commit(boolean force) {
            if (OrmContextHolder.isActualTransactionActive()) {
                this.holder.getSqlSession().commit(force);
            }
        }

        @Override
        public void rollback() {
            if (OrmContextHolder.isActualTransactionActive()) {
                this.holder.getSqlSession().rollback();
            }
        }

        @Override
        public void rollback(boolean force) {
            if (OrmContextHolder.isActualTransactionActive()) {
                this.holder.getSqlSession().rollback(force);
            }
        }

        @Override
        public void close() {
            if (this.holderActive && !this.holder.isOpen()) {
                OrmContextHolder.unbindResource(sessionFactory);
                this.holderActive = false;
                this.holder.getSqlSession().close();
            }
            //todo 如果上一个if没有进入，应该怎么办
            this.holder.reset();
        }
    }
}
