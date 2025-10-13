package com.trapuce.projectHub.dto.response;

import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    private Integer estimatedHours;
    private Integer actualHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectResponse project;
    private UserResponse assignee;
    private UserResponse creator;
    private TaskResponse parentTask;
    private List<TaskResponse> subtasks;
    private Long commentCount;
}
