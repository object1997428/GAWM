package com.cute.gawm.domain.live.repository;

import com.cute.gawm.domain.live.entity.Live;
import com.cute.gawm.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveRepository extends JpaRepository<Live, Integer> {
    List<Live> findByUser(User user);

    Optional<Live> findByUserAndIsDeletedFalse(User user);

    Live findByLiveId(Integer liveId);

    void deleteByLiveId(Integer liveId);

    Live findLiveByUser(User user);

}
