package com.cute.gawm.domain.lookbook.service;

import com.cute.gawm.domain.lookbook.dto.response.LookBookTopResponse_v2;
import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class LookbookServiceTest {

    @Autowired
    LookbookService lookbookService;

    @Test
    void likes_v3() {
        //given
        User user=User.builder()
                .userId(1)
                .level(2)
                .point(3)
                .build();
        User author=User.builder()
                .userId(2)
                .level(1)
                .point(2)
                .build();
        Lookbook lookbook=Lookbook.builder()
                .lookbookId(1)
                .user(author)
                .build();

        //when
        lookbookService.likes_v3(user,lookbook);

        //then

    }

    @Test
    void 랭킹게시물_조회(){
        List<LookBookTopResponse_v2> result =
                lookbookService.getTopLookbooks_v3();

        log.info("result: {}",result);

    }


}