package com.cute.gawm.domain.lookbook_image.repository;

import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.lookbook_image.entity.LookbookImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LookbookImageRepository extends JpaRepository<LookbookImage, Integer> {
    List<LookbookImage> findAllByLookbook_LookbookId(Integer lookbookId);

    LookbookImage findFirstByLookbook_LookbookId(Integer lookbookId);
    void deleteByLookbook(Lookbook lookbookId);

}
