package club.emperorws.orm.starter.session.proxy;

import club.emperorws.orm.session.ExecutorType;
import club.emperorws.orm.session.SqlSession;

import java.util.concurrent.atomic.LongAdder;

import static club.emperorws.orm.starter.util.Assert.notNull;


/**
 * SqlSession的存储对象
 *
 * @author: EmperorWS
 * @date: 2023/5/16 10:42
 * @description: SqlSessionHolder: SqlSession的存储对象
 */
public class SqlSessionHolder {

    /**
     * 该SqlSession是否由事务管理器管理
     */
    private boolean synchronizedWithTransaction = false;

    private final SqlSession sqlSession;

    private final ExecutorType executorType;

    private final LongAdder referenceCount = new LongAdder();

    public SqlSessionHolder(SqlSession sqlSession, ExecutorType executorType) {

        notNull(sqlSession, "SqlSession must not be null");
        notNull(executorType, "ExecutorType must not be null");

        this.sqlSession = sqlSession;
        this.executorType = executorType;
    }

    public void setSynchronizedWithTransaction(boolean synchronizedWithTransaction) {
        this.synchronizedWithTransaction = synchronizedWithTransaction;
    }

    public boolean isSynchronizedWithTransaction() {
        return this.synchronizedWithTransaction;
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public ExecutorType getExecutorType() {
        return executorType;
    }

    public void requested() {
        referenceCount.increment();
    }

    public void released() {
        referenceCount.decrement();
    }

    public boolean isOpen() {
        return this.referenceCount.sum() > 0;
    }

    public void reset() {
        this.referenceCount.reset();
    }

    public void close() {
        reset();
        sqlSession.close();
    }
}
