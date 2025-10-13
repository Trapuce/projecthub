package com.trapuce.projectHub.dto.request;

import com.trapuce.projectHub.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Priority is required")
    private Priority priority;
    
    private LocalDate startDate;
    private LocalDate dueDate;
    
    private List<Long> memberIds;
}
