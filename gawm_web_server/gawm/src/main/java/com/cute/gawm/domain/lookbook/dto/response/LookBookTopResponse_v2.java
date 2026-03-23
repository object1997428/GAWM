package com.cute.gawm.domain.lookbook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookBookTopResponse_v2 {
    private int rank;
    private int lookbookId;
    private int userId;
    private String userNickname;
    private String userProfileImg;
    private String thumbNail;
    private Integer likeCnt;
    private LocalDateTime createdAt;

    public LookBookTopResponse_v2(int lookbookId, int userId, String userNickname, String userProfileImg, String thumbNail, Integer likeCnt, LocalDateTime createdAt) {
        this.lookbookId = lookbookId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfileImg = userProfileImg;
        this.thumbNail = thumbNail;
        this.likeCnt = likeCnt;
        this.createdAt = createdAt;
    }
}
