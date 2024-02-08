package com.cute.gawm.domain.live.service;

import com.cute.gawm.common.exception.DataMismatchException;
import com.cute.gawm.common.exception.DataNotFoundException;
import com.cute.gawm.common.exception.UserNotFoundException;
import com.cute.gawm.common.exception.UserNotMatchException;
import com.cute.gawm.domain.clothes.dto.response.ClothesInfoResponse;
import com.cute.gawm.domain.clothes.entity.Clothes;
import com.cute.gawm.domain.clothes.repository.ClothesDetailRepository;
import com.cute.gawm.domain.clothes.repository.ClothesRepository;
import com.cute.gawm.domain.following.entity.Following;
import com.cute.gawm.domain.following.repository.FollowingRepository;
import com.cute.gawm.domain.live.dto.request.LiveCreateRequest;
import com.cute.gawm.domain.live.entity.Live;
import com.cute.gawm.domain.live.repository.LiveRepository;
import com.cute.gawm.domain.user.entity.User;
import com.cute.gawm.domain.user.repository.UserRepository;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;

@Service
@AllArgsConstructor
public class LiveService {
    private final LiveRepository liveRepository;
    private final FollowingRepository followingRepository;
    private final UserRepository userRepository;
    private final ClothesRepository clothesRepository;
    private final ClothesDetailRepository clothesDetailRepository;
//
//    @Value("${OPENVIDU_URL}")
//    private String OPENVIDU_URL;
//
//    @Value("${OPENVIDU_SECRET}")
//    private String OPENVIDU_SECRET;
//
//    private OpenVidu openvidu;

//    @PostConstruct
//    public void init() {
//        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
//    }

    public PageImpl<Live> getFollowingLive(Integer userId, Pageable pageable){
        Following followings = followingRepository.findByUserId(userId);
        List<Live> liveList = new ArrayList<>();
        followings.getFollowingList().forEach(followingId -> {
            User user = userRepository.findById(followingId).orElseThrow(() -> new UserNotFoundException("해당 유저가 존재하지 않습니다."));
            Live live = liveRepository.findByUserAndIsDeletedFalse(user);
            if(live!=null){
                liveList.add(live);
            }
        });

        return new PageImpl<>(liveList, pageable, liveList.size());
    }

    @Transactional
    public void createLive(Integer userId, LiveCreateRequest liveCreateRequest, Map<String, Object> params) throws OpenViduJavaClientException, OpenViduHttpException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("해당 유저가 존재하지 않습니다."));
        Live live = liveRepository.findByUserAndIsDeletedFalse(user);
        if(live!=null){
            throw new DataMismatchException("해당 유저에게 아직 종료되지 않은 라이브가 존재합니다.");
        }
        SessionProperties properties = SessionProperties.fromJson(params).build();
//        Session session = openvidu.createSession(properties);

        live = Live.builder()
                .name(liveCreateRequest.getName())
                .user(user)
                .build();

        liveRepository.save(live);
    }
    @Transactional
    public void deleteLive(Integer userId, Integer liveId){
        Live live=liveRepository.findByLiveId(liveId);
        if(live.getUser().getUserId()!=userId) throw new UserNotMatchException("해당 유저에게 라이브 삭제 권한이 존재하지 않습니다.");
//        live.endLive(); //후 처리 전제
        liveRepository.deleteByLiveId(liveId);
    }

    public List<ClothesInfoResponse> getLiveUserAllCloset(Integer liveId) {
        Live live=liveRepository.findByLiveId(liveId);
        if(live==null){
            throw new DataNotFoundException("해당 라이브가 존재하지 않습니다.");
        }
        List<Clothes> clothesList = clothesRepository.findByUserUserId(live.getUser().getUserId());
        return clothesList.stream()
                .map(clothes -> new ClothesInfoResponse(clothes, clothesDetailRepository.findByClothesId(clothes.getClothesId())))
                .collect(Collectors.toList());
    }
}
