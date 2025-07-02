package _ITHON.ReturnZone.domain.lostpost.service;

import _ITHON.ReturnZone.domain.lostpost.dto.res.LostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.entity.SortType;
import _ITHON.ReturnZone.domain.lostpost.repository.LostPostRepository;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostPostService {

    private final LostPostRepository lostPostRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<SimpleLostPostResponseDto> getLostPostList(SortType sort, Double lat, Double lng,
                                                           Boolean instant, String category, Pageable pageable) {

        log.info("[분실물 목록 조회] page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        // 기본 Pageable은 최신순 정렬
        Pageable finalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<LostPost> lostPostPage;

        if (sort == SortType.DISTANCE) {
            if (lat == null || lng == null) {
                throw new IllegalArgumentException("거리순 정렬에는 latitude/longitude 값이 필요합니다.");
            }
            // 거리순 정렬은 쿼리에서 처리, pageable 정렬은 제거
            Pageable distancePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            lostPostPage = lostPostRepository.findByFilterOrderByDistance(lat, lng, category, instant, distancePageable);
        } else {
            // 기본 최신순 정렬
            lostPostPage = lostPostRepository.findByFilter(category, instant, finalPageable);
        }

        List<SimpleLostPostResponseDto> simpleLostPostResponseDtos = lostPostPage.stream()
                .map(lostPost -> SimpleLostPostResponseDto.builder().lostPost(lostPost).build()).toList();

        log.info("[분실물 목록 조회 성공]");

        return simpleLostPostResponseDtos;
    }

    @Transactional(readOnly = true)
    public LostPostResponseDto getLostPost(Long lostPostId) {

        log.info("[분실물 정보 상세 조회 요청]");

        LostPost lostPost = lostPostRepository.findById(lostPostId)
                .orElseThrow(() -> {
                    log.warn("[분실물 조회 실패] 분실물 없음: lostPostId={}", lostPostId);
                    return new IllegalArgumentException("존재하지 않는 분실물입니다.");
                });

        Member member = memberRepository.findById(lostPost.getMemberId())
                .orElseThrow(() -> {
                    log.warn("[회원 조회 실패] 회원 없음: memberId={}", lostPost.getMemberId());
                    return new IllegalArgumentException("작성 회원 정보가 존재하지 않습니다.");
                });

        log.info("[분실물 정보 상세 조회 성공]");

        return LostPostResponseDto.builder().lostPost(lostPost).nickname(member.getNickname()).build();
    }
}
