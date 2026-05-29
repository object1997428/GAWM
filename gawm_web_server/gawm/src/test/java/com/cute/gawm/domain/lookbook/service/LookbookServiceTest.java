package com.cute.gawm.domain.lookbook.service;

import com.cute.gawm.domain.lookbook.dto.response.LookBookTopResponse_v2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.List;


@SpringBootTest
@Slf4j
class LookbookServiceTest {

    @Autowired
    LookbookService lookbookService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private final String lookbookLikeRanking10MinKey = "lookbook:like:ranking:10min";


    @Test
    void getTopLookbooks_v4() {

        // when
        List<LookBookTopResponse_v2> result = lookbookService.getTopLookbooks_v4();

        // then
        log.info("result = {}",result);

    }

    @Test
    void updateTopLookbook() {

        //when
        lookbookService.updateTopLookbook_v2();
    }
}