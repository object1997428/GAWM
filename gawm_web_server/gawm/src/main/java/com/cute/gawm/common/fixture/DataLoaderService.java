package com.cute.gawm.common.fixture;

import com.cute.gawm.common.BaseEntity;
import com.cute.gawm.domain.like.entity.Likes;
import com.cute.gawm.domain.lookbook.entity.Lookbook;
import com.cute.gawm.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class DataLoaderService {
    @PersistenceContext
    private EntityManager entityManager;
    private static final int BATCH_SIZE = 1000;

    @Transactional
    public void loadData() {
        // 1. User 1만개 생성
        log.info("User 데이터 생성 시작");
        List<User> users = createUsers(10000);
        batchInsert(users);
        log.info("User 데이터 생성 종료: {}개",users.size());

        // 1-2. DB에서 다시 조회
        users=entityManager.createQuery("SELECT u FROM User u",User.class)
                .getResultList();

        // 2. Lookbook 3만개 생성
        log.info("Lookbook 데이터 생성 시작");
        List<Lookbook> lookbooks = createLookbooks(30000, users);
        batchInsert(lookbooks);
        log.info("Lookbook 데이터 생성 종료: {}개",lookbooks.size());

        // 2-2. DB에서 다시 조회
        lookbooks=entityManager.createQuery("SELECT l FROM Lookbook l",Lookbook.class)
                .getResultList();

        // 3. Like 100만개 생성
        log.info("Like 데이터 생성 시작");
        createOldLikesInBatch(600000, users, lookbooks);
        create20MinLikesInBatch(400000, users, lookbooks);
        log.info("Like 데이터 생성 종료");
    }


    private List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            User user = User.builder()
                    .nickname("User_" + i)
                    .profileImg("https://example.com/profile_" + i + ".jpg")
                    .build();
            users.add(user);
        }
        return users;
    }

    private List<Lookbook> createLookbooks(int count, List<User> users) {
        List<Lookbook> lookbooks = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            Lookbook lookbook = Lookbook.builder()
                    .user(randomUser) //랜덤한 유저로 세팅
                    .thumbnail("https://example.com/thumbnail_" + i + ".jpg")
                    .build();
            lookbooks.add(lookbook);
        }
        return lookbooks;
    }

    // 20분 이전
    private void createOldLikesInBatch(int totalCount, List<User> users, List<Lookbook> lookbooks) {
        Random random = new Random();
        int batchCount = 0;

        for (int i = 1; i <= totalCount; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            Lookbook randomLookbook=lookbooks.get(random.nextInt(lookbooks.size()));

            Likes like=Likes.builder()
                    .user(randomUser)
                    .lookbook(randomLookbook)
                    .build();

            setOldTimestamp(like,random);

            entityManager.persist(like);
            batchCount++;

            if (i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
                log.info("Likes 진행률: {} / {}",i,totalCount);
            }
        }

        entityManager.flush();
        entityManager.clear();
    }


    // 20분 이전~ 30분 이후 에 update된 Like 생성 -- 80퍼는 인기있는 룩북에 부여 나머지 20퍼는 랜덤 룩북에 부여
    // 20분 이전 : redis에서 이전 [0,10) [10,20), [20,30), [30,40), [40,50), [50,60) 구간을 집계하기때문에, (집계시점, 현재]의 데이터도 넣기 위해서
    // 30분 이후 : 데이터 로드 시점과 성능 측정 시점 차이를 고려하기 위해
    private void create20MinLikesInBatch(int totalCount, List<User> users, List<Lookbook> lookbooks) {
        Random random = new Random();
        int batchCount = 0;


        int hotLookbookCount=1000;
        List<Lookbook> hotLookbooks=lookbooks.subList(0,hotLookbookCount); //lookbook에서 앞에 1000개 따로 담기

        for (int i = 1; i <= totalCount; i++) {
            User randomUser = users.get(random.nextInt(users.size()));

            // 최근 10분동안의 like 중 80%는 인기있는 Lookbook에게, 20%는 랜덤 lookbook에게 주기
            Lookbook randomLookbook=random.nextDouble()<0.8
                    ? hotLookbooks.get(random.nextInt(hotLookbooks.size())) // 80퍼는 앞의 hotLookbook 중 하나
                    : lookbooks.get(random.nextInt(lookbooks.size())); // 20퍼는 전체 Lookbook 중 하나

            Likes like=Likes.builder()
                    .user(randomUser)
                    .lookbook(randomLookbook)
                    .build();

            setRecentTimestamp(like,random);

            entityManager.persist(like);
            batchCount++;

            if (i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
                log.info("Likes 진행률: {} / {}",i,totalCount);
            }
        }

        entityManager.flush();
        entityManager.clear();
    }

    // 20분 이전
    private void setOldTimestamp(Likes like, Random random) {
        try {
            // 11분 ~ 24시간 전 랜덤
            int minutesAgo = 11 + random.nextInt(24 * 60 - 21); // 21 ~ 1440분
            LocalDateTime oldTime = LocalDateTime.now().minusMinutes(minutesAgo);

            Field createdAtField = BaseEntity.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(like, oldTime);

            Field updatedAtField = BaseEntity.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(like, oldTime);

        } catch (Exception e) {
            log.error("타임스탬프 설정 실패", e);
        }
    }

    // 최근 20분 타임스탬프 설정 (리플렉션 사용)
    private void setRecentTimestamp(Likes like, Random random) {
        try {
            // 0 ~ 10분 전 랜덤
            int secondsAgo = random.nextInt(50*60)-(20*60); // -(0 ~ 1200초)(20분), +(0~3000초)(30분)
            // - 20분 ~ +30분
            LocalDateTime recentTime = LocalDateTime.now().plusSeconds(secondsAgo);

            Field createdAtField = BaseEntity.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(like, recentTime);

            Field updatedAtField = BaseEntity.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(like, recentTime);

        } catch (Exception e) {
            log.error("타임스탬프 설정 실패", e);
        }
    }

    private <T> void batchInsert(List<T> entities) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));

            if ((i + 1) % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

}
