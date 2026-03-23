package com.cute.gawm.domain.lookbook.repository;

import com.cute.gawm.domain.lookbook.dto.TopLookBookDto;
import com.cute.gawm.domain.lookbook.dto.TopLookBookIdsDto;
import com.cute.gawm.domain.lookbook.dto.response.LookBookTopResponse_v2;
import com.cute.gawm.domain.lookbook.entity.Lookbook;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface LookbookRepositoryCustom {
       PageImpl<Lookbook> findAllLookbook(Pageable pageable);
       PageImpl<Lookbook> findAllLookbookByUserId(int userId, Pageable pageable);
       PageImpl<Lookbook> searchLookbook(String keyword, Pageable pageable);
       List<Lookbook> findTopLookbook(LocalDateTime startDate, LocalDateTime endDate);
       List<LookBookTopResponse_v2> findTopLookbook_v2(LocalDateTime startDate, LocalDateTime endDate);
       List<TopLookBookDto> getTopLookBookDetailInfo(List<Integer> lookbookIds);
       PageImpl<Lookbook> searchLookbookByTag(ArrayList<String> tags, Pageable pageable);

}
