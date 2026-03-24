package com.mipt.sharipovrazil.service;

import com.mipt.sharipovrazil.dto.AttachmentResponseDto;
import com.mipt.sharipovrazil.exception.AttachmentNotFoundException;
import com.mipt.sharipovrazil.exception.TaskNotFoundException;
import com.mipt.sharipovrazil.model.TaskAttachment;
import com.mipt.sharipovrazil.repository.AttachmentRepository;
import com.mipt.sharipovrazil.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachmentService {

    private final Path uploadPath;
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    public AttachmentService(
            @Value("${app.upload.dir:uploads}") String uploadDir,
            AttachmentRepository attachmentRepository,
            TaskRepository taskRepository) throws IOException {
        this.uploadPath = Paths.get(uploadDir);
        this.attachmentRepository = attachmentRepository;
        this.taskRepository = taskRepository;

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    public AttachmentResponseDto storeAttachment(Long taskId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Проверка на размер файла
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("File size exceeds limit: " + file.getSize() + " bytes");
        }

        // Проверяем существование задачи
        if (taskRepository.findById(taskId).isEmpty()) {
            throw new TaskNotFoundException("Task not found with id: " + taskId);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        Path targetPath = uploadPath.resolve(storedFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(originalFilename);
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());

        TaskAttachment saved = attachmentRepository.save(attachment);

        return mapToResponseDto(saved);
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment not found with id: " + attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadPath.resolve(attachment.getStoredFileName());

        if (!Files.exists(filePath)) {
            throw new AttachmentNotFoundException("File not found for attachment id: " + attachmentId);
        }

        return new InputStreamResource(Files.newInputStream(filePath));
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);
        Path filePath = uploadPath.resolve(attachment.getStoredFileName());

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        attachmentRepository.deleteById(attachmentId);
    }

    public List<AttachmentResponseDto> getAttachmentsByTaskId(Long taskId) {
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private AttachmentResponseDto mapToResponseDto(TaskAttachment attachment) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setSize(attachment.getSize());
        dto.setUploadedAt(attachment.getUploadedAt());
        return dto;
    }
}