package com.cute.gawm.domain.lookbook.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
@Slf4j
class LookbookServiceTest {

    @Autowired
    LookbookService lookbookService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private final String lookbookLikeRanking10MinKey = "lookbook:like:ranking:10min";

}