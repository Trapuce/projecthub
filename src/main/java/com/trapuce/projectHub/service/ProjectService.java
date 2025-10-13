package com.trapuce.projectHub.service;

import com.trapuce.projectHub.dto.request.CreateProjectRequest;
import com.trapuce.projectHub.dto.response.ProjectResponse;
import com.trapuce.projectHub.entity.Project;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.ProjectStatus;
import com.trapuce.projectHub.enums.Role;
import com.trapuce.projectHub.exception.ProjectAccessDeniedException;
import com.trapuce.projectHub.exception.ResourceNotFoundException;
import com.trapuce.projectHub.repository.ProjectRepository;
import com.trapuce.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        log.info("Fetching all projects with pagination");
        Page<Project> projects = projectRepository.findAll(pageable);
        return projects.map(this::convertToProjectResponse);
    }
    
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        log.info("Fetching project by ID: {}", id);
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        return convertToProjectResponse(project);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByUser(String userEmail, Pageable pageable) {
        log.info("Fetching projects for user: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Page<Project> projects = projectRepository.findByOwnerOrMember(user, pageable);
        return projects.map(this::convertToProjectResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> searchProjects(String search, Pageable pageable) {
        log.info("Searching projects with term: {}", search);
        Page<Project> projects = projectRepository.findBySearchTerm(search, pageable);
        return projects.map(this::convertToProjectResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByStatus(ProjectStatus status, Pageable pageable) {
        log.info("Fetching projects by status: {}", status);
        Page<Project> projects = projectRepository.findByStatus(status, pageable);
        return projects.map(this::convertToProjectResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByPriority(Priority priority, Pageable pageable) {
        log.info("Fetching projects by priority: {}", priority);
        Page<Project> projects = projectRepository.findByPriority(priority, pageable);
        return projects.map(this::convertToProjectResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjectsByDueDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("Fetching projects by due date range: {} to {}", startDate, endDate);
        Page<Project> projects = projectRepository.findByDueDateBetween(startDate, endDate, pageable);
        return projects.map(this::convertToProjectResponse);
    }
    
    @Transactional(readOnly = true)
    public List<ProjectResponse> getOverdueProjects() {
        log.info("Fetching overdue projects");
        List<Project> projects = projectRepository.findOverdueProjects(ProjectStatus.IN_PROGRESS, LocalDate.now());
        return projects.stream().map(this::convertToProjectResponse).toList();
    }
    
    public ProjectResponse createProject(CreateProjectRequest request, String userEmail) {
        log.info("Creating new project: {}", request.getName());
        
        User owner = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setPriority(request.getPriority());
        project.setStartDate(request.getStartDate());
        project.setDueDate(request.getDueDate());
        project.setStatus(ProjectStatus.TODO);
        project.setOwner(owner);
        
        // Add members if specified
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            List<User> members = userRepository.findAllById(request.getMemberIds());
            project.setMembers(members);
        }
        
        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with ID: {}", savedProject.getId());
        
        return convertToProjectResponse(savedProject);
    }
    
    public ProjectResponse updateProject(Long id, CreateProjectRequest request, String userEmail) {
        log.info("Updating project with ID: {}", id);
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        
        // Check permissions
        if (!canModifyProject(project, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this project");
        }
        
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setPriority(request.getPriority());
        project.setStartDate(request.getStartDate());
        project.setDueDate(request.getDueDate());
        
        // Update members if specified
        if (request.getMemberIds() != null) {
            List<User> members = userRepository.findAllById(request.getMemberIds());
            project.setMembers(members);
        }
        
        Project updatedProject = projectRepository.save(project);
        log.info("Project updated successfully with ID: {}", updatedProject.getId());
        
        return convertToProjectResponse(updatedProject);
    }
    
    public ProjectResponse updateProjectStatus(Long id, ProjectStatus status, String userEmail) {
        log.info("Updating project status for ID: {} to {}", id, status);
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        
        // Check permissions
        if (!canModifyProject(project, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this project");
        }
        
        project.setStatus(status);
        if (status == ProjectStatus.COMPLETED) {
            project.setCompletedAt(LocalDate.now());
        }
        
        Project updatedProject = projectRepository.save(project);
        log.info("Project status updated successfully for ID: {}", id);
        
        return convertToProjectResponse(updatedProject);
    }
    
    public void deleteProject(Long id, String userEmail) {
        log.info("Deleting project with ID: {}", id);
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        
        // Check permissions - only owner or admin can delete
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!project.getOwner().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new ProjectAccessDeniedException("Only project owner or admin can delete projects");
        }
        
        projectRepository.delete(project);
        log.info("Project deleted successfully with ID: {}", id);
    }
    
    public ProjectResponse addMemberToProject(Long projectId, Long userId, String userEmail) {
        log.info("Adding member {} to project {}", userId, projectId);
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        
        // Check permissions
        if (!canModifyProject(project, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this project");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        if (!project.getMembers().contains(user)) {
            project.getMembers().add(user);
            projectRepository.save(project);
            log.info("Member added successfully to project {}", projectId);
        }
        
        return convertToProjectResponse(project);
    }
    
    public ProjectResponse removeMemberFromProject(Long projectId, Long userId, String userEmail) {
        log.info("Removing member {} from project {}", userId, projectId);
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        
        // Check permissions
        if (!canModifyProject(project, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this project");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        if (project.getMembers() != null) {
            project.getMembers().remove(user);
        }
        projectRepository.save(project);
        log.info("Member removed successfully from project {}", projectId);
        
        return convertToProjectResponse(project);
    }
    
    @Transactional(readOnly = true)
    public long getProjectCountByStatus(ProjectStatus status) {
        return projectRepository.countByStatus(status);
    }
    
    private boolean canModifyProject(Project project, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return project.getOwner().getId().equals(user.getId()) || 
               (project.getMembers() != null && project.getMembers().contains(user)) || 
               user.getRole() == Role.ADMIN;
    }
    
    private ProjectResponse convertToProjectResponse(Project project) {
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getStatus(),
            project.getPriority(),
            project.getStartDate(),
            project.getDueDate(),
            project.getCompletedAt(),
            project.getCreatedAt(),
            project.getUpdatedAt(),
            convertToUserResponse(project.getOwner()),
            project.getMembers() != null ? project.getMembers().stream().map(this::convertToUserResponse).toList() : List.of(),
            project.getTasks() != null ? (long) project.getTasks().size() : 0L,
            project.getTasks() != null ? project.getTasks().stream()
                .filter(task -> task.getStatus().name().equals("COMPLETED")).count() : 0L
        );
    }
    
    private com.trapuce.projectHub.dto.response.UserResponse convertToUserResponse(User user) {
        return new com.trapuce.projectHub.dto.response.UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            user.getStatus(),
            user.getAvatar(),
            user.getPhone(),
            user.getDepartment(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
