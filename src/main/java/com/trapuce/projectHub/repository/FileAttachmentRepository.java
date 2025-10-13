package com.trapuce.projectHub.repository;

import com.trapuce.projectHub.entity.FileAttachment;
import com.trapuce.projectHub.entity.Project;
import com.trapuce.projectHub.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    
    Page<FileAttachment> findByProject(Project project, Pageable pageable);
    
    Page<FileAttachment> findByTask(Task task, Pageable pageable);
    
    @Query("SELECT f FROM FileAttachment f WHERE f.project = :project OR f.task IN " +
           "(SELECT t FROM Task t WHERE t.project = :project)")
    Page<FileAttachment> findByProjectOrProjectTasks(@Param("project") Project project, Pageable pageable);
    
    @Query("SELECT f FROM FileAttachment f WHERE " +
           "LOWER(f.originalFileName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<FileAttachment> findByFileNameContaining(@Param("search") String search, Pageable pageable);
    
    List<FileAttachment> findByContentType(String contentType);
    
    @Query("SELECT SUM(f.fileSize) FROM FileAttachment f WHERE f.project = :project")
    Long getTotalFileSizeByProject(@Param("project") Project project);
}
