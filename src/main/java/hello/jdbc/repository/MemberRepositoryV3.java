package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭샨 - 트랜잭션 매니저 (동기화매니저 접근해서 가져옴)
 * 커넥션 획득시 DataSourceUtils.getConnection() 사용할것!
 * 커넥션 닫을 때 DataSourceUtils.releaseConnection()사용할 것!
 */
@Slf4j
public class MemberRepositoryV3 {

    //DataSource의존관계 주입
    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?, ?)";
        Connection con = null;  //연결
        PreparedStatement pstmt = null; //db에 쿼리 날려
        try {
            con = getConnection();   //드라이버 매니저로 커넥션 획득
            pstmt = con.prepareStatement(sql);  //sql넘기기, sql예외는 try-catch로
            pstmt.setString(1, member.getMemberId());   //파라미너 바인딩
            pstmt.setInt(2, member.getMoney());//파라미터 바인딩
            pstmt.executeUpdate();//
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);    //연결 닫기
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();  //가져오고
            pstmt = con.prepareStatement(sql);  //sql넣어주고
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) {    //한번은 호출
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {    //data가 없다
                throw new NoSuchElementException("member not found memberId=" +
                        memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야됨.
        DataSourceUtils.releaseConnection(con,dataSource);
        //JdbcUtils.closeConnection(con); 주석 처리
    }
    private Connection getConnection() throws SQLException {
        //주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야됨.
       Connection con = DataSourceUtils.getConnection(dataSource);
        //Connection con = dataSource.getConnection(); 주석 처리 필요없음.
        log.info("get connection={}, class={}", con, con.getClass());
        return con;  //커넥션 가져오기
    }
}
