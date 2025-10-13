package com.trapuce.projectHub.controller;

import com.trapuce.projectHub.dto.request.CreateProjectRequest;
import com.trapuce.projectHub.dto.response.ApiResponse;
import com.trapuce.projectHub.dto.response.ProjectResponse;
import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.ProjectStatus;
import com.trapuce.projectHub.service.ProjectService;
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
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Projects", description = "Project management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieve paginated list of all projects")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getAllProjects(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching all projects with pagination");
        Page<ProjectResponse> projects = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/my-projects")
    @Operation(summary = "Get user's projects", description = "Retrieve projects where user is owner or member")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getMyProjects(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching projects for user: {}", authentication.getName());
        Page<ProjectResponse> projects = projectService.getProjectsByUser(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieve a specific project by its ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        log.info("Fetching project by ID: {}", id);
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(project));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search projects", description = "Search projects by name or description")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> searchProjects(
            @Parameter(description = "Search term") @RequestParam String search,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching projects with term: {}", search);
        Page<ProjectResponse> projects = projectService.searchProjects(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get projects by status", description = "Retrieve projects filtered by status")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getProjectsByStatus(
            @Parameter(description = "Project status") @PathVariable ProjectStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching projects by status: {}", status);
        Page<ProjectResponse> projects = projectService.getProjectsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get projects by priority", description = "Retrieve projects filtered by priority")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getProjectsByPriority(
            @Parameter(description = "Project priority") @PathVariable Priority priority,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching projects by priority: {}", priority);
        Page<ProjectResponse> projects = projectService.getProjectsByPriority(priority, pageable);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/due-date")
    @Operation(summary = "Get projects by due date range", description = "Retrieve projects within a due date range")
    public ResponseEntity<ApiResponse<Page<ProjectResponse>>> getProjectsByDueDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching projects by due date range: {} to {}", startDate, endDate);
        Page<ProjectResponse> projects = projectService.getProjectsByDueDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue projects", description = "Retrieve projects that are overdue")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getOverdueProjects() {
        log.info("Fetching overdue projects");
        List<ProjectResponse> projects = projectService.getOverdueProjects();
        return ResponseEntity.ok(ApiResponse.success(projects));
    }
    
    @PostMapping
    @Operation(summary = "Create new project", description = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        log.info("Creating new project: {}", request.getName());
        ProjectResponse project = projectService.createProject(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Project created successfully", project));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Update project information")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request,
            Authentication authentication) {
        log.info("Updating project with ID: {}", id);
        ProjectResponse project = projectService.updateProject(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", project));
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update project status", description = "Update project status")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProjectStatus(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam ProjectStatus status,
            Authentication authentication) {
        log.info("Updating project status for ID: {} to {}", id, status);
        ProjectResponse project = projectService.updateProjectStatus(id, status, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Project status updated successfully", project));
    }
    
    @PostMapping("/{id}/members/{userId}")
    @Operation(summary = "Add member to project", description = "Add a user as a member to the project")
    public ResponseEntity<ApiResponse<ProjectResponse>> addMemberToProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Parameter(description = "User ID") @PathVariable Long userId,
            Authentication authentication) {
        log.info("Adding member {} to project {}", userId, id);
        ProjectResponse project = projectService.addMemberToProject(id, userId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Member added successfully", project));
    }
    
    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove member from project", description = "Remove a user from the project members")
    public ResponseEntity<ApiResponse<ProjectResponse>> removeMemberFromProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Parameter(description = "User ID") @PathVariable Long userId,
            Authentication authentication) {
        log.info("Removing member {} from project {}", userId, id);
        ProjectResponse project = projectService.removeMemberFromProject(id, userId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", project));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Delete a project (owner or admin only)")
    public ResponseEntity<ApiResponse<String>> deleteProject(
            @Parameter(description = "Project ID") @PathVariable Long id,
            Authentication authentication) {
        log.info("Deleting project with ID: {}", id);
        projectService.deleteProject(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", "Project deleted"));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get project statistics", description = "Get project count by status")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getProjectStats() {
        log.info("Fetching project statistics");
        Map<String, Long> stats = Map.of(
            "todo", projectService.getProjectCountByStatus(ProjectStatus.TODO),
            "in_progress", projectService.getProjectCountByStatus(ProjectStatus.IN_PROGRESS),
            "on_hold", projectService.getProjectCountByStatus(ProjectStatus.ON_HOLD),
            "completed", projectService.getProjectCountByStatus(ProjectStatus.COMPLETED),
            "archived", projectService.getProjectCountByStatus(ProjectStatus.ARCHIVED)
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}

