package com.mipt.sharipovrazil.repository;

import com.mipt.sharipovrazil.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    List<TaskAttachment> findByTask_Id(Long taskId);
}