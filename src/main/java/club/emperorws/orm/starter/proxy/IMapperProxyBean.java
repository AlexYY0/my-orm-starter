package club.emperorws.orm.starter.proxy;

/**
 * 可直接使用的全局Mapper代理，可直接获取Mapper
 *
 * @author: EmperorWS
 * @date: 2023/8/5 15:08
 * @description: IMapperProxyBean: 可直接使用的全局Mapper代理，可直接获取Mapper
 */
public interface IMapperProxyBean {

    /**
     * 初始化ORM框架
     */
    void init();

    /**
     * 获取可执行Mapper，以供直接使用
     *
     * @param type Mapper的class类型
     * @param <T>  Mapper
     * @return Mapper实例
     */
    <T> T getObject(Class<T> type);
}
