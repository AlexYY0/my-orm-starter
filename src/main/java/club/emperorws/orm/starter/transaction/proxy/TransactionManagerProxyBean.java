package club.emperorws.orm.starter.transaction.proxy;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;
import club.emperorws.orm.starter.transaction.TransactionSynchronization;
import club.emperorws.orm.starter.util.OrmContextHolder;

import java.util.List;

/**
 * 事务管理器动态代理后可直接使用的单例Bean
 *
 * @author: EmperorWS
 * @date: 2023/5/17 9:18
 * @description: TransactionManagerProxyBean: 事务管理器动态代理后可直接使用的单例Bean
 */
public class TransactionManagerProxyBean {

    private static final Log log = LogFactory.getLog(TransactionManagerProxyBean.class);

    /**
     * 单例
     */
    private static TransactionManagerProxyBean INSTANCE = new TransactionManagerProxyBean();

    public static TransactionManagerProxyBean me() {
        return INSTANCE;
    }

    public void startTransaction() {
        OrmContextHolder.setActualTransactionActive(true);
    }

    /**
     * 真正执行SqlSession的commit
     */
    public void commit() {
        List<TransactionSynchronization> synchronizationTransactions = OrmContextHolder.getSynchronizations();
        for (TransactionSynchronization synchronizationTransaction : synchronizationTransactions) {
            synchronizationTransaction.commit();
        }
    }

    /**
     * 真正执行SqlSession的commit
     */
    public void commit(boolean force) {
        List<TransactionSynchronization> synchronizationTransactions = OrmContextHolder.getSynchronizations();
        for (TransactionSynchronization synchronizationTransaction : synchronizationTransactions) {
            synchronizationTransaction.commit(force);
        }
    }

    /**
     * 真正执行SqlSession的rollback
     */
    public void rollback() {
        List<TransactionSynchronization> synchronizationTransactions = OrmContextHolder.getSynchronizations();
        for (TransactionSynchronization synchronizationTransaction : synchronizationTransactions) {
            synchronizationTransaction.rollback();
        }
    }

    /**
     * 真正执行SqlSession的rollback
     */
    public void rollback(boolean force) {
        List<TransactionSynchronization> synchronizationTransactions = OrmContextHolder.getSynchronizations();
        for (TransactionSynchronization synchronizationTransaction : synchronizationTransactions) {
            synchronizationTransaction.rollback(force);
        }
    }

    /**
     * 真正执行SqlSession的close
     */
    public void close() {
        try {
            List<TransactionSynchronization> synchronizationTransactions = OrmContextHolder.getSynchronizations();
            for (TransactionSynchronization synchronizationTransaction : synchronizationTransactions) {
                synchronizationTransaction.close();
            }
        } finally {
            //释放synchronizationTransactions资源
            OrmContextHolder.clearSynchronization();
        }
    }

    public void endTransaction() {
        try {
            close();
        } catch (Exception e) {
            log.error("endTransaction error!", e);
        } finally {
            OrmContextHolder.clearActualTransactionActive();
        }
    }
}
