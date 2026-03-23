package com.cute.gawm.domain.lookbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopLookBookDto {
    private int lookbookId;
    private int userId;
    private String userNickname;
    private String userProfileImg;
    private LocalDateTime createdAt;
    private String thumbNail;
    private Integer likeCnt;
    private Boolean isPublic;
}
