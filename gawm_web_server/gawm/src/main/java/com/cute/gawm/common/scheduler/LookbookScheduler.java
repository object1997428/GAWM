package com.cute.gawm.common.scheduler;

import com.cute.gawm.domain.lookbook.service.LookbookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LookbookScheduler {
    private final LookbookService lookbookService;


    //    @Scheduled(cron = "* 10 * * * *")
//    @Scheduled(fixedDelay = 10000) //10초마다 실행
    public void getTopLookbook() {
        log.info("스케줄러 시작");
        lookbookService.updateTopLookbook();
    }
}
