package com.trapuce.projectHub.dto.response;

import com.trapuce.projectHub.enums.Priority;
import com.trapuce.projectHub.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Priority priority;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse owner;
    private List<UserResponse> members;
    private Long taskCount;
    private Long completedTaskCount;
}
