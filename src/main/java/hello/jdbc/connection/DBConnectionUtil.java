package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {
    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("getConnection={}, class={}", connection, connection.getClass()); //커넥션과 클래스 정보 출력
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
