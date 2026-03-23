package com.cute.gawm.domain.clothes.repository;

import com.cute.gawm.domain.clothes.entity.ClothesDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothesDetailRepository extends MongoRepository<ClothesDetail, String> {

    void deleteByClothesId(int clothesId);
    ClothesDetail findByClothesId(int clothesId);
}