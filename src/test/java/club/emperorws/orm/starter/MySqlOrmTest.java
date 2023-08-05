package club.emperorws.orm.starter;

import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.starter.config.MapperProxyBean;
import club.emperorws.orm.starter.entity.Student;
import club.emperorws.orm.starter.mapper.StudentMapper;
import club.emperorws.orm.starter.proxy.IMapperProxyBean;
import club.emperorws.orm.starter.transaction.proxy.TransactionManagerProxyBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * my-orm的MySQL执行测试
 *
 * @author: EmperorWS
 * @date: 2023/8/5 12:44
 * @description: MySqlOrmTest: my-orm的MySQL执行测试
 */
@Tag("MySQL执行测试@Tag")
@DisplayName("MySQL执行测试@DisplayName")
public class MySqlOrmTest {

    private static final Logger logger = LoggerFactory.getLogger(MySqlOrmTest.class);

    private static IMapperProxyBean mapperProxyBean;

    @BeforeAll
    static void setUp() throws Exception {
        mapperProxyBean = new MapperProxyBean();
        mapperProxyBean.init();
    }

    @DisplayName("MySQL的Select查询测试")
    @Test
    public void mysqlSelectTest() {
        try {
            String sql = "select * from student where name like concat('%',#{keyword},'%')";
            StudentMapper studentMapper = mapperProxyBean.getObject(StudentMapper.class);
            List<Student> studentList = studentMapper.selectList(new SqlSource.Builder(sql).build(), "a");
            studentList.forEach(student -> logger.info(student.toString()));
        } catch (Exception e) {
            logger.error("mysqlSelectTest has an error.", e);
        }
    }

    @DisplayName("MySQL的Select带事务的查询测试")
    @Test
    public void mysqlSelectTransactionTest() {
        try {
            TransactionManagerProxyBean.me().startTransaction();

            String sql = "select * from student where name like concat('%',#{keyword},'%')";
            StudentMapper studentMapper = mapperProxyBean.getObject(StudentMapper.class);
            List<Student> studentList = studentMapper.selectList(new SqlSource.Builder(sql).build(), "a");
            studentList.forEach(student -> logger.info(student.toString()));

            TransactionManagerProxyBean.me().commit();
        } catch (Exception e) {
            logger.error("mysqlSelectTest has an error.", e);
            TransactionManagerProxyBean.me().rollback();
        } finally {
            TransactionManagerProxyBean.me().endTransaction();
        }
    }
}
