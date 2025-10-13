package com.trapuce.projectHub.controller;

import com.trapuce.projectHub.dto.request.CreateTaskRequest;
import com.trapuce.projectHub.dto.response.ApiResponse;
import com.trapuce.projectHub.dto.response.TaskResponse;
import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.TaskStatus;
import com.trapuce.projectHub.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Task management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve paginated list of all tasks")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getAllTasks(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all tasks with pagination");
        Page<TaskResponse> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/my-tasks")
    @Operation(summary = "Get user's tasks", description = "Retrieve tasks assigned to the current user")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getMyTasks(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching tasks for user: {}", authentication.getName());
        Page<TaskResponse> tasks = taskService.getTasksByUser(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @Parameter(description = "Task ID") @PathVariable Long id) {
        log.info("Fetching task by ID: {}", id);
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project", description = "Retrieve tasks for a specific project")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByProject(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching tasks for project ID: {}", projectId);
        Page<TaskResponse> tasks = taskService.getTasksByProject(projectId, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/project/{projectId}/my-tasks")
    @Operation(summary = "Get user's tasks in project", description = "Retrieve user's tasks within a specific project")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByProjectAndUser(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching tasks for project {} and user {}", projectId, authentication.getName());
        Page<TaskResponse> tasks = taskService.getTasksByProjectAndUser(projectId, authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Search tasks by title or description")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> searchTasks(
            @Parameter(description = "Search term") @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching tasks with term: {}", search);
        Page<TaskResponse> tasks = taskService.searchTasks(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve tasks filtered by status")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable TaskStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching tasks by status: {}", status);
        Page<TaskResponse> tasks = taskService.getTasksByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority", description = "Retrieve tasks filtered by priority")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByPriority(
            @Parameter(description = "Task priority") @PathVariable Priority priority,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching tasks by priority: {}", priority);
        Page<TaskResponse> tasks = taskService.getTasksByPriority(priority, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/due-date")
    @Operation(summary = "Get tasks by due date range", description = "Retrieve tasks within a due date range")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getTasksByDueDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching tasks by due date range: {} to {}", startDate, endDate);
        Page<TaskResponse> tasks = taskService.getTasksByDueDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", description = "Retrieve tasks that are overdue")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks() {
        log.info("Fetching overdue tasks");
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/root")
    @Operation(summary = "Get root tasks", description = "Retrieve tasks that don't have a parent task")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getRootTasks(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching root tasks");
        Page<TaskResponse> tasks = taskService.getRootTasks(pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @GetMapping("/{parentTaskId}/subtasks")
    @Operation(summary = "Get subtasks", description = "Retrieve subtasks for a specific parent task")
    public ResponseEntity<ApiResponse<Page<TaskResponse>>> getSubtasks(
            @Parameter(description = "Parent task ID") @PathVariable Long parentTaskId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching subtasks for parent task ID: {}", parentTaskId);
        Page<TaskResponse> tasks = taskService.getSubtasks(parentTaskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @PostMapping
    @Operation(summary = "Create new task", description = "Create a new task")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        log.info("Creating new task: {}", request.getTitle());
        TaskResponse task = taskService.createTask(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", task));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update task information")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        log.info("Updating task with ID: {}", id);
        TaskResponse task = taskService.updateTask(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update task status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam TaskStatus status,
            Authentication authentication) {
        log.info("Updating task status for ID: {} to {}", id, status);
        TaskResponse task = taskService.updateTaskStatus(id, status, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task status updated successfully", task));
    }
    
    @PutMapping("/{id}/assign/{userId}")
    @Operation(summary = "Assign task", description = "Assign a task to a user")
    public ResponseEntity<ApiResponse<TaskResponse>> assignTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "User ID") @PathVariable Long userId,
            Authentication authentication) {
        log.info("Assigning task {} to user {}", id, userId);
        TaskResponse task = taskService.assignTask(id, userId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task assigned successfully", task));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete a task")
    public ResponseEntity<ApiResponse<String>> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            Authentication authentication) {
        log.info("Deleting task with ID: {}", id);
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", "Task deleted"));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get task statistics", description = "Get task count by status")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTaskStats() {
        log.info("Fetching task statistics");
        Map<String, Long> stats = Map.of(
            "todo", taskService.getTaskCountByStatus(TaskStatus.TODO),
            "in_progress", taskService.getTaskCountByStatus(TaskStatus.IN_PROGRESS),
            "on_hold", taskService.getTaskCountByStatus(TaskStatus.ON_HOLD),
            "completed", taskService.getTaskCountByStatus(TaskStatus.COMPLETED)
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/project/{projectId}/stats")
    @Operation(summary = "Get project task statistics", description = "Get task count by status for a specific project")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getProjectTaskStats(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {
        log.info("Fetching task statistics for project: {}", projectId);
        Map<String, Long> stats = Map.of(
            "todo", taskService.getTaskCountByProjectAndStatus(projectId, TaskStatus.TODO),
            "in_progress", taskService.getTaskCountByProjectAndStatus(projectId, TaskStatus.IN_PROGRESS),
            "on_hold", taskService.getTaskCountByProjectAndStatus(projectId, TaskStatus.ON_HOLD),
            "completed", taskService.getTaskCountByProjectAndStatus(projectId, TaskStatus.COMPLETED)
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

