package com.cute.gawm.common.fixture;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDataLoaderService {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    private final String lookbookLikeRankingTotalKey = "lookbook:like:ranking:total";
    private final String lookbookLikeRanking10MinKey = "lookbook:like:ranking:10min";

    @Transactional(readOnly = true)
    public void loadLikeCountToRedis() {
        loadLikeCountTotal();
        loadLikeCount10Min();
    }


    private void loadLikeCountTotal() {

        // 1. DB에서 집계
        List<LookbookLikeCnt> counts = fetchLikeCountsFromDB();

        // 2. Redis에 저장
        saveLikeCountsToRedis(lookbookLikeRankingTotalKey, counts);

        // 3. 검증
        validateData();
    }

    private void loadLikeCount10Min() {

        // 1. DB에서 집계
        List<LookbookLikeCnt> counts = fetch10MinLikeCountsFromDB();

        // 2. Redis에 저장
        saveLikeCountsToRedis(lookbookLikeRanking10MinKey, counts);

        // 3. 검증
        validate10MinData();
    }


    private List<LookbookLikeCnt> fetchLikeCountsFromDB() {
        String sql = "SELECT lookbook_id, COUNT(*) as like_count " +
                "FROM likes " +
                "GROUP BY lookbook_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new LookbookLikeCnt(rs.getInt("lookbook_id"),
                        rs.getInt("like_count"))
        );
    }


    private List<LookbookLikeCnt> fetch10MinLikeCountsFromDB() {
        //이전 [0,10) [10,20), [20,30), [30,40), [40,50), [50,60) 구간을 조회해서 redis에 넣기
        LocalDateTime now = LocalDateTime.now();
        int endMinute = (now.getMinute() / 10) * 10; // ex) 현재 12분이라면: (12 / 10) * 10 = 1*10 = 10

        LocalDateTime endTime=now.withMinute(endMinute).withSecond(0).withNano(0); // HH:10:00
        LocalDateTime startTime=endTime.minusMinutes(10);

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String sql = "SELECT lookbook_id, COUNT(*) as like_count " +
                "FROM likes " +
                "WHERE updated_at >= ? AND updated_at < ? " +
                "GROUP BY lookbook_id";

        List<LookbookLikeCnt> counts=jdbcTemplate.query(sql,
                new Object[]{startTimeStr,endTimeStr},
                (rs, rowNum) -> new LookbookLikeCnt(
                        rs.getInt("lookbook_id"),
                        rs.getInt("like_count")
                )
        );

        // 기존 데이터 삭제
        return counts;
    }

    private void saveLikeCountsToRedis(String key, List<LookbookLikeCnt> counts) {
        redisTemplate.delete(key);
        counts.forEach(count ->
                redisTemplate.opsForZSet().add(
                        key,
                        String.valueOf(count.getLookbookId()),
                        (double) count.getLikeCnt()
                )
        );
    }

    private void validateData() {
        log.info("데이터 검증 시작");

        //1. 전체 lookbook 개수 확인
        // db
        Long totalLookbooks = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT lookbook_id) FROM likes",
                Long.class
        );

        //sorted set에서의 lookbook
        Long redisSize = redisTemplate.opsForZSet().zCard(lookbookLikeRankingTotalKey);

        log.info("전체 Lookbook 개수 -- DB: {}, Redis: {}", totalLookbooks, redisSize);

        if (!totalLookbooks.equals(redisSize)) {
            throw new IllegalArgumentException("Lookbook 전체 개수 불일치");
        }

        //2. LookbooId와 likeCnt 일치 검증 (랜덤 10개)
        List<Integer> sampleIds = jdbcTemplate.queryForList(
                "SELECT DISTINCT lookbook_id FROM likes ORDER BY RAND() LIKE 10",
                Integer.class
        );

        int mismatchCount = 0;
        for (Integer lookbookId : sampleIds) {
            Long dbCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM likes WHERE lookbook_id = ?",
                    Long.class,
                    lookbookId);

            Double redisScore = redisTemplate.opsForZSet()
                    .score(lookbookLikeRankingTotalKey, String.valueOf(lookbookId));

            if (redisScore == null || !dbCount.equals(redisScore.longValue())) {
                log.error("불일치 발견 -- Lookbook {}: DB={}, Redis={}", lookbookId, dbCount, redisScore);
                mismatchCount++;
            }

            if (mismatchCount > 0) {
                throw new IllegalStateException(
                        String.format("샘플 검증 실패: %d개 불일치", mismatchCount)
                );
            }

        }

        log.info("데이터 검증 완료 - 모두 일치");

    }

    private void validate10MinData() {
        log.info("=== 10분 랭킹 검증 시작 ===");


        log.info("=== 10분 랭킹 검증 종료 ===");
    }


    @Getter
    @AllArgsConstructor
    private static class LookbookLikeCnt {
        private int lookbookId;
        private int likeCnt;
    }

}
