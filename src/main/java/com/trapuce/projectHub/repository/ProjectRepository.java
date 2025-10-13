package com.trapuce.projectHub.repository;

import com.trapuce.projectHub.entity.Project;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);
    
    Page<Project> findByPriority(Priority priority, Pageable pageable);
    
    Page<Project> findByOwner(User owner, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE :user MEMBER OF p.members")
    Page<Project> findByMember(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Project> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.dueDate BETWEEN :startDate AND :endDate")
    Page<Project> findByDueDateBetween(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate, 
                                      Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.status = :status AND p.dueDate < :date")
    List<Project> findOverdueProjects(@Param("status") ProjectStatus status, 
                                     @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status")
    long countByStatus(@Param("status") ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.owner = :owner OR :owner MEMBER OF p.members")
    Page<Project> findByOwnerOrMember(@Param("owner") User owner, Pageable pageable);
}
