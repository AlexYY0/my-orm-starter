package club.emperorws.orm.starter.util;

import club.emperorws.orm.starter.transaction.TransactionSynchronization;

import java.util.*;

/**
 * Orm运行过程中产生的全局遍历
 * <p>todo 注意多线程下ThreadLocal的数据丢失问题，本质上多线程下没有事务管理，没有共有的SqlSession等</p>
 *
 * @author: EmperorWS
 * @date: 2023/5/16 11:18
 * @description: OrmContextHolder: Orm运行过程中产生的全局遍历
 */
public class OrmContextHolder {

    /**
     * 一些资源存储
     */
    private static final ThreadLocal<Map<Object, Object>> resources = new ThreadLocal<>();

    /**
     * 事务链
     */
    private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations = new ThreadLocal<>();

    /**
     * 判断当前线程是否开启了事务，且事务由统一事务管理器管理
     */
    private static final ThreadLocal<Boolean> actualTransactionActive = new ThreadLocal<>();

    /*****************************************************资源管理****************************************************/

    /**
     * 获取绑定的资源
     *
     * @param key key
     * @return 绑定的资源
     */
    public static Object getResource(Object key) {
        Map<Object, Object> map = (Map) resources.get();
        if (map == null) {
            return null;
        } else {
            return map.get(key);
        }
    }

    /**
     * 设置（绑定）resources上下文值
     *
     * @param key   key
     * @param value value
     * @throws IllegalStateException 异常
     */
    public static void bindResource(Object key, Object value) throws IllegalStateException {
        Assert.notNull(value, "Value must not be null");
        Map<Object, Object> map = (Map) resources.get();
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }

        Object oldValue = ((Map) map).put(key, value);
        if (oldValue != null) {
            throw new IllegalStateException("Already value [" + oldValue + "] for key [" + key + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }
    }

    /**
     * 删除（取消绑定）resources上下文值
     *
     * @param key key
     * @return 被删除的旧值
     * @throws IllegalStateException 异常
     */
    public static Object unbindResource(Object key) throws IllegalStateException {
        Object value = doUnbindResource(key);
        if (value == null) {
            throw new IllegalStateException("No value for key [" + key + "] bound to thread [" + Thread.currentThread().getName() + "]");
        }
        return value;
    }

    /**
     * 删除（取消绑定）resources上下文值
     *
     * @param key key
     * @return 被删除的旧值
     */
    public static Object unbindResourceIfPossible(Object key) {
        return doUnbindResource(key);
    }

    public static Object doUnbindResource(Object key) throws IllegalStateException {
        Map<Object, Object> map = (Map) resources.get();
        if (map == null) {
            return null;
        } else {
            Object value = map.remove(key);
            if (map.isEmpty()) {
                resources.remove();
            }
            return value;
        }
    }

    /*****************************************************事务链管理****************************************************/

    public static boolean isSynchronizationActive() {
        return (synchronizations.get() != null);
    }

    public static void initSynchronization() throws IllegalStateException {
        if (isSynchronizationActive()) {
            throw new IllegalStateException("Cannot activate transaction synchronization - already active");
        }
        synchronizations.set(new LinkedHashSet<TransactionSynchronization>());
    }

    public static void registerSynchronization(TransactionSynchronization synchronization) throws IllegalStateException {
        Assert.notNull(synchronization, "TransactionSynchronization must not be null");

        Set<TransactionSynchronization> synchs = synchronizations.get();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        synchs.add(synchronization);
    }

    public static List<TransactionSynchronization> getSynchronizations() throws IllegalStateException {
        Set<TransactionSynchronization> synchs = synchronizations.get();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        if (synchs.isEmpty()) {
            return Collections.emptyList();
        } else {
            // Sort lazily here, not in registerSynchronization.
            List<TransactionSynchronization> sortedSynchs = new ArrayList<>(synchs);
            //Collections.sort(sortedSynchs); 暂时不需要排序，ignore
            return Collections.unmodifiableList(sortedSynchs);
        }
    }

    public static void clearSynchronization() throws IllegalStateException {
        if (!isSynchronizationActive()) {
            throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
        }
        synchronizations.remove();
    }

    /*****************************************************事务是否开启的判断****************************************************/

    public static boolean isActualTransactionActive() {
        return (actualTransactionActive.get() != null);
    }

    public static void setActualTransactionActive(boolean active) {
        actualTransactionActive.set(active ? Boolean.TRUE : null);
    }

    public static void clearActualTransactionActive() {
        actualTransactionActive.remove();
    }

    public static void clear() {
        synchronizations.remove();
        actualTransactionActive.remove();
    }
}
