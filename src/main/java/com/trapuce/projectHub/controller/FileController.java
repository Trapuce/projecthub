package com.trapuce.projectHub.controller;

import com.trapuce.projectHub.dto.response.ApiResponse;
import com.trapuce.projectHub.entity.FileAttachment;
import com.trapuce.projectHub.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Files", description = "File management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class FileController {
    
    private final FileService fileService;
    
    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Upload a file to a project or task")
    public ResponseEntity<ApiResponse<FileAttachment>> uploadFile(
            @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Project ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "Task ID") @RequestParam(required = false) Long taskId,
            Authentication authentication) {
        log.info("Uploading file: {} for project: {} and task: {}", file.getOriginalFilename(), projectId, taskId);
        FileAttachment fileAttachment = fileService.uploadFile(file, projectId, taskId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", fileAttachment));
    }
    
    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download file", description = "Download a file by its ID")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "File ID") @PathVariable Long fileId,
            Authentication authentication) {
        log.info("Downloading file with ID: {}", fileId);
        Resource resource = fileService.downloadFile(fileId, authentication.getName());
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get files by project", description = "Retrieve files associated with a project")
    public ResponseEntity<ApiResponse<Page<FileAttachment>>> getFilesByProject(
            @Parameter(description = "Project ID") @PathVariable Long projectId,
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching files for project ID: {}", projectId);
        Page<FileAttachment> files = fileService.getFilesByProject(projectId, pageable, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(files));
    }
    
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get files by task", description = "Retrieve files associated with a task")
    public ResponseEntity<ApiResponse<Page<FileAttachment>>> getFilesByTask(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Fetching files for task ID: {}", taskId);
        Page<FileAttachment> files = fileService.getFilesByTask(taskId, pageable, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(files));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search files", description = "Search files by filename")
    public ResponseEntity<ApiResponse<Page<FileAttachment>>> searchFiles(
            @Parameter(description = "Search term") @RequestParam String search,
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching files with term: {}", search);
        Page<FileAttachment> files = fileService.searchFiles(search, pageable, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(files));
    }
    
    @GetMapping("/content-type/{contentType}")
    @Operation(summary = "Get files by content type", description = "Retrieve files filtered by content type")
    public ResponseEntity<ApiResponse<List<FileAttachment>>> getFilesByContentType(
            @Parameter(description = "Content type") @PathVariable String contentType) {
        log.info("Fetching files by content type: {}", contentType);
        List<FileAttachment> files = fileService.getFilesByContentType(contentType);
        return ResponseEntity.ok(ApiResponse.success(files));
    }
    
    @GetMapping("/project/{projectId}/size")
    @Operation(summary = "Get total file size by project", description = "Get total size of all files in a project")
    public ResponseEntity<ApiResponse<Long>> getTotalFileSizeByProject(
            @Parameter(description = "Project ID") @PathVariable Long projectId) {
        log.info("Fetching total file size for project ID: {}", projectId);
        Long totalSize = fileService.getTotalFileSizeByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(totalSize));
    }
    
    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Delete a file")
    public ResponseEntity<ApiResponse<String>> deleteFile(
            @Parameter(description = "File ID") @PathVariable Long fileId,
            Authentication authentication) {
        log.info("Deleting file with ID: {}", fileId);
        fileService.deleteFile(fileId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", "File deleted"));
    }
}

