package com.skytakeout.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserRepository {
    /**
     * 根据动态时间范围统计新增用户数量，使用id作为主键计数
     * */
    @Query("select count(id) from User where createTime > :begin and createTime < :end")
    Integer countNewUsersByTimeRange(@Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);

    /**
     * 统计截至某个时间点的总用户数
     */
    @Query("select count(u.id) from User u where u.createTime <= :endTime")
    Integer countTotalUsersUntil(@Param("endTime") LocalDateTime endTime);
}
