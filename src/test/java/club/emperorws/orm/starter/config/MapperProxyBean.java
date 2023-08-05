package club.emperorws.orm.starter.config;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.mapping.Environment;
import club.emperorws.orm.metadata.TableModelInfoHelper;
import club.emperorws.orm.starter.datasource.DynamicDataSource;
import club.emperorws.orm.starter.proxy.IMapperProxyBean;
import club.emperorws.orm.starter.session.proxy.SqlSessionFactoryProxyBean;
import club.emperorws.orm.transaction.jdbc.JdbcTransactionFactory;
import club.emperorws.orm.util.MapUtil;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可直接使用的Mapper代理
 *
 * @author: EmperorWS
 * @date: 2023/5/18 11:19
 * @description: MapperProxyBean: 可直接使用的Mapper代理
 */
public class MapperProxyBean implements IMapperProxyBean {

    /**
     * Mapper Class与Mapper实例的映射（Mapper实例为单例）
     */
    private static final Map<Class, Object> MAPPER_SINGLETON_MAP = new ConcurrentHashMap<>();

    /**
     * 初始化ORM
     */
    @Override
    public void init() {
        //新版本的ORM
        //1. 初始化配置文件Configuration（包括数据源dataSource）
        Configuration configuration = new Configuration();
        //2. 设置数据源（支持多数据源）
        Map<Object, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", HikariDataSourceFactory.createDataSource());
        DynamicDataSource dataSource = new DynamicDataSource(dataSourceMap, "master");
        //3. 设置ORM环境
        Environment environment = new Environment(new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        //4. 添加Mapper扫描
        configuration.addMapperPackages("club.emperorws.orm.starter.mapper", "club.emperorws.orm.starter.mapper2");
        SqlSessionFactoryProxyBean.me().init(configuration);
        //5. 添加扫描实体信息
        TableModelInfoHelper.scanPackageAddTableModelInfo(configuration, "club.emperorws.orm.starter.entity", "club.emperorws.orm.starter.entity2");
        //6. 别名化实体，可以直接使用类名（或@Alias注解）来代表实体，不需要写完整类路径
        //todo 现阶段似乎没有必要使用，暂时注释
        //TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
        //typeAliasRegistry.registerAliasPackages("com.cib.cap.project.entity", "com.cib.cap.openapi.entity", "com.cib.cap.commbus.entity", "com.cib.cap.common_core.entity");
    }

    /**
     * 获取可执行Mapper，以供直接使用
     *
     * @param type Mapper的class类型
     * @param <T>  Mapper
     * @return Mapper实例
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> type) {
        return (T) MapUtil.computeIfAbsent(MAPPER_SINGLETON_MAP, type, t -> SqlSessionFactoryProxyBean.me().getSqlSessionProxyBean().getMapper(t));
    }
}
