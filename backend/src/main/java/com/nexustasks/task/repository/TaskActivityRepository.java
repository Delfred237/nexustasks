package com.nexustasks.task.repository;

import com.nexustasks.task.entity.TaskActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    Page<TaskActivity> findByTaskIdAndDeletedFalseOrderByCreatedAtDesc(Long taskId, Pageable pageable);
}