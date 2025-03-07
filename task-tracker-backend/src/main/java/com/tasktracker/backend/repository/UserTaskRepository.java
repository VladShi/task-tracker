package com.tasktracker.backend.repository;

import com.tasktracker.backend.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    @Query("SELECT ut FROM UserTask ut WHERE ut.user.id = :id")
    List<UserTask> findTasksByUserId(@Param("id") long id);

}
