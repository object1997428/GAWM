package com.cute.gawm.domain.user.controller;


import com.cute.gawm.common.response.BasicResponse;
import com.cute.gawm.common.auth.LoginUser;
import com.cute.gawm.domain.user.dto.UserEditForm;
import com.cute.gawm.domain.user.dto.SessionUser;
import com.cute.gawm.domain.user.dto.UserInfoDto;
import com.cute.gawm.domain.user.entity.User;
import com.cute.gawm.domain.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/userInfo")
    public BasicResponse userInfo(@LoginUser SessionUser sessionUser) {
        UserInfoDto userInfo = userService.getUserInfo(sessionUser.getId());
        log.info("sessionUser.getId()={}", sessionUser.getId());
        log.info("userInfo={}", userInfo);
        return new BasicResponse(HttpStatus.OK.value(), userInfo);
    }

    @PatchMapping("/userInfo")
    public BasicResponse edit(UserEditForm form, @LoginUser SessionUser sessionUser) throws IOException {
        userService.updateMember(sessionUser.getId(), form);
        return new BasicResponse(HttpStatus.OK.value(), null);
    }
}
