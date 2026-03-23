package com.cute.gawm.domain.lookbook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookbookThumbnailResponse {
    private int lookbookId;
    private String userNickname;
    private String userProfileImg;
    private List<String> images;
    private Integer likeCnt;
    private Boolean isPublic;
    private LocalDateTime createdAt;
}
