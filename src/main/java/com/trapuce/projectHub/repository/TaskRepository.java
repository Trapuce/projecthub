package com.trapuce.projectHub.repository;

import com.trapuce.projectHub.entity.Project;
import com.trapuce.projectHub.entity.Task;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    Page<Task> findByProject(Project project, Pageable pageable);
    
    Page<Task> findByAssignee(User assignee, Pageable pageable);
    
    Page<Task> findByCreator(User creator, Pageable pageable);
    
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    
    Page<Task> findByPriority(Priority priority, Pageable pageable);
    
    Page<Task> findByParentTask(Task parentTask, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.parentTask IS NULL")
    Page<Task> findRootTasks(Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Task> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    Page<Task> findByDueDateBetween(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate, 
                                   Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.dueDate < :date")
    List<Task> findOverdueTasks(@Param("status") TaskStatus status, 
                               @Param("date") LocalDate date);
    
    @Query("SELECT t FROM Task t WHERE t.project = :project AND (t.assignee = :user OR t.creator = :user)")
    Page<Task> findByProjectAndUser(@Param("project") Project project, 
                                   @Param("user") User user, 
                                   Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(@Param("status") TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project = :project AND t.status = :status")
    long countByProjectAndStatus(@Param("project") Project project, 
                                @Param("status") TaskStatus status);
}
