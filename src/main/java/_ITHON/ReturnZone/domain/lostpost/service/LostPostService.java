package _ITHON.ReturnZone.domain.lostpost.service;

import _ITHON.ReturnZone.domain.lostpost.dto.res.LostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.repository.LostPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostPostService {

    private final LostPostRepository lostPostRepository;

    public List<SimpleLostPostResponseDto> getLostPostList(Pageable pageable) {


    }

    public LostPostResponseDto getLostPost(Long lostPostId) {


    }
}
