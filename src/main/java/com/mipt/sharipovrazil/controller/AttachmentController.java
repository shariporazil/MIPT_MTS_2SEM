package com.mipt.sharipovrazil.controller;

import com.mipt.sharipovrazil.dto.AttachmentResponseDto;
import com.mipt.sharipovrazil.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "Управление вложениями")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Загрузить файл для задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Файл успешно загружен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректный файл")
    })
    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDto> uploadAttachment(
            @Parameter(description = "ID задачи") @PathVariable Long taskId,
            @Parameter(description = "Файл для загрузки") @RequestParam("file") MultipartFile file)
            throws IOException {
        AttachmentResponseDto response = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Скачать файл")
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId)
            throws IOException {
        Resource resource = attachmentService.loadAsResource(attachmentId);
        com.mipt.sharipovrazil.model.TaskAttachment attachment = attachmentService.getAttachment(
                attachmentId);

        String contentDisposition = "attachment; filename=\"" + attachment.getFileName() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @Operation(summary = "Удалить файл")
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId)
            throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить список всех вложений задачи")
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getTaskAttachments(
            @PathVariable Long taskId) {
        List<AttachmentResponseDto> attachments = attachmentService.getAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(attachments);
    }
}