package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    //커넥션을 각각 가져오는지 ->서로 다른 커넥션
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //커넥션 하나를 db랑 얻게됨
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD); //2개 얻고
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    //spring이 제공하는 datasource가 적용된 deivermanager사용
    @Test
    void datasourceDriverManager() throws SQLException {
        //DriverManagerDataSource는 항상 새로운 커넥션 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    @Test
    void dataSourceConnectionPool() throws SQLException,InterruptedException {
        //커넥션 풀링 using히카리
        HikariDataSource dataSourcea = new HikariDataSource();
        dataSourcea.setJdbcUrl(URL);
        dataSourcea.setUsername(USERNAME);
        dataSourcea.setPassword(PASSWORD);
        dataSourcea.setMaximumPoolSize(10);
        dataSourcea.setPoolName("MyPool");

        useDataSource(dataSourcea);
        Thread.sleep(1000);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
