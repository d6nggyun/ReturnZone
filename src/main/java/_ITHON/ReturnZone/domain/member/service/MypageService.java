package _ITHON.ReturnZone.domain.member.service;

import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.repository.LostPostRepository;
import _ITHON.ReturnZone.domain.member.dto.req.UpdateMyPageRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.ExchangeResponseDto;
import _ITHON.ReturnZone.domain.member.dto.res.MyPageResponseDto;
import _ITHON.ReturnZone.domain.member.entity.BankAccount;
import _ITHON.ReturnZone.domain.member.entity.Exchange;
import _ITHON.ReturnZone.domain.member.entity.ExchangeStatus;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.BankAccountRepository;
import _ITHON.ReturnZone.domain.member.repository.ExchangeRepository;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import _ITHON.ReturnZone.global.aws.s3.AwsS3Uploader;
import _ITHON.ReturnZone.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final BankAccountRepository bankAccountRepository;
    private final ExchangeRepository exchangeRepository;
    private final LostPostRepository lostPostRepository;

    @Transactional
    public MyPageResponseDto getMyPage(Long myId) {

        log.info("마이페이지 조회 요청: memberId={}", myId);

        Member member = memberRepository.findById(myId)
                .orElseThrow(() -> {
                    log.warn("[마이페이지 조회 실패] 존재하지 않는 MemberId: {}", myId);
                    return new IllegalArgumentException("회원이 존재하지 않습니다.");
                });

        BankAccount bankAccount = getOrCreateBankAccount(member);

        log.info("마이페이지 조회 성공: memberId={}", myId);

        return MyPageResponseDto.builder().member(member).bankAccount(bankAccount).build();
    }

    @Transactional
    public MyPageResponseDto updateMyPage(Long myId, UpdateMyPageRequestDto updateMyPageRequestDto, MultipartFile image) {

        log.info("마이페이지 수정 요청: memberId={}", myId);

        Member member = memberRepository.findById(myId)
                .orElseThrow(() -> {
                    log.warn("[마이페이지 수정 실패] 존재하지 않는 MemberId: {}", myId);
                    return new IllegalArgumentException("회원이 존재하지 않습니다.");
                });

        BankAccount bankAccount = getOrCreateBankAccount(member);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = awsS3Uploader.upload(image, "members");
            } catch (IOException e) {
                log.error("이미지 파일 S3 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("이미지 파일 업로드에 실패했습니다.", e);
            }
        }

        member.updateMyPage(updateMyPageRequestDto, imageUrl);
        bankAccount.updateBankAccount(updateMyPageRequestDto);

        log.info("마이페이지 수정 성공: memberId={}", myId);

        return MyPageResponseDto.builder().member(member).bankAccount(bankAccount).build();
    }

    @Transactional
    public MyPageResponseDto exchange(Long myId) {

        log.info("환전 요청: memberId={}", myId);

        Member member = memberRepository.findById(myId)
                .orElseThrow(() -> {
                    log.warn("[환전 실패] 존재하지 않는 MemberId: {}", myId);
                    return new IllegalArgumentException("회원이 존재하지 않습니다.");
                });

        if (member.getPoint().compareTo(BigDecimal.valueOf(1000)) < 0) {
            log.warn("[환전 실패] 환전 가능한 최소 포인트(1000) 미달: memberId={}, point={}", myId, member.getPoint());
            throw new IllegalArgumentException("환전 가능한 최소 포인트는 1000입니다.");
        }

        BankAccount bankAccount = getOrCreateBankAccount(member);

        member.usePoint(member.getPoint());

        Exchange exchange = Exchange.builder()
                .member(member).bankAccountId(bankAccount.getId()).status(ExchangeStatus.PENDING).build();

        log.info("포인트 차감 및 환전 객체 생성");

        exchangeRepository.save(exchange);

        log.info("환전 요청 성공: memberId={}", myId);

        return MyPageResponseDto.builder().member(member).bankAccount(bankAccount).build();
    }

    @Transactional
    public MyPageResponseDto processExchange(Long exchangeId, boolean approve, String memo) {

        log.info("환전 처리 요청: exchangeId={}", exchangeId);

        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> {
                    log.warn("[환전 처리 실패] 존재하지 않는 ExchangeId: {}", exchangeId);
                    return new IllegalArgumentException("환전 요청이 존재하지 않습니다.");
                });

        if (exchange.getStatus() != ExchangeStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");

        Member member = memberRepository.findById(exchange.getMemberId())
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        if (approve) {
            // 승인
            exchange.approve(memo);
        } else {
            // 반려
            member.refundPoint(exchange.getPoint());
            exchange.reject(memo);
        }

        log.info("환전 처리 성공: exchangeId={}", exchangeId);

        return MyPageResponseDto.builder().member(member).bankAccount(getOrCreateBankAccount(member)).build();
    }

    @Transactional(readOnly = true)
    public SliceResponse<SimpleLostPostResponseDto> getMyLostPosts(Long myId, int page) {

        log.info("내가 등록한 분실물 조회 요청: memberId={}", myId);

        if (!memberRepository.existsById(myId)) {
            throw new IllegalArgumentException("회원이 존재하지 않습니다.");
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<LostPost> slice = lostPostRepository.findByMemberId(myId, pageable);

        Slice<SimpleLostPostResponseDto> dtoSlice = slice.map(lostPost -> SimpleLostPostResponseDto.builder().lostPost(lostPost).build());

        log.info("내가 등록한 분실물 조회 성공: memberId={}", myId);

        return SliceResponse.from(dtoSlice);
    }

    private BankAccount getOrCreateBankAccount(Member member) {
        return bankAccountRepository.findByMemberId(member.getId())
                .orElseGet(() -> {
                    BankAccount newAccount = BankAccount.builder().memberId(member.getId()).build();
                    return bankAccountRepository.save(newAccount);
                });
    }

    @Transactional(readOnly = true)
    public List<ExchangeResponseDto> getMyExchanges(Long memberId) {
        List<Exchange> exchanges = exchangeRepository.findByMemberIdOrderByRequestedAtDesc(memberId);

        return exchanges.stream()
                .map(exchange ->  ExchangeResponseDto.builder().exchange(exchange).build())
                .toList();
    }
}
