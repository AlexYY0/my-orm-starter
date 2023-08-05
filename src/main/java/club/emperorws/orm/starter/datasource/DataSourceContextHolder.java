package club.emperorws.orm.starter.datasource;

/**
 * 多数据源的key存储
 * <p>todo 注意多线程下的数据丢失问题，后续如果涉及到多数据源问题，建议改为阿里的TTL（TransmittableThreadLocal）</p>
 * <p>
 * <b>How to use:</b>
 * <pre>
 * DataSourceContextHolder.setDbType("APIDB_NEW")
 *
 * doSomethingDbOperation();
 *
 * DataSourceContextHolder.clearDbType();
 * </pre>
 * <p> 注意事务情况的使用：</p>
 * <p>方法1：编程式事务</p>
 * <p>
 * 开启事务后切换数据源会导致位置错误。
 * 可选择在切换数据源后。编程式事务
 * <pre>
 *     try{
 *         DataSourceContextHolder.setDbType("APIDB_NEW")
 *     }finally{
 *         DataSourceContextHolder.clearDbType();
 *     }
 * </pre>
 * 编程式事务结束
 * <p>方法2：注解的方式</p>
 * <pre>
 *     todo
 * </pre>
 *
 * @author: EmperorWS
 * @date: 2023/5/18 10:38
 * @description: DataSourceContextHolder: 多数据源的key存储
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 切换数据源
     *
     * @param dbType 数据源key
     */
    public static void setDbType(String dbType) {
        CONTEXT_HOLDER.set(dbType);
    }

    public static String getDbType() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 切回默认数据源
     */
    public static void clearDbType() {
        CONTEXT_HOLDER.remove();
    }
}
