package com.cute.gawm.common.fixture;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DBDataLoader implements CommandLineRunner {

    private final DataLoaderService dataLoaderService;

    @Override
    public void run(String... args) throws Exception {
//        log.info("======= 대량 데이터 생성 시작 ========");
//        dataLoaderService.loadData();
//        log.info("======= 대량 데이터 생성 종료 ========");
    }


}
