package com.hotelerp.userservice.repository;

import com.hotelerp.common.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByRoomId(Long roomId);
    List<Task> findByStatus(Task.TaskStatus status);
}
