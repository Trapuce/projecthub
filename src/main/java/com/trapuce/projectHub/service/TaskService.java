package com.trapuce.projectHub.service;

import com.trapuce.projectHub.dto.request.CreateTaskRequest;
import com.trapuce.projectHub.dto.response.TaskResponse;
import com.trapuce.projectHub.entity.Project;
import com.trapuce.projectHub.entity.Task;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.Role;
import com.trapuce.projectHub.enums.TaskStatus;
import com.trapuce.projectHub.exception.ProjectAccessDeniedException;
import com.trapuce.projectHub.exception.ResourceNotFoundException;
import com.trapuce.projectHub.repository.ProjectRepository;
import com.trapuce.projectHub.repository.TaskRepository;
import com.trapuce.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        log.info("Fetching all tasks with pagination");
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        log.info("Fetching task by ID: {}", id);
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        return convertToTaskResponse(task);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProject(Long projectId, Pageable pageable) {
        log.info("Fetching tasks for project ID: {}", projectId);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        
        Page<Task> tasks = taskRepository.findByProject(project, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByUser(String userEmail, Pageable pageable) {
        log.info("Fetching tasks for user: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Page<Task> tasks = taskRepository.findByAssignee(user, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByProjectAndUser(Long projectId, String userEmail, Pageable pageable) {
        log.info("Fetching tasks for project {} and user {}", projectId, userEmail);
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Page<Task> tasks = taskRepository.findByProjectAndUser(project, user, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(String search, Pageable pageable) {
        log.info("Searching tasks with term: {}", search);
        Page<Task> tasks = taskRepository.findBySearchTerm(search, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByStatus(TaskStatus status, Pageable pageable) {
        log.info("Fetching tasks by status: {}", status);
        Page<Task> tasks = taskRepository.findByStatus(status, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByPriority(Priority priority, Pageable pageable) {
        log.info("Fetching tasks by priority: {}", priority);
        Page<Task> tasks = taskRepository.findByPriority(priority, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByDueDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.info("Fetching tasks by due date range: {} to {}", startDate, endDate);
        Page<Task> tasks = taskRepository.findByDueDateBetween(startDate, endDate, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        log.info("Fetching overdue tasks");
        List<Task> tasks = taskRepository.findOverdueTasks(TaskStatus.IN_PROGRESS, LocalDate.now());
        return tasks.stream().map(this::convertToTaskResponse).toList();
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getRootTasks(Pageable pageable) {
        log.info("Fetching root tasks (no parent)");
        Page<Task> tasks = taskRepository.findRootTasks(pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getSubtasks(Long parentTaskId, Pageable pageable) {
        log.info("Fetching subtasks for parent task ID: {}", parentTaskId);
        Task parentTask = taskRepository.findById(parentTaskId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent task not found with ID: " + parentTaskId));
        
        Page<Task> tasks = taskRepository.findByParentTask(parentTask, pageable);
        return tasks.map(this::convertToTaskResponse);
    }
    
    public TaskResponse createTask(CreateTaskRequest request, String userEmail) {
        log.info("Creating new task: {}", request.getTitle());
        
        User creator = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + request.getProjectId()));
        
        // Check if user can create tasks in this project
        if (!canModifyProject(project, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to create tasks in this project");
        }
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        task.setCreator(creator);
        
        // Set assignee if specified
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with ID: " + request.getAssigneeId()));
            task.setAssignee(assignee);
        }
        
        // Set parent task if specified
        if (request.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(request.getParentTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent task not found with ID: " + request.getParentTaskId()));
            task.setParentTask(parentTask);
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        
        return convertToTaskResponse(savedTask);
    }
    
    public TaskResponse updateTask(Long id, CreateTaskRequest request, String userEmail) {
        log.info("Updating task with ID: {}", id);
        
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        
        // Check permissions
        if (!canModifyTask(task, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this task");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        
        // Update assignee if specified
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with ID: " + request.getAssigneeId()));
            task.setAssignee(assignee);
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with ID: {}", updatedTask.getId());
        
        return convertToTaskResponse(updatedTask);
    }
    
    public TaskResponse updateTaskStatus(Long id, TaskStatus status, String userEmail) {
        log.info("Updating task status for ID: {} to {}", id, status);
        
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        
        // Check permissions
        if (!canModifyTask(task, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this task");
        }
        
        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        log.info("Task status updated successfully for ID: {}", id);
        
        return convertToTaskResponse(updatedTask);
    }
    
    public TaskResponse assignTask(Long taskId, Long userId, String userEmail) {
        log.info("Assigning task {} to user {}", taskId, userId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        
        // Check permissions
        if (!canModifyTask(task, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to modify this task");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        task.setAssignee(user);
        Task updatedTask = taskRepository.save(task);
        log.info("Task assigned successfully");
        
        return convertToTaskResponse(updatedTask);
    }
    
    public void deleteTask(Long id, String userEmail) {
        log.info("Deleting task with ID: {}", id);
        
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        
        // Check permissions
        if (!canModifyTask(task, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to delete this task");
        }
        
        taskRepository.delete(task);
        log.info("Task deleted successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public long getTaskCountByStatus(TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
    
    @Transactional(readOnly = true)
    public long getTaskCountByProjectAndStatus(Long projectId, TaskStatus status) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        return taskRepository.countByProjectAndStatus(project, status);
    }
    
    private boolean canModifyProject(Project project, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return project.getOwner().getId().equals(user.getId()) || 
               project.getMembers().contains(user) || 
               user.getRole() == Role.ADMIN;
    }
    
    private boolean canModifyTask(Task task, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return task.getCreator().getId().equals(user.getId()) ||
               (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) ||
               canModifyProject(task.getProject(), userEmail);
    }
    
    private TaskResponse convertToTaskResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate(),
            task.getCompletedAt(),
            task.getEstimatedHours(),
            task.getActualHours(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            convertToProjectResponse(task.getProject()),
            task.getAssignee() != null ? convertToUserResponse(task.getAssignee()) : null,
            convertToUserResponse(task.getCreator()),
            null, // Parent task - éviter la récursion infinie
            null, // Subtasks - éviter la récursion infinie
            task.getComments() != null ? (long) task.getComments().size() : 0L
        );
    }
    
    private com.trapuce.projectHub.dto.response.ProjectResponse convertToProjectResponse(Project project) {
        return new com.trapuce.projectHub.dto.response.ProjectResponse(
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
            project.getMembers().stream().map(this::convertToUserResponse).toList(),
            project.getTasks() != null ? (long) project.getTasks().size() : 0L,
            project.getTasks() != null ? project.getTasks().stream()
                .filter(task -> task.getStatus().name().equals("COMPLETED")).count() : 0L
        );
    }
    
    private com.trapuce.projectHub.dto.response.UserResponse convertToUserResponse(User user) {
        if (user == null) {
            return null;
        }
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
