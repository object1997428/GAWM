package com.cute.gawm.domain.lookbook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookBookTopResponse {
    private int lookbookId;
    private String userNickname;
    private String userProfileImg;
    private Timestamp createdAt;
    private String thumbNail;
    private Integer likeCnt;
    private Boolean isPublic;
}
