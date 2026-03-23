package com.cute.gawm.domain.lookbook.dto;

import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopLookBookHashDto {
    private int rank;
    private int lookbookId;
    private int userId;
    private String userNickname;
    private String userProfileImg;
    private String thumbNail;
    private LocalDateTime createdAt;


    public TopLookBookHashDto(Lookbook lookbook) {
        this.lookbookId = lookbook.getLookbookId();
        this.thumbNail = lookbook.getThumbnail();
        this.createdAt = lookbook.getCreatedAt();
        if(lookbook.getUser()!=null){
            this.userId = lookbook.getUser().getUserId();
            this.userNickname = lookbook.getUser().getNickname();
            this.userProfileImg = lookbook.getUser().getProfileImg();
        }
    }
}
