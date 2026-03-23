package com.cute.gawm.domain.like.repository;

import com.cute.gawm.domain.lookbook.dto.TopLookBookIdsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface LikeRepositoryCustom {
    List<TopLookBookIdsDto> findTopLookbook_v3(LocalDateTime startAt, LocalDateTime endAt);
}
