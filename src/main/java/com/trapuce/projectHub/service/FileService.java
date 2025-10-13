package com.trapuce.projectHub.service;

import com.trapuce.projectHub.entity.FileAttachment;
import com.trapuce.projectHub.entity.Project;
import com.trapuce.projectHub.entity.Task;
import com.trapuce.projectHub.entity.User;
import com.trapuce.projectHub.exception.ProjectAccessDeniedException;
import com.trapuce.projectHub.exception.FileUploadException;
import com.trapuce.projectHub.exception.ResourceNotFoundException;
import com.trapuce.projectHub.repository.FileAttachmentRepository;
import com.trapuce.projectHub.repository.ProjectRepository;
import com.trapuce.projectHub.repository.TaskRepository;
import com.trapuce.projectHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FileService {
    
    private final FileAttachmentRepository fileAttachmentRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.allowed-types}")
    private String allowedTypes;
    
    public FileAttachment uploadFile(MultipartFile file, Long projectId, Long taskId, String userEmail) {
        log.info("Uploading file: {} for project: {} and task: {}", file.getOriginalFilename(), projectId, taskId);
        
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Validate file
        validateFile(file);
        
        // Check permissions
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
            if (!canAccessProject(project, userEmail)) {
                throw new ProjectAccessDeniedException("You don't have permission to upload files to this project");
            }
        }
        
        if (taskId != null) {
            Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
            if (!canAccessProject(task.getProject(), userEmail)) {
                throw new ProjectAccessDeniedException("You don't have permission to upload files to this task");
            }
        }
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create file attachment record
            FileAttachment fileAttachment = new FileAttachment();
            fileAttachment.setFileName(uniqueFilename);
            fileAttachment.setOriginalFileName(originalFilename);
            fileAttachment.setFilePath(filePath.toString());
            fileAttachment.setContentType(file.getContentType());
            fileAttachment.setFileSize(file.getSize());
            fileAttachment.setUploadedBy(user);
            
            if (projectId != null) {
                Project project = projectRepository.findById(projectId).orElse(null);
                fileAttachment.setProject(project);
            }
            
            if (taskId != null) {
                Task task = taskRepository.findById(taskId).orElse(null);
                fileAttachment.setTask(task);
            }
            
            FileAttachment savedFile = fileAttachmentRepository.save(fileAttachment);
            log.info("File uploaded successfully with ID: {}", savedFile.getId());
            
            return savedFile;
            
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new FileUploadException("Failed to upload file: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public Resource downloadFile(Long fileId, String userEmail) {
        log.info("Downloading file with ID: {}", fileId);
        
        FileAttachment fileAttachment = fileAttachmentRepository.findById(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + fileId));
        
        // Check permissions
        if (fileAttachment.getProject() != null) {
            if (!canAccessProject(fileAttachment.getProject(), userEmail)) {
                throw new ProjectAccessDeniedException("You don't have permission to download this file");
            }
        }
        
        if (fileAttachment.getTask() != null) {
            if (!canAccessProject(fileAttachment.getTask().getProject(), userEmail)) {
                throw new ProjectAccessDeniedException("You don't have permission to download this file");
            }
        }
        
        try {
            Path filePath = Paths.get(fileAttachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                log.info("File downloaded successfully: {}", fileAttachment.getOriginalFileName());
                return resource;
            } else {
                throw new FileUploadException("File not found or not readable");
            }
            
        } catch (MalformedURLException e) {
            log.error("Error downloading file: {}", e.getMessage());
            throw new FileUploadException("Failed to download file: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public Page<FileAttachment> getFilesByProject(Long projectId, Pageable pageable, String userEmail) {
        log.info("Fetching files for project ID: {}", projectId);
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        
        if (!canAccessProject(project, userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to view files in this project");
        }
        
        return fileAttachmentRepository.findByProjectOrProjectTasks(project, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<FileAttachment> getFilesByTask(Long taskId, Pageable pageable, String userEmail) {
        log.info("Fetching files for task ID: {}", taskId);
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
        
        if (!canAccessProject(task.getProject(), userEmail)) {
            throw new ProjectAccessDeniedException("You don't have permission to view files in this task");
        }
        
        return fileAttachmentRepository.findByTask(task, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<FileAttachment> searchFiles(String search, Pageable pageable, String userEmail) {
        log.info("Searching files with term: {}", search);
        return fileAttachmentRepository.findByFileNameContaining(search, pageable);
    }
    
    public void deleteFile(Long fileId, String userEmail) {
        log.info("Deleting file with ID: {}", fileId);
        
        FileAttachment fileAttachment = fileAttachmentRepository.findById(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + fileId));
        
        // Check permissions
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!fileAttachment.getUploadedBy().getId().equals(user.getId()) && user.getRole().name().equals("ADMIN")) {
            throw new ProjectAccessDeniedException("You can only delete files you uploaded");
        }
        
        try {
            // Delete physical file
            Path filePath = Paths.get(fileAttachment.getFilePath());
            Files.deleteIfExists(filePath);
            
            // Delete database record
            fileAttachmentRepository.delete(fileAttachment);
            log.info("File deleted successfully with ID: {}", fileId);
            
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new FileUploadException("Failed to delete file: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public List<FileAttachment> getFilesByContentType(String contentType) {
        return fileAttachmentRepository.findByContentType(contentType);
    }
    
    @Transactional(readOnly = true)
    public Long getTotalFileSizeByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        return fileAttachmentRepository.getTotalFileSizeByProject(project);
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new FileUploadException("File size exceeds maximum allowed size (10MB)");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new FileUploadException("File type not allowed. Allowed types: " + allowedTypes);
        }
    }
    
    private boolean canAccessProject(Project project, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return project.getOwner().getId().equals(user.getId()) || 
               project.getMembers().contains(user) || 
               user.getRole().name().equals("ADMIN");
    }
}
