package _ITHON.ReturnZone.domain.payment;

import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // BigDecimal import

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MemberRepository memberRepository;

    /**
     * 게시글 등록 시 입력한 현상금을 거래 완료 시 채팅 상대에게 지급
     * @param payerId 현상금 지급자(게시글 작성자) ID
     * @param receiverId 채팅 상대방(현상금 수령자) ID
     * @param rewardAmount 지급할 현상금 포인트
     */
    @Transactional
    public void payReward(Long payerId, Long receiverId, BigDecimal rewardAmount) { // BigDecimal로 변경
        Member payer = memberRepository.findById(payerId)
                .orElseThrow(() -> new IllegalArgumentException("지급자 회원을 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("수령자 회원을 찾을 수 없습니다."));

        // 지급자의 포인트가 부족한지 확인
        if (payer.getTotalPoint().compareTo(rewardAmount) < 0) { // BigDecimal 비교
            throw new IllegalArgumentException("지급자의 포인트가 부족합니다.");
        }

        // 지급자 포인트 차감
        payer.setTotalPoint(payer.getTotalPoint().subtract(rewardAmount));
        // 환전 가능 포인트는 총 포인트보다 많을 수 없으므로 조정 (음수 방지)
        payer.setExchangeablePoint(payer.getExchangeablePoint().min(payer.getTotalPoint()));
        if (payer.getExchangeablePoint().compareTo(BigDecimal.ZERO) < 0) {
            payer.setExchangeablePoint(BigDecimal.ZERO);
        }


        // 수령자 포인트 적립
        receiver.setTotalPoint(receiver.getTotalPoint().add(rewardAmount));
        receiver.setExchangeablePoint(receiver.getExchangeablePoint().add(rewardAmount));

        memberRepository.save(payer);
        memberRepository.save(receiver);

        log.info("현상금 지급 완료: {}님이 {}님에게 {} 포인트 지급", payer.getNickname(), receiver.getNickname(), rewardAmount);
    }

    /**
     * 관리자가 직접 포인트를 충전하는 메서드
     * @param memberId 포인트를 충전할 회원 ID
     * @param points 충전할 포인트 수량
     */
    @Transactional
    public void addPoints(Long memberId, BigDecimal points) { // BigDecimal로 변경
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        member.setTotalPoint(member.getTotalPoint().add(points));
        member.setExchangeablePoint(member.getExchangeablePoint().add(points));

        memberRepository.save(member);

        log.info("회원 ID {} 포인트 충전 완료: +{}", memberId, points);
    }
}