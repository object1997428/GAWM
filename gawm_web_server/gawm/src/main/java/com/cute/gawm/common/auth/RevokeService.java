package com.cute.gawm.common.auth;

import com.cute.gawm.common.exception.DataMismatchException;
import com.cute.gawm.common.exception.DataNotFoundException;
import com.cute.gawm.common.util.s3.S3Uploader;
import com.cute.gawm.domain.bookmark.repository.BookmarkRepository;
import com.cute.gawm.domain.clothes.entity.Clothes;
import com.cute.gawm.domain.clothes.repository.ClothesDetailRepository;
import com.cute.gawm.domain.clothes.repository.ClothesRepository;
import com.cute.gawm.domain.clothes_lookbook.repository.ClothesLookbookRepository;
import com.cute.gawm.domain.clothes_stylelog.repository.ClothesStylelogRepository;
import com.cute.gawm.domain.comment.repository.CommentRepository;
import com.cute.gawm.domain.following.entity.Follower;
import com.cute.gawm.domain.following.entity.Following;
import com.cute.gawm.domain.following.repository.FollowerRepository;
import com.cute.gawm.domain.following.repository.FollowingRepository;
import com.cute.gawm.domain.like.repository.LikesRepository;
import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.lookbook.repository.LookbookRepository;
import com.cute.gawm.domain.lookbook_image.entity.LookbookImage;
import com.cute.gawm.domain.lookbook_image.repository.LookbookImageRepository;
import com.cute.gawm.domain.stylelog.entity.Stylelog;
import com.cute.gawm.domain.stylelog.repository.StylelogRepository;
import com.cute.gawm.domain.tag_lookbook.repository.TagLookbookRepository;
import com.cute.gawm.domain.user.entity.User;
import com.cute.gawm.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RevokeService {
    private final UserRepository userRepository;
    private final StylelogRepository stylelogRepository;
    private final ClothesStylelogRepository clothesStylelogRepository;
    private final ClothesRepository clothesRepository;
    private final ClothesDetailRepository clothesDetailRepository;
    private final TagLookbookRepository tagLookbookRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final LookbookRepository lookbookRepository;
    private final LikesRepository likesRepository;
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    private final ClothesLookbookRepository clothesLookbookRepository;
    private final LookbookImageRepository lookbookImageRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void deleteGoogleAccount(Integer sessionUserId, OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        Optional<User> user = userRepository.findById(sessionUserId);
        if (!user.isPresent()) {
            throw new DataNotFoundException("해당 유저가 존재하지 않습니다");
        }
        // 1. 세션 삭제
        deleteSession();
        // 2. 토큰 만료
        String data = "token=" + oAuth2AuthorizedClient.getAccessToken().getTokenValue();
        sendRevokeRequest(data, "google", null);
        // 3. 관련 엔티티 삭제
        deleteRelatedEntities(sessionUserId);
        // 4. 유저 엔티티 삭제
        deleteUserAccount(user.get());
    }

    @Transactional
    public void deleteKakaoAccount(Integer sessionUserId, OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        Optional<User> user = userRepository.findById(sessionUserId);
        if (!user.isPresent()) {
            throw new DataNotFoundException("해당 유저가 존재하지 않습니다");
        }
        // 1. 세션 삭제
        deleteSession();
        // 2. 토큰 만료
        sendRevokeRequest(null, "kakao", oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        // 3. 관련 엔티티 삭제
        deleteRelatedEntities(sessionUserId);
        // 4. 유저 엔티티 삭제
        deleteUserAccount(user.get());
    }
    @Transactional
    public void deleteSession() {
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public void deleteUserAccount(User user) {
        userRepository.delete(user);
    }

    private void sendRevokeRequest(String data, String provider, String accessToken) {
        String googleRevokeUrl = "https://accounts.google.com/o/oauth2/revoke";
        String kakaoRevokeUrl = "https://kapi.kakao.com/v1/user/unlink";

        RestTemplate restTemplate = new RestTemplate();
        String revokeUrl = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);

        switch (provider) {
            case "google":
                revokeUrl = googleRevokeUrl;
                break;
            case "kakao":
                revokeUrl = kakaoRevokeUrl;
                headers.setBearerAuth(accessToken);
                break;
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();

    }

    @Transactional
    public void deleteRelatedEntities(Integer sessionUserId) {

        List<Stylelog> stylelogList = stylelogRepository.findByUserUserId(sessionUserId);
        for (Stylelog stylelog : stylelogList) {
            clothesStylelogRepository.deleteByStylelog_StylelogId(stylelog.getStylelogId()); //clothesStylelog 삭제
        }
        stylelogRepository.deleteByUserUserId(sessionUserId); //stylelog삭제

        List<Clothes> clothesList = clothesRepository.findByUserUserId(sessionUserId);
        for (Clothes clothes : clothesList) {
            clothesDetailRepository.deleteByClothesId(clothes.getClothesId()); //clothesDetail 삭제
        }
        clothesRepository.deleteByUserUserId(sessionUserId);//clothes 삭제

        List<Lookbook> lookbookList = lookbookRepository.findByUserUserId(sessionUserId);
        for (Lookbook lookbook : lookbookList) {
            Integer lookbookId=lookbook.getLookbookId();
            tagLookbookRepository.deleteByLookbookLookbookId(lookbookId); //Tag-Lookbook 삭제
            commentRepository.deleteByLookbookLookbookId(lookbookId); //comment 삭제
            bookmarkRepository.deleteByLookbookLookbookId(lookbookId); //bookmark 삭제
            likesRepository.deleteByLookbookLookbookId(lookbookId); //like 삭제
            clothesLookbookRepository.deleteAllByLookbook(lookbook); //clothesLookbook삭제

            //lookbookImage 삭제
            List<LookbookImage> lookbookImages = lookbookImageRepository.findAllByLookbook_LookbookId(lookbookId);
            lookbookImages.forEach((lookbookImage) -> {
                s3Uploader.deleteFile(lookbookImage.getImage());
            });
            lookbookImageRepository.deleteByLookbook(lookbook);
        }
        lookbookRepository.deleteByUser_UserId(sessionUserId); //lookbook 삭제
        likesRepository.deleteByUserUserId(sessionUserId);
        bookmarkRepository.deleteByUserUserId(sessionUserId);
        commentRepository.deleteByUserUserId(sessionUserId);

        //following들에서 userId 삭제
        Following userFollowing = followingRepository.findByUserId(sessionUserId);
        userFollowing.getFollowingList().forEach(followId->{
            Follower follower=followerRepository.findByUserId(followId); //내가 팔로우한 사람들
            if(follower==null) return; //이미 followId가 회원탈퇴한 경우
            boolean unfollowed = follower.unfollow(sessionUserId); //내가 팔로우한 사람들의 follwer 목록에서 나를 삭제
            if(!unfollowed){
                log.error("내가 팔로우한 사람의 follower 목록에 내가 없습니다. followId={}",followId);
                throw new DataMismatchException("내가 팔로우한 사람의 follower 목록에 내가 없습니다.");
            }
            followerRepository.save(follower);
        });
        //following 삭제
        followingRepository.deleteByUserId(sessionUserId);

        //follower들에서 userId삭제
        Follower userFollower = followerRepository.findByUserId(sessionUserId);
        userFollower.getFollowerList().forEach(followId->{
            Following following=followingRepository.findByUserId(followId); //나를 팔로우한 사람들
            if(following==null) return;
            boolean unfollowed=following.unfollow(sessionUserId); //나를 팔로우한 사람들의 following 목록에서 나를 삭제
            if(!unfollowed){
                log.error("나를 팔로우한 사람의 following 목록에 내가 없습니다. followId={}",followId);
                throw new DataMismatchException("나를 팔로우한 사람의 following 목록에 내가 없습니다.");
            }
            followingRepository.save(following);
        });
        followerRepository.deleteByUserId(sessionUserId);
    }

}
