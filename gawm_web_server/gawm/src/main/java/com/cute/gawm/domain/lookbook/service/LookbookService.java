package com.cute.gawm.domain.lookbook.service;

import com.cute.gawm.common.exception.*;
import com.cute.gawm.common.response.PagingResponse;
import com.cute.gawm.common.util.s3.S3Uploader;
import com.cute.gawm.domain.bookmark.entity.Bookmark;
import com.cute.gawm.domain.bookmark.repository.BookmarkRepository;
import com.cute.gawm.domain.clothes.dto.response.ClothesMiniResponse;
import com.cute.gawm.domain.clothes.entity.Clothes;
import com.cute.gawm.domain.clothes.repository.ClothesRepository;
import com.cute.gawm.domain.clothes_lookbook.entity.ClothesLookbook;
import com.cute.gawm.domain.clothes_lookbook.repository.ClothesLookbookRepository;
import com.cute.gawm.domain.comment.dto.response.CommentResponse;
import com.cute.gawm.domain.comment.entity.Comment;
import com.cute.gawm.domain.comment.repository.CommentRepository;
import com.cute.gawm.domain.following.entity.Following;
import com.cute.gawm.domain.following.repository.FollowingRepository;
import com.cute.gawm.domain.following.service.FollowService;
import com.cute.gawm.domain.like.entity.Likes;
import com.cute.gawm.domain.like.repository.LikesRepository;
import com.cute.gawm.domain.lookbook.dto.TopLookBookHashDto;
import com.cute.gawm.domain.lookbook.dto.request.LookbookCreateRequest;
import com.cute.gawm.domain.lookbook.dto.request.LookbookUpdateRequest;
import com.cute.gawm.domain.lookbook.dto.response.*;
import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.lookbook.repository.LookbookRepository;
import com.cute.gawm.domain.lookbook_image.entity.LookbookImage;
import com.cute.gawm.domain.lookbook_image.repository.LookbookImageRepository;
import com.cute.gawm.domain.tag.dto.response.TagResponse;
import com.cute.gawm.domain.tag.entity.Tag;
import com.cute.gawm.domain.tag.repository.TagRepository;
import com.cute.gawm.domain.tag_lookbook.entity.TagLookbook;
import com.cute.gawm.domain.tag_lookbook.repository.TagLookbookRepository;
import com.cute.gawm.domain.user.entity.User;
import com.cute.gawm.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class LookbookService {
    private final LookbookRepository lookbookRepository;
    private final CommentRepository commentRepository;
    private final S3Uploader s3Uploader;
    private final ClothesLookbookRepository clothesLookbookRepository;
    private final TagLookbookRepository tagLookbookRepository;
    private final LookbookImageRepository lookbookImageRepository;
    private final UserRepository userRepository;
    private final ClothesRepository clothesRepository;
    private final TagRepository tagRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FollowingRepository followingRepository;
    private final LikesRepository likesRepository;
    private final FollowService followService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper jacksonObjectMapper;

    private final String lookbookLikeRanking10MinKey = "lookbook:like:ranking:10min";
    private final String lookbookLikeRankingTotalKey = "lookbook:like:ranking:total";
    private final String lookbookUserIndexKey = "lookbook:user:index";
    @Value("${REDIS_HASH_LOOKBOOK_KEY}")
    private String lookbookRankingSnapshotKey;
    @Value("${REDIS_SET_USER_KEY}")
    private String userLookbookMappingKey;

    public LookbookResponse getLookbook(final int sessionUserId, final int lookbookId) {
        User sessionUser = userRepository.findById(sessionUserId).orElseThrow(() -> new UserNotFoundException("해당 유저가 존재하지 않습니다."));

        final Lookbook lookbook = lookbookRepository.findByLookbookId(lookbookId);
        if (lookbook == null) throw new DataNotFoundException("해당 룩북은 존재하지 않습니다.");
        final List<ClothesLookbook> clotheLookbooks = clothesLookbookRepository.getAllByLookbookId(lookbookId);
        final List<TagLookbook> tagLookbooks = tagLookbookRepository.getAllByLookbookId(lookbookId);
        final List<Comment> commentList = commentRepository.getAllByLookbookId(lookbookId);
        List<LookbookImage> lookbookImages = lookbookImageRepository.findAllByLookbook_LookbookId(lookbookId);
        final User user = lookbook.getUser();

        List<ClothesMiniResponse> miniResponses = new ArrayList<>();
        clotheLookbooks.forEach(clotheLookbook -> {
            final Clothes clothes = clotheLookbook.getClothes();
            final int id = clothes.getClothesId();
            ClothesMiniResponse clotheMiniResp = ClothesMiniResponse.builder()

                    .clothesId(id)
                    .name(clothes.getName())
                    .brand(clothes.getBrand())
                    .clothesImg(clothes.getClothesImg())
                    .build();
            miniResponses.add(clotheMiniResp);
        });

        List<String> lookbookImgs = lookbookImages.stream().map(LookbookImage -> LookbookImage.getImage()).collect(Collectors.toList());
        Integer likeCnt = likesRepository.countByLookbook(lookbook);

        List<TagResponse> tags = tagLookbooks.stream().map(tagLookbook -> new TagResponse(tagLookbook.getTag())).collect(Collectors.toList());

        List<CommentResponse> comments = new ArrayList<>();
        commentList.forEach(comment -> {
            CommentResponse commentResp = CommentResponse.builder()
                    .commentId(comment.getCommentId())
                    .content(comment.getContent())
                    .userNickname(comment.getUser().getNickname())
                    .userProfileImg(comment.getUser().getProfileImg())
                    .isCommentAuthor(comment.getUser().equals(sessionUser))
                    .build();
            comments.add(commentResp);
        });

        boolean isLiked = likesRepository.existsByLookbookAndUserUserId(lookbook, sessionUserId);
        boolean isBookmarked = bookmarkRepository.existsByLookbookAndUserUserId(lookbook, sessionUserId);
        boolean isFollowed = followService.isFollowing(sessionUserId, user.getUserId());

        return LookbookResponse.builder()
                .lookbookId(lookbookId)
                .userId(user.getUserId())
                .userNickname(user.getNickname())
                .userProfileImg(user.getProfileImg())
                .createdAt(lookbook.getCreatedAt())
                .clothes(miniResponses)
                .lookbookImgs(lookbookImgs)
                .likeCnt(likeCnt)
                .view(lookbook.getView())
                .tag(tags)
                .comment(comments)
                .isPublish(lookbook.isPublic())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .isFollowed(isFollowed)
                .build();
    }

    public List<LookbookCardResponse> getUserBookmarkedLookbooks(int sessionUserId) {
        userRepository.findById(sessionUserId).orElseThrow(() -> new UserNotFoundException("해당 유저가 존재하지 않습니다."));

        var lookbooks = bookmarkRepository.findByUserUserId(sessionUserId);
        List<LookbookCardResponse> response = new ArrayList<>(lookbooks.size());

        for (Bookmark lookbook : lookbooks) {
            List<LookbookImage> lookbookImages = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbook().getLookbookId());

            response.add(
                    LookbookCardResponse.builder(
                    ).lookbookId(
                            lookbook.getLookbook().getLookbookId()
                    ).image(
                            lookbookImages.get(0).getImage()
                    ).isPublic(lookbook.getLookbook().isPublic()).build()
            );
        }

        return response;
    }

    @Transactional
    public int createLookbook(Integer userId, List<MultipartFile> images, LookbookCreateRequest lookbookRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("해당 유저가 존재하지 않습니다."));
        Lookbook lookbook = Lookbook.builder()
                .user(user)
                .view(0)
                .isPublic(lookbookRequest.getIsPublic())
                .build();
        int lookbookId = lookbookRepository.save(lookbook).getLookbookId();

        images.forEach(img -> {
            String name = s3Uploader.uploadFile(img);
            LookbookImage lookbookImage = LookbookImage.builder()
                    .image(name)
                    .lookbook(lookbook)
                    .build();
            lookbookImageRepository.save(lookbookImage);
        });

        lookbookRequest.getClothes().forEach(clotheId -> {
            ClothesLookbook clothesLookbook = ClothesLookbook.builder()
                    .lookbook(lookbook)
                    .clothes(clothesRepository.findById(clotheId).orElseThrow(() -> new ClothesNotFoundException("해당 옷이 존재하지 않습니다.")))
                    .build();

            clothesLookbookRepository.save(clothesLookbook);
        });

        lookbookRequest.getTags().forEach(tagName -> {
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                tag = Tag.builder()
                        .name(tagName)
                        .build();
                tagRepository.save(tag);
            }
            TagLookbook tagLookbook = TagLookbook.builder()
                    .lookbook(lookbook)
                    .tag(tag)
                    .build();
            tagLookbookRepository.save(tagLookbook);
        });

        user.addPoint(10);
        userRepository.save(user);

        redisTemplate.opsForZSet().add("toplist", Integer.toString(lookbookId), 0);

        return lookbookId;
    }

    public PagingResponse<List<LookbookThumbnailResponse>> getLookbooks(Pageable pageable) {
        Page<Lookbook> lookbooks = lookbookRepository.findAllLookbook(pageable);
        List<LookbookThumbnailResponse> lookbookResponse = new ArrayList<>();

        lookbooks.forEach(lookbook -> {
            List<LookbookImage> lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId());
            List<String> ImageUrls = lookbookImage.stream().map(Image -> Image.getImage()).collect(Collectors.toList());
            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            User user = lookbook.getUser();
            LookbookThumbnailResponse build = LookbookThumbnailResponse.builder()
                    .isPublic(lookbook.isPublic())
                    .lookbookId(lookbook.getLookbookId())
                    .createdAt(lookbook.getCreatedAt())
                    .likeCnt(likeCnt)
                    .userNickname(user.getNickname())
                    .userProfileImg(user.getProfileImg())
                    .images(ImageUrls)
                    .build();
            lookbookResponse.add(build);
        });

        return new PagingResponse(
                HttpStatus.OK.value(),
                lookbookResponse,
                lookbooks.isFirst(),
                lookbooks.isLast(),
                lookbooks.getPageable().getPageNumber(),
                lookbooks.getTotalPages(),
                lookbooks.getSize(),
                false,
                false,
                false
        );
    }

    public PagingResponse<List<LookbookCardResponse>> getUserLoobooks(int userId, Pageable pageable) {
        Page<Lookbook> lookbooks = lookbookRepository.findAllLookbookByUserId(userId, pageable);
        List<LookbookCardResponse> lookbookResponse = new ArrayList<>();

        lookbooks.forEach(lookbook -> {
            LookbookImage lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId()).get(0);

            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            User user = lookbook.getUser();
            LookbookCardResponse build = LookbookCardResponse.builder()
                    .lookbookId(lookbook.getLookbookId())
                    .image(lookbookImage.getImage())
                    .isPublic(lookbook.isPublic())
                    .build();
            lookbookResponse.add(build);
        });

        return new PagingResponse(
                HttpStatus.OK.value(),
                lookbookResponse,
                lookbooks.isFirst(),
                lookbooks.isLast(),
                lookbooks.getPageable().getPageNumber(),
                lookbooks.getTotalPages(),
                lookbooks.getSize(),
                false,
                false,
                false
        );
    }

    @Transactional
    public void updateLookbook(Integer userId, Integer lookbookId, List<MultipartFile> images, LookbookUpdateRequest lookbookUpdateRequest) throws UserNotMatchException {
        Lookbook lookbook = lookbookRepository.findByLookbookId(lookbookId);
        if (lookbook == null) throw new DataNotFoundException("해당 룩북은 존재하지 않습니다.");
        if (lookbook.getUser().getUserId() != userId) throw new UserNotMatchException("해당 유저에게 룩북 수정 권한이 존재하지 않습니다.");

        if (!images.isEmpty()) {
            deleteExistingImages(lookbookId, lookbook);

            uploadAndSaveImages(images, lookbook);
        }

        if (!lookbookUpdateRequest.getClothes().isEmpty()) {
            clothesLookbookRepository.deleteAllByLookbook(lookbook);

            lookbookUpdateRequest.getClothes().forEach((clotheId) -> {
                Clothes clothe = clothesRepository.findByClothesId(clotheId);
                ClothesLookbook clothesLookbook = ClothesLookbook.builder()
                        .lookbook(lookbook)
                        .clothes(clothe)
                        .build();
                clothesLookbookRepository.save(clothesLookbook);
            });
        }

        if (!lookbookUpdateRequest.getTags().isEmpty()) {
            tagLookbookRepository.deleteByLookbookLookbookId(lookbookId);

            lookbookUpdateRequest.getTags().forEach(tagName -> {
                Tag tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = Tag.builder()
                            .name(tagName)
                            .build();
                    tagRepository.save(tag);
                }
                TagLookbook tagLookbook = TagLookbook.builder()
                        .tag(tag)
                        .lookbook(lookbook)
                        .build();
                tagLookbookRepository.save(tagLookbook);
            });
        }
    }

    @Transactional
    public void updateLookbook_v2(Integer userId, Integer lookbookId, List<MultipartFile> images, LookbookUpdateRequest lookbookUpdateRequest) throws UserNotMatchException {
        Lookbook lookbook = lookbookRepository.findByLookbookId(lookbookId);
        if (lookbook == null) throw new DataNotFoundException("해당 룩북은 존재하지 않습니다.");
        if (lookbook.getUser().getUserId() != userId) throw new UserNotMatchException("해당 유저에게 룩북 수정 권한이 존재하지 않습니다.");

        if (!images.isEmpty()) {
            deleteExistingImages(lookbookId, lookbook);

            List<String> ImageUrls = uploadAndSaveImages(images, lookbook);

            // 룩북 썸네일 변경
            lookbook.updateThumbnail(ImageUrls.get(0));
        }

        if (!lookbookUpdateRequest.getClothes().isEmpty()) {
            clothesLookbookRepository.deleteAllByLookbook(lookbook);

            lookbookUpdateRequest.getClothes().forEach((clotheId) -> {
                Clothes clothe = clothesRepository.findByClothesId(clotheId);
                ClothesLookbook clothesLookbook = ClothesLookbook.builder()
                        .lookbook(lookbook)
                        .clothes(clothe)
                        .build();
                clothesLookbookRepository.save(clothesLookbook);
            });
        }

        if (!lookbookUpdateRequest.getTags().isEmpty()) {
            tagLookbookRepository.deleteByLookbookLookbookId(lookbookId);

            lookbookUpdateRequest.getTags().forEach(tagName -> {
                Tag tag = tagRepository.findByName(tagName);
                if (tag == null) {
                    tag = Tag.builder()
                            .name(tagName)
                            .build();
                    tagRepository.save(tag);
                }
                TagLookbook tagLookbook = TagLookbook.builder()
                        .tag(tag)
                        .lookbook(lookbook)
                        .build();
                tagLookbookRepository.save(tagLookbook);
            });
        }

        // 캐싱된 랭킹 룩북 update
        // 1. 조회
        String cachedLookbookJson = (String) redisTemplate.opsForHash().get(lookbookRankingSnapshotKey, lookbookId);
        if (cachedLookbookJson == null) return;

        // 2. 역직렬화
        TopLookBookHashDto lookBookHashDto;
        try {
            lookBookHashDto = jacksonObjectMapper.readValue(cachedLookbookJson, TopLookBookHashDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("역직렬화 처리 중 오류가 발생했습니다");
        }

        // 3. hash 값 수정
        lookBookHashDto.setThumbNail(lookbook.getThumbnail());

        // 4. 직렬화
        String serializedLookbook;
        try{
            serializedLookbook = jacksonObjectMapper.writeValueAsString(lookBookHashDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("직렬화 처리 중 오류가 발생했습니다");
        }

        // 5. hash update
        redisTemplate.opsForHash().put(lookbookRankingSnapshotKey, lookbookId.toString(), serializedLookbook);

    }

    private List<String> uploadAndSaveImages(List<MultipartFile> images, Lookbook lookbook) {
        List<String> ImageUrls = new ArrayList<>();

        images.forEach((image) -> {
            String name = s3Uploader.uploadFile(image);
            LookbookImage lookbookImage = LookbookImage.builder()
                    .image(name)
                    .lookbook(lookbook)
                    .build();
            lookbookImageRepository.save(lookbookImage);
        });

        return ImageUrls;
    }

    private void deleteExistingImages(Integer lookbookId, Lookbook lookbook) {
        List<LookbookImage> lookbookImages = lookbookImageRepository.findAllByLookbook_LookbookId(lookbookId);
        lookbookImages.forEach((lookbookImage) -> {
            s3Uploader.deleteFile(lookbookImage.getImage());
        });
        lookbookImageRepository.deleteByLookbook(lookbook);
    }

    @Transactional
    public void deleteLookbook(Integer userId, Integer lookbookId) {
        Lookbook lookbook = lookbookRepository.findByLookbookId(lookbookId);
        if (lookbook == null) throw new DataNotFoundException("해당 룩북은 존재하지 않습니다.");
        if (lookbook.getUser().getUserId() != userId) throw new UserNotMatchException("해당 유저에게 룩북 삭제 권한이 존재하지 않습니다.");

        tagLookbookRepository.deleteByLookbookLookbookId(lookbookId); //Tag-Lookbook 삭제
        commentRepository.deleteByLookbookLookbookId(lookbookId); //comment 삭제
        bookmarkRepository.deleteByLookbookLookbookId(lookbookId); //bookmark 삭제
        likesRepository.deleteByLookbookLookbookId(lookbookId); //like 삭제
        clothesLookbookRepository.deleteAllByLookbook(lookbook); //clothesLookbook삭제

        //lookbookImage 삭제
        deleteExistingImages(lookbookId, lookbook);

        lookbookRepository.deleteByLookbookId(lookbookId);
        redisTemplate.opsForZSet().remove("toplist", Integer.toString(lookbookId));
    }

    public PageImpl<LookbookThumbnailResponse> getFollowingLookbooks(Integer userId, Pageable pageable) {
        Following followingList = followingRepository.findByUserId(userId);
        List<LookbookThumbnailResponse> lookbookResponse = new ArrayList<>();

        followingList.getFollowingList().forEach(followingId -> {
            if (userRepository.existsById(followingId)) {
                List<Lookbook> lookbooks = lookbookRepository.findByUserUserId(followingId);
                lookbooks.forEach(lookbook -> {
                    List<LookbookImage> lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId());
                    List<String> ImageUrls = lookbookImage.stream().map(Image -> Image.getImage()).collect(Collectors.toList());
                    Integer likeCnt = likesRepository.countByLookbook(lookbook);
                    User user = lookbook.getUser();
                    LookbookThumbnailResponse build = LookbookThumbnailResponse.builder()
                            .lookbookId(lookbook.getLookbookId())
                            .createdAt(lookbook.getCreatedAt())
                            .likeCnt(likeCnt)
                            .isPublic(lookbook.isPublic())
                            .userNickname(user.getNickname())
                            .userProfileImg(user.getProfileImg())
                            .images(ImageUrls)
                            .build();
                    lookbookResponse.add(build);
                });
            }
        });
        if (pageable.getSort().isSorted()) {
            // createdAt 필드를 기준으로 정렬
            Comparator<LookbookThumbnailResponse> comparator = Comparator.comparing(LookbookThumbnailResponse::getCreatedAt);
            if (pageable.getSort().getOrderFor("createdAt").getDirection().equals(Sort.Direction.DESC)) {
                // 내림차순 정렬
                comparator = comparator.reversed();
            }
            // 정렬 적용
            Collections.sort(lookbookResponse, comparator);
        }
        System.out.println(lookbookResponse);
        return new PageImpl<>(lookbookResponse, pageable, lookbookResponse.size());
    }


    public PageImpl<LookbookThumbnailResponse> getSearchLookbook(String keyword, Pageable pageable) {
        log.info("keyword={}", keyword);
        PageImpl<Lookbook> lookbooks = lookbookRepository.searchLookbook(keyword, pageable);
        List<LookbookThumbnailResponse> responseList = new ArrayList<>();
        lookbooks.forEach(lookbook -> {
            List<LookbookImage> lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId());
            List<String> ImageUrls = lookbookImage.stream().map(Image -> Image.getImage()).collect(Collectors.toList());
            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            User user = lookbook.getUser();
            LookbookThumbnailResponse build = LookbookThumbnailResponse.builder()
                    .lookbookId(lookbook.getLookbookId())
                    .createdAt(lookbook.getCreatedAt())
                    .likeCnt(likeCnt)
                    .userNickname(user.getNickname())
                    .userProfileImg(user.getProfileImg())
                    .images(ImageUrls)
                    .isPublic(lookbook.isPublic())
                    .build();
            responseList.add(build);
        });
        return new PageImpl<>(responseList, pageable, responseList.size());
    }


    public PageImpl<LookbookThumbnailResponse> getSearchLookbookByTag(ArrayList<String> tags, Pageable pageable) {
        log.info("keyword={}", tags);
        PageImpl<Lookbook> lookbooks = lookbookRepository.searchLookbookByTag(tags, pageable);
        List<LookbookThumbnailResponse> responseList = new ArrayList<>();
        lookbooks.forEach(lookbook -> {
            List<LookbookImage> lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId());
            List<String> ImageUrls = lookbookImage.stream().map(Image -> Image.getImage()).collect(Collectors.toList());
            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            User user = lookbook.getUser();
            LookbookThumbnailResponse build = LookbookThumbnailResponse.builder()
                    .lookbookId(lookbook.getLookbookId())
                    .createdAt(lookbook.getCreatedAt())
                    .likeCnt(likeCnt)
                    .userNickname(user.getNickname())
                    .userProfileImg(user.getProfileImg())
                    .images(ImageUrls)
                    .build();
            responseList.add(build);
        });
        return new PageImpl<>(responseList, pageable, responseList.size());
    }

    @Transactional
    public String manageBookmark(Integer userId, Integer lookbookId) {
        Lookbook lookbook = lookbookRepository.findByLookbookId(lookbookId);
        if (lookbook == null) throw new DataNotFoundException("해당 룩북은 존재하지 않습니다.");
        boolean isBookmarked = bookmarkRepository.existsByLookbookAndUserUserId(lookbook, userId);
        if (isBookmarked) {
            bookmarkRepository.deleteByLookbookLookbookId(lookbookId);
            return "북마크 취소 완료";
        } else {
            User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("해당 유저가 존재하지 않습니다."));
            Bookmark bookmark = Bookmark.builder()
                    .lookbook(lookbook)
                    .user(user)
                    .build();

            bookmarkRepository.save(bookmark);
            return "북마크 반영 완료";
        }
    }


    @Transactional
    public String manageLikes(Integer userId, Integer lookbookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("해댱 유저가 존재하지 않습니다."));
        Lookbook lookbook = lookbookRepository.findByLookbookId(lookbookId);
        if (lookbook == null) throw new DataNotFoundException("해당 룩북은 존재하지 않습니다.");
        boolean isLiked = likesRepository.existsByLookbookAndUserUserId(lookbook, userId);
        if (isLiked) {
            unlikes(user, lookbook);
            return "좋아요 취소 완료";
        } else {
            likes(user, lookbook);
            return "좋아요 반영 완료";
        }
    }


    @Transactional
    public void unlikes(User user, Lookbook lookbook) {
        likesRepository.deleteByLookbookAndUser(lookbook, user);

        user.minusPoint(3);
        userRepository.save(user);
        User author = lookbook.getUser();
        author.minusPoint(5);
        userRepository.save(author);

        redisTemplate.opsForZSet().incrementScore("toplist", Integer.toString(lookbook.getLookbookId()), -1);
    }

    @Transactional
    public void unlikes_v2(User user, Lookbook lookbook) {
        likesRepository.deleteByLookbookAndUser(lookbook, user);

        user.minusPoint(3);
        userRepository.save(user);
        User author = lookbook.getUser();
        author.minusPoint(5);
        userRepository.save(author);

//        lookbook.decreaseLikeCnt();
    }

    @Transactional
    public void unlikes_v3(User user, Lookbook lookbook) {
        user.minusPoint(3);
        userRepository.save(user);
        User author = lookbook.getUser();
        author.minusPoint(5);
        userRepository.save(author);

        redisTemplate.opsForZSet().incrementScore("post:like:total", Integer.toString(lookbook.getLookbookId()), -1);
        redisTemplate.opsForZSet().incrementScore("post:like:10min:", Integer.toString(lookbook.getLookbookId()), -1);
    }

    @Transactional
    public void likes(User user, Lookbook lookbook) {
        Likes likes = Likes.builder().lookbook(lookbook).user(user).build();
        likesRepository.save(likes);

        user.addPoint(3);
        userRepository.save(user);
        User author = lookbook.getUser();
        author.addPoint(5);
        userRepository.save(author);

        redisTemplate.opsForZSet().incrementScore("toplist", Integer.toString(lookbook.getLookbookId()), 1);
    }

    @Transactional
    public void likes_v2(User user, Lookbook lookbook) {
        Likes likes = Likes.builder().lookbook(lookbook).user(user).build();
        likesRepository.save(likes);

        user.addPoint(3);
        userRepository.save(user);
        User author = lookbook.getUser();
        author.addPoint(5);
        userRepository.save(author);

//        lookbook.increaseLikeCnt();
    }

    @Transactional
    public void likes_v3(User user, Lookbook lookbook) {
        user.addPoint(3);
        userRepository.save(user);
        User author = lookbook.getUser();
        author.addPoint(5);
        userRepository.save(author);

        redisTemplate.opsForZSet().incrementScore("post:like:total", Integer.toString(lookbook.getLookbookId()), 1);
        redisTemplate.opsForZSet().incrementScore("post:like:10min", Integer.toString(lookbook.getLookbookId()), 1);
    }

    public List<LookbookThumbnailResponse> getTopLookbooks() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(5);
        List<Lookbook> topLookbooks = lookbookRepository.findTopLookbook(startDate, endDate);
        List<LookbookThumbnailResponse> responseList = new ArrayList<>();

        topLookbooks.forEach(lookbook -> {
            List<LookbookImage> lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId());
            List<String> ImageUrls = lookbookImage.stream().map(Image -> Image.getImage()).collect(Collectors.toList());
            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            User user = lookbook.getUser();
            LookbookThumbnailResponse build = LookbookThumbnailResponse.builder()
                    .lookbookId(lookbook.getLookbookId())
                    .createdAt(lookbook.getCreatedAt())
                    .isPublic(lookbook.isPublic())
                    .likeCnt(likeCnt)
                    .userNickname(user.getNickname())
                    .userProfileImg(user.getProfileImg())
                    .images(ImageUrls)
                    .build();
            responseList.add(build);
        });
        return responseList;
    }

    public List<LookBookTopResponse_v2> getTopLookbooks_v2() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMinutes(10);
        List<LookBookTopResponse_v2> response = lookbookRepository.findTopLookbook_v2(startDate, endDate);

        for (int i = 0; i < response.size(); i++) {
            response.get(i).setRank(i+1);
        }

        return response;
    }


    @Transactional
    public List<LookbookThumbnailResponse> getTopLookbooksByRedis() {
        Set<ZSetOperations.TypedTuple<String>> toplist = redisTemplate.opsForZSet().reverseRangeWithScores("toplist", 0, 14);
        List<LookbookThumbnailResponse> responseList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : toplist) {
            //tuple.getValue()
            Lookbook lookbook = lookbookRepository.findByLookbookId(Integer.parseInt(tuple.getValue()));
            List<LookbookImage> lookbookImage = lookbookImageRepository.findAllByLookbook_LookbookId(lookbook.getLookbookId());
            List<String> ImageUrls = lookbookImage.stream().map(Image -> Image.getImage()).collect(Collectors.toList());
            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            User user = lookbook.getUser();
            LookbookThumbnailResponse build = LookbookThumbnailResponse.builder()
                    .lookbookId(lookbook.getLookbookId())
                    .createdAt(lookbook.getCreatedAt())
                    .isPublic(lookbook.isPublic())
                    .likeCnt(likeCnt)
                    .userNickname(user.getNickname())
                    .userProfileImg(user.getProfileImg())
                    .images(ImageUrls)
                    .build();
            responseList.add(build);
        }
        return responseList;
    }


    public List<LookBookTopResponse_v2> getTopLookbooks_v3() {
        // 1. hash에서 to20 조회(hgetall)
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(lookbookRankingSnapshotKey);


        // 2. 응답데이터 가공
        List<LookBookTopResponse_v2> responseList = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String lookbookId = (String) entry.getKey();
            String json = (String) entry.getValue();

            // hash의 value값 역직렬화
            try {
                TopLookBookHashDto lookBookHashDto = jacksonObjectMapper.readValue(json, TopLookBookHashDto.class);

                // total sorted set에서 top20의 likecnt 조회
                Double likeCount = redisTemplate.opsForZSet().score(lookbookLikeRankingTotalKey, lookbookId);
                log.info("lookbookId: {}, likeCount: {}", lookbookId, likeCount);

                LookBookTopResponse_v2 response = LookBookTopResponse_v2.builder()
                        .rank(lookBookHashDto.getRank())
                        .lookbookId(lookBookHashDto.getLookbookId())
                        .userId(lookBookHashDto.getUserId())
                        .userNickname(lookBookHashDto.getUserNickname())
                        .userProfileImg(lookBookHashDto.getUserProfileImg())
                        .thumbNail(lookBookHashDto.getThumbNail())
                        .likeCnt(likeCount == null ? 0 : likeCount.intValue())
                        .createdAt(lookBookHashDto.getCreatedAt())
                        .build();

                responseList.add(response);

            } catch (JsonProcessingException e) {
                throw new RuntimeException("직렬화 처리 중 오류가 발생했습니다");
            }
        }

        responseList.sort(Comparator.comparing(LookBookTopResponse_v2::getRank));
        log.info("responseList: {}", responseList);
        return responseList;
    }


    // Redis N+1문제 개선 버전
    public List<LookBookTopResponse_v2> getTopLookbooks_v4() {
        // 1. Hash에서 to20 조회(hgetall)
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(lookbookRankingSnapshotKey);

        // 2. 전체 좋아요 집계 Sorted Set에서 top20의 좋아요 조회
        // Hash에서 받아온 entry 객체들에서 lookbookId만 추출해서 배열로 생성
        List<Object> lookbookIds = new ArrayList<>(entries.keySet());
        Object[] lookbookIdArray = lookbookIds.toArray();

        List<Double> likeCounts = redisTemplate.opsForZSet().score(lookbookLikeRankingTotalKey, lookbookIdArray);


        // 3. 응답데이터 가공
        List<LookBookTopResponse_v2> responseList = new ArrayList<>();
        for (int i = 0; i < lookbookIds.size(); i++) {
            String lookbookId = (String) lookbookIds.get(i);
            String json=(String) entries.get(lookbookId);
            Double likeCount = likeCounts.get(i);
            log.info("lookbookId: {}, likeCount: {}", lookbookId, likeCount);

            try {
                //Hash의 value값 역직렬화
                TopLookBookHashDto lookBookHashDto = jacksonObjectMapper.readValue(json, TopLookBookHashDto.class);

                LookBookTopResponse_v2 response = LookBookTopResponse_v2.builder()
                        .rank(lookBookHashDto.getRank())
                        .lookbookId(lookBookHashDto.getLookbookId())
                        .userId(lookBookHashDto.getUserId())
                        .userNickname(lookBookHashDto.getUserNickname())
                        .userProfileImg(lookBookHashDto.getUserProfileImg())
                        .thumbNail(lookBookHashDto.getThumbNail())
                        .likeCnt(likeCount == null ? 0 : likeCount.intValue())
                        .createdAt(lookBookHashDto.getCreatedAt())
                        .build();

                responseList.add(response);

            } catch (JsonProcessingException e) {
                throw new RuntimeException("직렬화 처리 중 오류가 발생했습니다");
            }
        }


        responseList.sort(Comparator.comparing(LookBookTopResponse_v2::getRank));
        log.info("responseList: {}", responseList);
        return responseList;
    }

    @Transactional
    public void updateTopLookbook() {
        // 1. 10분간 집계된 sorted set에서 상위 20개 게시물Id 가져오기 (postId)
        Set<String> range = redisTemplate.opsForZSet().reverseRange(lookbookLikeRanking10MinKey, 0, 19);
        List<Integer> top20LookbookIds = (range == null) ? Collections.emptyList() :
                range.stream().map(Integer::valueOf).collect(Collectors.toList());
        log.info("top20LookbookIds: {}",top20LookbookIds);

        if (top20LookbookIds.isEmpty()) throw new DataNotFoundException("랭킹 룩북의 데이터가 존재하지 않습니다.");

        redisTemplate.delete(lookbookLikeRanking10MinKey); //sorted set 초기화

        // 2. 게시물Id로 mariaDB에서 게시물+유저 상세정보 가져오기 c.f top20PostIds와 순서 다름
        List<Lookbook> lookbookList = lookbookRepository.findAllByLookbookIdInWithUser(top20LookbookIds);

        // 3. hash에 상위 20개 게시물 상세정보 넣기
        Map<Integer, Lookbook> lookbookMap = lookbookList.stream()
                .collect(Collectors.toMap(Lookbook::getLookbookId, l -> l));

        Map<String, String> hashData = new HashMap<>();
        //위의 db 조회(in절)은 순서를 보장하지 않기 때문에
        //sorted set에서 받아온 List순서대로(순위 순서) map에서 꺼내서 map의 상세정보와 순위를 함께 직렬화해서 hashmap에 저장
        for (int i = 0; i < top20LookbookIds.size(); i++) {
            Integer lookbookId = top20LookbookIds.get(i);
            Lookbook lookbook = lookbookMap.get(lookbookId);

            if (lookbook != null) {
                TopLookBookHashDto dto = new TopLookBookHashDto(lookbook);
                dto.setRank(i + 1);

                //직렬화
                try {
                    String jsonValue = jacksonObjectMapper.writeValueAsString(dto);
                    hashData.put(lookbookId.toString(), jsonValue);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("직렬화 처리 중 오류가 발생했습니다");
                }
            }
        }

        if (!hashData.isEmpty()) {
            redisTemplate.delete(lookbookRankingSnapshotKey); //기존 데이터 삭제
            redisTemplate.opsForHash().putAll(lookbookRankingSnapshotKey, hashData); //HashMap에 저장한 정보들 모두 hash에 넣기
        }

        // 4. sets에 상위 20개 게시물의 유저Id와 게시물Id 매핑하기(검색용)
        Set<String> oldUserIds = redisTemplate.opsForSet().members(lookbookUserIndexKey);
        if (oldUserIds != null && !oldUserIds.isEmpty()) {
            redisTemplate.delete(oldUserIds); // 이전 유저Id set에 담긴 lookbookId들 삭제(allUsersKey안의 데이터)
            redisTemplate.delete(lookbookUserIndexKey); // userId set들 자체 삭제
        }


        for (Lookbook lookbook : lookbookList) {
            String userId = String.valueOf(lookbook.getUser().getUserId());
            String lookbookId = String.valueOf(lookbook.getLookbookId());

            redisTemplate.opsForSet().add(userLookbookMappingKey + userId, lookbookId);
            redisTemplate.expire(userLookbookMappingKey + userId, 1, TimeUnit.HOURS); //TTL 설정

            redisTemplate.opsForSet().add(lookbookUserIndexKey, userId);
        }

    }

    public void fetchRedis() {
        List<Lookbook> list = lookbookRepository.findAll();
        list.forEach(lookbook -> {
            Integer likeCnt = likesRepository.countByLookbook(lookbook);
            redisTemplate.opsForZSet().add("toplist", Integer.toString(lookbook.getLookbookId()), likeCnt);
        });
    }
}
