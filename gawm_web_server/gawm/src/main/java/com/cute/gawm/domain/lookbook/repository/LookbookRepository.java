package com.cute.gawm.domain.lookbook.repository;

import com.cute.gawm.domain.lookbook.entity.Lookbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LookbookRepository extends JpaRepository<Lookbook, Integer>, LookbookRepositoryCustom {
    Integer countByUserUserId(Integer userId);

    List<Lookbook> findByUserUserId(Integer userId);

    void deleteByUser_UserId(Integer userId);

    Lookbook findByLookbookId(Integer lookbookId);

    @Query("SELECT l FROM Lookbook l JOIN FETCH l.user WHERE l.lookbookId IN :ids")
    List<Lookbook> findAllByLookbookIdInWithUser(@Param("ids") List<Integer> lookbookIds);

    void deleteByLookbookId(Integer lookbookId);

    List<Lookbook> findAll();




}

