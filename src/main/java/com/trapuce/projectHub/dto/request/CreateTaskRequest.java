package com.trapuce.projectHub.dto.request;

import com.trapuce.projectHub.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Task title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    @NotNull(message = "Priority is required")
    private Priority priority;
    
    private LocalDate dueDate;
    private Integer estimatedHours;
    private Long assigneeId;
    private Long parentTaskId;
}
