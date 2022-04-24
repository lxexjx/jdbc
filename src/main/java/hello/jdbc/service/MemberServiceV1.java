package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

//계좌 이체해서  입금하는 비지니스 로직
@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    //계좌이체 로직
    public void accountTransfer(String fromId, String toId, int money) throws
            SQLException {
        Member fromMember = memberRepository.findById(fromId);  //회원 꺼내고
        Member toMember = memberRepository.findById(toId);  //누구에게 보낼지

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }
    //계좌이체 실패
    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
