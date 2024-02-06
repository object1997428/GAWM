package com.cute.gawm.domain.lookbook.repository;

import com.cute.gawm.domain.lookbook.entity.Lookbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LookbookRepository extends JpaRepository<Lookbook, Integer>, LookbookRepositoryCustom {
    Integer countByUserUserId(Integer userId);

    List<Lookbook> findByUserUserId(Integer userId);

    void deleteByUser_UserId(Integer userId);

    Lookbook findLookbookByLookbookId(Integer lookbookId);

//    PageImpl<Lookbook> findAllBy(Pageable pageable);

}

