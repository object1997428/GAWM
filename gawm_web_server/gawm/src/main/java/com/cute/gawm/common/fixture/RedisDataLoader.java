package com.cute.gawm.common.fixture;


import com.cute.gawm.domain.lookbook.service.LookbookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDataLoader implements CommandLineRunner {

    private final RedisDataLoaderService dataLoaderService;
    private final LookbookService lookbookService;

    @Override
    public void run(String... args) throws Exception {
//        log.info("======= 대량 데이터 생성 시작 ========");
//        dataLoaderService.loadLikeCountToRedis();
//        log.info("======= 대량 데이터 생성 종료 ========");
//
//        log.info("======= 집계 시작 ========");
//        lookbookService.updateTopLookbook_v2();
//        log.info("======= 집계 종료 ========");
    }


}
