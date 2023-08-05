package club.emperorws.orm.starter.transaction;

/**
 * Orm事务同步处理器接口
 *
 * @author: EmperorWS
 * @date: 2023/5/16 16:42
 * @description: TransactionSynchronization: Orm事务同步处理器接口
 */
public interface TransactionSynchronization {

    int getOrder();

    void commit();

    void commit(boolean force);

    void rollback();

    void rollback(boolean force);

    void close();
}
