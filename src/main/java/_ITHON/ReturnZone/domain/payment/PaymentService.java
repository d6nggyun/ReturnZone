package _ITHON.ReturnZone.domain.payment;

import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.repository.LostPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final MemberRepository memberRepository;
    private final LostPostRepository lostPostRepository; // LostPostRepository 주입

    /**
     * 게시글 등록 시 입력한 현상금을 거래 완료 시 채팅 상대에게 지급
     * @param payerId 현상금 지급자(게시글 작성자) ID
     * @param receiverId 채팅 상대방(현상금 수령자) ID
     * @param rewardAmount 지급할 현상금 포인트
     */
    @Transactional
    public void payReward(Long payerId, Long receiverId, BigDecimal rewardAmount) {
        Member payer = memberRepository.findById(payerId)
                .orElseThrow(() -> new IllegalArgumentException("지급자 회원을 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("수령자 회원을 찾을 수 없습니다."));

        // 지급자의 totalPoint가 부족한지 확인
        if (payer.getTotalPoint().compareTo(rewardAmount) < 0) { // totalPoint 필드 사용
            throw new IllegalArgumentException("지급자의 포인트가 부족합니다.");
        }

        // 지급자 totalPoint 차감
        payer.setTotalPoint(payer.getTotalPoint().subtract(rewardAmount)); // totalPoint 필드 사용

        // 수령자 totalPoint 적립
        receiver.setTotalPoint(receiver.getTotalPoint().add(rewardAmount)); // totalPoint 필드 사용

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
    public void addPoints(Long memberId, BigDecimal points) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        member.setTotalPoint(member.getTotalPoint().add(points)); // totalPoint 필드 사용

        memberRepository.save(member);

        log.info("회원 ID {} 포인트 충전 완료: +{}", memberId, points);
    }

    /**
     * 특정 게시글의 현상금 금액을 조회합니다.
     */
    @Transactional(readOnly = true)
    public BigDecimal getRewardByPostId(Long postId) {
        LostPost post = lostPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        return post.getReward();
    }

    /**
     * 특정 게시글의 현상금 금액을 수정합니다.
     */
    @Transactional
    public void updateReward(Long postId, BigDecimal newReward) {
        LostPost post = lostPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        post.setReward(newReward);
        lostPostRepository.save(post);

        log.info("게시글 {}의 현상금이 {}로 수정되었습니다.", postId, newReward);
    }
}