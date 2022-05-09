package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Slf4j
public class UnCheckedAppTest {

    @Test
    void checked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }
    static class Controller {
        Service service = new Service();
        public void request() throws SQLException, ConnectException {
            service.logic();
        }
    }
    static class Service{
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();
        public void logic() throws SQLException, ConnectException {
            repository.call();
            networkClient.call();
        }
    }
    static class NetworkClient{
        public void call() throws ConnectException {
            throw new RuntimeConnectException("연결 실패");
        }
    }
    static class Repository {
        public void call() {
            try {
                runSQL();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }
    private void runSQL() throws SQLException {
        throw new SQLException("ex");
    }
}

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException() {
        }
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }
}
