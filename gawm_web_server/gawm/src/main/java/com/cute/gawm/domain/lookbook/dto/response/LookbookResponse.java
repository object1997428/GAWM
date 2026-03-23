package com.cute.gawm.domain.lookbook.dto.response;

import com.cute.gawm.domain.clothes.dto.response.ClothesMiniResponse;
import com.cute.gawm.domain.comment.dto.response.CommentResponse;
import com.cute.gawm.domain.tag.dto.response.TagResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LookbookResponse {
    private Integer lookbookId;
    private Integer userId;
    private String userNickname;
    private String userProfileImg;
    private LocalDateTime createdAt;
    private List<ClothesMiniResponse> clothes;
    private List<String> lookbookImgs;
    private int likeCnt;
    private int view;
    private List<TagResponse> tag;
    private List<CommentResponse> comment;
    private Boolean isPublic;

    private boolean isPublish;
    private boolean isLiked;
    private boolean isBookmarked;
    private boolean isFollowed;
}
