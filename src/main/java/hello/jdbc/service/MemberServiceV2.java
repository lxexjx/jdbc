package hello.jdbc.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//계좌 이체해서  입금하는 비지니스 로직
//트랜잭션 - 파라키너 연동, 풀을 고려한 종료
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    //계좌이체 로직
    public void accountTransfer(String fromId, String toId, int money) throws
            SQLException {
        Connection con = dataSource.getConnection();
        try{
            con.setAutoCommit(false);   //이게 트랜잭션 시작
            bizLogic(con, fromId, toId, money);
             con.commit();   //성공시 커밋
        }catch (Exception e){
            con.rollback(); //실패시 롤백
            throw new IllegalStateException(e);
        }finally {
            release(con);
        }
    }

    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);
        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    //계좌이체 실패
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true); //커넥션 풀 고려
                con.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }
}
