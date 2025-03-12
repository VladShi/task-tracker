package com.tasktracker.scheduler.repository;

import com.tasktracker.scheduler.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN FETCH u.tasks t
        WHERE t.completed = false
           OR (t.completed = true AND t.completedAt BETWEEN :startTime AND :endTime)
        """)
    Slice<User> findUsersWithUncompletedOrCompletedInPeriodTasks(
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );
}
